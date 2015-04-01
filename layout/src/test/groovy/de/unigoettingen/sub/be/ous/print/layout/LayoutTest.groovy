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

import de.unigoettingen.sub.be.ous.print.util.Util

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import org.junit.Test

import static org.junit.Assert.*


@Log4j
class LayoutTest extends TestBase {

    static Boolean CLEANUP = false
    @Test
    @TypeChecked
    void testEncoding() {
        for (slip in SLIP_LBS3_FILES) {
            log.info('converting ' + slip.toString())
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + Layout.readFile(slip, Layout.DEFAULT_ENCODING))
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
        }
    }
    
    @Test
    void testLayoutXml() {
        for (slip in SLIP_LBS3_FILES) {
            String pdfOut = generateFileName(slip, this.class, '.pdf', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_XML, 'Cp850')
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
        for (slip in SLIP_LBS3_FILES) {
            String pdfOut = generateFileName(slip, this.class, '.pdf', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_XML, 'Cp850')
            l.setDebug(true)
            l.layout()
            l = null
        }
    }
    
    @Test
    void testLayoutTxt() {
        for (slip in SLIP_LBS3_FILES) {
            String pdfOut = generateFileName(slip, this.class, '.a4.pdf', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_TXT_LBS3, 'Cp850')
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
    void testLayoutTxtLBS4() {
        for (slip in SLIP_LBS4_FILES) {
            String pdfOut = generateFileName(slip, this.class, '.a4-lbs4.pdf', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_TXT_LBS4, 'ISO-8859-1')
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
        for (slip in SLIP_LBS3_FILES) {
            String pdfOut = generateFileName(slip, this.class, '.a5.pdf', 5)
            log.trace('Writing PDF file to ' + pdfOut)
            FileOutputStream fos = new FileOutputStream(new File(pdfOut))
            addTestfile(new File(pdfOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PDF, fos, XSLFO, null, PARSER_TXT_LBS3)
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
        for (slip in SLIP_LBS3_FILES) {
            String psOut = generateFileName(slip, this.class, '.ps', 5)
            log.trace('Writing Postscript file to ' + psOut)
            FileOutputStream fos = new FileOutputStream(new File(psOut))
            addTestfile(new File(psOut))
            Layout l = new Layout(FORMAT.TEXT, slip.openStream(), FORMAT.PS, fos, XSLFO, null, PARSER_TXT_LBS3)
            
            l.layout()
            l = null
            
        }
    }
    
}

