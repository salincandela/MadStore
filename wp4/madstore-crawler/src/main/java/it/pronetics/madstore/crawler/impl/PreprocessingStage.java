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
package it.pronetics.madstore.crawler.impl;

import it.pronetics.madstore.crawler.Stage;
import it.pronetics.madstore.crawler.model.Page;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.CompactXmlSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XmlSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocessing {@link it.pronetics.madstore.crawler.Stage} implementation for cleaning up (X)HTML pages
 * and forcing UTF-8 charset encoding.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class PreprocessingStage implements Stage {

    private static final Logger LOG = LoggerFactory.getLogger(PreprocessingStage.class);

    public Page execute(Page page) {
        try {
            LOG.info("Cleaning up page: {}", page.getLink());
            HtmlCleaner htmlCleaner = new HtmlCleaner();
            CleanerProperties cleanerProperties = htmlCleaner.getProperties();
            cleanerProperties.setOmitComments(true);
            cleanerProperties.setTranslateSpecialEntities(false);
            cleanerProperties.setRecognizeUnicodeChars(false);
            cleanerProperties.setOmitUnknownTags(true);
            cleanerProperties.setOmitDoctypeDeclaration(false);
            cleanerProperties.setOmitXmlDeclaration(false);
            cleanerProperties.setUseCdataForScriptAndStyle(true);

            TagNode tagNode = htmlCleaner.clean(page.getData());
            tagNode.removeAttribute("xmlns:xml");
            XmlSerializer xmlSerializer = new CompactXmlSerializer(cleanerProperties);
            String cleanedPage = xmlSerializer.getXmlAsString(tagNode, "UTF-8");
            LOG.debug("Cleaned page: {}", cleanedPage);
            return new Page(page.getLink(), cleanedPage);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            return null;
        }
    }
}
