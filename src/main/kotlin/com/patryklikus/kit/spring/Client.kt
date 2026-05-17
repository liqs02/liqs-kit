package com.patryklikus.kit.spring

import org.springframework.stereotype.Component

/**
 * Stereotype for a class wrapping an outbound HTTP / external API client.
 *
 * Marks a Spring component whose primary responsibility is translating between domain types and an
 * external system's wire format (REST, SOAP, gRPC, ...). Used for hand-written wrappers around
 * generated clients (Feign, RestClient, gRPC stubs) so that callers depend on a domain-shaped port,
 * not on the transport-level interface.
 *
 * @see Component
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class Client
