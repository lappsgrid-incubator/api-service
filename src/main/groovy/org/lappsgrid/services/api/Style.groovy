package org.lappsgrid.services.api

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * @author Keith Suderman
 */
//@RestController
class Style {

//    @RequestMapping(path = "/style/main.css", method = RequestMethod.GET)
    String style() {
        InputStream stream = this.class.getResourceAsStream('/style/main.css')
        if (stream == null) {
            println "Stylesheet not found."
            return 'not found'
        }
        return stream.text
    }
}
