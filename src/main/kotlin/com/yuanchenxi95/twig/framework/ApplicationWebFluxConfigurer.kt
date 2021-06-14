package com.yuanchenxi95.twig.framework

import com.yuanchenxi95.twig.framework.codecs.ProtobufJsonDecoder
import com.yuanchenxi95.twig.framework.codecs.ProtobufJsonEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ViewResolverRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.thymeleaf.spring5.ISpringWebFluxTemplateEngine
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ITemplateResolver

@EnableWebFlux
@Configuration
@ComponentScan
class ApplicationWebFluxConfigurer : WebFluxConfigurer {

    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        // Registers codecs for converting data between JSON and Protobuf .
        configurer.customCodecs().register(ProtobufJsonEncoder())
        configurer.customCodecs().register(ProtobufJsonDecoder())
    }

    @Bean
    fun thymeleafTemplateResolver(): ITemplateResolver {
        val resolver = SpringResourceTemplateResolver()
        resolver.prefix = "classpath:templates/"
        resolver.suffix = ".html"
        resolver.templateMode = TemplateMode.HTML
        resolver.isCacheable = false
        resolver.checkExistence = false
        return resolver
    }

    @Bean
    fun thymeleafTemplateEngine(): ISpringWebFluxTemplateEngine {
        val templateEngine = SpringWebFluxTemplateEngine()
        templateEngine.setTemplateResolver(thymeleafTemplateResolver())
        return templateEngine
    }

    @Bean
    fun thymeleafReactiveViewResolver(): ThymeleafReactiveViewResolver {
        val viewResolver = ThymeleafReactiveViewResolver()
        viewResolver.templateEngine = thymeleafTemplateEngine()
        return viewResolver
    }

    override fun configureViewResolvers(registry: ViewResolverRegistry) {
        registry.viewResolver(thymeleafReactiveViewResolver())
    }
}
