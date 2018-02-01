package org.lappsgrid.services.api.util

import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder

/**
 *
 */
@CompileStatic
class HTML {
    static String render(String layout, String title, Closure content)  {
        Map data = [:]
        data.title = title
        data.content = content
        return render(layout, data)
    }

    static String render(String layout, Map data) {
        InputStream stream = HTML.getResourceAsStream("/templates/${layout}.layout");
        if (stream == null) {
            return "Unable to load the $layout template"
        }

        StringWriter writer = new StringWriter()
        MarkupBuilder html = new MarkupBuilder(writer)

        Binding binding = new Binding()
        binding.setVariable('html', html)
        binding.setVariable('data', data)

        GroovyShell shell = new GroovyShell(binding)
        Script script = shell.parse(new InputStreamReader(stream))
        script.run()
        return writer.toString()
    }
}
