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
package it.pronetics.madstore.repository.index;

import javax.xml.xpath.XPathExpression;

/**
 * Describe information about the path of an Atom entry property to index, more specifically:
 * <ul>
 * <li>The name of the property.</li>
 * <li>The xPath expression to use for accessing the property value on the Atom entry XML.</li>
 * <li>The boost value, defining the relevance of the property in the index.</li>
 * </ul>
 *
 * @author Salvatore Incandela
 */
public class PropertyPath {

    private String name;
    private XPathExpression xPathExpression;
    private int boost;

    public PropertyPath(String name, XPathExpression xPath, int boost) {
        this.name = name;
        this.xPathExpression = xPath;
        this.boost = boost;
    }

    public String getName() {
        return name;
    }

    public XPathExpression getXPathExpression() {
        return xPathExpression;
    }

    public int getBoost() {
        return boost;
    }
}
