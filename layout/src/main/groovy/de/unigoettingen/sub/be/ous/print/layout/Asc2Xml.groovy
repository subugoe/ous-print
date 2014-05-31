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
 *
 * @author cmahnke
 */

@Log4j
class Asc2Xml extends AbstractTransformer {
    /** The location of the stylesheet */
    def static String xslt = "/xslt/asc2xml.xsl"
    
    /** The URL of the stylesheet to used */
    protected static URL stylesheet = this.getClass().getResource(xslt)
    
    //Configuration of Stylesheet
    /** Parameters of the stylesheet */
    def static paramPrototypes = ['layoutFile': '', 'encoding': 'ASCII']
            
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
     * Construts a empty Asc2Xml and sets 
     * the parameters of the transformation.
     */
    Asc2Xml () {
        // Fill the map with the names of the params
        paramPrototypes.each() { name, value -> params[name] = value }
        //TODO: this is a ugly hack, it's not thread safe
        AbstractTransformer.resolver = new XSLTIncludeClasspathURIResolver(this, xslt)
    }
    
    /**
     * Construts a Asc2Xml, sets and the parameters of the transformation and sets the given input.
     * @param input the {@link java.net.URL URL} of the document to be transformed
     * @see #Asc2Xml()
     */
    Asc2Xml (URL input) {
        this()
        //Validate input file
        if (!Layout.validateAsc(input)) {
            def message = "File ${input} contains invalid characters ([\\x10-\\x1f])"
            log.error(message)
            throw new IllegalStateException(message)
            
        }
        //The input file will be given as param to a empty transformation
        this.setLayoutFile(input.toString())
        this.input = xml
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
    
    /**
     * @see AbstractTransformer#validateParams(Map)
     */
    @Override
    protected static Boolean validateParams (Map params) {
        if (params['layoutFile'] == '') {
            throw new IllegalStateException('Layout not configured')
            
        }
        return true
    }

}

