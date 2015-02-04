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
 * Test class for Layout class 
 * @author cmahnke
 */

import de.unigoettingen.sub.be.ous.print.layout.Asc2Xml
import de.unigoettingen.sub.be.ous.print.util.Util

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*


@Log4j
class LayoutTest {
    static File SLIPS = new File(Xml2ParserTest.getClass().getResource('/hotfolder/in/').toURI())
    static List<URL> SLIP_FILES = new ArrayList<URL>()
    static URL PARSER_XML = Xml2ParserTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")
    static URL PARSER_TXT = Xml2ParserTest.getClass().getResource("/layouts/ous40_layout_001_du.asc")
    static URL XSLFO = Layout2FoTest.getClass().getResource("/xslt/layout2fo.xsl")
    static Boolean CLEANUP = false
    
    @BeforeClass
    static void setUp () {
        assertNotNull(SLIPS)
        def p = ~/.*\.print/
        SLIPS.eachFileMatch(p) {
            f ->
            SLIP_FILES.add(f.toURI().toURL())
            log.info('Added URL ' + f.toURI().toURL().toString() + ' to test file list')
        }
        assertNotNull(XSLFO)
        assertNotNull(PARSER_XML)
        assertNotNull(PARSER_TXT)
    }
    
    @Test
    @TypeChecked
    void testEncoding() {
        for (slip in SLIP_FILES) {
            log.info('converting ' + slip.toString())
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + Layout.readFile(slip, Layout.DEFAULT_ENCODING))
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
            
        }
    }
    
    @Test
    void testLayoutXml() {
        for (slip in SLIP_FILES) {
            def pdfOut = slip.toString().substring(5) + '.pdf'
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_XML)
            try {
                l.layout()
            } catch (Exception e) {
                log.error('Transformation failed', e)
                log.warn('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n')
                log.warn(Util.docAsString(l.l2f.result))
                log.warn('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
                fail()
            }
            l = null
        }
    }
    
    @Test
    void testLayoutXmlDebug() {
        for (slip in SLIP_FILES) {
            def pdfOut = slip.toString().substring(5) + '.pdf'
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_XML)
            l.setDebug(true)
            l.layout()
            l = null
        }
    }
    
    @Test
    void testLayoutTxt() {
        for (slip in SLIP_FILES) {
            def pdfOut = slip.toString().substring(5) + '.a4.pdf'
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_TXT)
            //Use try / catch block to get the offending content
            try {
                l.layout()
            } catch (Exception e) {
                log.error('Transformation failed', e)
                log.warn('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n')
                log.warn(Util.docAsString(l.l2f.result))
                log.warn('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
                fail()
            }
            l = null
        }
    }
    
    @Test
    void testLayoutA5() {
        for (slip in SLIP_FILES) {
            def pdfOut = slip.toString().substring(5) + '.a5.pdf'
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_TXT)
            l.setPageSize(Layout.PageSize.A5)
            //Use try / catch block to get the offending content
            try {
                l.layout()
            } catch (Exception e) {
                log.error('Transformation failed', e)
                log.warn('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n')
                log.warn(Util.docAsString(l.l2f.result))
                log.warn('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
                fail()
            }
            l = null
        }
    }
    
    @Test
    void testLayoutPs() {
        for (slip in SLIP_FILES) {
            def psOut = slip.toString().substring(5) + '.ps'
            log.trace('Writing Postscript file to ' + psOut)
            FileOutputStream fos = new FileOutputStream(new File(psOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PS, fos, XSLFO, null, PARSER_TXT)
            
            l.layout()
            l = null
            
        }
    }
    
    @AfterClass
    static void cleanUp () {
        if (CLEANUP) {
            def p = ~/.*\.pdf/
            SLIPS.eachFileMatch(p) {
                f ->
                f.delete()
                log.info('Removed Test file ' + f.toURI().toURL().toString())
            }
        }
    }

    
}

