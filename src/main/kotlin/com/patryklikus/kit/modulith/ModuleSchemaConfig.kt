package com.patryklikus.kit.modulith

import com.patryklikus.kit.spring.Config
import org.hibernate.jpa.boot.spi.IntegratorProvider
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.modulith.core.ApplicationModules

@Config
class ModuleSchemaConfig {
    @Bean
    fun moduleSchemaHibernateCustomizer(modules: ApplicationModules): HibernatePropertiesCustomizer =
        HibernatePropertiesCustomizer { props ->
            props["hibernate.integrator_provider"] =
                IntegratorProvider { listOf(ModuleSchemaIntegrator(modules)) }
        }
}
