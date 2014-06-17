/*
 * This file is part of the OUS print system, Copyright 2014 SUB Göttingen
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

import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemException
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.VFS

/**
 * A resolver to get the contents of a zip file, used to transform contents of ODF files.
 * @author cmahnke
 */
@Log4j
//@TypeChecked
@CompileStatic
class ZipFileResolver implements URIResolver {
    /** the zip file to search in. */
    protected URL zipFile
    
    /** The FileObject of the given file  */
    protected FileObject zipFileObject
    
    /**
     * Constructor which takes a zip file to resolve files from
     * @param zipFile
     * 
     *
     */
    ZipFileResolver (URL zipFile) {
        FileSystemManager fsManager = VFS.getManager()
        FileObject zipFileObject = fsManager.resolveFile(zipFile)
        this.zipFile = zipFile
    }
    
    /**
     * @see URIResolver#resolve(String, String)
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            FileObject result = zipFileObject.resolveFile(href)
            return new StreamSource(result.getContent().getInputStream())
        } catch (FileSystemException fse) {
            throw new TransformerException('File ' + href + ' not found in archive ' + zipFile.toString())
        }
    }
}

