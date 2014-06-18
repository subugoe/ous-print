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

package de.unigoettingen.sub.be.ous.print.util

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked
import groovy.transform.CompileStatic
import groovy.xml.XmlUtil

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.Transformer
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * This class contains some static utility methods for DOM handling (like read and write) and for validation.
 * @author cmahnke
 */
//TODO: Reenable this, if the problem with XmlUtil is fixed
//@CompileStatic
@Log4j
class Util {
    /**
     * Writes a DOM {@link org.w3c.dom.Document Document} to the given {@link java.io.OutputStream OutputStream}
     * 
     * @param doc the {@link org.w3c.dom.Document Document} to be written
     * @param out the {@link java.io.OutputStream OutputStream} where the {@link org.w3c.dom.Document Document} will be written to 
     */
    @TypeChecked
    static void writeDocument (Document doc, OutputStream out) {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        def source = new DOMSource(doc);
        def result = new StreamResult(out);
        transformer.transform(source, result);
    }
    
    /**
     * Writes a DOM {@link org.w3c.dom.Document Document} to the given {@link java.io.File File}
     * 
     * @param doc the {@link org.w3c.dom.Document Document} to be written
     * @param file the {@link java.io.File File} where the {@link org.w3c.dom.Document Document} will be written to
     * @see #writeDocument (org.w3c.dom.Document Document,java.io.OutputStream)
     */
    @TypeChecked
    static void writeDocument (Document doc, File file) {
        writeDocument(doc, new FileOutputStream(file))
    }
    
    /**
     * Writes a DOM {@link org.w3c.dom.Document Document} to the given {@link java.net.URL URL}.
     * Please not that this only works for local URLs (with file:// prefix)
     * 
     * @param doc the {@link org.w3c.dom.Document Document} to be written
     * @param url the {@link java.net.URL URL} where the {@link org.w3c.dom.Document Document} will be written to
     * @see #writeDocument (org.w3c.dom.Document Document,java.io.OutputStream)
     * @see #writeDocument (org.w3c.dom.Document Document,java.io.File)
     */
    @TypeChecked
    static void writeDocument (Document doc, URL url) {
        writeDocument(doc, new File(url.toURI()))
    }
    
    /**
     * Loads a DOM {@link org.w3c.dom.Document Document} from an {@link java.net.URL URL}.
     * This is just a wrapper for {@link #loadDocument(InputStream) loadDocument}
     * @param xml the {@link java.net.URL URL} of the document to be loaded
     * @return the loaded DOM {@link org.w3c.dom.Document Document}
     * @see #loadDocument(InputStream)
     */
    @TypeChecked
    static Document loadDocument (URL xml) {
        loadDocument(xml.openStream())
    }
    
    /**
     * Loads a DOM {@link org.w3c.dom.Document Document} from an {@link java.io.InputStream InputStream}
     * @param input the {@link java.io.InputStream InputStream} of the document to be loaded
     * @return the loaded DOM {@link org.w3c.dom.Document Document}
     */
    @TypeChecked
    static Document loadDocument (InputStream input) {
        DocumentBuilder builder = getDocumentBuilder()
        builder.parse(input)
    }
    
    /**
     * Gets a {@link javax.xml.parsers.DocumentBuilder DocumentBuilder} 
     * @returns a {@link javax.xml.parsers.DocumentBuilder DocumentBuilder}  instance
     */
    @TypeChecked
    protected static DocumentBuilder getDocumentBuilder () {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.setNamespaceAware(true)
        return factory.newDocumentBuilder()
    }
    
    /**
     * Transforms a {@Link org.w3c.dom.Node Node} into it's own {@link org.w3c.dom.Element element}
     * @param node the {@Link org.w3c.dom.Node Node} to be transformed
     * @return a {@link org.w3c.dom.Element element}
     */
    //TODO: this doesn't work yet
    @Deprecated
    static Element getElementFromNode (Node node) {
        if (node == null) {
            throw new IllegalStateException('Node is null!')
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) node
        } else {
            return getDocumentFromNode(node).getDocumentElement()
        }      
    }
    
    /**
     * Transforms a {@Link org.w3c.dom.Node Node} into it's own {@link org.w3c.dom.Document Document}
     * @param node the {@Link org.w3c.dom.Node Node} to be transformed
     * @return a {@link org.w3c.dom.Document Document}
     */
    @TypeChecked
    static Document getDocumentFromNode (Node node) {
        Document docTarget = getDocumentBuilder().newDocument()
        docTarget.appendChild(docTarget.adoptNode(node.cloneNode(true)));
        return docTarget
    }
    
    /**
     * Returns the namespace of the root element of the given {@link java.net.URL URL}
     * Make sure that the parser being used is namespace aware.
     * @param xml the {@link java.net.URL URL} of the document which root namespace should be retrieved.
     * @returns the namespace URI as String
     * @see #getRootNamespace(org.w3c.dom.Document)
     */
    @TypeChecked
    static String getRootNamespace (URL xml) {
        getRootNamespace(loadDocument(xml))
    }

    /**
     * Returns the namespace of the root element of the given {@link org.w3c.dom.Document Document}#+
     * Make sure that the parser being used is namespace aware.
     * @param doc the {@link org.w3c.dom.Document Document} which root namespace should be retrieved.
     * @returns the namespace URI as String 
     */
    @TypeChecked
    static String getRootNamespace (Document doc) {
        //Default namespace set
        if (doc.getDocumentElement().getAttributeNode("xmlns")) {
            doc.getDocumentElement().getAttributeNode("xmlns").getValue()
        } else {
            //No default namespace set, get namespace of root element
            doc.getDocumentElement().getNamespaceURI()
        }
    }
      
    /**
     * Returns the name of the root element of the provided document
     * @param url the {@link java.net.URL URL} for the document.
     * @returns the root element name as String 
     */
    @TypeChecked
    static String getRootElementName (URL xml) {
        loadDocument(xml).documentElement.getTagName()
    }
    
        /**
     * Returns a temporary {@link java.io.File File} for a given {@link java.net.URL URL}.
     * The user is responsible to delete the file after usage.
     * @param {@link java.net.URL URL} the URL to make accessable as file
     * @ returns {@link java.io.File File} the File.
     */
    @TypeChecked
    static File xmlURLAsFile (URL url) {
        File out = File.createTempFile('ugh-util-tmp-xml-url', '.xml')
        log.trace('Got URL of W3C DOM Document, write File to ' + out.getAbsolutePath() + ' This will fail if the document isn\'t valid')
        writeDocument(loadDocument(url), out)
        return out
    }

    /**
     * Returns a temporary {@link java.io.File File} for a given {@link org.w3c.dom.Document Document}.
     * The user is responsible to delete the file after usage.
     * @param {@link org.w3c.dom.Document Document} the DOM Document to make accessable as file
     * @returns {@link java.io.File File} the File.
     */
    @TypeChecked
    static File docAsFile (Document doc) {
        //create a temp file
        File out = File.createTempFile('ugh-util-tmp', '.xml')
        log.trace('Got W3C DOM Document, write File to ' + out.getAbsolutePath())
        writeDocument(doc, out)
        return out
    }
    
    /**
     * Returns a String representation of a {@link org.w3c.dom.Document Document}.
     * @param {@link org.w3c.dom.Document Document} the DOM Document
     * @returns the document as String 
     */
    @TypeChecked
    static String docAsString (Document doc) {
        return XmlUtil.serialize(doc.documentElement)
    }
    
    /**
     * Convert a given URL into a String representation of a relative path
     */
    @TypeChecked
    static public String uRL2RelPath (URL u) {
        def base = new File('.').toURI().toURL().toString()
        return '.' + u.toString().substring(base.length() - 3)
    } 
    
    /**
     * Returns a given Source as String (depending on implementation)
     * May even return null if the source is not supported
     * Supported are SAXSource, DOMSource and StreamSource
     * @param Source the Source
     * @returns the source as String
     */
    //@TypeChecked
    static public sourceToString (Source source) {
        if (source instanceof SAXSource) {
            return ((SAXSource) source).getInputSource().getByteStream().text
        } else if (source instanceof StreamSource) {
            return ((StreamSource) source).getInputStream().text
        } else if (source instanceof DOMSource) {
            Node root = ((DOMSource) source).getNode()
            return XmlUtil.serialize(root)
        } else {
            return null
        }
    }
}