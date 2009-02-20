/**
 * Copyright 2008 - 2009 Pro-Netics S.P.A.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.pronetics.madstore.server.jaxrs.atom.providers;

import it.pronetics.madstore.common.AtomConstants;
import org.apache.abdera.model.Document;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.model.Service;
import org.apache.abdera.writer.Writer;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.apache.abdera.Abdera;

/**
 * JAX-RS provider for writing and reading Atom Service documents.
 *
 * @author Sergio Bossa
 */
@Provider
@Consumes(AtomConstants.ATOM_SERVICE_MEDIA_TYPE)
@Produces(AtomConstants.ATOM_SERVICE_MEDIA_TYPE)
public class AbderaServiceDocumentProvider implements MessageBodyReader<Service>, MessageBodyWriter<Service> {

    private final Abdera abdera = Abdera.getInstance();

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(Service.class);
    }

    public Service readFrom(Class<Service> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        Parser parser = abdera.getParser();
        Document<Service> doc = parser.parse(entityStream);
        return doc.getRoot();
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Service.class.isAssignableFrom(type);
    }

    public long getSize(Service service, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(Service service, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Writer writer = abdera.getWriter();
        writer.writeTo(service, entityStream);
    }
}
