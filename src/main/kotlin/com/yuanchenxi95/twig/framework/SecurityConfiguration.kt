package com.yuanchenxi95.twig.framework

import com.yuanchenxi95.twig.constants.generateAuthenticationError
import com.yuanchenxi95.twig.framework.codecs.encodeProtobufValue
import com.yuanchenxi95.twig.models.StoredUserPOC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono.just

@Service
class StoredUserService : DefaultOAuth2UserService() {
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest)
        return StoredUserPOC(user)
    }
}

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration {

    @Autowired
    private val oauthUserService: StoredUserService? = null

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // Disable default security.
        http.httpBasic().disable()
            .formLogin().disable()
            .csrf().disable()
            .logout().disable()

//        http.exceptionHandling().authenticationEntryPoint { serverWebExchange, exception ->
//            val response = serverWebExchange.response
//            val bufferFactory = response.bufferFactory()
//            response.headers.contentType = MediaType.APPLICATION_JSON
//            response.statusCode = HttpStatus.UNAUTHORIZED
//            val dataBuffer = encodeProtobufValue(generateAuthenticationError(exception), bufferFactory)
//            response.writeWith(just(dataBuffer))
//        }

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
        http.authorizeExchange().pathMatchers("/", "/login").permitAll()
        http.authorizeExchange().anyExchange().authenticated().and().oauth2Login()
        return http.build()
    }
}
