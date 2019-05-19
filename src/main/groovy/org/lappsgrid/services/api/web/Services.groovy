package org.lappsgrid.services.api.web

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.lappsgrid.services.api.error.ApiError
import org.lappsgrid.services.api.util.HTML
import org.lappsgrid.services.api.util.ServiceHandler
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
@Slf4j('logger')
@RestController
class Services {

    ConfigObject configuration;

    public Services() {
//        File file = new File("/etc/lapps/api.ini")
//        if (file.exists()) {
//            configuration = new ConfigSlurper().parse(file.text)
//        }
//        else {
//            logger.error("Unable to locate the configuration file.")
//        }
        if (!loadIniFile(("/etc/lapps/api.ini"))) {
            if (!loadIniFile("/run/secrets/api.ini")) {
                logger.error("Unable to locate a configuration file.")
            }
        }
    }

    private boolean loadIniFile(String path) {
        File file = new File(path)
        if (file.exists()) {
            configuration = new ConfigSlurper().parse(file.text)
            return true
        }
        return false
    }

    @GetMapping(path="/services", produces = 'text/html')
    String services() {
        return HTML.render('main', 'Services') {
            h1 'Service Nodes'
            p 'There are currently two main nodes running LAPPS Grid services.'
            ol {
                li { a(href:'/services/brandeis', 'the Brandeis node') }
                li { a(href:'/services/vassar', 'the Vassar node') }
            }
            p 'Other nodes may be brought online in the future.'
            p {
                span { b 'Note:'}
                span '''There may be LAPPS services running on other servers. The Brandeis 
                and Vassar nodes are simply the servers that are running Service Manager
                instances.'''
            }
        }
    }

    @GetMapping(path="/services/vassar", produces = 'text/html')
    String vassarHtml(@RequestParam(name='id', required = false) String id,
                      @RequestParam(name='name', required = false) String name) throws ApiError {

        logger.debug("Generate HTML for Vassar services.")
        Map data = vassar(id, name)
        data.title = 'Vassar Node'
        data.name = 'Vassar'
        return HTML.render('services', data)
    }

    @GetMapping(path="/services/vassar", produces = 'application/json')
    String vassarJson(@RequestParam(name='id', required=false) String id,
                      @RequestParam(name='name', required = false) String name) throws ApiError
    {
        logger.debug("Generating JSON for Vassar services.")
        Map data = vassar(id, name)
        return new JsonBuilder(data).toString()
    }

    protected Map vassar(String id, String name) throws ApiError {

        if (configuration == null) {
            throw new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "This service has not been properly configured.")
        }

        logger.debug("Querying Vassar server.")
        String username = configuration.vassar.username
        String password = configuration.vassar.password
        String url = 'http://vassar.lappsgrid.org'

        Map params = [:]
        if (id) {
            logger.debug("Filtering by ID {}", id)
            params.id = id
        }
        if (name) {
            logger.debug("Filtering by name {}", name)
            params.name = name
        }
        ServiceHandler handler = new ServiceHandler()
        handler.params = params
        handler.url = url
        handler.username = username
        handler.password = password
        return handler.handle()

    }

    @GetMapping(path="/services/brandeis", produces = 'text/html')
    String brandeisHtml(@RequestParam(name='id', required=false) String id,
                    @RequestParam(name='name', required = false) String name)
            throws ApiError
    {
        logger.debug("Generating HTML for Brandeis services.")
        Map data = brandeis(id, name)
        data.name = 'Brandeis'
        return HTML.render('services', data)
    }

    @GetMapping(path="/services/brandeis", produces = 'application/json')
    String brandeisJson(@RequestParam(name='id', required=false) String id,
                        @RequestParam(name='name', required = false) String name)
            throws ApiError
    {
        logger.debug("Generating JSON for Brandeis services.")
        Map data = brandeis(id, name)
        return new JsonBuilder(data).toString()
    }

    Map brandeis(String id, String name) {
        if (configuration == null) {
            throw new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "This service has not been properly configured.")
        }

        logger.debug("Qeuerying Brandeis server.")
        String username = configuration.brandeis.username
        String password = configuration.brandeis.password
        String url = 'http://eldrad.cs-i.brandeis.edu:8080/service_manager'

        Map params = [:]
        if (id) {
            logger.debug("Filtering by ID {}", id)
            params.id = id
        }
        if (name) {
            logger.debug("Filtering by name {}", name)
            params.name = name
        }
        ServiceHandler handler = new ServiceHandler()
        handler.params = params
        handler.url = url
        handler.username = username
        handler.password = password
        return handler.handle()
    }

}
