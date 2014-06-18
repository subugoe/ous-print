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

package de.unigoettingen.sub.be.ous.print.layout

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.Parser
import org.apache.tika.metadata.TikaMetadataKeys
import org.apache.tika.metadata.HttpHeaders

import org.w3c.dom.Document

import org.xml.sax.helpers.DefaultHandler

import javax.print.attribute.standard.MediaSizeName
import javax.print.attribute.standard.OrientationRequested
import java.awt.print.Paper

import org.apache.fop.apps.MimeConstants

import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser.LayoutParser
import de.unigoettingen.sub.be.ous.print.util.Util

/**
 * This is the main class for layouts.
 * @author cmahnke
 */

@Log4j
class Layout {
    static PageSize DEFAULT_PAGE_SIZE = PageSize.A4
    
    /** The input FORMAT */
    FORMAT inFormat
    
    /** The {@link java.io.InputStream InputStream} to consume */
    InputStream input
    
    /** The desired output FORMAT*/
    FORMAT outFormat
    
    /** The {@link java.io.OutputStream OutputStream} to write results to */
    OutputStream output
    
    /** The {@link java.net.URL URL} of the location of the XSL-FO file */
    URL xslfo
    
    /** The {@link java.net.URL URL} of the path to search for files referenced in the XSL-FO file */
    URL includePath
    
    /** The {@link java.net.URL URL} of the location of the parser (can also be an ASC template) */
    URL parser
    
    /** Whether the layouter should work in debug mode */
    Boolean debug = false
    
    /** Parameters for the XSLT transformation */
    Map<String, String> params = [:]
    
    /** The desired page size of the result */
    PageSize pageSize = DEFAULT_PAGE_SIZE
    
    //The converters, these are defined at class level to get their content for debugging.
    /** Internal property of the LayoutParser to be used, used for debugging */
    LayoutParser lp
    
    /** Internal property of the Layout2Fo to be used, used for debugging */
    Layout2Fo l2f
    
    /** Internal property of the Xml2Parser to be used, used for debugging */
    Xml2Parser x2p
    
    /**
     * Creates a Layout object
     * @param inFormat the input FORMAT
     * @param input the InputStream to read
     * @param outFormat the output FORMAT
     * @param output the {@link java.io.OutputStream OutputStream}
     * @param xslfo, {@link java.net.URL URL} of the XLS-FO file to use
     * @param includePath, {@link java.net.URL URL} of the include path for references inside the XSL FO
     * @param parser, {@link java.net.URL URL} of the parser to use
     */
    @TypeChecked
    Layout (FORMAT inFormat, InputStream input, FORMAT outFormat, OutputStream output, URL xslfo, URL includePath, URL parser) {
        this.inFormat = inFormat
        this.input = input
        this.outFormat = outFormat
        if (output != null) {
            this.output = output
        } else {
            this.output = new ByteArrayOutputStream() 
        }
        
        this.xslfo = xslfo
        this.includePath = includePath
        this.parser = parser 
    }
    
    /**
     * Starts the layouting, writes to the given {@link java.io.OutputStream OutputStream}
     */
    def layout () {
        //TODO: Finish other in and outputs
        //Create Parser XML File
        if (parser != null) {
            lp = getLayoutParser(parser)
            //Layout parser should be ready
            if (inFormat == FORMAT.TEXT || inFormat == FORMAT.ASC) {
                lp.parse(input)
            }
        }
        
        if (outFormat == FORMAT.PDF || outFormat == FORMAT.XSLFO || outFormat == FORMAT.PS) {
            if (xslfo == null) {
                throw new IllegalArgumentException('No XSL-FO set for output of formated Layout')
            }
            
            log.trace('Writing PDF or FO file')
            l2f = new Layout2Fo(lp.result, xslfo)
            if (debug) {
                l2f.setDebugParam(true)
            }
            if (includePath != null) {
                l2f.setIncludePath(includePath)
            }
            log.trace('PageSize set to ' + pageSize.toString())
            l2f.setFormat(pageSize.toString())
            l2f.transform()
            if (outFormat == FORMAT.PDF) {
                l2f.format(output)
            } else if (outFormat == FORMAT.PS) {
                l2f.format(output, MimeConstants.MIME_POSTSCRIPT)
            } else if (outFormat == FORMAT.XSLFO) {
                output.write(l2f.getXML().getBytes())
            }
        } else if (outFormat == FORMAT.XSL) {
            log.trace('Writing XSL file')
            output.write(lp.getXML().getBytes())
        }
    }
    
    /**
     * Validates a given {@link java.net.URL URL} if the file contains invalid unicode characters
     * @returns Boolean if the file is valid or not
     */
    @TypeChecked
    def static Boolean validateAsc (URL input) {
        def file = new File(input.toURI())
        if (file.text =~ /[\x10-\x1f]/) {
            return false
        }
        return true
    }
    
    /**
     * Returns the contents of a given {@link java.net.URL URL} in codepage 858
     * @return String the contents (converted from codepage 858 into UTF-8) of the given URL
     */
    @TypeChecked
    def static String readFile (URL input) {
        //See http://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
        //See http://www.torsten-horn.de/techdocs/encoding.htm
        return readFile(input.openStream())
    }
    
    /**
     * Returns the contents of a given {@link java.io.InputStream InputStream} in codepage 858
     * @return String the contents (converted from codepage 858 into UTF-8) of the given InputStream
     */
    @TypeChecked
    def static String readFile (InputStream input) {
        //See http://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html
        //See http://www.torsten-horn.de/techdocs/encoding.htm
        return input.getText('Cp858')
    }
    
    /**
     * Converts a {@link java.net.URL URL} object into a file object
     * @returns {@link java.io.File File} representing the given {@link java.net.URL URL}
     */
    @TypeChecked
    def static File URLToFile (URL url) {
        //taken from https://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html
        File f;
        try {
            f = new File(url.toURI())
        } catch(URISyntaxException e) {
            log.trace('Cought URISyntaxException', e)
            f = new File(url.getPath())
        }
        return f
    } 
    
    /**
     * Guesses the content (Mime) type of the given {@link java.net.URL URL}, uses Apache Tika
     * @param {@link java.net.URL URL} of the file
     * @returns String, containing the Mime type
     */
    @TypeChecked
    def static String guessContentType (URL url) {
        File file = URLToFile(url)
        AutoDetectParser parser = new AutoDetectParser()
        parser.setParsers(new HashMap<MediaType, Parser>())

        Metadata metadata = new Metadata()
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, file.getName())

        InputStream stream = new FileInputStream(file)
        parser.parse(stream, new DefaultHandler(), metadata, new ParseContext())
        stream.close()

        return metadata.get(HttpHeaders.CONTENT_TYPE)
    }
    
    /**
     * Internal function to get a LayoutParser for the given {@link java.net.URL URL}. Detects the Mime
     * type of the file and can create LayoutParser objects for TXT (ASC) and XML
     * reprentations
     * 
     * @param parser, the {@link java.net.URL URL} of the layout representation
     * @returns a LayoutParser object
     */
    @TypeChecked
    protected LayoutParser getLayoutParser(URL parser) {
        LayoutParser lp
        String parserMime = guessContentType(parser)
        if (parserMime == "text/plain") {
            Asc2Xml a2x = new Asc2Xml(parser)
            a2x.transform()
            Xml2Parser x2p = new Xml2Parser(a2x.result)
            x2p.transform()
            lp = x2p.getParser()
        } else if (parserMime == 'text/xml' | parserMime == 'application/xml') {
            //TODO: Check if input is a XSL file
            if (Util.getRootElementName(parser) == 'layout') {
                //XML Representaion of ASC file
                Xml2Parser x2p = new Xml2Parser(parser)
                x2p.transform()
                lp = x2p.getParser()
            } else {
                x2p = new Xml2Parser()
                lp = x2p.getParser(parser, parser.toString())
            }
        }
        return lp
    }
    
    /**
     * Internal function to get a Layout2Fo for the given {@link java.net.URL URL}.
     * This can either be a XSL-FO file or a ODD file
     * 
     * @param parser, the {@link java.net.URL URL} of the XSL-FO ODF source
     * @param doc, the {@link org.w3c.dom.Document Document} to be transformed
     * @returns a Layout2Fo object
     * @throws IllegalStateException if mime type is not supported
     */
    @TypeChecked
    def static Layout2Fo getFormater(URL parser, Document doc) {
        Layout2Fo l2f
        String parserMime = guessContentType(parser)
        if (parserMime == 'application/vnd.oasis.opendocument.text') {
            l2f = new Odf2Fo(doc, parser)
        } else if (parserMime == 'application/xslt+xml') {
            l2f = new Layout2Fo(doc, parser)
        } else {
            throw new IllegalStateException('Mime type ' + parserMime + ' not supported')
        }
        return l2f
    }
    
    //Utility Functions
    /**
     * Resolves relative file pathes
     * @param path, String the path to resolve
     * @returns {@link java.io.File File} the absolute File
     */
    public static File getFile (String path) {
        String prefix = ''
        if (!path.startsWith('/')) {
            prefix = System.getProperty("user.dir") + File.separator
        }
        File f = new File(prefix + path)
        return f
    }
    
    /**
     * Resolves relative file pathes
     * @param path, String the path to resolve
     * @returns a absolute {@link java.net.URL URL}
     */
    public static URL getURL (String path) {
        return getFile(path).toURI().toURL()
    }
    
    /**
     * A Enum representing page sizes A4 and A5, providing some JPS constants
     */
    public enum PageSize {
        A4('A4'), A5('A5')
        /** The name of this PageSize */
        String name
        
        /**
         * Contructor, sets up a PageSize object
         */
        PageSize(String name) { 
            this.name = name 
        }
        /**
         * Get a PageSize for a given String
         * @returns PageSize the format or null
         */ 
        public static PageSize fromString(String size) {
            if (size != null) {
                for (PageSize ps : values()) {
                    if (size.equalsIgnoreCase(ps.name)) {
                        return ps
                    }
                }
            }
            return null
        }
        
        /**
         * Returns the MediaSizeName for the given PageSize
         * @returns {@link javax.print.attribute.standard.MediaSizeName MediaSizeName}, the nmae of the media size
         */
        public static MediaSizeName getMediaSizeName (PageSize ps) {
            if (ps == A4) {
                return MediaSizeName.ISO_A4
            } else if (ps == A5) {
                return MediaSizeName.ISO_A5
            } else {
                reurn null
            }
        }
        
        /**
         * Return the OrientationRequested for the given PageSize
         * @returns {@link javax.print.attribute.standard.OrientationRequested OrientationRequested}, the requested orientation
         */
        public static OrientationRequested getOrientationRequested (PageSize ps) {
            if (ps == A4) {
                return OrientationRequested.PORTRAIT
            } else if (ps == A5) {
                return OrientationRequested.LANDSCAPE
            } else {
                return null
            }
        }
        
        /**
         * Return the Paper for the given PageSize
         * @returns {@link java.awt.print.Paper Paper}, the paper
         */
        public static Paper getPaper(PageSize ps) {
            Paper paper = null
            if (ps == A4) {
                paper = new Paper()
                paper.setSize(595, 842)
                //A4 (borderless)
                paper.setImageableArea(0, 0, 595, 842)
            } else if (ps == A5) {
                paper = new Paper()
                paper.setSize(420, 595)
                //A5 (borderless)
                paper.setImageableArea(0,0,334,509)
            }
            return paper
        }
        
    }
    
}

