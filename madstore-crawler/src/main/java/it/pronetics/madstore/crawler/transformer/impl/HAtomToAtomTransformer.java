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
package it.pronetics.madstore.crawler.transformer.impl;

import it.pronetics.madstore.crawler.model.Page;
import it.pronetics.madstore.crawler.transformer.Transformer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link it.pronetics.madstore.crawler.transformer.Transformer} implementation using XSLT transformations for hAtom microformats
 * to Atom document conversion.
 *
 * @author Salvatore Incandela
 * @author Sergio Bossa
 */
public class HAtomToAtomTransformer implements Transformer {

    private static final transient Logger LOG = LoggerFactory.getLogger(HAtomToAtomTransformer.class);
    private static final String XSL_LOCATION = "xsl/";
    private static final String XSL_FILE = "hAtom2Atom.xsl";
    private Templates templates;

    /**
     * Create the transformer.
     */
    public HAtomToAtomTransformer() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(new CustomUriResolver());
        Source xsltSource = new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(XSL_LOCATION + XSL_FILE));
        try {
            templates = transformerFactory.newTemplates(xsltSource);
        } catch (TransformerConfigurationException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public byte[] transform(Page page) {
        ByteArrayInputStream dataInputStream = null;
        ByteArrayOutputStream resultInputStream = null;
        try {
            LOG.info("Transforming: {}", page.getLink());
            dataInputStream = new ByteArrayInputStream(page.getData().getBytes("UTF-8"));
            resultInputStream = new ByteArrayOutputStream();
            if (dataInputStream.available() <= 0) {
                dataInputStream.close();
                return new byte[0];
            } else {
                Source htmlSource = new StreamSource(dataInputStream);
                javax.xml.transform.Transformer transformer = templates.newTransformer();
                transformer.setParameter("source-uri", page.getLink().getLink());
                transformer.transform(htmlSource, new StreamResult(resultInputStream));
                return resultInputStream.toByteArray();
            }
        } catch (Exception ex) {
            LOG.info("Transformation abnormally terminated: {}", page.getLink());
            LOG.warn(ex.getMessage());
            LOG.debug(ex.getMessage(), ex);
            return new byte[0];
        } finally {
            try {
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                if (resultInputStream != null) {
                    resultInputStream.close();
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    private static class CustomUriResolver implements URIResolver {

        public Source resolve(String href, String base) throws TransformerException {
            return new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(XSL_LOCATION + href));
        }
    }
}
