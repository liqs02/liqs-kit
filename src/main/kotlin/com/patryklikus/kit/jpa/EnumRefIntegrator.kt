package com.patryklikus.kit.jpa

import jakarta.persistence.Enumerated
import org.hibernate.boot.Metadata
import org.hibernate.boot.model.relational.SimpleAuxiliaryDatabaseObject
import org.hibernate.boot.spi.BootstrapContext
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.integrator.spi.Integrator
import org.hibernate.service.spi.SessionFactoryServiceRegistry

/**
 * Generates the lookup table + foreign-key DDL for every `@EnumRef` field and forces its column to
 * `smallint`, mirroring [com.patryklikus.kit.jpa.KotlinNullabilityIntegrator]'s integrate-time column edits.
 *
 * Per Spike Decision #2:
 *  - one `CREATE TABLE <lookup>(id smallint not null, name varchar(255) not null unique, primary key (id))`
 *    aux object per *distinct* enum type (`beforeTables = true`, so it exists before entity tables),
 *  - one `ALTER TABLE <entity> ADD CONSTRAINT <fk> FOREIGN KEY (<col>) REFERENCES <lookup>(id)` aux object
 *    per field (`beforeTables = false`, so the entity table Hibernate creates already exists),
 *  - every name is schema-qualified from `persistentClass.table.schema` (Modulith places entities in a
 *    per-module schema; aux SQL is not auto-qualified),
 *  - the FK column is forced to `smallint` to match the lookup PK.
 *
 * It also populates [EnumRefTypes] (read by the Task 7 seeder) and rejects `@EnumRef` + `@Enumerated` on
 * the same property (Spike Decision #3: they silently coexist, so the conflict must be caught explicitly).
 *
 * Runs after [com.patryklikus.kit.modulith.ModuleSchemaIntegrator] (registered via `integrator_provider`,
 * which Hibernate adds before ServiceLoader integrators), so `table.schema` is already assigned here.
 */
class EnumRefIntegrator : Integrator {

    override fun integrate(
        metadata: Metadata,
        bootstrapContext: BootstrapContext,
        sessionFactory: SessionFactoryImplementor,
    ) {
        val fields = enumRefFields(metadata)
        if (fields.isEmpty()) return

        val createdLookups = mutableSetOf<Class<out Enum<*>>>()
        fields.forEach { field ->
            val persistentClass = metadata.getEntityBinding(field.entityClass.name)
            val property = persistentClass.getProperty(field.propertyName)

            requireNoEnumeratedConflict(field)

            val schemaPrefix = persistentClass.table.schema?.let { "$it." } ?: ""
            val lookup = "$schemaPrefix${field.tableName}"
            val entity = "$schemaPrefix${persistentClass.table.name}"
            val column = property.value.columns.single().name

            property.value.columns.forEach { it.sqlType = "smallint" }

            if (createdLookups.add(field.enumType)) {
                metadata.database.addAuxiliaryDatabaseObject(
                    SimpleAuxiliaryDatabaseObject(
                        metadata.database.defaultNamespace,
                        arrayOf(
                            "create table $lookup " +
                                "(id smallint not null, name varchar(255) not null unique, primary key (id))"
                        ),
                        arrayOf("drop table if exists $lookup cascade"),
                        emptySet(),
                        true,
                    )
                )
            }

            val fk = "fk_${persistentClass.table.name}_${column}"
            metadata.database.addAuxiliaryDatabaseObject(
                SimpleAuxiliaryDatabaseObject(
                    metadata.database.defaultNamespace,
                    arrayOf(
                        "alter table $entity add constraint $fk " +
                            "foreign key ($column) references $lookup(id)"
                    ),
                    arrayOf("alter table if exists $entity drop constraint if exists $fk"),
                    emptySet(),
                    false,
                )
            )

            EnumRefTypes.register(field.enumType, lookup)
        }
    }

    internal fun requireNoEnumeratedConflict(field: EnumRefField) {
        val jvmField = field.entityClass.getDeclaredField(field.propertyName)
        check(!jvmField.isAnnotationPresent(Enumerated::class.java)) {
            "${field.entityClass.name}.${field.propertyName} carries both @EnumRef and @Enumerated; " +
                "they silently coexist in Hibernate and would map the property twice. Use only @EnumRef."
        }
    }

    override fun disintegrate(
        sessionFactory: SessionFactoryImplementor,
        serviceRegistry: SessionFactoryServiceRegistry,
    ) {
    }
}
