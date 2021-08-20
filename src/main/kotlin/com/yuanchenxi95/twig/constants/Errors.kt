package com.yuanchenxi95.twig.constants

import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.protobuf.protobuf.api.twigApiError

fun generateInternalServerError(exception: Throwable, showInternalError: Boolean): TwigApiError {
    return twigApiError {
        code = 500
        errorType = TwigApiError.ErrorType.INTERNAL_SERVER_ERROR
        message = if (showInternalError) exception.message!! else "Internal Server Error"
    }
}

fun generateOperationFailedError(exception: Throwable): TwigApiError {
    return twigApiError {
        code = 400
        errorType = TwigApiError.ErrorType.OPERATION_FAILED_ERROR
        message = exception.message!!
    }
}

fun generateBadRequestError(exception: Throwable): TwigApiError {
    return twigApiError {
        code = 400
        errorType = TwigApiError.ErrorType.BAD_REQUEST
        message = exception.cause?.message ?: exception.message!!
    }
}

fun generateNotImplementedError(exception: Throwable): TwigApiError {
    return twigApiError {
        code = 404
        errorType = TwigApiError.ErrorType.NOT_IMPLEMENTED_ERROR
        message = exception.cause?.message ?: exception.message!!
    }
}

fun generateAuthenticationError(exception: Throwable): TwigApiError {
    return twigApiError {
        code = 401
        errorType = TwigApiError.ErrorType.AUTHENTICATION_ERROR
        message = exception.cause?.message ?: exception.message!!
    }
}
