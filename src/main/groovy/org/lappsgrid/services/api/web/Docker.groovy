package org.lappsgrid.services.api.web

import groovy.util.logging.Slf4j
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.services.api.util.HTML
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
@Slf4j("logger")
@RestController
class Docker {

    final static String API = "https://docker.lappsgrid.org/v2"
    final static String CATALOG = "$API/_catalog"

    String error = null
    @GetMapping(path="/docker", produces = "text/html")
    String list() {
        String json = get(CATALOG)
        if (json == null) {
            return HTML.render("main", "Error") {
                h1 "There was a problem."
                if (error) {
                    p(error)
                }
                p "If the problem persists please contact an administrator."
            }
        }
        Map repos = [:]
        Map data = Serializer.parse(json, HashMap)
        data.repositories.each { String it ->
            String[] parts = it.split("/")
            String org = parts[0]
            String module = parts[1..-1].join('/')
            List modules = repos[org]
            if (modules == null) {
                modules = []
                repos[org] = modules
            }
            modules.add(module)
        }

        return HTML.render("main", "Docker Catalog") {
            h1 'Docker Catalog'
            p 'The following images are available from https://docker.lappsgrid.org'
            repos.each { org, modules ->
                table {
                    tr {
                        th(colspan:2, org)
                    }
                    modules.each { String module ->
                        String tags = getTags(org, module)
                        tr {
                            td(width:'20%', module)
                            td tags
                        }
                    }
                }
                br()
            }
        }
    }

    String get(String url) {
        try {
            return new URL(url).text
        }
        catch (Exception e) {
            logger.error("Unable to retrieve URL $url", e)
            return null
        }
    }

    String getTags(String org, String repo) {
        String json = get("$API/$org/$repo/tags/list")
        if (json == null) {
            return "None"
        }
        Map data = Serializer.parse(json, HashMap)
        return data.tags.join(", ")
    }
}
