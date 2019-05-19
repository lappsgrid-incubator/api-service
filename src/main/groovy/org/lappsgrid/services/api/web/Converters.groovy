package org.lappsgrid.services.api.web

import org.lappsgrid.api.WebService
import org.lappsgrid.client.ServiceClient
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.services.api.error.ConfigurationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ResponseBody

import static org.lappsgrid.discriminator.Discriminators.*
import org.lappsgrid.serialization.Data
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
//@RestController
class Converters {

    WebService converter

    public Converters() {
        URL url = Converters.getResource('/config.groovy')
        if (url) {
            ConfigObject config = new ConfigSlurper().parse(url)
            converter = new ServiceClient(config.converter.url, config.converter.username, config.converter.password)
        }
    }

    @PostMapping(path='/converters/tcf2lif', consumes = ['text/xml','text/tcf+xml'], produces = 'application/json')
    @ResponseBody
    ResponseEntity<String> tcf2lif(@RequestBody String body) {
        if (converter == null) {
            return new ResponseEntity<String>('Unable to load configuration.', HttpStatus.INTERNAL_SERVER_ERROR)
        }
        Data data = new Data(Uri.TCF, body)
        String json = converter.execute(data.asJson())
        return new ResponseEntity<String>(json, HttpStatus.OK)
    }

    @PostMapping(path='/converters/tcf2lif', consumes='application/json', produces='application/json')
    @ResponseBody
    ResponseEntity tcfJson2Lif(@RequestBody String body) {
        if (converter == null) {
            return new ResponseEntity<String>('Unable to load configuration.', HttpStatus.INTERNAL_SERVER_ERROR)
        }
        String json = converter.execute(body)
        return new ResponseEntity<String>(json, HttpStatus.OK)
    }

}
