package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.constants.RequestMappingValues
import com.yuanchenxi95.twig.utils.httputils.redirectTo
import com.yuanchenxi95.twig.validators.validateUriScheme
import com.yuanchenxi95.twig.validators.validateUrl
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import java.net.URI

@RestController
class RedirectionController {

    @GetMapping(RequestMappingValues.REDIRECTION_URL)
    fun redirectTo(@RequestParam(value = "to", required = true) redirectTo: String, serverWebExchange: ServerWebExchange) {
        validateUrl(redirectTo)
        val uri = URI.create(redirectTo)
        println(uri)
        validateUriScheme(uri)
        redirectTo(serverWebExchange, uri)
    }
}
