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
package it.pronetics.madstore.crawler.model;

import java.io.Serializable;

/**
 * Model object representing a web link.
 *
 * @author Salvatore Incandela
 * @author Christian Mongillo
 * @author Sergio Bossa
 */
public class Link implements Serializable {

    private String link;

    public Link(String link) {
        if (link == null || link.equals("")) {
            throw new IllegalArgumentException("Link can't be null or empty.");
        }
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public boolean startsWith(String prefix) {
        return link.startsWith(prefix);
    }

    @Override
    public String toString() {
        return link;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Link) {
            Link other = (Link) obj;
            return this.link.equals(other.link);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return link.hashCode();
    }
}
