package org.lappsgrid.services.api.web

import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.core.util.AsJson
import groovy.json.JsonOutput
import groovy.util.logging.Slf4j
import org.anc.json.validator.Validator
import org.lappsgrid.api.WebService
import org.lappsgrid.client.ServiceClient
import org.lappsgrid.discriminator.Discriminators
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.services.api.error.NoSchemaException
import org.lappsgrid.services.api.error.ValidationException
import org.lappsgrid.services.api.util.HTML
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 *
 */
@Slf4j('logger')
@RestController
class JsonValidator {

    Validator instanceValidator
    Validator metadataValidator

    public JsonValidator() {
        instanceValidator = getValidator('/schemas/lif-schema.json')
        metadataValidator = getValidator('/schemas/metadata-schema.json')
    }

    @PostMapping(path='/validate/container', consumes = 'application/json', produces = 'application/json')
    String validateContainer(@RequestBody String json) {
        if (instanceValidator == null) {
            throw new NoSchemaException()
        }
        logger.info("Validating container.")
        ProcessingReport report

        try {
            report = instanceValidator.validate(json)
        }
        catch (Exception e) {
            logger.info("Validation failed")
            throw new ValidationException()
        }
        logger.info("Return JSON report.")
        String result = ((AsJson) report).asJson().toString()
        return JsonOutput.prettyPrint(result)
    }

    @PostMapping(path='/validate/data', consumes = 'application/json', produces = 'application/json')
    String validateData(@RequestBody String json) {
        logger.info("Validating a Data object.")
        Data data = Serializer.parse(json)
        if (data.discriminator != Discriminators.Uri.LAPPS && data.discriminator != Discriminators.Uri.LIF) {
            logger.warn("Wrong discriminator type: {}", data.discriminator)
            return error('Invalid discriminator.')
        }
        return validateContainer(Serializer.toJson(data.payload))
    }

    @GetMapping(path='/validate/metadata')
    ResponseEntity<String> validateMetadataGet(@RequestParam(name='id',required = false) String id, @RequestParam(name='url', required = false) String url) {
        if (id == null && url == null) {
            String html = info()
            HttpHeaders headers = new HttpHeaders()
            headers.setContentType(MediaType.TEXT_HTML)
            return new ResponseEntity<String>(html, headers, HttpStatus.OK)
        }
        if (id != null && url != null) {
            return new ResponseEntity<String>("Only one of the ID or URL should be specified, not both", HttpStatus.BAD_REQUEST)
        }
        if (id != null) {
            if (id.startsWith('anc:')) {
                url = "http://vassar.lappsgrid.org/invoker/$id"
            }
            else {
                url = "http://eldrad.cs-i.brandeis.edu:8080/service_manager/invoker/$id"
            }
        }
        WebService service = new ServiceClient(url, 'tester', 'tester')
        String metadata = service.getMetadata()
        if (metadata.contains(Discriminators.Uri.ERROR)) {
            return new ResponseEntity<String>('Unable to retrieve metadata from service', HttpStatus.BAD_REQUEST)
        }
        String response = validateMetadata(metadata)
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        return new ResponseEntity<String>(response, headers, HttpStatus.OK)
    }

    @PostMapping(path='/validate/metadata', consumes = 'application/json', produces = 'application/json')
    String validateMetadata(@RequestBody String body) {
        if (metadataValidator == null) {
            throw new NoSchemaException()
        }
        logger.info("Validating metadata.")
        Data data = Serializer.parse(body)
        if (data.discriminator != Discriminators.Uri.META) {
            logger.warn("Wrong discriminator type: {}", data.discriminator)
            return error("Invalid discriminator: ${data.discriminator}")
        }

        ProcessingReport report
        try {
            report = metadataValidator.validate(Serializer.toJson(data.payload))
        }
        catch (Exception e) {
            logger.info("Validation failed")
            throw new ValidationException()
        }
        logger.info("Return JSON report.")
        String result
        if (report.success) {
            Map map = [
                    level: 'ok',
                    message: 'Metadata passed validation.'
            ]
            result = Serializer.toJson(map)
        }
        else {
            result = ((AsJson) report).asJson().toString()
        }
        return JsonOutput.prettyPrint(result)
    }

    @GetMapping(path = "/validate")
    String validate() {
        return info()
    }

    @GetMapping('/validate/container')
    String validateContainer() {
        return info()
    }

    @GetMapping('/validate/data')
    String validateData() {
        return info()
    }

    String info() {
        return HTML.render("main", "JSON Schema Validation") {
            h1 "LAPPS Grid Validation Services"
            p "Use these services to validate the JSON serialization of LAPPS Grid objects against their JSON Schema."
            table {
                tr {
                    th 'URL'
                    th 'Method'
                    th 'Description'
                }
                tr {
                    td '/validate/container'
                    td 'POST'
                    td 'validates a LIF Container object'
                }
                tr {
                    td '/validate/data'
                    td 'POST'
                    td 'validates a LIF Data object'
                }
                tr {
                    td '/validate/metadata'
                    td 'POST'
                    td 'metadata object is POSTed to the service'
                }
                tr {
                    td '/validate/metadata?[id|url]'
                    td 'GET'
                    td 'metadata to be validated is retrieved directly from the service'
                }
            }
            h3 'Implementation'
            p {
                mkp.yieldUnescaped '''The JSON validation is done using the <a href='https://github.com/java-json-tools/json-schema-validator'>json-schema-validator</a> 
project available on <a href='https://github.com/'>GitHub</a>. Both services return the JSON 
serialization of the <em>ProcessingReport</em> object returned by the validator.
'''
            }
            p {
                mkp.yieldUnescaped '''Only <a href='http://json-schema.org/specification-links.html#draft-4'>Draft-4</a>
 of the JSON Schema Specification is supported at this time.'''
            }
            h3 'Examples'
            p {
                pre """curl -H 'Content-type: application/json' -d @input.lif https://api.lappsgrid.org/validate/data
curl https://api.lappsgrid.org/validate/metadata?id=anc:stanford.tokenizer_2.1.0"""
            }
        }
    }

    String error(String message) {
        Map error = [
                level: 'error',
                message: message
        ]
        return Serializer.toPrettyJson([ error ])
    }

    Validator getValidator(String schemaName) {
        URL url = this.class.getResource(schemaName)
        if (url == null) {
            return null
        }
        return new Validator(url.text)
    }
}
