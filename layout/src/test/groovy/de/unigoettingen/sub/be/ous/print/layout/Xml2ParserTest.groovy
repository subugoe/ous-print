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

package de.unigoettingen.sub.be.ous.print.layout

/**
 * Test class for generating parsers from ASC files
 * @author cmahnke
 */

import groovy.util.logging.Log4j
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.TransformerFactory
import org.junit.BeforeClass
import groovy.transform.TypeChecked

import static org.junit.Assert.*
import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser
import de.unigoettingen.sub.be.ous.print.util.LogErrorListener
import de.unigoettingen.sub.be.ous.print.util.Util
import org.junit.Ignore
import org.junit.Test
import org.w3c.dom.Document

import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser.LayoutParser


@Log4j
class Xml2ParserTest {
    static List<URL> URLS = [Xml2ParserTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml"), 
        Xml2ParserTest.getClass().getResource("/layouts-xml/ous40_layout_001_en.asc.xml")]
    static URL PARSER_XML = Xml2ParserTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")
    static File SLIPS = new File(Xml2ParserTest.getClass().getResource('/hotfolder/lbs3/in/').toURI())
    static List<URL> SLIP_FILES = new ArrayList<URL>()
    
    //Build file list
    @BeforeClass
    static void setUp () {
        assertNotNull(SLIPS)
        def p = ~/.*\.print/
        SLIPS.eachFileMatch(p) {
            f ->
            SLIP_FILES.add(f.toURI().toURL())
            log.info('Added URL ' + f.toURI().toURL().toString() + ' to test file list')
        }
    }
    
    //Test transformations
    @Test
    @TypeChecked
    void testTransform () {
        for (xml in URLS) {
            log.info('Transforming XML File ' + xml.toString() + ' to parser')
            Xml2Parser x2p = new Xml2Parser(xml)
            try {
                x2p.transform()
            } catch (TransformerException te) {
                log.error('Transformation failed!')
                fail(te.getMessage())
            }
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + x2p.getXML())
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
        }
        
    }
    
    //Test if the parsers read files
    @Test
    @TypeChecked
    void testParse() {
        log.info('Generating Parser for ' + PARSER_XML)
        Xml2Parser x2p = new Xml2Parser(PARSER_XML)
        x2p.transform()
        LayoutParser lp = x2p.getParser()
        def xslt = new DOMSource(x2p.result)
        for (slip in SLIP_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
            def xmlOut = slip.toString().substring(5) + '.xml'
            lp.parse(slip)

            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + lp.getXML())
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
            log.trace('Saving file to ' + xmlOut)
            Util.writeDocument(lp.result, new File(xmlOut).toURI().toURL())
               
        }
    }
    
}

