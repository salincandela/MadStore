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
package it.pronetics.madstore.server.jaxrs.atom.search;

import it.pronetics.madstore.server.jaxrs.atom.ResourceHandler;
import javax.ws.rs.core.Response;

/**
 * Resource handler for the Open Search description document.
 *
 * @param <R> Type of the response object representing the Open Search description document.
 *
 * @author Sergio Bossa
 */
public interface SearchDescriptionResourceHandler<R> extends ResourceHandler {

    /**
     * Get the resource object representing the Open Search description document.
     *
     * @return The web response containing the Open Search description document.
     */
    public Response getSearchDescription();

    /**
     * Set the Open Search description document short name.
     * @param shortName
     */
    public void setShortName(String shortName);

    /**
     * Set the Open Search description document description.
     * @param description
     */
    public void setDescription(String description);
}
