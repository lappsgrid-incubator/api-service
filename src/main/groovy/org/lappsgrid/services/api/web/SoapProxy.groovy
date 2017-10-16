package org.lappsgrid.services.api.web

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.lappsgrid.client.ServiceClient
import org.lappsgrid.services.api.error.ApiError
import org.lappsgrid.services.api.util.HTML
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 *
 */
@Slf4j('logger')
@RestController
class SoapProxy {
    @CompileStatic
    @PostMapping(path="/soap-proxy", consumes = 'text/plain')
    String post(@RequestParam(name="id", required = true) String id, @RequestBody String entity) {

        String url
        if (id == 'localhost') {
            logger.debug("id is localhost")
            url = 'http://localhost:8080/tcf%2Dconverter/1%2E0%2E0/services/converter'
        }
        else if (id.startsWith("anc:")) {
            logger.debug("calling vassar service")
            url = "http://vassar.lappsgrid.org/invoker/${id}"
        }
        else if (id.startsWith('brandeis')){
            logger.debug("calling brandeis service")
            url = "http://eldrad.cs-i.brandeis.edu:8080/service_manager/invoker/${id}"
        }
        else {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Invalid serivce ID.")
        }

        String username = 'tester'
        String password = 'tester'

        logger.info "Proxying ${url}"
        ServiceClient client = new ServiceClient(url, username, password)
        String json
        try {
            json = client.execute(entity)
        }
        catch(Throwable t) {
            logger.error("Error invoking the service", t)
            throw new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, 'Error executing the client.')
        }
        return json
    }

    @GetMapping('/soap-proxy')
    String get() {
        HTML.render('layout', 'Soap Proxy') {
            h1 "LAPPS Grid Soap Proxy"
            p "The soap-proxy service provides a REST-like HTTP interface to the LAPPS Grid SOAP services."
        }
    }

}
