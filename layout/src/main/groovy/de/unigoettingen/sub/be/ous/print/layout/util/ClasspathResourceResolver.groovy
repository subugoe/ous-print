/*
 * This file is part of the OUS Print Server, Copyright 2019 SUB Göttingen
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

package de.unigoettingen.sub.be.ous.print.layout.util

import groovy.transform.CompileStatic

import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.net.URL

import org.apache.fop.apps.io.ResourceResolverFactory
import org.apache.xmlgraphics.io.Resource
import org.apache.xmlgraphics.io.ResourceResolver

/**
 * This resolver can be can be used to get files from classpath.
 *
 * @author cmahnke
 * See https://stackoverflow.com/questions/41661997/set-fopfactorybuilder-baseuri-to-jar-classpath
 */

@CompileStatic
class ClasspathResourceResolver implements ResourceResolver {
    private ResourceResolver wrapped;

    public ClasspathResolverURIAdapter() {
        this.wrapped = ResourceResolverFactory.createDefaultResourceResolver()
    }

    @Override
    public Resource getResource(URI uri) throws IOException {
        if (uri.getScheme().equals("classpath")) {
            URL url = getClass().getClassLoader().getResource(uri.getSchemeSpecificPart())
            return new Resource(url.openStream())
        } else {
            return wrapped.getResource(uri)
        }
    }
    @Override
    public OutputStream getOutputStream(URI uri) throws IOException {
        return wrapped.getOutputStream(uri)
    }

}
