package org.lappsgrid.services.api.error

import org.springframework.http.HttpStatus

/**
 * @author Keith Suderman
 */
class ApiError extends Throwable {
    int status
    String message
    Throwable exception

    ApiError() {
        super()
    }

    ApiError(HttpStatus status, String message) {
        this(status.value(), message)
    }

    ApiError(int status, String message) {
        super()
        this.status = status
        this.message = message
    }

    ApiError(HttpStatus status, String message, Throwable t) {
        this(status.value(), message, t)
    }

    ApiError(int status, String message, Throwable exception) {
        this.status = status
        this.message = message
        this.exception = exception
    }

    int getStatus() {
        return status
    }

    void setStatus(int status) {
        this.status = status
    }

    String getMessage() {
        return message
    }

    void setMessage(String message) {
        this.message = message
    }
}
