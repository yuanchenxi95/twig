package com.yuanchenxi95.twig.controllers

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController {

    @GetMapping("/login")
    fun viewLoginPage(): String? {
        var authentication: Authentication = SecurityContextHolder.getContext().authentication
        if (authentication.isAuthenticated) {
            return "login"
        }
        return "please login"
    }
}
