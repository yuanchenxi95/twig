package com.yuanchenxi95.twig.framework.securities

import com.yuanchenxi95.twig.producermodules.users.LoginUserProducerModule
import com.yuanchenxi95.twig.producermodules.users.ValidateSessionProducerModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.ResponseCookie
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2LoginReactiveAuthenticationManager
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class TwigSecurityContextRepository : ServerSecurityContextRepository {

    @Autowired
    private lateinit var loginUserProducerModule: LoginUserProducerModule

    @Autowired
    private lateinit var validateSessionProducerModule: ValidateSessionProducerModule

    val authenticationManager = OAuth2LoginReactiveAuthenticationManager(
        WebClientReactiveAuthorizationCodeTokenResponseClient(),
        DefaultReactiveOAuth2UserService()
    )

    override fun save(
        serverWebExchange: ServerWebExchange,
        securityContext: SecurityContext
    ): Mono<Void> {
        return loginUserProducerModule.Executor(securityContext).execute()
            .map {
                val responseCookieBuilder = ResponseCookie.from(AUTHORIZATION, it.id)
                responseCookieBuilder
                    .httpOnly(true)
                    .path("/")
                    .sameSite("Strict")
                    // TODO(yuanchenxi95), check the request is HTTP or HTTPS.
                    .secure(false)
                    // TODO(yuanchenxi95) Take in the expiration time from the config file.
                    .maxAge(Duration.ofDays(30))
                serverWebExchange.response.addCookie(responseCookieBuilder.build())
            }.then()
    }

    override fun load(serverWebExchange: ServerWebExchange): Mono<SecurityContext> {
        val authorizationCookies = serverWebExchange.request.cookies[AUTHORIZATION]

        if (authorizationCookies == null || authorizationCookies.size == 0) {
            return Mono.empty()
        }

        return validateSessionProducerModule.Executor(authorizationCookies[0].value).execute()
            .map {
                val twigAuthenticationToken = TwigAuthenticationToken(it)
                SecurityContextImpl(twigAuthenticationToken)
            }
    }
}
