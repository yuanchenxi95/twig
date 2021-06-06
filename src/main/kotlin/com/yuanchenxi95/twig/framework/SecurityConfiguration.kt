package com.yuanchenxi95.twig.framework

import com.yuanchenxi95.twig.constants.generateAuthenticationError
import com.yuanchenxi95.twig.framework.codecs.encodeProtobufValue
import com.yuanchenxi95.twig.framework.utils.UuidUtils
import com.yuanchenxi95.twig.models.StoredSession
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

// TODO(jiang12358), Implements AuthenticationToken.
class TwigAuthenticationToken(
    private val storedSession: StoredSession? = null,
    authorities: Collection<out GrantedAuthority> = listOf()
) : AbstractAuthenticationToken(authorities) {

    init {
        super.setAuthenticated(storedSession != null)
    }

    override fun getCredentials(): Any {
        return storedSession as Any
    }

    override fun getPrincipal(): Any {
        TODO("Not yet implemented")
    }
}

@Component
class TwigSecurityContextRepository : ServerSecurityContextRepository {

    @Autowired
    private lateinit var uuidUtils: UuidUtils

    val authenticationManager = OAuth2LoginReactiveAuthenticationManager(
        WebClientReactiveAuthorizationCodeTokenResponseClient(),
        DefaultReactiveOAuth2UserService()
    )

    override fun save(serverWebExchange: ServerWebExchange, securityContext: SecurityContext): Mono<Void> {
        /** TODO(jiang12358), Implement load the user session from [com.yuanchenxi95.twig.models.StoredSession] */
        val responseCookieBuilder = ResponseCookie.from(AUTHORIZATION, uuidUtils.generateUUID())
        responseCookieBuilder.httpOnly(true)
            .sameSite("Strict")
            // TODO(yuanchenxi95), check the request is HTTP or HTTPS.
            .secure(false)
            .maxAge(Duration.ofDays(30))
        serverWebExchange.response.addCookie(responseCookieBuilder.build())
        return Mono.empty()
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        /** TODO(jiang12358), Implement load the user session from [com.yuanchenxi95.twig.models.StoredSession] */
        val twigAuthenticationToken = TwigAuthenticationToken()
        val securityContextImpl = SecurityContextImpl(twigAuthenticationToken)
        return Mono.just(securityContextImpl)
    }
}

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
