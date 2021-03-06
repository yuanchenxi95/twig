package com.yuanchenxi95.twig.framework.errorhandlers

import com.yuanchenxi95.protobuf.protobuf.api.TwigApiError
import com.yuanchenxi95.twig.constants.generateAuthenticationError
import com.yuanchenxi95.twig.constants.generateBadRequestError
import com.yuanchenxi95.twig.constants.generateNotImplementedError
import com.yuanchenxi95.twig.constants.generateOperationFailedError
import com.yuanchenxi95.twig.exceptions.AuthFailedException
import com.yuanchenxi95.twig.exceptions.OperationFailedException
import com.yuanchenxi95.twig.exceptions.ResourceNotFoundException
import com.yuanchenxi95.twig.framework.validation.ValidationError
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
        // TODO(yuanchenxi95), Logs the bad request error as a normal message.
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body(
                generateBadRequestError(exception)
            )
    }

    @ExceptionHandler(value = [ResourceNotFoundException::class])
    fun resourceNotFoundExceptionHandler(exception: ResourceNotFoundException): ResponseEntity<TwigApiError> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body(
                generateBadRequestError(exception)
            )
    }

    @ExceptionHandler(value = [OperationFailedException::class])
    fun operationFailedExceptionHandler(exception: OperationFailedException): ResponseEntity<TwigApiError> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body(
                generateOperationFailedError(exception)
            )
    }

    @ExceptionHandler(value = [AuthFailedException::class])
    fun authFailedHandler(exception: Exception): ResponseEntity<TwigApiError> {
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .contentType(APPLICATION_JSON)
            .body(
                generateAuthenticationError(exception)
            )
    }

    @ExceptionHandler(value = [ValidationError::class])
    fun validationErrorHandler(validationError: ValidationError): ResponseEntity<TwigApiError> {
        // TODO(yuanchenxi95), Logs the validation error as a normal message.
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(APPLICATION_JSON)
            .body(
                generateBadRequestError(validationError)
            )
    }

    @ExceptionHandler(value = [NotImplementedError::class])
    fun notImplementationErrorHandler(notImplementedError: NotImplementedError): ResponseEntity<TwigApiError> {
        // TODO(yuanchenxi95), Logs the validation error as a normal message.
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(APPLICATION_JSON)
            .body(
                generateNotImplementedError(notImplementedError)
            )
    }
}
