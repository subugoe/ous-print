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

package de.unigoettingen.sub.be.ous.print.layout.camel

import de.unigoettingen.sub.be.ous.print.layout.FORMAT
import de.unigoettingen.sub.be.ous.print.layout.Layout

import org.apache.camel.CamelContext
import org.apache.camel.Component
import org.apache.camel.Endpoint
import org.apache.camel.ResolveEndpointFailedException
import org.apache.camel.impl.UriEndpointComponent
import org.apache.camel.spi.UriParam

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

/**
 * Apache Camel Component for the layout engine. Properties will be given as 
 * strings and are also stored in their required types. these will be feed into
 * the Layout object
 * @see org.apache.camel.impl.UriEndpointComponent
 * @author cmahnke
 */
@Log4j
@TypeChecked
class LayoutComponent extends UriEndpointComponent {
    //Paths and files
    @UriParam
    /** The String of the location of the template (can also be an ASC template) */
    String template  = null
    /** The {@link java.net.URL URL} of the location of the template (can also be an ASC template) */
    URL templateURL = null
    @UriParam
    /** The String of the location of the XSL-FO file */
    String xslfo  = null
    /** The {@link java.net.URL URL} of the location of the XSL-FO file */
    URL xslfoURL =null
    @UriParam
    /** The String of the path to search for files referenced in the XSL-FO file */
    String includePath = null
    /** The {@link java.net.URL URL} of the path to search for files referenced in the XSL-FO file */
    URL includePathURL = null
    @UriParam
    /** The String of debug path, generated files can be saved there */
    String debugPath = null
    /** The {@link java.net.URL URL} of debug path, generated files can be saved there */
    URL debugPathURL = null
    //optional
    @UriParam
    /** The input FORMAT */
    FORMAT inputFormat = FORMAT.TEXT
    //optional
    @UriParam
    /** The desired output FORMAT*/
    FORMAT outputFormat = FORMAT.PDF
    
    @UriParam
    /** The desired page size of the result */
    String pageSize = Layout.DEFAULT_PAGE_SIZE
    
    /**
     * Public contructor of the LayoutComponent
     */
    public LayoutComponent () {
        super(LayoutEndpoint.class)
    }

    /**
     * Public contructor of the LayoutComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(CamelContext, Class<? extends Endpoint>)
     */
    public LayoutComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
        super(context, LayoutEndpoint.class)
    }
       
    /**
     * Public constructor of the LayoutComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(Class<? extends Endpoint>)
     */
    public LayoutComponent(Class<? extends Endpoint> endpointClass) {
        super(LayoutEndpoint.class)
    }
    
    /**
     * @see org.apache.camel.impl.DefaultComponent#createEndpoint(String, String, Map<String,Object>)
     * @throws Exception
     */
    @Override
    protected Endpoint createEndpoint(String uri, final String remaining, Map<String, Object> parameters) throws Exception {
        String resourceUri = remaining;
        if (parameters.get("inputFormat") != null) {
            inputFormat = FORMAT.fromString((String) parameters.get("inputFormat"))
            log.trace('Input Format set to ' + inputFormat.toString())
        }
        if (parameters.get("outputFormat") != null) {
            outputFormat = FORMAT.fromString((String) parameters.get("outputFormat"))
            log.trace('Output Format set to ' + outputFormat.toString())
        }        
        if (parameters.get("template") != null) {
            templateURL = Layout.getURL((String) parameters.get("template"))
            log.trace('Template set to ' + templateURL.toString())
        }
        if (parameters.get("xslfo") != null) {
            xslfoURL = Layout.getURL((String) parameters.get("xslfo"))
            log.trace('XSL-FO set to ' + xslfoURL.toString())
        }        
        if (parameters.get("includePath") != null) {
            includePathURL = Layout.getURL((String) parameters.get("includePath"))
            log.trace('Include path set to ' + includePathURL.toString()) 
        }
        if (parameters.get("debugPath") != null) {
            debugPathURL = Layout.getURL((String) parameters.get("debugPath"))
            log.trace('Debug path set to ' + debugPathURL.toString()) 
        }
            
        if (parameters.get("pageSize") != null) {
            pageSize = (String) parameters.get("pageSize")
            log.trace('Page size set to ' + pageSize) 
        }
        log.trace('Setup LayoutProcessor for incomming files using Template ' + templateURL.toString() + ' include path ' + includePathURL.toString() + ' and XSL-FO ' + xslfoURL.toString())

        LayoutProcessor lp = new LayoutProcessor(inputFormat, outputFormat, xslfoURL, includePathURL, templateURL, pageSize)
        return new LayoutEndpoint(uri, this, lp)
    }
    
    /**
     * @see org.apache.camel.impl.DefaultComponent#validateURI(String, String, Map<String,Object>)
     */
    @Override
    protected void validateURI(String uri, String path, Map<String,Object> parameters) {
        if (!parameters.get('template')) {
            log.trace('Endpoint not correctly configured: template')
            throw new ResolveEndpointFailedException('Template not set')
        }
        if (!parameters.get('xslfo')) {
            log.trace('Endpoint not correctly configured: xslfo')
            throw new ResolveEndpointFailedException('XSLFO not set')
        }
        //Validate Paths
        if (parameters.get("template") != null) {
            File f = Layout.getFile((String) parameters.get("template"))
            if (!f.exists()) {
                log.trace('Template ' + f.getAbsolutePath()  + 'couln\'t be found')
                throw new ResolveEndpointFailedException('Template doesn\'t exist')
            }
        }
        if (parameters.get("xslfo") != null) {
            File f = Layout.getFile((String) parameters.get("xslfo"))
            if (!f.exists()) {
                log.trace('XSL-FO ' + f.getAbsolutePath()  + 'couln\'t be found')
                throw new ResolveEndpointFailedException('XSL-FO doesn\'t exist')
            }
        }        
        if (parameters.get("includePath") != null) {
            File f = Layout.getFile((String) parameters.get("includePath"))
            if (!f.exists()) {
                log.trace('Include path ' + f.getAbsolutePath()  + 'couln\'t be found') 
                throw new ResolveEndpointFailedException('Include path doesn\'t exist')
            }
        }
        if (parameters.get("debugPath") != null) {
            File f = Layout.getFile((String) parameters.get("includePath"))
            if (!f.exists()) {
                log.trace('Debug path ' + f.getAbsolutePath()  + 'couln\'t be found') 
                throw new ResolveEndpointFailedException('debug path doesn\'t exist')
            }
        }
    }
    
    /**
     * @see org.apache.camel.impl.DefaultComponent#validateParameters(String, Map<String,Object>, String)
     */
    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
        //TODO: Check if files exist
    }
    
}

