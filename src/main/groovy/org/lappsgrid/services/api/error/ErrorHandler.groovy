package org.lappsgrid.services.api.error

import groovy.transform.CompileStatic
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 *
 */
@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(ApiError)
    ResponseEntity<ApiError> handleApiError(ApiError error) {
        return new ResponseEntity<ApiError>(error, error.status)
    }

    @ExceptionHandler(Throwable)
    ResponseEntity<Throwable> handleThrowable(Throwable t) {
        return new ResponseEntity<Throwable>(t, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
