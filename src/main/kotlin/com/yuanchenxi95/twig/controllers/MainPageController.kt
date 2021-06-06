package com.yuanchenxi95.twig.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class MainPageController {
    @RequestMapping("/")
    fun mainPage(): String? {
        return "main_page"
    }
}
