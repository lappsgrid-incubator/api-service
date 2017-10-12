package org.lappsgrid.services.api.util

import groovy.xml.MarkupBuilder

/**
 * @author Keith Suderman
 */
class HTML {
    static String render(String layout, String title, Closure content) {
        InputStream stream = HTML.getResourceAsStream("/templates/${layout}.template");
        if (stream == null) {
            return "Unable to load the $layout template"
        }

        StringWriter writer = new StringWriter()
        MarkupBuilder html = new MarkupBuilder(writer)

        Binding binding = new Binding()
        binding.setVariable('html', html)
        binding.setVariable('title', title)
        binding.setVariable('content', content)

        GroovyShell shell = new GroovyShell(binding)
        Script script = shell.parse(new InputStreamReader(stream))
        script.run()
        return writer.toString()
    }
}
