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
import groovy.util.logging.Log4j

/**
 * Converts a XML representation of a layout into text format
 * @author cmahnke
 */
@Log4j
class Xml2Asc extends AbstractTransformer {
    /** The location of the stylesheet */
    def static String xslt = "/xslt/xml2asc.xsl"
    
    /** The URL of the stylesheet to used */
    protected static URL stylesheet = this.getClass().getResource(xslt)
    
    //Configuration of Stylesheet
    /** Parameters of the stylesheet */
    def static paramPrototypes = ['output-heading': 'false']
            
    /*
     * This generates accessor methods for the parameters which live inside of the params Map 
     */
    static {
        paramPrototypes.keySet().each { name ->
            def methodName = name[0].toUpperCase() + name[1..-1]
            Xml2Asc.metaClass."set${methodName}" = {String value -> params."${name}" = value }
            Xml2Asc.metaClass."get${methodName}" = {-> params."${name}" }
        }
    }
    
    /**
     * Construts a empty Xml2Asc and sets 
     * the parameters of the transformation.
     */
    Xml2Asc () {
        // Fill the map with the names of the params
        paramPrototypes.each() { name, value -> params[name] = value }
        //TODO: this is a ugly hack, it's not thread safe
        AbstractTransformer.resolver = new XSLTIncludeClasspathURIResolver(this, xslt)
    }
    
    /**
     * Construts a Xml2Asc, sets and the parameters of the transformation and sets the given input.
     * @param input the {@link java.net.URL URL} of the document to be transformed
     * @see #Xml2Asc()
     */
    Xml2Asc (URL input) {
        this()
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
        result = transform(this.input, this.stylesheet, this.params)
    }
}

