package org.lappsgrid.services.api.web

import org.lappsgrid.services.api.util.HTML
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * @author Keith Suderman
 */
@RestController
class Info {

    @RequestMapping(path="/")
    String root() {
        return info()
    }

    @RequestMapping(path="/info", method = RequestMethod.GET)
    String info() {
        return HTML.render('layout', 'LAPPS Grid') {
            h1 'LAPPS Grid HTML API'
            p '''We refer to the following endpoints as a HTML API as opposed to
            a REST API as not all of the services are true REST.  For example, the
            soap-proxy accepts POST requests, but no resource is created on the server.
            Similarly there is no way to do content negotiation with the soap-proxy as the
            output type will depend on the SOAP service that is called.'''
            p 'The following endpoints are available:'
            table {
                thead {
                    td 'URL'
                    td 'Method'
                    td 'Produces'
                    td 'Description'
                }
                tbody {
                    tr {
                        td '/'
                        td 'GET'
                        td 'text/html'
                        td 'this page'
                    }
                    tr {
                        td '/services/brandeis'
                        td 'GET'
                        td 'text/html, application/json'
                        td 'lists the SOAP services available on the Bradeis node'
                    }
                    tr {
                        td '/services/vassar'
                        td 'GET'
                        td 'text/html, application/json'
                        td 'lists the SOAP services available on the Vassar node.'
                    }
                    tr {
                        td '/metadata?id=<service ID>'
                        td 'GET'
                        td 'text/html, application/json, application/x-cmdi+xml'
                        td 'displays the metadata for the service.'
                    }
                    tr {
                        td '/soap-proxy?id=<service ID>'
                        td 'POST'
                        td '*/*'
                        td 'A REST proxy for LAPPS SOAP services'
                    }
                    tr {
                        td '/json-compiler'
                        td 'POST'
                        td 'application/json'
                        td "A thin wrapper around Groovy's JsonBuilder that generates JSON from a Groovy DSL"
                    }
                    tr {
                        td '/password'
                        td 'GET'
                        td 'text/plain'
                        td 'Generates a cryptographically secure sequence of random characters'
                    }
                    tr {
                        td '/uuid'
                        td 'GET'
                        td 'text/plain'
                        td 'Generates a type 4 UUID.  This just calls the UUID.randomUUID() Java method.'
                    }
                }
            }
        }
    }

}
