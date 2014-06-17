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
import de.unigoettingen.sub.be.ous.print.util.Util

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j
import groovy.xml.XmlUtil

import javax.xml.transform.TransformerException
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamSource

import org.w3c.dom.Document

/**
 * Converts XML representation of a layout into a parser and returns a Groovy class
 * representing the generated parser
 * @author cmahnke
 */
@Log4j
class Xml2Parser extends AbstractTransformer {
    protected Document inDoc = null
    
    /** The location of the stylesheet */
    def static String xslt = "/xslt/xml2parser.xsl"
    
    /** The URL of the stylesheet to used */
    protected static URL stylesheet = this.getClass().getResource(xslt)
    
    //Configuration of Stylesheet
    /** Parameters of the stylesheet */
    def static paramPrototypes = ['encoding': 'UTF-8']
            
    /*
     * This generates accessor methods for the parameters which live inside of the params Map 
     */
    static {
        paramPrototypes.keySet().each { name ->
            def methodName = name[0].toUpperCase() + name[1..-1]
            Asc2Xml.metaClass."set${methodName}" = {String value -> params."${name}" = value }
            Asc2Xml.metaClass."get${methodName}" = {-> params."${name}" }
        }
    }
    
    /**
     * Construts a empty Xml2Parser 
     */
    Xml2Parser () {
        // Fill the map with the names of the params
        paramPrototypes.each() { name, value -> params[name] = value }
        AbstractTransformer.resolver = new XSLTIncludeClasspathURIResolver(this, xslt)
    }
    
    /**
     * Construts a Xml2Parser, sets and the parameters of the transformation and sets the given input.
     * @param input the {@link java.net.URL URL} of the document to be transformed
     * @see #Xml2Parser()
     */
    Xml2Parser (URL input) {
        this()
        this.input = input
    }
    
    /**
     * Construts a Xml2Parser, sets and the parameters of the transformation and sets the given input.
     * @param input the {@link org.w3c.dom.Document Document} of the document to be transformed
     * @see #Xml2Parser()
     */
    Xml2Parser (Document inDoc) {
        this()
        //Input as Document
        this.inDoc = inDoc
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
        if (inDoc == null) {
            log.trace('Processing'  + this.input)
            log.debug("Using stylesheet " + stylesheet.toString())
            result = transform(this.input, stylesheet, this.params)
        } else {
            log.trace('Processing internal Document')
            result = transform(new DOMSource(inDoc), new StreamSource(stylesheet.openStream()), this.params)
        }
    }
    
    /**
     * Returns a Java Object, representing the generated parser 
     * @throws IllegalStateException if not configured correctly
     * @return LayoutParser the parser for the given layout
     */
    LayoutParser getParser() {
        if (result != null) {
            return new LayoutParser(result, input.toString())
        }
        throw new IllegalStateException('No result stylesheet')
    }
    
    /**
     * Returns a Java Object, representing the parser ftom a given styleheet
     * Use this if you are working with precompiled parsers
     * @return LayoutParser the parser for the given stylesheet
     */
    static LayoutParser getParser(URL stylsheet, String name) {
        return new LayoutParser(new StreamSource(stylesheet.openStream()), name)
    }
    
    /**
     * Class encapsulating a generated XSLT based parser
     */
    @TypeChecked
    protected class LayoutParser extends AbstractTransformer {
        /** The memory representaion of the parser */
        Document parser
        
        /** The Name of the layout used */
        String layoutName
        
        /** Params */
        def Map params = ['input': '']
        
        /** The result {@link org.w3c.dom.Document Document} */
        protected Document result
        
        /**
         * Construts a empty LayoutParser
         */
        protected LayoutParser () {
        
        }
        
        /**
         * Construts a LayoutParser using a parser given as document and the name of the source layout
         * @param {@link org.w3c.dom.Document Document} the parser
         * @param {@link java.lang.String String} the name of the source layout
         */
        protected LayoutParser (Document parser, String layoutName) {
            this()
            this.parser = parser
            this.layoutName = layoutName
            this.input = AbstractTransformer.getEmptyXml()
        }

        
        /**
         * Checks if the required parameters are set and performes the transformation
         * @throws IllegalStateException if the paramters are empty or not set
         * @see de.unigoettingen.sub.be.ous.print.layout.AbstractTransformer#transform()
         */
        @Override
        void transform () {
            if (parser == null) {
                throw new IllegalStateException('No parser stylesheet given')
            }
            log.debug("Using stylesheet generated from " + layoutName)
            log.trace('XSLT: \n' + Util.docAsString(parser))
            result = transform(new StreamSource(input.openStream()), new DOMSource(parser), this.params)
        }
        
        /**
         * Parses a given URL with the generated parser
         * @param URL of the file to be parsed
         */
        void parse (URL input) {
            log.trace('Parsing InputStream')
            parse(input.openStream())
        }
        
        /**
         * Parses a given InputStream
         * Creates a temp file for converting the content of the InputStream into UTF-8
         * @param the InputStream to be parsed
         * @throws TransformerException
         */
        void parse (InputStream input) {
            File temp = File.createTempFile("temp", ".txt");
            temp.deleteOnExit();
            temp.write(Layout.readFile(input))
            log.debug('Created temp file ' + temp.getAbsolutePath()) 
            this.params['input'] = temp.toURI().toURL().toString()
            log.trace('Setting input to ' + this.params['input'] + 'starting transformation...')
            try {
                this.transform()
            } catch (TransformerException te) {
                throw te
            } finally {
                temp.delete()
            }
        }
        
        //TODO: Check why this method isn't inherited
        /**
         * Returns the result XML as String
         * @return String the result of the Transformation as String
         */
        String getXML() {
            if (result != null) {
                return XmlUtil.serialize(result.documentElement)
            }
        }
        
    }
}

