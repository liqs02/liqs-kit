# `@EnumRef` — lookup table dla enumów JPA

Data: 2026-06-25
Status: zaakceptowany do planowania

## Problem

Mapowanie enumów w JPA daje dwie złe opcje:

- `@Enumerated(STRING)` — czytelne i stabilne, ale przy wielu wierszach string
  niepotrzebnie zajmuje miejsce.
- `@Enumerated(ORDINAL)` — kompaktowe, ale niebezpieczne: przestawienie lub
  usunięcie stałej cicho przesuwa znaczenie istniejących danych.

Chcemy **kompaktowy mały int w wierszu encji** + **stabilne, bezpieczne
mapowanie** + **integralność na poziomie bazy (FK)** + czytelną tabelę do joinów.

## Rozwiązanie w skrócie

Jedna adnotacja `@EnumRef` na **polu encji** — zamiennik `@Enumerated`. Enum
pozostaje czysty, bez żadnej adnotacji:

```kotlin
enum class Something { VAL1, VAL2, VAL3 }   // czysty enum, zero adnotacji

@Entity
class Foo(
    @EnumRef val something: Something,        // zamiast @Enumerated → tabela + FK
) : BaseEntity()
```

powstaje:

- tabela `something(id smallint PK, name varchar unique)`, zaseedowana
  `1→VAL1, 2→VAL2, 3→VAL3`,
- kolumna `foo.something` typu `smallint` z **FK → something(id)**.

`@EnumRef` to alternatywna strategia mapowania pola — mentalnie jak
`@Enumerated`, tylko zamiast STRING/ORDINAL daje „tabela lookup + FK". Pole
pozostaje czystym enumem; nie jest to asocjacja JPA (`@ManyToOne` do enuma jest
zabronione przez specyfikację JPA, sekcja 2.1 — enum nie może być encją).

`id` jest nadawane automatycznie w bazie (auto-ID); kluczem stabilności jest
**nazwa** stałej, nie kolejność.

### Jedna tabela per typ enuma

Tabela jest kluczowana po **typie enuma**, nie po polu. Jeśli wiele pól (w jednej
lub wielu encjach) używa `@EnumRef val x: Something`, powstaje **jedna** tabela
`something`, a wszystkie te pola dostają FK do niej. Biblioteka deduplikuje po
klasie enuma: jeden `CREATE TABLE` i jeden seed per typ, niezależnie od liczby
pól.

## Decyzje projektowe

| Temat | Decyzja |
|---|---|
| Adnotacja | `@EnumRef` — wyłącznie na polu encji (opcjonalnie `name=` na nazwę tabeli) |
| Enum | bez żadnej adnotacji; trigger jest na polu |
| Konflikt z `@Enumerated` | `@EnumRef` + `@Enumerated` na tym samym polu → twardy błąd przy starcie |
| Nazwa tabeli | z nazwy klasy enuma (`Something` → `something`); jedna tabela per typ |
| Typ kolumny id | `smallint` |
| Źródło id | auto-ID w bazie; klucz = `name` (unique) |
| Stabilność | istniejące nazwy nigdy nie zmieniają id; nowa stała → nowy id |
| Usunięta stała (orphan w tabeli) | **twardy błąd przy starcie** |
| Tryby schematu | Hibernate DDL **oraz** migracje (Flyway/Liquibase) |
| Walidacja w trybie migracji | rekoncyliacja zbioru nazw przy starcie (bez dodatkowych zapytań) |
| Metadane per wartość (label/order/…) | poza zakresem (YAGNI, możliwe później) |

## Komponenty

| Komponent | Rola |
|---|---|
| `@EnumRef` | marker na polu encji; włącza pole na konwerter + FK; opcjonalne `name` nadpisuje nazwę tabeli |
| `EnumRefRegistry` | singleton runtime: `enum const ↔ smallint id`; wczytany przy starcie; jeden wpis per typ enuma |
| `EnumRefConverter` | `AttributeConverter` (jedna instancja per typ enuma) podpinany do pól `@EnumRef`; mapuje enum ↔ `smallint` przez rejestr |
| `EnumRefIntegrator` | analogicznie do `KotlinNullabilityIntegrator`: skanuje encje, zbiera pola `@EnumRef`, **(1)** dla każdego *typu* enuma (dedup) rejestruje `AuxiliaryDatabaseObject` (`CREATE TABLE` lookup), **(2)** dla każdego pola `@EnumRef` dokłada `ALTER TABLE … ADD FOREIGN KEY`, **(3)** waliduje konflikt `@EnumRef`+`@Enumerated` |
| `EnumRefSeeder` | bean Spring (auto-config); przy starcie: upsert nazw→id, wczytanie rejestru, rekoncyliacja/drift, twardy błąd na orphanach |

## Przepływ przy starcie

1. Hibernate buduje SessionFactory. `EnumRefConverter` podpina się do pól
   oznaczonych `@EnumRef` (konflikt z `@Enumerated` → twardy błąd).
   `AuxiliaryDatabaseObject` emituje DDL **tylko** w trybie generacji schematu
   (`create`/`update`); przy `validate`/`none` nie robi nic (tabelę dostarcza
   migracja).
2. `EnumRefSeeder` uruchamia się **po** SessionFactory, a **przed** obsługą
   ruchu i jakimkolwiek dostępem do danych, dla każdego użytego typu enuma:
   - `SELECT id, name` z tabeli lookup (jedno zapytanie per typ),
   - nazwy z kodu nieobecne w tabeli → `INSERT` z nowym id,
   - nazwy w tabeli nieobecne w kodzie (orphan) → **twardy błąd**,
   - zbudowanie `EnumRefRegistry` (`enum const → id`).
3. Od tej pory `EnumRefConverter` zapisuje/odczytuje encje przez rejestr.

### Kolejność (krytyczne)

`EnumRefRegistry` musi być wypełniony zanim ktokolwiek odczyta/zapisze encję
używającą enuma. Seeder realizujemy tak, by działał przed pierwszym dostępem do
danych (np. `SmartInitializingSingleton`/uporządkowany inicjalizator), również w
testach z `create-drop`.

## Stabilność i drift

- Klucz = `name` (`unique`). Istniejące wartości zachowują id także po restarcie.
- W trybie `create-drop` (testy) tabela jest odtwarzana pusta i seedowana od
  nowa — id startują od 1; brak orphanów.
- W trwałej bazie (prod): istniejące nazwy stabilne; usunięcie stałej w kodzie
  bez migracji → twardy błąd przy starcie (świadoma decyzja zamiast cichego
  rozjazdu).
- `id` to surogat wewnętrzny: stabilny w obrębie jednej bazy, między
  środowiskami może się różnić — bez wpływu na integralność.

## Rejestracja

- `EnumRefIntegrator` przez `META-INF/services/org.hibernate.integrator.spi.Integrator`
  (jak `KotlinNullabilityIntegrator`).
- Konwertery + `AuxiliaryDatabaseObject` kontrybuowane podczas budowy metadanych.
- `EnumRefSeeder` przez auto-config (`AutoConfiguration.imports`).

## Modulith / schemat

Tabela lookup trafia do schematu modułu, w którym leży **enum** (spójnie z
`ModuleSchemaIntegrator` i pełnym identyfikatorem modułu). Nazwa tabeli z nazwy
klasy enuma; przy kolizji prostych nazw między modułami rozróżnia schemat.

## Testy (IT, Testcontainers/Postgres — jak istniejące `*IT.kt`)

- generacja tabeli lookup + FK w trybie `create-drop`,
- seedowanie wartości (`1→VAL1`, …) i kompaktowy typ kolumny (`smallint`),
- round-trip: zapis i odczyt encji z polem enum,
- jedna tabela dla dwóch encji używających tego samego enuma (dedup), oba FK,
- idempotentny restart (istniejące id niezmienione),
- dodanie nowej stałej → nowy id na końcu,
- integralność FK: odrzucenie zapisu z nieistniejącym id,
- orphan (nazwa w tabeli bez stałej w kodzie) → twardy błąd przy starcie,
- `@EnumRef` + `@Enumerated` na tym samym polu → twardy błąd przy starcie.

## Poza zakresem

- Metadane per wartość (label, kolejność, opis, flagi).
- Konfigurowalny typ id (na razie zawsze `smallint`).
- Aktywne zarządzanie FK w trybie migracji (introspekcja `information_schema`).
- Automatyczne kasowanie/oznaczanie osieroconych wierszy (zamiast tego: błąd).

## Ryzyka / do zweryfikowania w implementacji

- Dokładny mechanizm podpięcia konwertera per typ enuma (auto-apply
  `AttributeConverter` vs. niestandardowy typ przez kontrybutora metadanych) —
  do potwierdzenia spikem.
- Idempotentność DDL z `AuxiliaryDatabaseObject` w trybie `update`
  (`CREATE TABLE IF NOT EXISTS`, guard na `ADD FOREIGN KEY`).
- Gwarancja kolejności: seeder/rejestr gotowe przed pierwszym dostępem do danych.
