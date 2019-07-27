package org.lappsgrid.services.api.web

import org.lappsgrid.services.api.Version
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

    @GetMapping("/version")
    String version() {
        return Version.getVersion()
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
                        td '/metadata?id=:ID'
                        td 'GET'
                        td 'text/html, application/json, application/x-cmdi+xml'
                        td 'displays the metadata for the service.'
                    }
                    tr {
                        td { a href:'/docker', '/docker' }
                        td 'GET'
                        td 'text/html'
                        td 'Lists the images available from https://docker.lappsgrid.org'
                    }
                    tr {
                        td '/soap-proxy?id=:ID'
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
                        td '/validate/container'
                        td 'POST'
                        td 'application/json'
                        td "Validates the JSON for a LIF Container object."
                    }
                    tr {
                        td '/validate/data'
                        td 'POST'
                        td 'application/json'
                        td "Validates LAPPS Data object with a LIF Container payload."
                    }
                    tr {
                        td '/validate/metadata'
                        td 'POST | GET'
                        td 'application/json'
                        td 'Validates the metadata returned by a LAPPS service.'
                    }
                    tr {
                        td { a href:'/version', '/version'}
                        td 'GET'
                        td 'text/plain'
                        td "Returns the version string as defined in the project's pom.xml file."
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
                    tr {
                        td { a href:'/inception', '/inception' }
                        td 'POST | GET'
                        td '[201 CREATED] if the file was uploaded.'
                        td 'Service used by INCEpTION to send files to LAPPS/Galaxy'
                    }
                }
            }
            p {
                span 'See the '
                a href:'https://github.com/lappsgrid-incubator/api-service', 'GitHub repository'
                span ' for more detailed usage instructions.'
            }
        }
    }

}
