package com.patryklikus.kit.testapp

import com.patryklikus.kit.modulith.EnableAppModules
import com.patryklikus.kit.testutil.PostgresContainerConfig
import com.tngtech.archunit.core.importer.ImportOption
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.modulith.core.ApplicationModules

@SpringBootApplication
@EnableJpaAuditing
@EnableAppModules
@ConfigurationPropertiesScan
@Import(PostgresContainerConfig::class)
class TestApp {
    @Bean
    fun applicationModules(): ApplicationModules =
        ApplicationModules.of(TestApp::class.java, ImportOption { true })
}
