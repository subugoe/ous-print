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

import javax.print.DocPrintJob
import javax.print.PrintService

import org.apache.camel.EndpointInject
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.ShutdownRunningTask

import org.junit.Test

/**
 * This Test file should test if all results end up at the virtual printer.
 * @author cmahnke
 * See http://camel.apache.org/testing.html
 */
class LayoutProcessorTest extends CamelTestSupport {
    static URL PARSER_TXT = LayoutProcessorTest.getClass().getResource("/layouts/ous40_layout_001_du.asc")
    static URL FO = LayoutProcessorTest.getClass().getResource("/xslt/layout2fo.xsl")
    
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;
    
    @Test
    public void testMessageCount() {
        //We've got 36 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(3000)
        resultEndpoint.setResultWaitTime(30000)
        resultEndpoint.expectedMessageCount(36)
        assertMockEndpointsSatisfied();       
    }
    
    public void mockPrinter () {
        //Taken from https://svn.apache.org/repos/asf/camel/trunk/components/camel-printer/src/test/java/org/apache/camel/component/printer/PrinterPrintTest.java
        // setup javax.print 
        int numberOfPrintservicesBefore = PrintServiceLookup.lookupPrintServices(null, null).length;
        
        PrintService ps1 = mock(PrintService.class);
        when(ps1.getName()).thenReturn("printer");
        when(ps1.isDocFlavorSupported(any(DocFlavor.class))).thenReturn(Boolean.TRUE);
        
        boolean res1 = PrintServiceLookup.registerService(ps1);
        assertTrue("PrintService #1 should be registered.", res1);
       
        
        PrintService[] pss = PrintServiceLookup.lookupPrintServices(null, null);
        assertEquals("lookup should report two PrintServices.", numberOfPrintservicesBefore + 2, pss.length);

        DocPrintJob job1 = mock(DocPrintJob.class);
        when(ps1.createPrintJob()).thenReturn(job1);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                
                LayoutProcessor processor = new LayoutProcessor()
                processor.setDebugPath(new File('./target/').toURI().toURL())
                processor.setTemplate(PARSER_TXT)
                processor.setXslfo(FO)
                from("file:./target//generated-test-resources/hotfolder/lbs3/in?noop=true&include=.*.print")
                .shutdownRunningTask(ShutdownRunningTask.CompleteAllTasks)
                .process(processor).to("mock:result")
                
            }
        };
    }
    
}

