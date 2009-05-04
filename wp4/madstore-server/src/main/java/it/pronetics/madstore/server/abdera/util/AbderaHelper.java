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
package it.pronetics.madstore.server.abdera.util;

import it.pronetics.madstore.common.dom.DomHelper;
import java.io.InputStream;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.parser.Parser;
import org.w3c.dom.Element;

/**
 * Helper class for converting DOM-based Atom elements into Abdera-based Atom objects.
 *
 * @author Sergio Bossa
 */
public class AbderaHelper {

    public static Collection getCollectionFromDom(Element domCollection) throws Exception {
        InputStream domStream = DomHelper.getStreamFromDomElement(domCollection);
        try {
            Parser parser = Abdera.getInstance().getParser();
            Document<Collection> documentCollection = parser.<Collection>parse(domStream);
            return documentCollection.getRoot();
        } finally {
            if (domStream != null) {
                domStream.close();
            }
        }
    }
    
    public static Entry getEntryFromDom(Element domEntry) throws Exception {
        InputStream domStream = DomHelper.getStreamFromDomElement(domEntry);
        try {
            Parser parser = Abdera.getInstance().getParser();
            Document<Entry> documentEntry = parser.<Entry>parse(domStream);
            return documentEntry.getRoot();
        } finally {
            if (domStream != null) {
                domStream.close();
            }
        }
    }
}
