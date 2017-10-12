package org.lappsgrid.services.api.error

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

    ApiError(int status, String message) {
        super()
        this.status = status
        this.message = message
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
