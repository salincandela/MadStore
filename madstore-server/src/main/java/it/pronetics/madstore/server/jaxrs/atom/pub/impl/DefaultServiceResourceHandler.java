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
import it.pronetics.madstore.server.jaxrs.atom.pub.ServiceResourceHandler;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceName;
import it.pronetics.madstore.server.jaxrs.atom.resolver.ResourceUriFor;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link it.pronetics.madstore.server.jaxrs.atom.pub.ServiceResourceHandler} implementation based on
 * JBoss Resteasy and Abdera atom model.
 *
 * @author Sergio Bossa
 */
@Path("/")
public class DefaultServiceResourceHandler extends AbstractResourceHandler implements ServiceResourceHandler<Service> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServiceResourceHandler.class);
    private String workspaceTitle;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    @ResourceUriFor(resource = ResourceName.SERVICE)
    @GET
    @Path("/service")
    @Produces(AtomConstants.ATOM_SERVICE_MEDIA_TYPE)
    public Service getServiceResource() {
        try {
            Factory abderaFactory = Abdera.getInstance().getFactory();
            Service service = abderaFactory.newService();
            Workspace workspace = abderaFactory.newWorkspace();
            List<Collection> collections = readCollectionsFromRepository();
            for (Collection collection : collections) {
                workspace.addCollection(collection);
            }
            workspace.setTitle(workspaceTitle);
            service.addWorkspace(workspace);
            return service;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new WebApplicationException(Response.serverError().build());
        }
    }

    public void setWorkspaceTitle(String workspaceTitle) {
        this.workspaceTitle = workspaceTitle;
    }
}
