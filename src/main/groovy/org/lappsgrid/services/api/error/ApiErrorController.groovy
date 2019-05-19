package org.lappsgrid.services.api.error

import org.lappsgrid.services.api.util.HTML
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
//import org.springframework.boot.autoconfigure.web.ErrorAttributes
//import org.springframework.boot.autoconfigure.web.ErrorController
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 */
@RestController
class ApiErrorController implements ErrorController {

    private boolean debug = true

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = '/error', produces = 'application/json')
    ErrorJson errorJson(HttpServletRequest request, HttpServletResponse response) {
        // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring.
        // Here we just define response body.
        return new ErrorJson(response.getStatus(), getErrorAttributes(request, debug));
    }

    @RequestMapping(value = '/error', produces = 'text/html')
    String errorHtml(HttpServletRequest request, HttpServletResponse response) {
        int status = response.status
        Map<String,Object> attributes = getErrorAttributes(request, true)
        String error = (String) attributes.get("error");
        String message = (String) attributes.get("message");
        String timeStamp = attributes.get("timestamp").toString();
        String trace = (String) attributes.get("trace");
        return HTML.render('main', 'Something is amiss...') {
            h1 error
            p message
            p timeStamp
            pre trace
        }
    }

    @Override
    public String getErrorPath() {
        return '/error';
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }

}

class ErrorJson {

    public Integer status;
    public String error;
    public String message;
    public String timeStamp;
    public String trace;

    public ErrorJson(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.trace = (String) errorAttributes.get("trace");
    }

}
