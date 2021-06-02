package com.yuanchenxi95.twig.framework.validation

class ValidationError(message: String) : RuntimeException(message)

fun validationAssert(value: Boolean, message: String) {
    validationAssert(value) { message }
}

inline fun validationAssert(value: Boolean, lazyMessage: () -> String) {
    if (!value) {
        val message = lazyMessage()
        throw ValidationError(message)
    }
}
