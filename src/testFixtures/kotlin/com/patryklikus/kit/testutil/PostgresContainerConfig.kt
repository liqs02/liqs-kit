package com.patryklikus.kit.testutil

import com.patryklikus.kit.spring.Config
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@Config
object PostgresContainerConfig {
    @Bean(initMethod = "start")
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer =
        PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(60))
}
