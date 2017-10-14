package org.lappsgrid.services.api.web

import groovy.xml.MarkupBuilder
import org.lappsgrid.client.ServiceClient
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.services.api.error.ApiError
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.lappsgrid.serialization.Data
import org.springframework.web.bind.annotation.RestController

/**
 * @author Keith Suderman
 */
@RestController
@RequestMapping("/metadata")
class Metadata {

    String brandeis = 'http://eldrad.cs-i.brandeis.edu:8080/service_manager'
    String vassar = 'http://vassar.lappsgrid.org'

    @GetMapping(produces='text/html')
    String getHtml(@RequestParam("id") String id) {
        if (id == null) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Missing ID parameter.")
        }

        String json = get(id)
        StringWriter writer = new StringWriter()

        String url = "$brandeis/invoker/$id"
        if (id.startsWith('anc:')) {
            url = "$vassar/invoker/$id"
        }

        Data data = Serializer.parse(json, Data)
        def binding = new Binding()
        binding.url = url
        binding.html = new MarkupBuilder(new PrintWriter(writer))
        binding.payload = data.payload
        String license = data.payload.license
        if (license.startsWith('http')) {
            binding.license = "<p><a href='$license'>$license</a></p>"
        }
        else {
            binding.license = markdown(license)
        }
        InputStream stream = this.class.getResourceAsStream("/templates/metadata.gsp")
        def script = new GroovyShell(binding).parse(stream.text)
        script.run()
        return writer.toString()
    }

    @GetMapping(produces = ['application/x-cmdi+xml', 'text/xml'])
    String getXml(@RequestParam("id") String id) {
        if (id == null) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Missing ID parameter.")
        }
        String url = "$brandeis/invoker/$id"
        if (id.startsWith('anc:')) {
            url = "$vassar/invoker/$id"
        }

        String json = get(id)
        StringWriter writer = new StringWriter()
        Data data = Serializer.parse(json, Data)
        def binding = new Binding()
        binding.url = url
        binding.xml = new MarkupBuilder(new PrintWriter(writer))
        binding.md = data.payload
        binding.id = id

        InputStream stream = this.class.getResourceAsStream("/templates/cmdi.gsp")
        def script = new GroovyShell(binding).parse(stream.text)
        script.run()

        return writer.toString()
    }

    @GetMapping(produces = "application/json")
    String getJson(@RequestParam("id") String id) {
        if (id == null) {
            throw new ApiError(HttpStatus.BAD_REQUEST, "Missing ID parameter.")
        }
        return get(id)
    }


    String get(String id) {
        // Derive the service URL from the ID.
        String url = "$brandeis/invoker/$id"
        if (id.startsWith('anc:')) {
            url = "$vassar/invoker/$id"
        }

        def client = new ServiceClient(url, 'tester', 'tester')
        String json = client.getMetadata();

        Data data = Serializer.parse(json, Data)
        if (data.discriminator == Discriminators.Uri.ERROR) {
            throw new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, data.payload)
        }
        return json
    }

    String markdown(String input) {
        File file = File.createTempFile('pandoc', '.md')
        file.text = input
        String html = "pandoc -f rst -t html ${file.path}".execute().text
        if (!file.delete()) {
            file.deleteOnExit()
        }
        return html
    }
}
