package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.producermodules.users.GetUserInformationProducerModule
import com.yuanchenxi95.twig.protobuf.api.GetUserInformationResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController {

    @Autowired
    private lateinit var getUserInformationProducerModule: GetUserInformationProducerModule

    @GetMapping(RequestMappingValues.GET_USER_INFORMATION)
    fun viewUserInfo(authentication: TwigAuthenticationToken): Mono<GetUserInformationResponse> {
        return getUserInformationProducerModule.Executor(authentication).execute()
    }
}
