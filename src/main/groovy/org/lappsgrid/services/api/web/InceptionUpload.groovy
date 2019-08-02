package org.lappsgrid.services.api.web

import org.lappsgrid.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.Serializer
import org.lappsgrid.serialization.lif.Annotation
import org.lappsgrid.serialization.lif.Container
import org.lappsgrid.serialization.lif.View
import org.lappsgrid.services.api.util.HTML
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletResponse
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import static org.lappsgrid.discriminator.Discriminators.Uri

/**
 *
 */
@RestController
class InceptionUpload {

    InceptionUpload() {
    }

    @GetMapping("/inception")
    String get() {
        return HTML.render("main", "INCEpTION") {
            h1 'INCEpTION Upload Service'
            p "This is the REST endpoint used by INCEpTION to upload files to the LAPPS/Galaxy instance."
            p "Useful help information will be provided shortly."
        }
    }

    @PostMapping(path="/inception", produces = "text/plain")
    String acceptZipFile(@RequestParam MultipartFile file, @RequestParam String user, HttpServletResponse response) {
        PostOffice po = new PostOffice("galaxy.upload.service")

        ByteArrayInputStream input = new ByteArrayInputStream(file.bytes)
        ByteArrayOutputStream output = new ByteArrayOutputStream()

        ZipInputStream zin = new ZipInputStream(input)
        ZipOutputStream zout = new ZipOutputStream(output)

        ZipEntry entry = zin.getNextEntry()
        while (entry != null) {
            if (entry.name.startsWith("annotation/")) {
                File f = new File(entry.name)
                if (f.parentFile) {
                    String id = f.parentFile.name.replace(".json", "")
                    //println "Adding $id to zip file."
                    String filename = "${id}.lif"
                    String username = f.name.replace(".lif", "")
                    String json = read(zin)
                    json = addID(json, id)
                    ZipEntry newEntry = new ZipEntry("$user/$username/$filename")
                    zout.putNextEntry(newEntry)
                    zout.write(json.bytes)
                    zout.closeEntry()
                }
            }
            entry = zin.getNextEntry()
        }
        zout.close()
        po.send("zip", output.toByteArray())
        po.close()
        response.setStatus(HttpStatus.CREATED.value())
        return "CREATED\ns"
    }

    String addID(String json, String id) {
        Container container = Serializer.parse(json, Container)
        if (container.metadata.id == null) {
            container.metadata.id = id
        }
        for (View view : container.views) {
            for (Annotation a : view.annotations) {
                if (a.atType == Uri.TOKEN) {
                    if (a.features.word == null) {
                        a.features.word = text(container, a)
                    }
                }
                if (a.atType == Uri.NE) {
                    if (a.features.category == null && a.label != null) {
                        a.features.category = a.label
                    }
                }
            }
        }
        return new Data(Uri.LIF, container).asJson()
    }

    String read(ZipInputStream zip) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        byte[] buffer = new byte[4096]
        int len = zip.read(buffer)
        while (len > 0) {
            out.write(buffer, 0, len)
            len = zip.read(buffer)
        }
        return new String(out.toByteArray(), "UTF-8")
    }

    String text(Container container, Annotation a) {
        int start = a.start.intValue()
        int end = a.end.intValue()
        return container.text.substring(start, end)
    }
}
