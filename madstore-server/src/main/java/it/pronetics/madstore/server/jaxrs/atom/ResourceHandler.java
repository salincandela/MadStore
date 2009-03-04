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
package it.pronetics.madstore.server.jaxrs.atom;

import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver;
import javax.ws.rs.core.UriInfo;

/**
 * Common interface for all JAX-RS classes handling resource access.
 *
 * @author Sergio Bossa
 */
public interface ResourceHandler {

    /**
     * Get the {@link it.pronetics.madstore.repository.CollectionRepository}.
     *
     * @return The {@link it.pronetics.madstore.repository.CollectionRepository}.
     */
    public CollectionRepository getCollectionRepository();

    /**
     * Get the {@link it.pronetics.madstore.repository.EntryRepository}.
     *
     * @return The {@link it.pronetics.madstore.repository.EntryRepository}.
     */
    public EntryRepository getEntryRepository();

    /**
     * Get the {@link it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver}.
     *
     * @return The {@link it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceResolver}.
     */
    public ResourceResolver getResourceResolver();

    /**
     * Get the JAX-RS specific {@link javax.ws.rs.core.UriInfo} object.
     * 
     * @return The {@link javax.ws.rs.core.UriInfo} object.
     */
    public UriInfo getUriInfo();
}
