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
package it.pronetics.madstore.repository.spring;

import it.pronetics.madstore.common.configuration.spring.AbstractMadStoreConfigurationFactoryBean;
import it.pronetics.madstore.common.configuration.spring.MadStoreConfigurationBean;
import it.pronetics.madstore.repository.index.PropertyPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

/**
 * @author Salvatore Incandela
 */
public class IndexedPropertiesFactoryBean extends AbstractMadStoreConfigurationFactoryBean {

    public Object getObject() throws Exception {
        MadStoreConfigurationBean madstoreConfiguration = getMadStoreConfiguration();
        List<PropertyPath> propertyPaths = new ArrayList<PropertyPath>();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        xPath.setNamespaceContext(new CustomNamespaceContext(madstoreConfiguration.getIndexConfiguration().getIndexedPropertiesNamespaces()));
        for (MadStoreConfigurationBean.IndexConfiguration.Property indexedProperty : madstoreConfiguration.getIndexConfiguration().getIndexedProperties()) {
            PropertyPath propertyPath = new PropertyPath(indexedProperty.getName(), xPath.compile(indexedProperty.getXPath()), indexedProperty.getBoost());
            propertyPaths.add(propertyPath);
        }
        return propertyPaths;
    }

    public Class getObjectType() {
        return List.class;
    }

    public boolean isSingleton() {
        return true;
    }

    private static class CustomNamespaceContext implements NamespaceContext {

        private Map<String, String> namespaces = new HashMap<String, String>();

        public CustomNamespaceContext(Map<String, String> namespaces) {
            this.namespaces = namespaces;
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new NullPointerException("Null prefix");
            }
            return namespaces.get(prefix);
        }

        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        public Iterator getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }
}
