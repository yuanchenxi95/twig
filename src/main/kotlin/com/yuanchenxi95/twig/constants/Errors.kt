package com.yuanchenxi95.twig.constants

import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError


val DEFAULT_TWIG_INTERNAL_ERROR: TwigApiError = TwigApiError.newBuilder()
    .setCode(500)
    .setErrorType(TwigApiError.ErrorType.INTERNAL_SERVER_ERROR)
    .setMessage("Internal Server Error")
    .build()

fun generateBadRequestError(exception: Exception): TwigApiError {
    return TwigApiError.newBuilder()
        .setCode(400)
        .setErrorType(TwigApiError.ErrorType.BAD_REQUEST)
        .setMessage(exception.cause?.message ?: exception.message)
        .build()
}

fun generateAuthenticationError(exception: Exception): TwigApiError {
    return TwigApiError.newBuilder()
        .setCode(403)
        .setErrorType(TwigApiError.ErrorType.AUTHENTICATION_ERROR)
        .setMessage(exception.message)
        .build()
}
