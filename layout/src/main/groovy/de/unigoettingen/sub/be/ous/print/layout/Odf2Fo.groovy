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

import de.unigoettingen.sub.be.ous.print.util.Util

import groovy.util.logging.Log4j

import org.w3c.dom.Document

/**
 * This class extends Layout2Fo since it's XSL-FO capabilities can be sued for 
 * ODF templates as well. Use ths class if you want to work with ODF templates directly.
 * This code is experimental, there is no support.
 * @author cmahnke
 */

@Log4j
class Odf2Fo extends Layout2Fo {
    /** The location of the stylesheet */
    def static String xslt = "/xslt/odf2xslfo.xsl"
    
    /** The URL of the stylesheet to used */
    protected static URL stylesheet = this.getClass().getResource(xslt)
    
    /** The URL of the ODF to generate the XSL-FO from */
    protected URL odf
       
    /**
     * Construts a empty Odf2Fo and sets 
     * the parameters of the transformation.
     */
    Odf2Fo () {
        //TODO: Set resolver here
        
    }
    
    /**
     * Construts a Odf2Fo and sets the given input.
     * @param input the {@link java.net.URL URL} of the document to be transformed
     * @param odf the {@link java.net.URL URL} for the ODF file which will be used to create a transformator
     * @see #Layout2Fo(URL, URL)
     */
    Odf2Fo (URL input, URL odf) {
        this(Util.loadDocument(input), odf)
    }
    
    /**
     * Construts a Odf2Fo and sets the given input.
     * @param input the {@link org.w3c.dom.Document Document} of the document to be transformed
     * @param odf the {@link java.net.URL URL} for the ODF file which will be used to create a transformator
     * @see #Layout2Fo(Document, URL)
     */
    Odf2Fo (Document input, URL odf) {
        this()
        //Input as Document
        this.inDoc = input
        //Transformation to XSL-FO
        this.stylesheet = stylesheet
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

