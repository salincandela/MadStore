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
import org.apache.abdera.ext.opensearch.model.OpenSearchDescription;

/**
 * JAX-RS provider for writing and reading Open Search Description documents.
 *
 * @author Sergio Bossa
 */
@Provider
@Consumes(AtomConstants.OPENSEARCH_DESCRIPTION_MEDIA_TYPE)
@Produces(AtomConstants.OPENSEARCH_DESCRIPTION_MEDIA_TYPE)
public class AbderaOpenSearchDescriptionProvider implements MessageBodyReader<OpenSearchDescription>, MessageBodyWriter<OpenSearchDescription> {

    private final Abdera abdera = Abdera.getInstance();

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(OpenSearchDescription.class);
    }

    public OpenSearchDescription readFrom(Class<OpenSearchDescription> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        Parser parser = abdera.getParser();
        Document<OpenSearchDescription> doc = parser.parse(entityStream);
        return doc.getRoot();
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return OpenSearchDescription.class.isAssignableFrom(type);
    }

    public long getSize(OpenSearchDescription description, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(OpenSearchDescription description, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Writer writer = abdera.getWriter();
        writer.writeTo(description, entityStream);
    }
}
