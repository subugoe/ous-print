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

import de.unigoettingen.sub.be.ous.print.util.PrinterUtil


import groovy.transform.TypeChecked

import org.apache.camel.EndpointInject

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport

import org.junit.Before
import org.junit.Test

import javax.print.DocPrintJob
import javax.print.DocFlavor
import javax.print.PrintService
import javax.print.PrintServiceLookup
import javax.print.attribute.standard.MediaTray
import javax.print.attribute.standard.Media

import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Tests against a virtual printer, if available, otherwise mock one
 * @author cmahnke
 */
class LayoutEndpointPrintTest extends CamelTestSupport  {
    static String VIRTUAL_PRINTER = 'PDFwriter'
    boolean print = false
    
    @EndpointInject(uri = 'mock:result')
    protected MockEndpoint resultEndpoint;
    
    @Before
    @TypeChecked
    public void setup() {
        setupJavaPrint()

        for (printer in PrinterUtil.getPrinterNames()) {
            log.info("Available Printer: " + printer) 
        }
        log.info('Camel started, waiting for completition')
        resultEndpoint.setMinimumResultWaitTime(5000)
        resultEndpoint.setResultWaitTime(30000)
        
    }
    
    @Test
    @TypeChecked
    void testPrint () {
        //getMockEndpoint("mock:output").setExpectedMessageCount(1)
        context.addRoutes(new RouteBuilder() {
                public void configure() {
                    if (PrinterUtil.getPrinterNames().contains(VIRTUAL_PRINTER)) {
                        log.warn("Printing to ${VIRTUAL_PRINTER}")
                        from("file:./target/generated-test-resources/hotfolder/lbs3/in?include=.*.print&noop=true&charset=Cp850")
                        .to("layout:.?xslfo=./target/test-classes/xslt/layout2fo.xsl&template=./target/test-classes/layouts/ous40_layout_001_du.asc")
                        .to('lpr://localhost/' + VIRTUAL_PRINTER).to('mock:result')
                    } else if (print == true) {
                        log.warn("Printing to default printer")
                        from("file:./target/generated-test-resources/hotfolder/lbs3/in?noop=true&include=.*.print&idempotent=true&charset=Cp850")
                        .to("layout:.?xslfo=./target/test-classes/xslt/layout2fo.xsl&template=./target/test-classes/layouts/ous40_layout_001_du.asc")
                        .to("lpr://localhost/default").to('mock:result')
                    } else {
                        log.error('No virtual printer given and printing to default printer disabled')
                        from("file:./target/generated-test-resources/hotfolder/lbs3/in?noop=true&include=.*.print&idempotent=true&charset=Cp850")
                                .to("layout:.?xslfo=./target/test-classes/xslt/layout2fo.xsl&template=./target/test-classes/layouts/ous40_layout_001_du.asc")
                                .to('mock:result')
                    }

                }
            })
        resultEndpoint.expectedMessageCount(28)
        assertMockEndpointsSatisfied()
    }
    
    protected void setupJavaPrint() {
        // "install" another default printer
        PrintService psDefault = mock(PrintService.class)
        when(psDefault.getName()).thenReturn("DefaultPrinter")
        when(psDefault.isDocFlavorSupported(any(DocFlavor.class))).thenReturn(Boolean.TRUE)
        PrintServiceLookup psLookup = mock(PrintServiceLookup.class)
        PrintService[] pservices = [psDefault]
        when(psLookup.getPrintServices()).thenReturn(pservices)
        when(psLookup.getDefaultPrintService()).thenReturn(psDefault)
        DocPrintJob docPrintJob = mock(DocPrintJob.class)
        when(psDefault.createPrintJob()).thenReturn(docPrintJob)
        MediaTray[] trays = [MediaTray.TOP, MediaTray.MIDDLE, MediaTray.BOTTOM]
        when(psDefault.getSupportedAttributeValues(Media.class, null, null)).thenReturn(trays)
        PrintServiceLookup.registerServiceProvider(psLookup)
    }
    
   
}

