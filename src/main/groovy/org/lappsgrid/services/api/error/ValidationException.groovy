package org.lappsgrid.services.api.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Encountered an error validating the input.")
class ValidationException extends RuntimeException{
    /* Intentionally left empty. */
}
