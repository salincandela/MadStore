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
 * Model object representing a web page.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 * @author Christian Mongillo
 */
public class Page implements Serializable {

    private Link link;
    private String data;

    public Page(Link link) {
        this.link = link;
        this.data = "";
    }

    public Page(Link link, String data) {
        this.link = link;
        this.data = data;
    }

    public Link getLink() {
        return link;
    }

    public String getData() {
        return data;
    }

    public boolean isEmpty() {
        return data.equals("");
    }

    @Override
    public String toString() {
        return link.toString() + " : " + data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Page) {
            Page other = (Page) obj;
            return this.link.equals(other.link) && this.data.equals(other.data);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // TODO: hash code implementation too trivial!
        return link.hashCode() + data.hashCode();
    }
}
