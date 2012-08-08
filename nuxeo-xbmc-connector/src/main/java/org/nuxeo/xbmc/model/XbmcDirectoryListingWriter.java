package org.nuxeo.xbmc.model;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.JsonGenerator;
import org.nuxeo.ecm.automation.server.jaxrs.io.JsonWriter;

@Provider
@Produces("text/json")
public class XbmcDirectoryListingWriter implements
        MessageBodyWriter<XbmcDirectoryListing> {

    @Override
    public long getSize(XbmcDirectoryListing arg0, Class<?> arg1, Type arg2,
            Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> arg0, Type arg1, Annotation[] arg2,
            MediaType arg3) {
        return XbmcDirectoryListing.class == arg0;
    }

    @Override
    public void writeTo(XbmcDirectoryListing items, Class<?> arg1, Type arg2,
            Annotation[] arg3, MediaType arg4,
            MultivaluedMap<String, Object> arg5, OutputStream out)
            throws IOException, WebApplicationException {

        JsonGenerator jg = JsonWriter.createGenerator(out);
        jg.useDefaultPrettyPrinter();
        jg.writeStartArray();
        for (XbmcDirectoryItem item : items) {
            jg.writeObject(item);
        }
        jg.writeEndArray();
        jg.flush();
        jg.close();
    }

}
