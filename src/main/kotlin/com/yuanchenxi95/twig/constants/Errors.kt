package com.yuanchenxi95.twig.constants

import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError

fun generateInternalServerError(exception: Throwable, showInternalError: Boolean): TwigApiError {
    return TwigApiError.newBuilder()
        .setCode(500)
        .setErrorType(TwigApiError.ErrorType.INTERNAL_SERVER_ERROR)
        .setMessage(if (showInternalError) exception.message else "Internal Server Error")
        .build()
}

fun generateBadRequestError(exception: Throwable): TwigApiError {
    return TwigApiError.newBuilder()
        .setCode(400)
        .setErrorType(TwigApiError.ErrorType.BAD_REQUEST)
        .setMessage(exception.cause?.message ?: exception.message)
        .build()
}

fun generateNotImplementedError(exception: Throwable): TwigApiError {
    return TwigApiError.newBuilder()
        .setCode(404)
        .setErrorType(TwigApiError.ErrorType.NOT_IMPLEMENTED_ERROR)
        .setMessage(exception.cause?.message ?: exception.message)
        .build()
}

fun generateAuthenticationError(exception: Throwable): TwigApiError {
    return TwigApiError.newBuilder()
        .setCode(403)
        .setErrorType(TwigApiError.ErrorType.AUTHENTICATION_ERROR)
        .setMessage(exception.message)
        .build()
}
