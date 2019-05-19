package org.lappsgrid.services.api.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to load the JSON schema.")
class NoSchemaException extends RuntimeException {
    /* Intentionally left empty. */
}
