package com.yuanchenxi95.twig.controllers

import com.yuanchenxi95.twig.framework.securities.TwigAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping("/public/users/me")
    fun viewUserInfo(authentication: TwigAuthenticationToken?): String {
        if (authentication?.isAuthenticated == true) {
            return "login"
        }
        return "not_login"
    }
}
