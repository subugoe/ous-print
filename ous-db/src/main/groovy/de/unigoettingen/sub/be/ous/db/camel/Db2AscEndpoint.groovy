/*
 * This file is part of the OUS Print Server, Copyright 2015 SUB Göttingen
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

package de.unigoettingen.sub.be.ous.db.camel

import groovy.transform.CompileStatic

import org.apache.camel.Component
import org.apache.camel.Consumer
import org.apache.camel.impl.ProcessorEndpoint
import org.apache.camel.spi.UriEndpoint

/**
 * Created by cmahnke on 04.03.15.
 */
@CompileStatic
@UriEndpoint(scheme = "db2asc", title = "DB2Asc", syntax = "layout:resourceUri" /*, consumerClass = Db2AscConsumer.class */)
class Db2AscEndpoint extends ProcessorEndpoint {
    /** The Db2AscProcessor to be used */
    protected Db2AscProcessor d2ap

    /**
     * Simple constructor, that calls the constructor of the super class
     * @param endpointUri , the endpoint URI
     * @param component , the component
     * @param d2ap , the Db2AscProcessor
     */

    public Db2AscEndpoint(String endpointUri, Component component, Db2AscProcessor d2ap) throws Exception {
        super(endpointUri, component, d2ap)
        this.d2ap = d2ap
    }

    public Consumer createConsumer() throws Exception {
        return new Db2AscConsumer(this)
    }
}
