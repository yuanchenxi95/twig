package com.yuanchenxi95.twig

import com.yuanchenxi95.twig.application.TwigConfigurations
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        /**
         *  Remove the default error handler, handles errors using
         *  [com.yuanchenxi95.twig.framework.errorhandlers.GlobalErrorHandler].
         */
        ErrorWebFluxAutoConfiguration::class,
    ]
)
@EnableConfigurationProperties(TwigConfigurations::class)
class TwigApplication

fun main(args: Array<String>) {
    runApplication<TwigApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
        setBannerMode(Banner.Mode.CONSOLE)
    }
}
