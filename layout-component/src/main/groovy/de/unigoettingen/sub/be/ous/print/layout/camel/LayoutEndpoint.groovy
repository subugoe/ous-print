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

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import org.apache.camel.Component
import org.apache.camel.impl.ProcessorEndpoint
import org.apache.camel.spi.UriEndpoint

/**
 * A simple endpoint for the LayoutProcessor, extending ProcessorEndpoint
 * @author cmahnke
 */
@TypeChecked
@CompileStatic
@UriEndpoint(scheme = "layout", title = "Layout", syntax = "layout:resourceUri")
class LayoutEndpoint extends ProcessorEndpoint {
    /** The LayoutProcessor to be used */
    protected LayoutProcessor lp
    
    /**
     * Simple constructor, that calls the constructor of the super class
     * @param endpointUri, the endpoint URI
     * @param component, the component
     * @param lp, the LayoutProcessor
     */
    public LayoutEndpoint(String endpointUri, Component component, LayoutProcessor lp) throws Exception {
        super(endpointUri, component, lp)
        this.lp = lp
    }
}

