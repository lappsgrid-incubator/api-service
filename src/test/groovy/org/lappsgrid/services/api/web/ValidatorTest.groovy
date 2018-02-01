package org.lappsgrid.services.api.web

import groovy.json.JsonOutput
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.springframework.http.ResponseEntity

/**
 *
 */
@Ignore
class ValidatorTest {

    JsonValidator validator

    @Before
    void setup() {
        validator = new JsonValidator()
    }

    @After
    void teardown() {
        validator = null
    }

    @Test
    void testStanfordNER() {
        String json = loadStanfordNER()
        Data data = Serializer.parse(json)
        println JsonOutput.prettyPrint(validator.validateData(data.asJson()))
    }

    @Test
    void validateMetadataById() {
        ResponseEntity response = validator.validateMetadataGet('anc:stanford.tokenizer_2.1.0', null)
        assert 200 == response.statusCode.value()
    }

    String loadStanfordNER() {
        InputStream stream = this.class.getResourceAsStream('/stanford-ner.lif')
        return stream.text
    }
}
