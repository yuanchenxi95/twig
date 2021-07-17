package com.yuanchenxi95.twig.producermodules.users

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import com.yuanchenxi95.twig.models.StoredUser
import com.yuanchenxi95.twig.producermodules.ProducerModule
import com.yuanchenxi95.twig.protobuf.api.GetUserInformationResponse
import com.yuanchenxi95.twig.utils.databaseutils.selectOneById
import com.yuanchenxi95.twig.utils.protobufutils.convertInstantToTimestamp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetUserInformationProducerModule {

    @Autowired
    lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    inner class Executor(
        private val authentication: TwigAuthenticationToken
    ) : ProducerModule.ProducerModuleExecutor<GetUserInformationResponse> {
        override fun execute(): Mono<GetUserInformationResponse> {
            return selectOneById<StoredUser>(authentication.getUserId(), r2dbcEntityTemplate)
                .map {
                    val expirationTime = convertInstantToTimestamp(authentication.getExpirationTime())
                    GetUserInformationResponse.newBuilder()
                        .setId(it.id)
                        .setEmail(it.userEmail)
                        .setName(it.name)
                        .setExpirationTime(expirationTime)
                        .build()
                }
        }
    }
}
