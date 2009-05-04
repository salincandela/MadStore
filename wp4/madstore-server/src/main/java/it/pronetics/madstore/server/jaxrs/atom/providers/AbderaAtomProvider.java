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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.writer.Writer;

/**
 * JAX-RS provider for writing and reading Atom feeds and entries.
 *
 * @author Sergio Bossa
 */
@Provider
@Consumes(AtomConstants.ATOM_MEDIA_TYPE)
@Produces(AtomConstants.ATOM_MEDIA_TYPE)
public class AbderaAtomProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {

    private final Abdera abdera = Abdera.getInstance();

    public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (Feed.class.isAssignableFrom(type) || Entry.class.isAssignableFrom(type));
    }

    public void writeTo(Object feedOrEntry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream outputStream) throws IOException, WebApplicationException {
        if (feedOrEntry instanceof Feed || feedOrEntry instanceof Entry) {
            Writer writer = abdera.getWriter();
            Base base = (Base) feedOrEntry;
            writer.writeTo(base, outputStream);
        } else {
            throw new IOException("Unexpected object: " + feedOrEntry);
        }
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (Feed.class.isAssignableFrom(type) || Entry.class.isAssignableFrom(type));
    }

    public Object readFrom(Class<Object> feedOrEntry, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream inputStream) throws IOException, WebApplicationException {
        Parser parser = abdera.getParser();
        if (Feed.class.isAssignableFrom(feedOrEntry)) {
            Document<Feed> doc = parser.parse(inputStream);
            return doc.getRoot();
        } else if (Entry.class.isAssignableFrom(feedOrEntry)) {
            Document<Entry> doc = parser.parse(inputStream);
            return doc.getRoot();
        } else {
            throw new IOException("Unexpected payload: " + feedOrEntry);
        }
    }
}
