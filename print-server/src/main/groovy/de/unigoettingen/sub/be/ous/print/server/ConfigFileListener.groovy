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

import org.apache.camel.CamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.RoutesDefinition

import org.apache.commons.vfs2.FileChangeEvent
import org.apache.commons.vfs2.FileListener

/**
 * This FileListener watches a Camel XML configuration and loads changes in the
 * given {@link org.apache.camel.CamelContext CamelContext}.
 * @author cmahnke
 */
@Log4j
@TypeChecked
class ConfigFileListener implements FileListener {
    /** The {@link org.apache.camel.CamelContext CamelContext} to update */
    CamelContext cc
    
    /**
     * Constructor taking a {@link org.apache.camel.CamelContext CamelContext} to update
     * @param cc the {@link org.apache.camel.CamelContext CamelContext} to change if a new file is found
     */
    ConfigFileListener(CamelContext cc) {
        this.cc = cc
    }

    /**
     * Watches for changes of configuration file
     * @see org.apache.commons.vfs2.FileListener#fileChanged()
     */
    void fileChanged(FileChangeEvent event){
        RoutesDefinition newRoutes = null
        log.info('Config file changed')
        try {
            InputStream config = event.getFile().getContent().getInputStream()
            newRoutes = PrintServerRouteBuilder.loadRoutes(config)
            //Catch everything else since if an error occurs here it can bring down the complete application
        } catch (Throwable t) {
            log.error('Can\'t reload routes!', t)
        }
        if (newRoutes != null) {
            for (RouteDefinition r in cc.getRouteDefinitions()) {
                log.info('Shutting down routes - Does nothing yet')
            }
        }
    }
    /**
     * Just to implement the interface, does nothing
     * @see org.apache.commons.vfs2.FileListener#fileCreated()
     */
    void fileCreated(FileChangeEvent event) {
        //Do nothing
    }
    
    /**
     * Just to implement the interface, does nothing
     * @see org.apache.commons.vfs2.FileListener#fileDeleted()
     */
    void fileDeleted(FileChangeEvent event) {
        //Do nothing
    }
}

