package com.patryklikus.kit.jpa

import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@AutoConfiguration
class EnumRefAutoConfiguration {
    @Bean
    fun enumRefSeeder(emf: EntityManagerFactory, txm: PlatformTransactionManager): EnumRefSeeder =
        EnumRefSeeder(emf, TransactionTemplate(txm), EnumRefTypes.all())
}
