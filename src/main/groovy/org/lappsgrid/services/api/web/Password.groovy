package org.lappsgrid.services.api.web

import org.lappsgrid.services.api.error.ApiError
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.security.SecureRandom

/**
 *
 */
@RestController
class Password {
    static SecureRandom random = new SecureRandom()

    static Map charsets = [
            default : "!%^*_+1234567890-=,.:abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            safe : "_1234567890-.+abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            alphanum : "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            alpha : "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
            num : "1234567890",
            hex : "0123456789abcdef"
    ]

    @GetMapping(path="/password", produces = 'text/plain')
    String password(
            @RequestParam(name='type', required=false) String type,
            @RequestParam(name='chars', required = false) String chars,
            @RequestParam(name='length', required = false) Integer length
    ) {
        if (type == null) {
            type = 'default'
        }
        if (chars == null) {
            chars = charsets[type]
            if (chars == null) {
                throw new ApiError()
            }
        }
        if (length == null || length < 16) {
            length = 16
        }
        StringBuilder buffer = new StringBuilder()
        for (int i = 0; i < length; ++i) {
            buffer << chars.charAt(random.nextInt(chars.length()));
        }
        return buffer.toString()
    }

    @GetMapping(path='/uuid', produces = 'text/plain')
    String uuid() {
        return UUID.randomUUID()
    }
}
