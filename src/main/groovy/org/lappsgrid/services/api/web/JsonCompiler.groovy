package org.lappsgrid.services.api.web

import groovy.util.logging.Slf4j
import org.anc.json.compiler.SchemaCompiler
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.services.api.util.HTML
import org.springframework.web.bind.annotation.GetMapping
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
        String result
        try {
            result = compiler.compile(input)
        }
        catch (Throwable t) {
            result = Serializer.toPrettyJson([status:'error', message:t.message])
        }
        return result
    }

    @GetMapping(path='/json-compiler', produces = 'text/html')
    String get() {
        HTML.render('main', 'JSON Compiler') {
            h1 "LAPPS Grid JSON Compiler v${org.anc.json.compiler.Version.version}"
            p '''Compiles the LAPPS Alternate Sytax (LAX) into JSON and is intended to help simplify writing
JSON files, in particular JSON Schema files, by hand.'''
            p {
                mkp.yieldUnescaped '''See the <a href="https://github.com/oanc/org.anc.json.schema-compiler">GitHub repository</a>
for more information on the alternate systax.'''
            }
            h2 'Example'
            pre '''$> curl -H 'Content-type: text/plain' -d @input.txt https://api.lappsgrid.org/json-compiler'''
        }

    }
}
