package org.lappsgrid.services.api.web

import groovy.util.logging.Slf4j
import org.lappsgrid.client.ServiceClient
import org.lappsgrid.serialization.Data
import org.lappsgrid.services.api.error.ApiError
import org.lappsgrid.services.api.util.HTML
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

import static org.lappsgrid.discriminator.Discriminators.*

/**
 *
 */
@Slf4j('logger')
@RestController
class SoapProxy {
    @PostMapping(path="/soap-proxy", consumes = ['application/json', 'application/ld+json'], produces = 'application/ld+json')
    String postJson(@RequestParam(name="id", required = true) String id, @RequestBody String body) {
        return proxy(id, body)
    }

    @PostMapping(path="/soap-proxy", consumes = 'text/plain', produces = 'application/ld+json')
    String postText(@RequestParam(name="id", required = true) String id, @RequestBody String body) {
//        Data data = new Data(Uri.TEXT, body)
//        return proxy(id, data.asJson())
        return proxy(id, body)
    }

    @PostMapping(path="/soap-proxy", consumes = ['text/xml'], produces = 'application/ld+json')
    String postXml(@RequestParam(name="id", required = true) String id, @RequestBody String xml) {
        Data data = new Data(Uri.XML, xml)
        return proxy(id, data.asJson())
    }

    @PostMapping(path="/soap-proxy", consumes = ['text/tcf+xml'], produces = 'application/ld+json')
    String postTcf(@RequestParam(name="id", required = true) String id, @RequestBody String body) {
        Data data = new Data(Uri.TCF, body)
        return proxy(id, data.asJson())
    }

    String proxy(String id, String body) {
        String url = getUrl(id)
        logger.info "Proxying ${url}"

        String username = 'tester'
        String password = 'tester'
        ServiceClient client = new ServiceClient(url, username, password)
        String json
        try {
            json = client.execute(body)
        }
        catch(Throwable t) {
            logger.error("Error invoking the service {}", url, t)
            throw new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, 'Error executing the client.')
        }
        return json
    }

    String getUrl(String id) {
        String url = null
        if (id == 'localhost') {
            logger.debug("id is localhost")
            url = 'http://localhost:8000/tcf%2Dconverter/1%2E0%2E0/services/converter'
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
        return url
    }

    @GetMapping('/soap-proxy')
    String get() {
        HTML.render('main', 'Soap Proxy') {
            h1 "LAPPS Grid Soap Proxy"
            p "The soap-proxy service provides a REST-like HTTP interface to the LAPPS Grid SOAP services."
            p {
                mkp.yieldUnescaped """The <em>soap-proxy</em> service accepts a single url parameter <em>id</em>
                    which is the service ID of the service to be invoked. To see a list of all available
                    services and their service IDs visit 
                    <a href='https://api.lappsgrid.org/services/brandeis'>https://api.lappsgrid.org/services/brandeis</a>
                    or <a href='href:'https://api.lappsgrid.org/services/vassar'>href:'https://api.lappsgrid.org/services/vassar</a>.
"""
            }
            strong 'Example'
            br()
            p 'https://api.lappsgrid.org/soap-proxy?id=anc:stanford.tokenier_2.1.0'
        }
    }

    String link(ref) {
        return
    }

}
