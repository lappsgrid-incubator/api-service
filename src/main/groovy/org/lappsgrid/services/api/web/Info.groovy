package org.lappsgrid.services.api.web

import org.lappsgrid.services.api.util.HTML
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
@RestController
class Info {

    @GetMapping("/")
    String root() {
        return info()
    }

    @GetMapping("/info")
    String info() {
        return HTML.render('main', 'LAPPS Grid') {
            h1 'LAPPS Grid HTTP API'
            p 'The following endpoints are available:'
            table {
                thead {
                    th 'URL'
                    th 'Method'
                    th 'Produces'
                    th 'Description'
                }
                tbody {
                    tr {
                        td '/'
                        td 'GET'
                        td 'text/html'
                        td 'this page'
                    }
                    tr {
                        td { a href:'/services/brandeis', '/services/brandeis' }
                        td 'GET'
                        td 'text/html, application/json'
                        td 'lists the SOAP services available on the Bradeis node'
                    }
                    tr {
                        td { a href:'/services/vassar', '/services/vassar' }
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
                        td { a href:'/password', '/password' }
                        td 'GET'
                        td 'text/plain'
                        td 'Generates a cryptographically secure sequence of random characters'
                    }
                    tr {
                        td { a href:'/uuid', '/uuid' }
                        td 'GET'
                        td 'text/plain'
                        td 'Generates a type 4 UUID.  This just calls the UUID.randomUUID() Java method.'
                    }
                }
            }
        }
    }

}
