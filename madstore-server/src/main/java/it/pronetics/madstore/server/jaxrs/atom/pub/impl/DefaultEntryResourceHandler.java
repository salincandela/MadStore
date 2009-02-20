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
package it.pronetics.madstore.server.jaxrs.atom.pub.impl;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.server.jaxrs.atom.AbstractResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.pub.EntryResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceUriFor;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link it.pronetics.madstore.server.jaxrs.atom.pub.EntryResourceHandler} implementation based on
 * JBoss Resteasy and Abdera atom model.
 *
 * @author Sergio Bossa
 */
@Path("/")
public class DefaultEntryResourceHandler extends AbstractResourceHandler implements EntryResourceHandler<Entry> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEntryResourceHandler.class);
    private String collectionKey;
    private String entryKey;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @ResourceUriFor(resource = ResourceName.ENTRY)
    @GET
    @Path("/{collectionKey}/{entryKey}")
    @Produces(AtomConstants.ATOM_MEDIA_TYPE)
    public Entry getEntryResource() {
        try {
            Entry entry = readEntryFromRepository(collectionKey, entryKey);
            return entry;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new WebApplicationException(Response.serverError().build());
        }
    }

    @PathParam("collectionKey")
    public void setCollectionKey(String collectionKey) {
        this.collectionKey = collectionKey;
    }

    @PathParam("entryKey")
    public void setEntryKey(String entryKey) {
        this.entryKey = entryKey;
    }
}
