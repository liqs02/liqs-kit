package com.patryklikus.kit.testapp

import com.patryklikus.kit.modulith.EnableAppModules
import com.patryklikus.kit.testapp.sample.SampleNestedProperties
import com.patryklikus.kit.testapp.sample.SampleProperties
import com.patryklikus.kit.testutil.PostgresContainerConfig
import com.tngtech.archunit.core.importer.ImportOption
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.modulith.core.ApplicationModules

@SpringBootApplication
@EnableJpaAuditing
@EnableAppModules
@EnableConfigurationProperties(SampleProperties::class, SampleNestedProperties::class, OutsideProperties::class)
@Import(PostgresContainerConfig::class)
class TestApp {
    @Bean
    fun applicationModules(): ApplicationModules =
        ApplicationModules.of(TestApp::class.java, ImportOption { true })
}
