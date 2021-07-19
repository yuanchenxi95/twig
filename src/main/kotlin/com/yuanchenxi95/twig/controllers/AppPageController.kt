package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.application.TwigConfigurations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AppPageController {

    @Autowired
    lateinit var twigConfigurations: TwigConfigurations

    @GetMapping("/app")
    fun appServing(): Resource {
        val frontendDistDirectory = twigConfigurations.frontendDistDirectory

        return FileSystemResource("${frontendDistDirectory}index.html")
    }
}
