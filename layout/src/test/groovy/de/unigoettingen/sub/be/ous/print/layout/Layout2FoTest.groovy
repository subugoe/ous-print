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
 * Test class for Layout2Fo class
 * @author cmahnke
 */

import de.unigoettingen.sub.be.ous.print.layout.Xml2Asc
import de.unigoettingen.sub.be.ous.print.util.Util
import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser.LayoutParser

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import org.w3c.dom.Document

import static org.junit.Assert.*

@Log4j
class Layout2FoTest {
    static URL LAYOUT = Layout2FoTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")
    static URL FO = Layout2FoTest.getClass().getResource("/xslt/layout2fo.xsl")
    static LayoutParser LP
    static File SLIPS = new File(Xml2ParserTest.getClass().getResource('/hotfolder/in/').toURI())
    static List<URL> SLIP_FILES = new ArrayList<URL>()
    
    @BeforeClass
    static void setUp () {
        //Set Up parser
        log.info('Generating Parser for ' + LAYOUT)
        Xml2Parser x2p = new Xml2Parser(LAYOUT)
        x2p.transform()
        LP = x2p.getParser()
        // Slip files
        assertNotNull(SLIPS)
        def p = ~/.*\.print/
        SLIPS.eachFileMatch(p) {
            f ->
            SLIP_FILES.add(f.toURL())
            log.info('Added URL ' + f.toURL().toString() + ' to test file list')
        }
    }
    
    @Test
    @TypeChecked
    void testFormatFo() {
        for (slip in SLIP_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
        
            LP.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP.result, FO)
            l2f.transform()
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + l2f.getXML())
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
        }
    }
    
    @Test
    @TypeChecked
    void testFormatPdf() {
        for (slip in SLIP_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
        
            LP.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP.result, FO)
            l2f.transform()
            def pdfOut = slip.toString().substring(5) + '.pdf'
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            l2f.format(fos)
        }
        
    }
    
    @Test
    @TypeChecked
    void testFormatXslfo() {
        for (slip in SLIP_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
        
            LP.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP.result, FO)
            l2f.transform()
            def xslfoOut = slip.toString().substring(5) + '.fo'
            log.trace('Writing XSL-FO file to ' + xslfoOut)
            Util.writeDocument(l2f.result, new File(xslfoOut).toURL())
        }
        
    }
    
    @AfterClass
    static void cleanUp () {
        def p = ~/.*\.pdf/
        SLIPS.eachFileMatch(p) {
            f ->
            f.delete()
            log.info('Removed Test file ' + f.toURL().toString())
        }
    }
}

