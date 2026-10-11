# liqs-kit

Personal Kotlin + Spring helpers.

## @EnumRef

Hibernate JPA converter that maps enum fields to a dedicated lookup table (one per enum type) with stable ids:

```kotlin
enum class Colour { RED, GREEN, BLUE }

@Entity
data class Car(
    @EnumRef val colour: Colour
)
```

Generates a `colour(id smallint, name)` lookup table, auto-seeded from the enum constants. The entity field becomes a `smallint` column with a foreign key to it. **One lookup table per enum type** (shared across all fields using it), and **the enum name is the stable key** — reordering constants never remaps existing ids. A safer, more compact alternative to `@Enumerated(STRING)` / `@Enumerated(ORDINAL)`.

## Status

Preview. Published as `0.0.0-SNAPSHOT` only; API may change without notice.
