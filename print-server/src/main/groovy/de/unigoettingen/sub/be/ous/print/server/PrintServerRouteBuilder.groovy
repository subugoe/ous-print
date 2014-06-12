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

package de.unigoettingen.sub.be.ous.print.server

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller

import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.Constants
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.RoutesDefinition
import org.apache.camel.util.ObjectHelper

/**
 * This RouteBuilder can read routes from XML Files
 * @author cmahnke
 */
@TypeChecked
class PrintServerRouteBuilder extends RouteBuilder {
    /** The {@link org.apache.camel.model.RoutesDefinition RoutesDefinition}, that have been read */
    protected RoutesDefinition rds = null
    /** The {@link java.net.URL URL} of the configuration file */
    protected URL config = null
    
    /**
     * Sets up a new {@link org.apache.camel.builder.RouteBuilder RouteBuilder} for the given configuration
     * @param config, the {@link java.net.URL URL} of the configuration file
     */
    PrintServerRouteBuilder (URL config) {
        this.config = config
        InputStream ins = config.openStream()
        this.rds = loadRoutes(ins)
        super.setRouteCollection(this.rds)
        
    }

    /**
     * @see org.apache.camel.builder.RouteBuilder#configure()
     */
    void configure () {        
    }
    
    //TODO: Check if this should be RouteDefinitions or RouteDefinition
    /**
     * This method is taken in parts from the Camel core, it reads Routes from the
     * provided {@link java.io.InputStream InputStream} XML content
     * @param is, the {@link java.io.InputStream InputStream} to load routes from
     * @returns {@link org.apache.camel.model.RoutesDefinition RoutesDefinition}, the readed routes
     * @throws SAXParseException on parser errors
     * @throws java.io.IOException if the stream has already be consumed
     */
    public static RoutesDefinition loadRoutes (InputStream is) {
        JAXBContext jaxbContext = JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES, CamelContext.class.getClassLoader());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Object result = unmarshaller.unmarshal(is);
        //log.trace('Route loaded, now casting to return type')
        RoutesDefinition answer = null;
        if (result instanceof RouteDefinition) {
            RouteDefinition route = (RouteDefinition) result;
            answer = new RoutesDefinition();
            answer.getRoutes().add(route);
        } else if (result instanceof RoutesDefinition) {
            answer = (RoutesDefinition) result;
        } else {
            throw new IllegalArgumentException("Unmarshalled object is an unsupported type: " + ObjectHelper.className(result) + " -> " + result);
        }
        return answer
    }
    
}

