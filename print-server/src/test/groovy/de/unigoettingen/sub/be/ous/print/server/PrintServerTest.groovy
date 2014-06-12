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

package de.unigoettingen.sub.be.ous.print.server

import de.unigoettingen.sub.be.ous.print.util.Util

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.xml.bind.Unmarshaller

import org.apache.camel.model.RoutesDefinition

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit

import static org.junit.Assert.assertNotNull

/**
 * Test class for PrintServer
 * @author cmahnke
 */
@Log4j
@TypeChecked
class PrintServerTest {
    
    static URL TEST_ROUTES = PrintServerTest.getClass().getResource("/config/test-routes.xml")
    static URL TEST_A5_ROUTES = PrintServerTest.getClass().getResource("/config/a5route.xml")
    static URL TEST_ROUTES_BROKEN = PrintServerTest.getClass().getResource("/config/test-routes-broken.xml")
    static Long RUN_TIME = 15000 
    
    //See http://stackoverflow.com/questions/6141252/dealing-with-system-exit0-in-junit-tests
    //See http://stefanbirkner.github.io/system-rules/index.html#ExpectedSystemExit
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none()
    
    @Test
    void testMain () {
        def routes = Util.uRL2RelPath(TEST_ROUTES)
        log.debug('Using test route from ' + routes)
        /* Variant invoking the Main method
        String[] args = ['-v', '-c', routes]
        PrintServer.main(args)
        */
        log.trace('Setting up PrintServer and stopping it after ' + RUN_TIME.toString() + ' millis')
        PrintServer ps = new PrintServer(TEST_ROUTES)
        Watcher w = new Watcher(ps, RUN_TIME)
        ps.boot()
        w.start()
    }
    
    @Test
    void testLoad () {
        assertNotNull('No route file', TEST_ROUTES)
        log.trace('Reading ' + TEST_ROUTES.toString())
        InputStream is = TEST_ROUTES.openStream()
        //log.trace('Input is: ' + is.text)
        
        RoutesDefinition rd = PrintServerRouteBuilder.loadRoutes(TEST_ROUTES.openStream())
        
        assertNotNull('No RoutesDefinition loaded', rd)
    }
    
    
    @Test
    void testLoadBroken () {
        def routes = Util.uRL2RelPath(TEST_ROUTES_BROKEN)
        log.debug('Using test route from ' + routes)
        
        String[] args = ['-v', '-c', routes]
        exit.expectSystemExit()
        PrintServer.main(args)
        
    }
    
    @Test
    void testPrintA5 () {
        def routes = Util.uRL2RelPath(TEST_A5_ROUTES)
        log.debug('Using test A5 route from ' + routes)
        log.trace('Setting up PrintServer for A5 and stopping it after ' + RUN_TIME.toString() + ' millis')
        PrintServer ps = new PrintServer(TEST_A5_ROUTES)
        Watcher w = new Watcher(ps, RUN_TIME)
        ps.boot()
        w.start()
    }
    
    @Log4j
    private class Watcher extends Thread {
        long wait
        PrintServer ps
        
        Watcher (PrintServer ps, long wait) {
            this.ps = ps
            this.wait = wait
        }
        
        public void run() {
                //Wait n seconds then stop
                Thread.sleep(wait)
                log.info('Stoping PrintServer')
                ps.stop()
            }
    }
    
}

