package com.patryklikus.kit.spring

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

/**
 * Stateless REST defaults for a module: matches [pathPatterns], requires authentication,
 * disables CSRF / form login / HTTP Basic.
 */
fun HttpSecurity.restModuleDefaults(vararg pathPatterns: String): SecurityFilterChain {
    invoke {
        securityMatcher(*pathPatterns)
        csrf { disable() }
        authorizeHttpRequests { authorize(anyRequest, authenticated) }
        formLogin { disable() }
        httpBasic { disable() }
    }
    return build()
}
