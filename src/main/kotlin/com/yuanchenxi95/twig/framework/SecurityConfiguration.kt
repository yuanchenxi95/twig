package com.yuanchenxi95.twig.framework

import com.yuanchenxi95.twig.constants.generateAuthenticationError
import com.yuanchenxi95.twig.framework.codecs.encodeProtobufValue
import com.yuanchenxi95.twig.framework.securities.TwigSecurityContextRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Autowired
    private lateinit var securityContextRepository: TwigSecurityContextRepository

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // Disable default security.
        http.httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .logout().disable()

        http.exceptionHandling().authenticationEntryPoint { serverWebExchange, exception ->
            val response = serverWebExchange.response
            val bufferFactory = response.bufferFactory()
            response.headers.contentType = MediaType.APPLICATION_JSON
            response.statusCode = HttpStatus.UNAUTHORIZED
            val dataBuffer = encodeProtobufValue(generateAuthenticationError(exception), bufferFactory)
            response.writeWith(Mono.just(dataBuffer))
        }

        http.exceptionHandling()
            .accessDeniedHandler { serverWebExchange, exception ->
                val response = serverWebExchange.response
                val bufferFactory = response.bufferFactory()
                response.headers.contentType = MediaType.APPLICATION_JSON
                response.statusCode = HttpStatus.FORBIDDEN
                val dataBuffer = encodeProtobufValue(generateAuthenticationError(exception), bufferFactory)
                response.writeWith(Mono.just(dataBuffer))
            }

        // Disable authentication for `/public/**`, `/login/**`, and `/` routes.
        http.authorizeExchange().pathMatchers("/public/**", "login/**", "/").permitAll()
        http.authorizeExchange().anyExchange().authenticated()

        http
            .securityContextRepository(securityContextRepository)
            .oauth2Login {
                it.authenticationManager(securityContextRepository.authenticationManager)
            }

        return http.build()
    }
}
