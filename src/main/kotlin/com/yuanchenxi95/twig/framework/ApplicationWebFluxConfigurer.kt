package com.yuanchenxi95.twig.framework

import com.yuanchenxi95.twig.framework.codecs.ProtobufJsonDecoder
import com.yuanchenxi95.twig.framework.codecs.ProtobufJsonEncoder
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@EnableWebFlux
@Configuration
@ComponentScan
class ApplicationWebFluxConfigurer : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        // Registers codecs for converting data between JSON and Protobuf .
        configurer.customCodecs().register(ProtobufJsonEncoder())
        configurer.customCodecs().register(ProtobufJsonDecoder())
    }
}
