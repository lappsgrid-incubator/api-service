package org.lappsgrid.services.api.error

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 *
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unable to load server configuration.")
class ConfigurationException {
    /* Intentionally left empty. */
}
