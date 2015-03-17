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

package de.unigoettingen.sub.be.ous.print.layout.cli

import de.unigoettingen.sub.be.ous.print.util.Util
import de.unigoettingen.sub.be.ous.print.util.PrinterUtil
import de.unigoettingen.sub.be.ous.print.layout.Layout

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.print.Doc
import javax.print.DocFlavor
import javax.print.DocPrintJob
import javax.print.PrintService
import javax.print.SimpleDoc
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.standard.Copies
import javax.print.attribute.standard.JobName

import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

import org.junit.contrib.java.lang.system.ExpectedSystemExit

import static org.junit.Assert.*

/**
 * Test class for Main class
 * @author cmahnke
 */

@Log4j
class MainTest {
    static File SLIPS = new File(MainTest.getClass().getResource('/hotfolder/lbs3/in/').toURI())
    static List<String> SLIP_FILES = new ArrayList<String>()
    static URL FO = MainTest.getClass().getResource("/xslt/layout2fo.xsl")
    static URL TEMPLATE = MainTest.getClass().getResource("/layouts/ous40_layout_001_du.asc")
    static String VIRTUAL_PRINTER = 'PDFwriter'
    
    @BeforeClass
    static void setUp () {
        assertNotNull(SLIPS)
        def p = ~/.*\.print/
        SLIPS.eachFileMatch(p) {
            f ->
            def path = Util.uRL2RelPath(f.toURI().toURL())
            SLIP_FILES.add(path)
            log.info('Added path ' + path + ' to test file list')
        }
        assertNotNull(FO)
        assertNotNull(TEMPLATE)
    }
    
    //See http://stackoverflow.com/questions/6141252/dealing-with-system-exit0-in-junit-tests
    //See http://stefanbirkner.github.io/system-rules/index.html#ExpectedSystemExit
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none()
	
    @Test
    @TypeChecked
    public void testEmpty() {
        //See also http://rkennke.wordpress.com/2012/04/12/how-to-test-drive-a-main-method/
        exit.expectSystemExit();
        String[] args = []
        Main.main(args);
    }

    @Test
    @TypeChecked
    public void testHelp() {
        String[] args = ['--help']
        Main.main(args)
    }
    
    @Test
    @TypeChecked
    public void testFormats() {
        String[] args = ['-lf']
        Main.main(args)
    }
    
    @Test
    @TypeChecked
    public void testPrinters() {
        String[] args = ['-lp']
        Main.main(args)
    }
    
    @Test
    @TypeChecked
    public void testTransformDefault() {
        for (slip in SLIP_FILES) {
            log.info('Transforming ' + slip + ' in default page size(' + Layout.DEFAULT_PAGE_SIZE + ')')
            String[] args = ['-v', '-i', slip, 
                        '-j', 'TXT', '-o', slip + '.default.pdf',
                         '-p', 'PDF',  '-x', Util.uRL2RelPath(FO), '-t', Util.uRL2RelPath(TEMPLATE)]
            Main.main(args)
        }
    }
    
    @Test
    @TypeChecked
    public void testTransformA5() {
        for (slip in SLIP_FILES) {
            log.info('Transforming ' + slip + ' in A5 page size')
            String[] args = ['-v', '-i', slip, 
                        '-j', 'TXT', '-o', slip + '.a5.pdf',
                         '-p', 'PDF',  '-x', Util.uRL2RelPath(FO), '-t', Util.uRL2RelPath(TEMPLATE), '-f', 'A5']
            Main.main(args)
        }
    }
    
    @Test
    @TypeChecked
    // Run this with mvn -Dtest=MainTest#testPrintDefault test
    public void testPrintDefault() {
        if (PrinterUtil.getPrinterNames().contains(VIRTUAL_PRINTER)) {
            log.warn('Using virtual printer ' + VIRTUAL_PRINTER + ' (Size: Default)')
            for (slip in SLIP_FILES) {
                log.info('Printing ' + slip + ' in default page size (' + Layout.DEFAULT_PAGE_SIZE + ')')
                String[] args = ['-v', '-i', slip, 
                        '-j', 'TXT', '-o', slip + '.pdf',
                         '-p', 'PDF',  '-x', Util.uRL2RelPath(FO), '-t', Util.uRL2RelPath(TEMPLATE), '-prt', VIRTUAL_PRINTER]
                Main.main(args)
            }
        } else {
            log.warn('No local virtual printer named ' + VIRTUAL_PRINTER)
        }
    }
    
    @Test
    @TypeChecked
    public void testPrintA5() {
        if (PrinterUtil.getPrinterNames().contains(VIRTUAL_PRINTER)) {
            log.warn('Using virtual printer ' + VIRTUAL_PRINTER+ ' (Size: A5)')
            for (slip in SLIP_FILES) {
                log.info('Printing ' + slip + ' in A5 page size')
                String[] args = ['-v', '-i', slip, 
                        '-j', 'TXT', '-o', slip + '.pdf',
                         '-p', 'PDF',  '-x', Util.uRL2RelPath(FO), '-t', Util.uRL2RelPath(TEMPLATE), '-prt', VIRTUAL_PRINTER, '-f', 'A5']
                Main.main(args)
            }
        } else {
            log.warn('No local virtual printer named ' + VIRTUAL_PRINTER)
        }
    }
    
    @Test
    @TypeChecked
    public void testMissingFile() {
        exit.expectSystemExit();
        log.info('Testing non existing file')
        String[] args = ['-v', '-i', 'non_existing_file', 
                        '-j', 'TXT', '-o', 'non_existing_file.pdf',
                         '-p', 'PDF',  '-x', Util.uRL2RelPath(FO), '-t', Util.uRL2RelPath(TEMPLATE)]
        Main.main(args)
    }
    
    @Test
    public void testAsc2Parser() {
        log.info('Testing generation of XSL parser')
        String[] args = ['-v', '-i', Util.uRL2RelPath(TEMPLATE), 
                        '-j', 'ASC', '-o', 'ous40_layout_001_du.xsl',
                         '-p', 'XSL',  ]
        Main.main(args)
    }
    
    @Test public void testPostscript () {
        if (PrinterUtil.getPrinterNames().contains(VIRTUAL_PRINTER)) {
            log.warn('Using virtual printer ' + VIRTUAL_PRINTER + ' (Size: Default)')
            for (slip in SLIP_FILES) {
                PrintService ps = PrinterUtil.getPrinter(VIRTUAL_PRINTER)
                String psFile = slip + '.ps'
                log.trace('Output set to ' + psFile)
                String[] args = ['-v', '-i', slip, 
                        '-j', 'TXT', '-o', psFile,
                         '-p', 'PS',  '-x', Util.uRL2RelPath(FO), '-t', Util.uRL2RelPath(TEMPLATE)]
                log.trace('Creating Postscript file')
                Main.main(args)
                File f = new File(psFile)
                log.trace('Reading Postscript file ')
                Doc doc = new SimpleDoc(new FileInputStream(f), DocFlavor.INPUT_STREAM.POSTSCRIPT, null)
                HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet()
                attr.add(new Copies(1))
                attr.add(new JobName(psFile, null))
          
                DocPrintJob pj = ps.createPrintJob()
                log.trace('Sending to printer')
                pj.print(doc, attr)
                
            }
            
        } else {
            log.warn('No local virtual printer named ' + VIRTUAL_PRINTER)
        }
        
    }
}

