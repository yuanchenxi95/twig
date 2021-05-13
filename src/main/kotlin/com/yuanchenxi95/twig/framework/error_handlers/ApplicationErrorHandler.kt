package com.yuanchenxi95.twig.framework.error_handlers

import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.constants.DEFAULT_TWIG_INTERNAL_ERROR
import com.yuanchenxi95.twig.constants.generateBadRequestError
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.UnsupportedMediaTypeStatusException

@RestControllerAdvice
class ApplicationErrorHandler {

    @ExceptionHandler(value = [ServerWebInputException::class, UnsupportedMediaTypeStatusException::class])
    fun badRequestHandler(exception: Exception): ResponseEntity<TwigApiError> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body(
                generateBadRequestError(exception)
            )
    }

    @ExceptionHandler(value = [Exception::class])
    fun unknownExceptionHandler(exception: Exception): ResponseEntity<TwigApiError> {
        println(exception)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(APPLICATION_JSON)
            .body(
                DEFAULT_TWIG_INTERNAL_ERROR
            )
    }
}
