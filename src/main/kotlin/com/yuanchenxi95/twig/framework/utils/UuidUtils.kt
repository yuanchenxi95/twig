package com.yuanchenxi95.twig.framework.utils

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UuidUtils {
    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}
