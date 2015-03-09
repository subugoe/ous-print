/*
 * This file is part of the OUS Print Server, Copyright 2011, 2012 SUB Göttingen
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */

package de.unigoettingen.sub.be.ous.print.layout

import de.unigoettingen.sub.be.ous.print.util.LogErrorListener
import de.unigoettingen.sub.be.ous.print.util.Util
import de.unigoettingen.sub.commons.metsmerger.util.NamespaceConstants
import groovy.util.logging.Log4j
import groovy.transform.TypeChecked
import groovy.xml.XmlUtil

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Result
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.Source
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException

import org.w3c.dom.Document

import javax.xml.xpath.XPath
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

/**
 * This abstract class is used as base for all classes that wrap XSLT stylesheets.
 * It wraps common methods for transformation and validation and contains fields
 * needed for this tasks like a link to a schema or the name of the required stylesheet.
 * @author cmahnke
 */
//@TypeChecked
@Log4j
abstract class AbstractTransformer {
    /** The result {@link org.w3c.dom.Document Document} */
    protected Document result
    /** The {@link java.net.URL URL} of the input file */
    def URL input
    /** The W3C document of the input */
    def Document inputDoc
    /** The parameters to be used by the transformation */
    def Map params = [:]
    /** The {@link java.net.URL URL} of the schema used for validation */
    def URL schemaUrl
    /** The result namespace, used to get the schema URL */
    def String resultNamespace
    /** The location of the empty XML file */
    def static String xmlFile = '/xml/empty.xml'
    /** The URL of the empty XML file to used */
    def static URL xml = AbstractTransformer.class.getResource(xmlFile)

        
    protected static URIResolver resolver = null 
    
    /**
     * Transforms the given document using the given stylesheet with parameters.
     * @param input the {@link java.net.URL URL} for the input document
     * @param xslt the {@link javax.xml.transform.dom.DOMSource DOMSource} of the stylesheet
     * @param params parameters of the stylesheet
     * @return the result {@link org.w3c.dom.Document Document}
     * @see #transform(javax.xml.transform.Source,javax.xml.transform.Source,java.util.Map)
     */
    def protected static Document transform (URL input, DOMSource xslt, Map params) {
        transform (new StreamSource(input.openStream()), xslt, params)
    }
    
    /**
     * Transforms the given document using the given stylesheet with parameters.
     * @param doc the {@link org.w3c.dom.Document Document} of the input document
     * @param xslt the {@link javax.xml.transform.dom.DOMSource DOMSource} of the stylesheet
     * @param params parameters of the stylesheet
     * @return the result {@link org.w3c.dom.Document Document}
     * @see #transform(javax.xml.transform.Source,javax.xml.transform.Source,java.util.Map)
     */
    def protected static Document transform (Document doc, DOMSource xslt, Map params) {
        transform (new DOMSource(doc), xslt, params)
    }
    
    /**
     * Transforms the given document using the given stylesheet with parameters.
     * @param input the {@link javax.xml.transform.Source Source} of the input document
     * @param xslt the {@link javax.xml.transform.dom.DOMSource DOMSource} of the stylesheet
     * @param params parameters of the stylesheet
     * @return the result {@link org.w3c.dom.Document Document}
     */
    def protected static Document transform (Source input, DOMSource xslt, Map params) {
        def factory = TransformerFactory.newInstance()
        //TODO: this is a ugly hack, it's not thread safe
        if (resolver != null) {
            factory.setURIResolver(resolver);
        }
        def transformer
        try {
            transformer = factory.newTransformer(xslt)
        } catch (TransformerConfigurationException tce) {
            log.error('Cought TransformerConfigurationException: ', tce)
            if (xslt == null) {
                log.error('Style sheet is null!')
            } else {
                log.debug('Offending stylesheet:\n' + Util.sourceToString(xslt))
            }
            throw tce
        }
        def listener = new LogErrorListener()
        transformer.setErrorListener(listener)
        Result result = new DOMResult()
        log.info('Checking if result will be DOM')
        //TODO: Add Handling for Result that are not DOM (like HTML or Text)
        //new StreamResult(new FileOutputStream(output)))
        XPathFactory xpathFactory = XPathFactory.newInstance()
        XPath xpath = xpathFactory.newXPath()
        xpath.setNamespaceContext(new NamespaceConstants())
        XPathExpression expr = xpath.compile('/xsl:stylesheet/xsl:output/@method')
        String method = expr.evaluate(xslt.getNode())
        if (method.equalsIgnoreCase('text') || method.equalsIgnoreCase('html')) {
            throw new IllegalStateException('No Dom Result to expect')
        }

        //Pass params to the stylesheet
        params.entrySet().each() {
            log.trace('Transformation Parameter \'' + it.key + '\', Value: \'' + it.value + '\'')
            transformer.setParameter(it.key, it.value)
        }

        try {
            transformer.transform(input, result)
        } catch (TransformerException te) {
            log.error('Transformation failed ', te)
            throw te
        }
        if (listener.fatal) {
            log.error('Transformation failed, check the log!')
        }
        
        def domResult = (Document) result.getNode()
        return domResult
    }
    
    /**
     * Transforms the given document using the given stylesheet with parameters.
     * This is just a wrapper for {@link #transform(URL,Source,Map) transform}
     * which opens an {@link java.io.InputStream InputStream} to create a Source from it an passes this as parameter
     *
     * @param input the {@link java.net.URL URL} for the input document
     * @param stylesheet the {@link java.net.URL URL} to the stylesheet
     * @param params parameters of the stylesheet
     * @return Document the result Document
     * @see #transform(javax.xml.transform.Source,javax.xml.transform.Source,java.util.Map)
     */
    @TypeChecked
    protected static Document transform (URL input, URL stylesheet, Map params) {
        log.trace("Transforming " + input.toString() + " using stylesheet " + stylesheet.toString())

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
        dbf.setNamespaceAware(true)
        // We need to have the style sheet as DOM to be able to use XPATH
        DocumentBuilder db = dbf.newDocumentBuilder()
        Document dom = db.parse(stylesheet.openStream())

        DOMSource xslt = new DOMSource(dom)
        return transform (input, xslt, params)
    }
    
    /**
     * Transforms the given document using the given stylesheet without parameters.
     * This is just a wrapper for {@link #transform(URL,URL,Map) transform}
     * which passes a empty Map as parameter
     * 
     * @param input the {@link java.net.URL URL} for the input document
     * @param stylesheet the {@link java.net.URL URL} to the stylesheet
     * @return the result {@link org.w3c.dom.Document Document}
     * @see #transform(java.net.URL,java.net.URL,java.util.Map)
     * 
     */
    
    protected static Document transform (URL input, URL stylesheet) {
        return transform (input, stylesheet, [:])
    }
    
    /**
     * Returns the result XML as String
     * @return String the result of the Transformation as String
     */
    String getXML() {
        if (result != null) {
            return XmlUtil.serialize(result.documentElement)
        } else {
            return null
        }
    }
    
    /**
     * Abstract method that needs to be implemented by extending classes that do
     * the actual transformation. These are usually just wrappers around the protected transform methods.
     * @see #transform(java.net.URL,java.net.URL)
     * @see #transform(java.net.URL,java.net.URL,java.util.Map)
     * @see #transform(java.net.URL,javax.xml.transform.Source,java.util.Map)
     * 
     */
    abstract void transform ();
    
    /**
     * Stub of a validation method, overide it fi your stylesheet needs a validation of params
     * @return Boolean the result of the validation
     */
    protected static Boolean validateParams (Map params) {
        return true
    }

    /**
     * Returns the UR of en empty XML file
     * @return the {@link java.net.URL URL} of en empty XML file
     */
    static URL getEmptyXml() {
        return xml
    }

    /**
     * Returns the result XML as Document
     * @return the result {@link org.w3c.dom.Document Document}
     */
    Document getResult() {
        if (result != null) {
            return result
        } else {
            return null
        }
    }
    
}

