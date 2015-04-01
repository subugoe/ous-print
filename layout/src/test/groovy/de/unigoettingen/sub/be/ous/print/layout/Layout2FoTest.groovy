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

import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser.LayoutParser

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

@Log4j
@TypeChecked
class Layout2FoTest extends TestBase {
    static URL LAYOUT = Layout2FoTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")
    static LayoutParser LP_LBS3
    static LayoutParser LP_LBS4

    
    @BeforeClass
    static void setUp () {
        //Stupid JUnit doesn't support @Before classes from base class
        super.setUp()
        //Set Up parser
        log.info("Generating Parser for ${LAYOUT} for LBS3 and LBS4")
        Xml2Parser x2pLbs3 = new Xml2Parser(LAYOUT, LBS3_CHARSET)
        Xml2Parser x2pLbs4 = new Xml2Parser(LAYOUT, LBS4_CHARSET)
        x2pLbs3.transform()
        x2pLbs4.transform()
        LP_LBS3 = x2pLbs3.getParser()
        LP_LBS4 = x2pLbs4.getParser()
    }
    
    @Test
    @TypeChecked
    void testFormatFo() {
        for (slip in SLIP_LBS3_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
            //This will fail if the charset is wrong
            LP_LBS3.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP_LBS3.getResult(), XSLFO)
            l2f.transform()
            String xslfoOut = generateFileName(slip, this.class, '-lbs3.fo', 5)
            dumpFile(xslfoOut, l2f, this.getClass(),'LBS3')
            addTestfile(new File(xslfoOut))
        }
    }

    @Test
    @TypeChecked
    void testFormatPdf() {
        for (slip in SLIP_LBS3_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
        
            LP_LBS3.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP_LBS3.result, XSLFO)
            l2f.transform()
            String pdfOut = generateFileName(slip, this.class, '.pdf', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            //Use try / catch block to get the offending content
            try {
                l2f.format(fos)
            } catch (Exception e) {
                log.error('Transformation failed', e)
                String xslfoOut = slip.toString().substring(5) + 'error..fo'
                dumpFile(xslfoOut, l2f, this.getClass(),'LBS3-ERROR')
                fail()
            }
        }
    }
    
    @Test
    @TypeChecked
    void testFormatXslfo() {
        for (slip in SLIP_LBS3_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
            LP_LBS3.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP_LBS3.result, XSLFO)
            l2f.transform()
            String xslfoOut = generateFileName(slip, this.class, '-.fo', 5)
            dumpFile(xslfoOut, l2f, this.getClass(),'LBS3')
            addTestfile(new File(xslfoOut))
        }
    }

    @Test
    @TypeChecked
    void testFormatPdfLBS4() {
        for (slip in SLIP_LBS4_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }

            LP_LBS4.parse(slip)
            Layout2Fo l2f = new Layout2Fo(LP_LBS4.result, XSLFO)
            l2f.transform()
            String pdfOut = generateFileName(slip, this.class, '-lbs4.fo', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            l2f.format(fos)
        }
    }

}

