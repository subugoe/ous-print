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
 * Tests for ASC to XML transformations
 * @author cmahnke
 */

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import javax.xml.transform.TransformerException

import static org.junit.Assert.*
import de.unigoettingen.sub.be.ous.print.layout.Asc2Xml
import org.junit.Ignore
import org.junit.Test

@TypeChecked
@Log4j
class Asc2XmlTest {
    static List<URL> URLS = [Asc2XmlTest.getClass().getResource("/layouts/ous40_layout_001_du.asc"), 
        Asc2XmlTest.getClass().getResource("/layouts/ous40_layout_001_en.asc")]
    static List<URL> BROKEN = [Asc2XmlTest.getClass().getResource("/layouts-broken/ous40_layout_001_du.asc"), 
        Asc2XmlTest.getClass().getResource("/layouts-broken/ous40_layout_001_en.asc")]
    
    //Test transformation
    @Test
    @TypeChecked
    void testTransform () {
        for (asc in URLS) {
            log.info('Transforming ASC File ' + asc.toString())
            Asc2Xml a2x = new Asc2Xml(asc)
            try {
                a2x.transform()
            } catch (TransformerException te) {
                log.error('Transformation failed!')
                fail(te.getMessage())
            }
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + a2x.getXML())
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
        }
        
    }
    
    //Test broken files
    @Test(expected=IllegalStateException.class)
    void testBroken () {
        for (asc in BROKEN) {
          log.info('Testing broken charset ' + asc.toString())
          Asc2Xml a2x = new Asc2Xml(asc)
          a2x.transform()
        }
    }

}

