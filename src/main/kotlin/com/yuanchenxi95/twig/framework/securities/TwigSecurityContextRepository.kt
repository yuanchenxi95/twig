package com.yuanchenxi95.twig.framework.securities

import com.yuanchenxi95.twig.producermodules.users.LoginUserProducerModule
import com.yuanchenxi95.twig.producermodules.users.ValidateSessionProducerModule
import com.yuanchenxi95.twig.utils.httputils.generateResponseCookie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
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
                serverWebExchange.response.addCookie(
                    generateResponseCookie(
                        AUTHORIZATION, it.id, Duration.ofDays(30)
                    )
                )
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
