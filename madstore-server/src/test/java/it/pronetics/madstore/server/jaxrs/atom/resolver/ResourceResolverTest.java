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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import junit.framework.TestCase;

public class ResourceResolverTest extends TestCase {

    public void testResolveResourceIdWithParameters() throws Exception {
        String baseUri = "http://example.com:8080/context";
        String resourceName = "resource";
        String[] resourceParams = {"key1", "key2"};
        ResourceResolver resolver = new ResourceResolver(new HashMap<String, Class>());
        String id = resolver.resolveResourceIdFor(baseUri, resourceName, resourceParams);
        assertEquals("tag:example.com:resource:key1:key2", id);
    }

    public void testResolveResourceIdWithNoParameters() throws Exception {
        String baseUri = "http://example.com:8080/context";
        String resourceName = "resource";
        ResourceResolver resolver = new ResourceResolver(new HashMap<String, Class>());
        String id = resolver.resolveResourceIdFor(baseUri, resourceName);
        assertEquals("tag:example.com:resource", id);
    }

    public void testResolveResourceUriWithSimpleResource() throws Exception {
        String basePath = "http://example.com/";
        Class resourceClass = TestSimpleResource.class;
        String resourceName = "resource";
        Map<String, Class> resourceClasses = new HashMap();
        resourceClasses.put(resourceName, resourceClass);
        ResourceResolver resolver = new ResourceResolver(resourceClasses);
        URL url = resolver.resolveResourceUriFor(resourceName, basePath, "me");
        assertNotNull(url);
        assertEquals("http://example.com/test/me", url.toString());
    }

    public void testResolveResourceUriWithMultipleResources() throws Exception {
        String basePath = "http://example.com/";
        Class resourceClass = TestMultiResource.class;
        String resourceName1 = "resource1";
        String resourceName2 = "resource2";
        Map<String, Class> resourceClasses = new HashMap();
        resourceClasses.put(resourceName1, resourceClass);
        resourceClasses.put(resourceName2, resourceClass);
        ResourceResolver resolver = new ResourceResolver(resourceClasses);
        URL url = resolver.resolveResourceUriFor(resourceName1, basePath, "me");
        assertNotNull(url);
        assertEquals("http://example.com/test1/me", url.toString());
        url = resolver.resolveResourceUriFor(resourceName2, basePath, "me");
        assertNotNull(url);
        assertEquals("http://example.com/test2/me", url.toString());
    }

    public void testResolveResourceUriWithMultipleLocatorsAndSubResource() throws Exception {
        String basePath = "http://example.com/";
        Class resourceClass = TestSubResource.class;
        String resourceName = "resource";
        Map<String, Class> resourceClasses = new HashMap();
        resourceClasses.put(resourceName, resourceClass);
        ResourceResolver resolver = new ResourceResolver(resourceClasses);
        URL url = resolver.resolveResourceUriFor(resourceName, basePath, "me");
        assertNotNull(url);
        assertEquals("http://example.com/locator0/locator1/test/me", url.toString());
    }

    public void testResolveResourceUriWithLocatorAndMultipleSubResources() throws Exception {
        String basePath = "http://example.com/";
        Class resourceClass1 = TestFirstSubResource.class;
        Class resourceClass2 = TestSecondSubResource.class;
        String resourceName1 = "resource1";
        String resourceName2 = "resource2";
        Map<String, Class> resourceClasses = new HashMap();
        resourceClasses.put(resourceName1, resourceClass1);
        resourceClasses.put(resourceName2, resourceClass2);
        ResourceResolver resolver = new ResourceResolver(resourceClasses);
        URL url = resolver.resolveResourceUriFor(resourceName1, basePath, "me");
        assertNotNull(url);
        assertEquals("http://example.com/multiLocator/test1/me", url.toString());
        url = resolver.resolveResourceUriFor(resourceName2, basePath, "me");
        assertNotNull(url);
        assertEquals("http://example.com/multiLocator/test2/me", url.toString());
    }

    public void testResolveResourceUriForLocator() throws Exception {
        String basePath = "http://example.com/";
        Class resourceClass = TestMultipleSubResourcesLocator.class;
        String locatorName = "locator";
        Map<String, Class> resourceClasses = new HashMap();
        resourceClasses.put(locatorName, resourceClass);
        ResourceResolver resolver = new ResourceResolver(resourceClasses);
        URL url = resolver.resolveResourceUriFor(locatorName, basePath);
        assertNotNull(url);
        assertEquals("http://example.com/multiLocator", url.toString());
    }

    @Path("/")
    private static class TestSimpleResource {

        @GET
        @ResourceUriFor(resource = "resource")
        @Path("/test/{param}")
        public Object getResource() {
            return null;
        }
    }

    @Path("/")
    private static class TestMultiResource {

        @GET
        @ResourceUriFor(resource = "resource1")
        @Path("/test1/{param}")
        public Object getResource1() {
            return null;
        }

        @GET
        @ResourceUriFor(resource = "resource2")
        @Path("/test2/{param}")
        public Object getResource2() {
            return null;
        }
    }

    @Path("/")
    private static class TestSubResourceLocator0 {

        @ResourceUriFor(resource = "locator0", subResources = {"locator1"})
        @Path("/locator0")
        public Object getResource() {
            return new TestSubResourceLocator1();
        }
    }

    private static class TestSubResourceLocator1 {

        @ResourceUriFor(resource = "locator1", subResources = {"resource"}, locator = TestSubResourceLocator0.class)
        @Path("/locator1")
        public Object getResource() {
            return new TestSubResource();
        }
    }

    private static class TestSubResource {

        @GET
        @ResourceUriFor(resource = "resource", locator = TestSubResourceLocator1.class)
        @Path("/test/{param}")
        public Object getResource() {
            return null;
        }
    }

    @Path("/")
    private static class TestMultipleSubResourcesLocator {

        @ResourceUriFor(resource = "locator", subResources = {"resource1", "resource2"})
        @Path("/multiLocator")
        public Object getResource() {
            if (System.currentTimeMillis() % 2 == 0) {
                return new TestFirstSubResource();
            } else {
                return new TestSecondSubResource();
            }
        }
    }

    private static class TestFirstSubResource {

        @GET
        @ResourceUriFor(resource = "resource1", locator = TestMultipleSubResourcesLocator.class)
        @Path("/test1/{param}")
        public Object getResource() {
            return null;
        }
    }

    private static class TestSecondSubResource {

        @GET
        @ResourceUriFor(resource = "resource2", locator = TestMultipleSubResourcesLocator.class)
        @Path("/test2/{param}")
        public Object getResource() {
            return null;
        }
    }
}
