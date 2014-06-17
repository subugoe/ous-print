/*
 * This file is part of the OUS print system, Copyright 2014 SUB Göttingen
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

import de.unigoettingen.sub.be.ous.print.util.resolver.XSLTIncludeClasspathURIResolver
import de.unigoettingen.sub.be.ous.print.util.resolver.BasePathResolver

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource

import org.apache.avalon.framework.configuration.Configuration
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder

import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.Fop
import org.apache.fop.apps.MimeConstants

import org.w3c.dom.Document

/**
 * Transforms a xml representation into XSL-FO file and offers a possibility to render the FO object.
 * 
 * @author cmahnke
 */
@Log4j
class Layout2Fo extends AbstractTransformer {
    /** The input {@link org.w3c.dom.Document Document} */
    protected Document inDoc = null
    
    /** The location of the stylesheet */
    def static String xslt = '/xslt/layout2fo.xsl'
    
    /** The location of the Fop configurtion */
    def static String fopXconf = '/xconf/fop.xconf'
    
    /** The URL of the stylesheet to used */
    protected static URL stylesheet = this.getClass().getResource(xslt)
    
    /** The URL of the FP configuration to used */
    protected static URL config = this.getClass().getResource(fopXconf)
    
    //Configuration of Stylesheet
    /** Parameters of the stylesheet */
    def static paramPrototypes = ['debugParam': 'false', 'barcodeParam': 'false', 'format': 'A5']
      
    /** the path to serch for external media like images */
    protected URL includePath = null
    
    /** The {@link org.apache.fop.apps.FopFactory FopFactory} to be used */
    protected FopFactory fopFactory = null
    
    /** Sets if an external FOP configuration should be used (use this also if you want to embed fonts in the result) */
    protected Boolean useConfig = true
    
    /*
     * This generates accessor methods for the parameters which live inside of the params Map 
     */
    static {
        paramPrototypes.keySet().each { name ->
            def methodName = name[0].toUpperCase() + name[1..-1]
            Layout2Fo.metaClass."set${methodName}" = {String value -> params."${name}" = value }
            Layout2Fo.metaClass."get${methodName}" = {-> params."${name}" }
        }
    }
    
    /**
     * Construts a empty Layout2Fo and sets 
     * the parameters of the transformation.
     */
    protected Layout2Fo () {
        // Fill the map with the names of the params
        paramPrototypes.each() { name, value -> params[name] = value }
    }
    
    /**
     * Construts a Layout2Fo, sets and the parameters of the transformation and sets the given input.
     * @param input the {@link org.w3c.dom.Document Document} of the document to be transformed
     * @param stylesheet the {@link java.net.URL URL} to the style to be used for the transformation
     * @see #Layout2Fo()
     */
    Layout2Fo (Document input, URL stylesheet) {
        this()
        //Input as Document
        this.inDoc = input
        //Transformation to XSL-FO
        this.stylesheet = stylesheet
    }
    
    /**
     * Construts a Layout2Fo, sets and the parameters of the transformation and sets the given input.
     * @param input the {@link java.net.URL URL} of the document to be transformed
     * @param stylesheet the {@link java.net.URL URL} to the style sheet to be used for the transformation
     * @see #Layout2Fo()
     */
    Layout2Fo (URL input, URL stylesheet) {
        this()
        //XML Representation of filled layout
        this.input = input
        //Transformation to XSL-FO
        this.stylesheet = stylesheet
    }
    
    /**
     * Construts a Layout2Fo, using the preconfigured transformation and sets the given input.
     * @param input the {@link java.net.URL URL} of the document to be transformed
     * @see #Layout2Fo()
     */
    Layout2Fo (URL input) {
        this()
        //XML Representation of filled layout
        this.input = input
    }
    
    /**
     * Checks if the required parameters are set and performes the transformation
     * @throws IllegalStateException if the paramters are empty or not set
     * @see de.unigoettingen.sub.be.ous.print.layout.AbstractTransformer#transform()
     */
    @Override
    void transform () {
        if (!validateParams(params)) {
            throw new IllegalStateException('Params not configured');
        }
        log.debug("Using stylesheet " + stylesheet.toString())
        if (inDoc == null) {
            log.trace('Processing'  + this.input)
            log.debug("Using stylesheet " + stylesheet.toString())
            result = transform(this.input, this.stylesheet, this.params)
        } else {
            log.trace('Processing internal Document')
            result = transform(new DOMSource(inDoc), new StreamSource(this.stylesheet.openStream()), this.params)
        }
    }

    /**
     * Formats the FOP result as PDF
     * @param the OutputStream to write the result to
     * see #format(OutputStream, String)
     */
    public void format (OutputStream out) {
        format(out, MimeConstants.MIME_PDF)
    }
    
    /**
     * Formats the FOP result as the requested mime type
     * @throws IllegalArgumentException if no result is present
     * @param the OutputStream to write the result to
     * @param the requested Mime type of the result, use {@link org.apache.fop.apps.MimeConstants MimeConstants} 
     */
    public void format (OutputStream out, String mimeType) {
        if (result == null) {
            throw new IllegalArgumentException('No FO result generated')
        }
        try {
            //See https://xmlgraphics.apache.org/fop/1.0/embedding.html
            
            if (fopFactory == null) {
                fopFactory = FopFactory.newInstance()
            }
            if (useConfig) {
                //See http://xmlgraphics.apache.org/fop/trunk/embedding.html#config-external
                DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder()
                Configuration cfg = cfgBuilder.build(config.openStream())
                log.trace('Loaded configuration file') 
                fopFactory.setUserConfig(cfg)
            }
            fopFactory.setURIResolver(new XSLTIncludeClasspathURIResolver(this.class))
           
            Fop fop = fopFactory.newFop(mimeType, out)
            
            if (includePath != null) {
                fop.getUserAgent().setURIResolver(new BasePathResolver(includePath))
            } else {
                fop.getUserAgent().setURIResolver(new XSLTIncludeClasspathURIResolver(this, xslt))
            }
            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            //Use this if you want to transform here
            //Transformer transformer = factory.newTransformer(xslfo);
            Transformer transformer = factory.newTransformer(); // identity transformer

            //Pass params to the stylesheet
            /*
            params.entrySet().each() {
            transformer.setParameter((String) it.key, it.value)
            }
             */
            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(new DOMSource(result), res);
        } catch (TransformerException te) {
            log.error("Transformation failed ", te)
            throw te

        }
    }
    
    /**
     * XSLT Parameters are Strings, convert them, maybe these method should be generated as well
     * @param should stylesheet debug mode be enabled
     */
    public void setDebugParam(Boolean debug) {
        setDebugParam(String.valueOf(debug))
    }
    
    
}


