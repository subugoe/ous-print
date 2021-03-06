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

import groovy.util.logging.Log4j
import groovy.transform.CompileStatic
//import groovy.transform.TypeChecked

import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.URIResolver
import javax.xml.transform.stream.StreamSource


/**
 * Internal class to resolve includes using class path, inspired by http://stackoverflow.com/a/12453881

 * @author cmahnke
 */

@Log4j
//@TypeChecked
@CompileStatic
class XSLTIncludeClasspathURIResolver implements URIResolver {
    /** The caller Class (used to get the ClassLoader) */
    protected Class<?> clazz
    /** The URL of the style sheet to resolve against */
    protected URL stylesheet = null
    
    
    /**
     * Constructor which takes the calling class loader into account
     * @param caller the {@link java.lang.Object} caller, needed to get the used class loader
     * @param xslt
     *
     */
    XSLTIncludeClasspathURIResolver (Object caller, String xslt) {
        clazz = caller.getClass()
        stylesheet = this.getClass().getResource(xslt)
        log.trace('Caller ' + clazz.getName() + ' with stylesheet ' + stylesheet.toString())
    }
    
    /**
     * Using this constructor, just look into the class path of the caller
     * to get resources in Jar files and on class path
     * @param Class of the caller, to get the right class loader
     * @author cmahnke
     * @see {@link http://www.publicstaticfinal.de/2011/01/26/fop-embedding-fonts-from-classpath/}
     */
    XSLTIncludeClasspathURIResolver(Class<?> clazz) {
        this.clazz = clazz
        log.trace('Created empty XSLTIncludeClasspathURIResolver for ' + clazz.getName())
    }
    
    /**
     * @see URIResolver#resolve(String, String)
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        //TODO: check if this works inside Jar files
        log.trace ("Got resolve request for '${href}' using '${base}' as base")
        if (!new URI(href).isAbsolute()) {
            InputStream source

            source = clazz.getClassLoader().getResourceAsStream(href)
            if (source != null) {
                log.trace("Found ${href} directly on class path")
                return new StreamSource(source)
            }
            
            //Try to work around an issue with sources in Jar files
            if (href.startsWith('./')) {
                log.trace('Converting relative path to absolute to look it um in side classpath')
                String absolutePath = href.substring(2)
                log.trace('Searching for absolute path on the class path: ' + absolutePath + ' Class loader of ' + clazz.getName())
                source = clazz.getClassLoader().getResourceAsStream(absolutePath)
                if (source != null) {
                    log.trace('Found ' + href + ' as ' + absolutePath + ' directly on class path')
                    return new StreamSource(source)
                }
            }

            //Next try to construct the URI from the given stylesheet path
            if (stylesheet != null) {
                URI resolved = stylesheet.toURI().resolve(href)
                if (resolved.toString().startsWith('file:') && new File(resolved).exists()) {
                    log.trace("Found ${href} as ${resolved}")
                    return new StreamSource(new FileInputStream(new File(resolved)))
                }
            } else {
                log.warn('No context stylesheet set (Not found on class path ' + System.getProperty('java.class.path') + ')!')
            }
            log.warn('Couldn\'t resolve URI')
        } else {
            return new StreamSource(new URL(href).openStream())
        }
        
        log.trace("Couldn't find ${href} on the class path")
        return null

    }
	
}

