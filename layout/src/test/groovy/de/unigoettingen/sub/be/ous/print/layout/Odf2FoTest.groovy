/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unigoettingen.sub.be.ous.print.layout

import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser.LayoutParser
import groovy.transform.TypeChecked

import groovy.util.logging.Log4j
import org.junit.BeforeClass
import org.junit.Test

import static org.junit.Assert.*

/**
 * Unit tests for converting ODf into XSL-FO
 * @author cmahnke
 */
@Log4j
class Odf2FoTest {
    static URL LAYOUT = Layout2FoTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")
    static URL ODF = Layout2FoTest.getClass().getResource("/odf/Bestellschein%20Template.odt")
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
        assertNotNull('ODF file not found', ODF)
        // Slip files
        assertNotNull('Test data not found', SLIPS)
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
            Odf2Fo o2f = new Odf2Fo(LP.result, ODF)
            o2f.transform()
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + o2f.getXML())
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
        }
    }
	
}

