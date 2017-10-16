package org.lappsgrid.services.api.web

import org.anc.json.compiler.SchemaCompiler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
@RestController
class JsonCompiler {

    @PostMapping(path='/json-compiler', consumes = 'text/plain', produces = 'application/json')
    String compile(@RequestBody String input) {
        SchemaCompiler compiler = new SchemaCompiler()
        compiler.prettyPrint = true
        return compiler.compile(input)
    }
}
