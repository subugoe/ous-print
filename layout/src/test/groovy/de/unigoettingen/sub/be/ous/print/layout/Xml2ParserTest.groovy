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

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.xml.transform.TransformerException
import javax.xml.transform.dom.DOMSource

import static org.junit.Assert.*
import org.junit.Test

import de.unigoettingen.sub.be.ous.print.util.Util
import de.unigoettingen.sub.be.ous.print.layout.Xml2Parser.LayoutParser


@Log4j
class Xml2ParserTest extends TestBase {

    //Test transformations
    @Test
    @TypeChecked
    void testTransform() {
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

    //Test transformations
    @Test
    @TypeChecked
    void testTransformEncoded() {
        for (xml in URLS) {
            log.info('Transforming XML File ' + xml.toString() + ' to parser')
            Xml2Parser x2p = new Xml2Parser(xml, LBS4_CHARSET)
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

    @Test
    @TypeChecked
    void testTransformLBS4() {
        log.info('Transforming ASC File ' + PARSER_TXT_LBS4.toString() + ' to parser')
        Asc2Xml a2x = new Asc2Xml(PARSER_TXT_LBS4)
        a2x.transform()
        Xml2Parser x2p = new Xml2Parser(a2x.result, LBS4_CHARSET)
        try {
            x2p.transform()
        } catch (TransformerException te) {
            log.error('Transformation failed!')
            fail(te.getMessage())
        }
        log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + x2p.getXML())
        log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
    }

    //Test if the parsers read files
    @Test
    @TypeChecked
    void testParse() {
        log.info('[LBS3] Generating Parser for ' + PARSER_XML)
        Xml2Parser x2p = new Xml2Parser(PARSER_XML, LBS3_CHARSET)
        x2p.transform()
        LayoutParser lp = x2p.getParser()
        def xslt = new DOMSource(x2p.result)
        for (slip in SLIP_LBS3_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
            def xmlOut = slip.toString().substring(5) + '.xml'
            lp.parse(slip)

            log.trace('Result:\n----------------[LBS3] START OF RESULT(' + this.getClass().getName() + ')\n' + lp.getXML())
            log.trace('----------------[LBS3] END OF RESULT(' + this.getClass().getName() + ')\n')
            log.trace('Saving file to ' + xmlOut)
            Util.writeDocument(lp.result, new File(xmlOut).toURI().toURL())

        }
    }

    //Test if the parsers read files
    @Test
    @TypeChecked
    void testParseLBS4() {
        log.info('[LBS4] Generating Parser for ' + PARSER_TXT_LBS4)
        Asc2Xml a2x = new Asc2Xml(PARSER_TXT_LBS4)
        a2x.transform()
        Xml2Parser x2p = new Xml2Parser(a2x.result, LBS4_CHARSET)
        x2p.transform()
        LayoutParser lp = x2p.getParser()
        def xslt = new DOMSource(x2p.result)
        for (slip in SLIP_LBS4_FILES) {
            if (!Layout.validateAsc(slip)) {
                log.warn('File contains invalid characters: ' + slip.toString())
                continue
            }
            def xmlOut = slip.toString().substring(5) + '.xml'
            lp.parse(slip)

            log.trace('Result:\n----------------[LBS4] START OF RESULT(' + this.getClass().getName() + ')\n' + lp.getXML())
            log.trace('----------------[LBS4] END OF RESULT(' + this.getClass().getName() + ')\n')
            log.trace('Saving file to ' + xmlOut)
            Util.writeDocument(lp.result, new File(xmlOut).toURI().toURL())

        }
    }

}

