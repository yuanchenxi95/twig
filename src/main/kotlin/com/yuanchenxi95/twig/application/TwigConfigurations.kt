package com.yuanchenxi95.twig.application

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "twig")
data class TwigConfigurations(
    val enableDatabaseSetup: Boolean,
    val showInternalServerError: Boolean,
    val frontendDistDirectory: String,
)
