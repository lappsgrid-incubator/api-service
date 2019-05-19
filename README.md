# LAPPS Grid API Services

The services available at `https://api.lappsgrid.org` are typically proof-of-concept prototype services implemented as [Groovlets](http://docs.groovy-lang.org/latest/html/documentation/servlet-userguide.html).  A Groovlet is simply a Groovy script that is run inside a Java Servlet container.  Currently calls to `https://api.lappsgrid.org` will forward to a Jetty server (see server.groovy) that serves these scripts as web services.

**Note** The Groovlets repository has a GitHub webhook enabled so pushing code to the *master* branch automatically deploys these services to api.lappsgrid.org. 

### Service Index
- [password](#password) - generates random strings for use as passwords or secret keys
- [uuid](#uuid) - generates a type 4 UUID
- [services](#services) - list all services installed on a particular node
- [metadata](#metadata) - fetch the metadata for a given service
- [soap-proxy](#soap-proxy) - RESTful proxy for LAPPS SOAP services
- [json-compiler](#json-compiler) - compiles the LAPPS Alternate Syntax into JSON
- [json-validator](#validator) - validates JSON documents agains the LAPPS JSON Schema
- [version](#version) - returns the version defined in the project's pom.xml file

# Available Services

<a name="password"></a>

## https://api.lappsgrid.org/password

The password service uses a cryptographically secure random number generator to produce a random sequence of characters.  Use the password service any time a secure password and/or security key is required.

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>GET</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/password?type=:type&length=:length&chars=:string</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            text/plain
        </td>
     </tr>
</table>

**URL Parameters**

- **type** one of *default*, *safe*, or *hex*
- **chars** the set of characters used to generate the password
- **length** the number of characters to produce.

If *chars* is specified then *type* is ignored.  Returns *400 Bad Request* if neither of *type* or *chars* is specified, or if *type* is not one of *default*, *safe*, or *hex*.

**Types**

- **default** the set of most printable ASCII characters (minus quotes).
- **safe** letters (upper and lower case), digits, and the characters _-=,.<br/>
The *safe* type is intended to be used to generate passwords or keys that can be safely included in scripts.
- **hex** the hexadecimal digits 0123456789abcde

**Examples**

```
> curl https://api.lappsgrid.org/password
=F9sXKGn2lteDdvk

> curl https://api.lappsgrid.org/password?type=hex&length=32
a0edc2709c4ebb9ee43f35415c12af5b

> curl https://api.lappsgrid.org/password?chars=01&length=32
00001001101101111101000000001001
```
The password service will always produce at least 16 characters of output.

<a name="uuid"></a>

## https://api.lappsgrid.org/uuid

Generates a *Type 4* UUID (Universally Unique IDentifier) according to [RFC 4122](https://www.ietf.org/rfc/rfc4122.txt). In practice the service simply calls `java.util.UUID.randomUUID().toString()` 

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>GET</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/uuid<br/></td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            text/plain
        </td>
     </tr>
</table>

**Example**

```bash
> curl https://api.lappsgrid.org/uuid

d085f907-0c00-4dd6-b500-5f98cbc0827f
```

<a name="services"></a>
  
## https://api.lappsgrid.org/services

Display the services installed on a service manager instance.

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>GET</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/services/:node?:key=:value [&:key=value...]</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json, text/html
        </td>
     </tr>
</table>

**Path Parameters**

- **node** One of `vassar` or `brandeis`

**URL Parameters**

- **key** a Service Manager search key used to filter services
- **value** the value to be matched. The value matches if it is a substring of the key's value. Text matches are case-insensitive.

Valid search keys are:

* active
* endpointUrl
* instanceType
* ownerUserId
* registeredDate
* serviceDescription
* serviceId
* serviceName
* serviceType
* serviceTypeDomain
* updatedDate

If no key/value pairs are specified as search terms then all services registered on the Service Manager instance will be listed.

**Example**

```
curl -H 'Accept: text/html' https://api.lappsgrid.org/services/brandeis
curl https://api.lappsgrid.org/services/vassar?serviceName=gate
```

<a name="metadata"></a>
  
## https://api.lappsgrid.org/metadata

Display metadata about a single service.

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>GET</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/metadata?id=:id</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json, text/html, application/x-cmdi+xml
        </td>
     </tr>
</table>

If an `Accept` header is not specified `application/json` will be returned.

**URL Parameters**

- **id** the ID, including gridId, of the service to get metadata from.

**Example**

```
curl https://api.lappsgrid.org/metadata?id=anc:gate.tokenizer_2.2.0
curl -H 'Accept: application/x-cmdi+xml' https://api.lappsgrid.org/metadata?id=anc:stanford.tagger_2.0.0
```

<a name="soap-proxy"></a>
  
## https://api.lappsgrid.org/soap-proxy

A RESTful proxy service for LAPPS Grid SOAP services.
  
<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>POST</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/soap-proxy</td>
     </tr>
     <tr>
        <td><b>Accepts</b></td>
        <td>text/plain</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json
        </td>
     </tr>
</table>

**URL Parameters**

- **id** the ID of the service to invoke.

**Example**

```
> curl -i -H 'Content-Type: text/plain' -X POST -d 'Karen flew to New York.' https://api.lappsgrid.org/soap-proxy?id=anc:wrap.lif_1.0.0

HTTP/1.1 200 
Server: nginx/1.4.6 (Ubuntu)
Date: Mon, 16 Oct 2017 16:59:50 GMT
Content-Type: text/plain;charset=UTF-8
Content-Length: 241
Connection: keep-alive

{
    "discriminator": "http://vocab.lappsgrid.org/ns/media/jsonld#lif",
    "payload": {
        "@context": "http://vocab.lappsgrid.org/context-1.0.0.jsonld",
        "metadata": {
            
        },
        "text": {
            "@value": "Karen flew to New York.",
            "@language": "en"
        },
        "views": []
    },
    "parameters": {}
}
```

<a name="json-compiler"></a>
  
## https://api.lappsgrid.org/json-compiler

Compiles the [LAPPS Alternative Syntax](https://github.com/oanc/org.anc.json.schema-compiler)
into an equivalent JSON document.

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>POST</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/json-compiler</td>
     </tr>
     <tr>
        <td><b>Accepts</b></td>
        <td>text/plain</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json
        </td>
     </tr>
</table>

**Example**

```
> curl -i -X POST -H "Content-type: text/plain" --data "type object; properties { name string }" https://api.lappsgrid.org/json-compiler

HTTP/1.1 200 OK
Server: nginx/1.4.6 (Ubuntu)
Date: Sun, 18 Jun 2017 18:30:18 GMT
Content-Type: application/json; charset=utf-8
Transfer-Encoding: chunked
Connection: keep-alive

{
    "type": "object",
    "properties": {
        "name": "string"
    }
}
```

<a name="validator"></a>

## https://api.lappsgrid.org/validate

Validates JSON documents against the JSON schema for [LIF documents](http://vocab.lappsgrid.org/schema/lif-schema.json) or the schema for [service metadata](http://vocab.lappsgrid.org/schema/service-schema.json).

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>POST</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/validate/data<br/>
        /validate/container<br/>
        /validate/metadata
        </td>
     </tr>
     <tr>
        <td><b>Accepts</b></td>
        <td>application/ld+json</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json
        </td>
     </tr>
</table>

**Example**

```
> curl -H 'Content-type: application/ld+json' -d @input.lif https://api.lappsgrid.org/validate/data
{
    "level": "ok",
    "message": "No problems found."
}
```

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>POST</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/validate/schema</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json
        </td>
     </tr>
</table>

**Examples**

```
> curl -H "Content-type: text/plain" --data-binary @lif.schema http://api.lappsgrid.org/validate/schema
```

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>GET</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/validate/metadata?id=:id <br/>
        /validate/metadata?url=:url</td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            application/json
        </td>
     </tr>
</table>

**URL Parameters**

- **id** the ID of the service to be validated.
- **url** the URL of a LAPPS service

When sending a GET request include either the service ID **or** the URL of a LAPPS service. It is an error to include both the *id* and *url* URL parameters.
                                                                                            

**Example**

```
> curl https://api.lappsgrid.org/validate/metadata?id=anc:stanford.tokenizer_2.1.0
{
    "level": "ok",
    "message": "Metadata passed validation."
}

> curl https://api.lappsgrid.org/validate/metadata?url=http://vassar.lappsgrid.org/invoker/anc:stanford.tokenizer_2.1.0
{
    "level": "ok",
    "message": "Metadata passed validation."
}
```

<a name="version"></a>

## https://api.lappsgrid.org/version

Return the version string defined in the pom.xml file. 

<table>
    <tr>
        <td style="width:20%"><b>Methods</b></td>
        <td>GET</td>
     </tr>
     <tr>
        <td><b>URL</b></td>
        <td>/version<br/></td>
     </tr>
     <tr>
        <td><b>Returns</b></td>
        <td>
            text/plain
        </td>
     </tr>
</table>

**Example**

```bash
> curl https://api.lappsgrid.org/version

1.0.1
```

