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
package it.pronetics.madstore.server.jaxrs.atom.resolver;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolve the resource uri for resource classes with logical names annotated through {@link ResourceUriFor}.
 * 
 * @author Sergio Bossa
 */
public class ResourceResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolver.class);
    private final Map<String, Class> resourceClasses;

    public ResourceResolver(Map<String, Class> resourceClasses) {
        this.resourceClasses = resourceClasses;
    }

    public URL resolveResourceUriFor(String resourceName, String uriBase, Object... uriParameters) {
        Class resourceClass = resourceClasses.get(resourceName);
        if (resourceClass != null) {
            List<Method> resourceUriMethodsStack = new LinkedList<Method>();
            Class resourceEntryPoint = resourceClass;
            Method resourceUriMethod = pushAnnotatedResourceMethod(resourceClass, resourceName, resourceUriMethodsStack);
            ResourceUriFor resourceUriAnnotation = resourceUriMethod.getAnnotation(ResourceUriFor.class);
            if (!resourceUriAnnotation.locator().equals(Void.class)) {
                String locatedResource = resourceName;
                Class locatorResource = resourceUriAnnotation.locator();
                do {
                    resourceUriMethod = pushAnnotatedLocatorMethod(locatorResource, locatedResource, resourceUriMethodsStack);
                    resourceUriAnnotation = resourceUriMethod.getAnnotation(ResourceUriFor.class);
                    resourceEntryPoint = locatorResource;
                    locatedResource = resourceUriAnnotation.resource();
                    locatorResource = resourceUriAnnotation.locator();
                } while (!locatorResource.equals(Void.class));
            }
            URL resourceUrl = buildResourceUrl(uriBase, resourceEntryPoint, resourceUriMethodsStack, uriParameters);
            return resourceUrl;
        } else {
            throw new IllegalStateException("No resource class with name " + resourceName);
        }
    }

    public String resolveResourceIdFor(String uriBase, String resourceName, String... resourceParameters) {
        String tagPrefix = "tag";
        String strippedUriBase = uriBase.replaceFirst("\\w+\\://", "");
        if (strippedUriBase.indexOf("/") != -1) {
            strippedUriBase = strippedUriBase.substring(0, strippedUriBase.indexOf("/"));
        }
        if (strippedUriBase.indexOf(":") != -1) {
            strippedUriBase = strippedUriBase.substring(0, strippedUriBase.indexOf(":"));
        }
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(tagPrefix).append(":").append(strippedUriBase).append(":").append(resourceName);
        if (resourceParameters.length > 0) {
            for (String parameter : resourceParameters) {
                idBuilder.append(":").append(parameter);
            }
        }
        return idBuilder.toString();
    }

    private Method pushAnnotatedResourceMethod(Class resourceClass, String resourceName, List<Method> resourceUriMethodsStack) {
        Method[] methods = resourceClass.getMethods();
        Method annotatedMethod = null;
        for (Method method : methods) {
            ResourceUriFor annotation = method.getAnnotation(ResourceUriFor.class);
            if (annotation != null && annotation.resource().equals(resourceName)) {
                annotatedMethod = method;
                break;
            }
        }
        if (annotatedMethod != null) {
            resourceUriMethodsStack.add(annotatedMethod);
            return annotatedMethod;
        } else {
            throw new IllegalStateException(
                    "No method with " + ResourceUriFor.class + " annotation found for class " + resourceClass + " and resource " + resourceName);
        }
    }

    private Method pushAnnotatedLocatorMethod(Class locatorResource, String locatedResource, List<Method> resourceUriMethodsStack) {
        Method[] methods = locatorResource.getMethods();
        Method annotatedMethod = null;
        for (Method method : methods) {
            ResourceUriFor annotation = method.getAnnotation(ResourceUriFor.class);
            if (annotation != null && Arrays.asList(annotation.subResources()).contains(locatedResource)) {
                annotatedMethod = method;
                break;
            }
        }
        if (annotatedMethod != null) {
            resourceUriMethodsStack.add(annotatedMethod);
            return annotatedMethod;
        } else {
            throw new IllegalStateException(
                    "No method with " + ResourceUriFor.class + " annotation found for class " + locatorResource + " and resource " + locatedResource);
        }
    }

    private URL buildResourceUrl(String baseUrl, Class resourceEntryPoint, List<Method> resourceUrlMethodsStack, Object[] urlParameters) {
        try {
            URL resourceUrl = null;
            UriBuilder builder = UriBuilder.fromUri(baseUrl).path(resourceEntryPoint);
            for (int i = resourceUrlMethodsStack.size() - 1; i >= 0; i--) {
                Method resourceUrlMethod = resourceUrlMethodsStack.get(i);
                builder.path(resourceUrlMethod);
            }
            resourceUrl = builder.build(urlParameters).toURL();
            return resourceUrl;
        } catch (MalformedURLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new WebApplicationException();
        }
    }
}
