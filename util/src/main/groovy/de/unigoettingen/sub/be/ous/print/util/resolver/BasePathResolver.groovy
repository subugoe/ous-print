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
import javax.xml.transform.stream.StreamSource

/**
 * This resolver can be can take a base path and resolve relative URIs against it.
 *
 * @author cmahnke
 * See http://stackoverflow.com/questions/5416421/apache-fop-how-to-set-base-url-for-accessing-external-resource-using-relative-p

 */
@Log4j
//@TypeChecked
@CompileStatic
class BasePathResolver implements URIResolver {
    /** The base URL to resolve against */
    URL baseURL 
    
    /**
     * Constructs a BasePathResolver for the given URL
     */
    
    public BasePathResolver (URL baseURL) {
        this.baseURL = baseURL
    }
    
    /**
     * @see URIResolver#resolve(String, String)
     */
    
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        log.trace('Resolving \'' + href + '\' with base \'' + base + '\'')
        log.trace('Base URL set to ' + baseURL)
        String resolvedPath = baseURL.toString() + File.separator + href
        log.trace('Trying directory ' + resolvedPath)
        URL u = new URL(resolvedPath)
        InputStream input
        Boolean failed = false
        try {
            input = u.openStream()
        } catch (IOException ioe) {
            log.error('URL ' + u.toString() + ' couldn\'t be opened!', ioe)
            failed = true
        }
        if (!failed) {
            return new StreamSource(input)
        } else {
            throw new TransformerException('URL ' + u.toString() + ' couldn\'t be opened!')
        }
    }

}

