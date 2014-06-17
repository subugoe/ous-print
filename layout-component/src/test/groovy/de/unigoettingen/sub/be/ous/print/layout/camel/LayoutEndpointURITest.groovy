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

package de.unigoettingen.sub.be.ous.print.layout.camel

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main
import org.junit.Test

/**
 * This test should check if A5 output is working, also testes if the URI Endpoint is working.
 * @author cmahnke
 */
@TypeChecked
@Log4j
class LayoutEndpointURITest {
    @Test
    void testA5 () {
        Main main = new Main()
        main.addRouteBuilder(new RouteBuilder () {
                public void configure() {
                    from('file:./target/generated-test-resources/hotfolder/in?include=sub473_\\d{16}_slip001.print&noop=true').
                        to('layout:.?xslfo=./target/test-classes/xslt/layout2fo.xsl&template=./target/test-classes/layouts/ous40_layout_001_du.asc&outputFormat=PDF&pageSize=A5').
                        to('file:./target/?fileName=${file:name}.a5.pdf&fileExist=Override')
                }
            })
        main.start()
        log.info('Camel started, waiting for completition')
        sleep(3000)
        Integer exchangeCount = main.getCamelContexts().get(0).getInflightRepository().size()
        while (exchangeCount != 0) {
            exchangeCount = main.getCamelContexts().get(0).getInflightRepository().size()
            log.info('There are still ' + exchangeCount + ' exchanges running, sleeping')
            sleep(1000)
        }
        log.info('All done, if there are no files, call \'mvn clean\' first')
    }
	
}

