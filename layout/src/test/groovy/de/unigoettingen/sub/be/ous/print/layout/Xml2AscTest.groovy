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
 * Test class for Xml2Asc class
 * @author cmahnke
 */

import de.unigoettingen.sub.be.ous.print.layout.Xml2Asc

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import org.junit.Test

import javax.xml.transform.TransformerException

import static org.junit.Assert.*

@TypeChecked
@Log4j
class Xml2AscTest extends TestBase {

    @Test(expected=IllegalStateException.class)
    @TypeChecked
    void testTransform () {
        for (xml in URLS) {
            log.info('Transforming ASC File (' + Xml2Asc.class.getName() + '):' +xml)
            Xml2Asc x2a = new Xml2Asc(xml)
            try {
                x2a.transform()
            } catch (TransformerException te) {
                log.error('Transformation failed!', te)
                fail('Xml2AscTest: Failed ' + te.getMessage())
            }
            log.trace('Result:\n----------------START OF RESULT(' + this.getClass().getName() + ')\n' + x2a.getXML())
            log.trace('----------------END OF RESULT(' + this.getClass().getName() + ')\n')
        }
        
    }
}

