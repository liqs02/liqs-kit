package com.patryklikus.kit.jpa

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var _id: Long? = null

    @CreatedDate
    private var _createdAt: Instant? = null

    @LastModifiedDate
    private var _updatedAt: Instant? = null

    val id: Long get() = requireNotNull(_id)
    val createdAt: Instant get() = requireNotNull(_createdAt)
    val updatedAt: Instant get() = requireNotNull(_updatedAt)
}
