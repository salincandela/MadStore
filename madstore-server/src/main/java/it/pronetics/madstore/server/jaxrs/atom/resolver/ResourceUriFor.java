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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate resource methods for defining the resource URI of a logically named (see {@link ResourceName}) resource,
 * and resolve it using {@link ResourceResolver}.
 *
 * @author Sergio Bossa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ResourceUriFor {

    /**
     * The logical name of the identified resource.
     */
    public String resource();

    /**
     * The logical names of eventual sub-resources returned by the identified resource.
     */
    public String[] subResources() default {};

    /**
     * The class of the eventual resource locator for the identified resource.
     */
    public Class locator() default Void.class;
}
