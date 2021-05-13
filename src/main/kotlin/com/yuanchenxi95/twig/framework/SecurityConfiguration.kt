package com.yuanchenxi95.twig.framework

import com.yuanchenxi95.twig.constants.generateAuthenticationError
import com.yuanchenxi95.twig.framework.codecs.encodeProtobufValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.just

@Component
class AuthenticationManager : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val authToken: String = authentication.credentials.toString()
        // TODO(jiangbryn) Handles the authentication using session ID.
        // return just(UsernamePasswordAuthenticationToken(authToken, authToken))
        return just(UsernamePasswordAuthenticationToken(authToken, authToken, listOf()))
    }
}

@Component
class SecurityContextRepository : ServerSecurityContextRepository {
    @Autowired
    private lateinit var authenticationManager: AuthenticationManager
    override fun save(swe: ServerWebExchange, sc: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val request: ServerHttpRequest = swe.request
        val authHeader: String? = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val authToken = authHeader.substring(7)
            val auth: Authentication = UsernamePasswordAuthenticationToken(authToken, authToken)
            authenticationManager.authenticate(auth).map { authentication: Authentication? ->
                SecurityContextImpl(
                    authentication
                )
            }
        } else {
            Mono.empty()
        }
    }
}

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var securityContextRepository: SecurityContextRepository

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // Disable default security.
        http.httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .logout().disable()

        // Add custom security.
        http.authenticationManager(this.authenticationManager)
            .securityContextRepository(this.securityContextRepository)

        http.exceptionHandling().authenticationEntryPoint { serverWebExchange, exception ->
            val response = serverWebExchange.response
            val bufferFactory = response.bufferFactory()
            response.headers.contentType = MediaType.APPLICATION_JSON
            response.statusCode = HttpStatus.UNAUTHORIZED
            val dataBuffer = encodeProtobufValue(generateAuthenticationError(exception), bufferFactory)
            response.writeWith(just(dataBuffer))
        }

        http.exceptionHandling()
            .accessDeniedHandler { serverWebExchange, exception ->
                val response = serverWebExchange.response
                val bufferFactory = response.bufferFactory()
                response.headers.contentType = MediaType.APPLICATION_JSON
                response.statusCode = HttpStatus.FORBIDDEN
                val dataBuffer = encodeProtobufValue(generateAuthenticationError(exception), bufferFactory)
                response.writeWith(just(dataBuffer))
            }

        // Disable authentication for `/public/**` routes.
        http.authorizeExchange().pathMatchers("/public/**").permitAll()
        // Disable authentication for `/authenticated/**` routes.
        http.authorizeExchange().pathMatchers("/authentication/**").permitAll()
        http.authorizeExchange().anyExchange().authenticated()
        return http.build()
    }
}
