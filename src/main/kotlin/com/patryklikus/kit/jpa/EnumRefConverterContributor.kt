package com.patryklikus.kit.jpa

import com.fasterxml.classmate.TypeResolver
import jakarta.persistence.AttributeConverter
import org.hibernate.boot.ResourceStreamLocator
import org.hibernate.boot.model.convert.internal.AutoApplicableConverterDescriptorStandardImpl
import org.hibernate.boot.model.convert.spi.ConverterDescriptor
import org.hibernate.boot.model.convert.spi.JpaAttributeConverterCreationContext
import org.hibernate.boot.spi.AdditionalMappingContributions
import org.hibernate.boot.spi.AdditionalMappingContributor
import org.hibernate.boot.spi.InFlightMetadataCollector
import org.hibernate.boot.spi.MetadataBuildingContext
import org.hibernate.mapping.BasicValue
import org.hibernate.resource.beans.spi.ProvidedInstanceManagedBeanImpl
import org.hibernate.type.descriptor.converter.internal.AttributeConverterBean
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter

/**
 * Binds an [EnumRefConverter] to every `@EnumRef` field's `BasicValue`, so the enum is stored as a
 * `smallint` id instead of the default `@Enumerated(ORDINAL)` mapping.
 *
 * Registered via ServiceLoader (`META-INF/services/org.hibernate.boot.spi.AdditionalMappingContributor`).
 * This hook runs *after* entity binding (`metadata.entityBindings` is populated, so `@EnumRef` is
 * discovered straight off the JVM field — no classpath scan, no runtime dependency) but its edits
 * still feed the runtime model. Because the enum `BasicValue` is already resolved when this runs, it:
 *   1. clears `enumerationStyle`,
 *   2. sets a per-type converter descriptor on the value,
 *   3. nulls the cached private `resolution` so Hibernate rebuilds it *with* the converter at
 *      SessionFactory build (Spike Decision #1 — proven on ORM 7.2.4; guarded with [runCatching]).
 */
class EnumRefConverterContributor : AdditionalMappingContributor {

    override fun contribute(
        contributions: AdditionalMappingContributions,
        metadata: InFlightMetadataCollector,
        resourceStreamLocator: ResourceStreamLocator,
        buildingContext: MetadataBuildingContext,
    ) {
        enumRefFields(metadata).forEach { field ->
            val persistentClass = metadata.getEntityBinding(field.entityClass.name)
            val property = persistentClass.getProperty(field.propertyName)
            val value = property.value as BasicValue
            value.setEnumerationStyle(null)
            value.setJpaAttributeConverterDescriptor(
                ExplicitDomainInstanceDescriptor(
                    EnumRefConverter(field.enumType),
                    field.enumType,
                    java.lang.Short::class.java,
                )
            )
            clearResolution(value, field.enumType)
        }
    }

    /**
     * Nulls the already-built `BasicValue.resolution` so the converter is honoured on rebuild.
     * Reflection on a Hibernate internal pinned to ORM 7.2.4 (Spike Decision #1's reflection caveat:
     * a non-reflection rebuild via a fresh `BasicValue` was rejected as more fragile than this proven
     * one-field clear); guarded with a clear failure message if the field ever disappears.
     */
    private fun clearResolution(value: BasicValue, enumType: Class<*>) {
        runCatching {
            BasicValue::class.java.getDeclaredField("resolution")
                .apply { isAccessible = true }
                .set(value, null)
        }.getOrElse {
            error(
                "Failed to clear BasicValue.resolution for @EnumRef ${enumType.name}; " +
                    "the EnumRefConverter would be ignored. This reflection is pinned to Hibernate " +
                    "ORM 7.2.4 — verify the internal field after an ORM upgrade. Cause: ${it.message}"
            )
        }
    }
}

/**
 * A [ConverterDescriptor] that holds a per-type converter *instance* and pins an *explicit* domain
 * type, so a single generic [EnumRefConverter] auto-applies to one enum type while still carrying
 * that type for the read path. Stock factories cannot do this (Spike Decision #1).
 */
class ExplicitDomainInstanceDescriptor(
    private val instance: AttributeConverter<*, *>,
    private val domainClass: Class<*>,
    private val relationalClass: Class<*>,
) : ConverterDescriptor<Any, Any> {
    private val typeResolver = TypeResolver()
    private val domain = typeResolver.resolve(domainClass)
    private val relational = typeResolver.resolve(relationalClass)

    @Suppress("UNCHECKED_CAST")
    override fun getAttributeConverterClass() =
        instance.javaClass as Class<out AttributeConverter<Any, Any>>

    override fun getDomainValueResolvedType() = domain

    override fun getRelationalValueResolvedType() = relational

    override fun getAutoApplyDescriptor() = AutoApplicableConverterDescriptorStandardImpl(this)

    @Suppress("UNCHECKED_CAST")
    override fun createJpaAttributeConverter(
        ctx: JpaAttributeConverterCreationContext,
    ): JpaAttributeConverter<Any, Any> =
        AttributeConverterBean(
            ProvidedInstanceManagedBeanImpl(instance as AttributeConverter<Any, Any>),
            ctx.javaTypeRegistry.resolveDescriptor(instance.javaClass),
            domainClass as Class<Any>,
            relationalClass as Class<Any>,
            ctx,
        )
}
