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
package it.pronetics.madstore.server.test.servlet;

import it.pronetics.madstore.common.AtomConstants;
import it.pronetics.madstore.common.dom.DomHelper;
import it.pronetics.madstore.repository.CollectionRepository;
import it.pronetics.madstore.repository.EntryRepository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Element;

/**
 * @author Sergio Bossa
 */
public class DataServlet extends HttpServlet {

    private WebApplicationContext ctx;
    private CollectionRepository collectionRepository;
    private EntryRepository entryRepository;
    private String collectionKey;

    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
            Map collectionRepositories = ctx.getBeansOfType(CollectionRepository.class);
            Map entryRepositories = ctx.getBeansOfType(EntryRepository.class);
            if (collectionRepositories.size() != 1 || entryRepositories.size() != 1) {
                throw new IllegalStateException();
            } else {
                collectionRepository = (CollectionRepository) collectionRepositories.values().toArray()[0];
                entryRepository = (EntryRepository) entryRepositories.values().toArray()[0];
            }
            InputStream collectionStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("entriesCollection.xml");
            InputStream entryStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("entry1.xml");
            InputStream entryStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("entry2.xml");
            InputStream entryStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream("entry3.xml");

            byte[] bytes = new byte[collectionStream.available()];
            collectionStream.read(bytes);
            Element collection = DomHelper.getDomFromString(new String(bytes));

            bytes = new byte[entryStream1.available()];
            entryStream1.read(bytes);
            Element entry1 = DomHelper.getDomFromString(new String(bytes));

            bytes = new byte[entryStream2.available()];
            entryStream2.read(bytes);
            Element entry2 = DomHelper.getDomFromString(new String(bytes));

            bytes = new byte[entryStream3.available()];
            entryStream3.read(bytes);
            Element entry3 = DomHelper.getDomFromString(new String(bytes));

            collectionKey = collectionRepository.putIfAbsent(collection);
            entryRepository.put(collectionKey, entry1);
            entryRepository.put(collectionKey, entry2);
            entryRepository.put(collectionKey, entry3);
        } catch (Exception ex) {
            throw new ServletException(ex.getMessage(), ex);
        }
    }

    public void destroy() {
        super.destroy();
        List<Element> elements = entryRepository.readEntries(collectionKey);
        for (Element element : elements) {
            String entryKey = element.getAttribute(AtomConstants.ATOM_KEY);
            entryRepository.delete(collectionKey, entryKey);
        }
        collectionRepository.delete(collectionKey);
    }
}
