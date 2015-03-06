package de.unigoettingen.sub.be.ous.print.layout

import de.unigoettingen.sub.be.ous.print.util.Util
import groovy.util.logging.Log4j
import org.junit.BeforeClass

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNotNull

/**
 * Created by cmahnke on 05.03.15.
 */
@Log4j
class TestBase {
    static File SLIPS_LBS3 = new File(TestBase.getClass().getResource('/hotfolder/lbs3/in/').toURI())
    static File SLIPS_LBS4 = new File(TestBase.getClass().getResource('/hotfolder/lbs4/in/').toURI())
    static List<URL> SLIP_LBS3_FILES = new ArrayList<URL>()
    static List<URL> SLIP_LBS4_FILES = new ArrayList<URL>()
    static URL PARSER_XML = TestBase.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")
    static URL PARSER_TXT_LBS3 = TestBase.getClass().getResource("/layouts/ous40_layout_001_du.asc")
    static URL PARSER_TXT_LBS4 = TestBase.getClass().getResource("/layouts/ous40_layout_001_du-lbs4.asc")
    static URL XSLFO = TestBase.getClass().getResource("/xslt/layout2fo.xsl")

    static List<URL> URLS = [TestBase.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml"),
                             TestBase.getClass().getResource("/layouts-xml/ous40_layout_001_en.asc.xml")]

    static URL LAYOUT = Layout2FoTest.getClass().getResource("/layouts-xml/ous40_layout_001_du.asc.xml")

    static String LBS3_CHARSET = 'Cp850'
    static String LBS4_CHARSET = 'ISO-8859-1'

    @BeforeClass
    static void setUp() {
        assertNotNull(SLIPS_LBS3)
        def p = ~/.*\.print/
        SLIPS_LBS3.eachFileMatch(p) {
            f ->
                SLIP_LBS3_FILES.add(f.toURI().toURL())
                log.info('Added URL ' + f.toURI().toURL().toString() + ' to test file list')
        }
        SLIPS_LBS4.eachFileMatch(p) {
            f ->
                SLIP_LBS4_FILES.add(f.toURI().toURL())
                log.info('Added URL ' + f.toURI().toURL().toString() + ' to test file list')
        }
        assertNotNull(XSLFO)
        assertNotNull(PARSER_XML)
        assertNotNull(PARSER_TXT_LBS3)
        assertNotNull(PARSER_TXT_LBS4)
    }

    public static dumpFile(String out, AbstractTransformer at, Class c, String prefix) {
        log.trace("Result:\n----------------[${prefix}] START OF RESULT(" + c.getName() + ')\n' + at.getXML())
        log.trace("----------------[${prefix}] END OF RESULT(" + c.getName() + ')\n')
        log.trace('Saving file to ' + out)
        Util.writeDocument(at.getResult(), new File(out).toURI().toURL())
    }
}
