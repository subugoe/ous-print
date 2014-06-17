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

package de.unigoettingen.sub.be.ous.print.util.resolver

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.URIResolver

/**
 * A stacking resolver, wich acts as a meta resolver for a list of given resolvers.
 * Note, that the first resolver which doesn't return null is used. exceptions
 * are catched and silently ignored 
 * @author cmahnke
 */
@CompileStatic
//@TypeChecked
@Log4j
class StackingResolver implements URIResolver {
    List<URIResolver> resolvers = new ArrayList<URIResolver>()
    
    /**
     * Build a stacking resolver from a resolver. You can more resolvers later 
     * by calling {@see #addResolver(URIResolver)}
     * 
     * @param resolver
     */
    StackingResolver(URIResolver resolver) {
        this.resolvers.add(resolver)
    }
    
    /**
     * Build a stacking resolver from a List of resolvers
     * @param resolvers
     */
    
    StackingResolver(List<URIResolver> resolvers) {
        this.resolvers.addAll(resolvers)
    }

    /**
     * @see URIResolver#resolve(String, String)
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        Source s
        for (resolver in resolvers) {
            try {
                s = resolver.resolve(href, base)
            } catch (TransformerException TE) {
                log.debug('Resolver ' + resolver.getClass().toString() + ' returned no result')
            } finally {
                if (s != null) {
                    return s
                }
            }
        }
        return null
    }
    
    /**
     * Adds a Resolver to tee stack
     * 
     * @param resolver
     */
    public addResolver(URIResolver resolver){
        resolvers.add(resolver)
    } 
}

