package com.patryklikus.kit.jpa

/**
 * Maps an enum field to a compact `smallint` foreign key backed by an auto-generated,
 * auto-seeded lookup table — a safer, smaller alternative to `@Enumerated`.
 *
 * One lookup table is created per enum type (deduplicated across all fields). [name] overrides
 * the table name, which otherwise derives from the enum's simple class name, lower-cased.
 *
 * Not a JPA association: an enum cannot be an `@Entity` (JPA spec 2.1), so `@ManyToOne` is impossible.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnumRef(val name: String = "")
