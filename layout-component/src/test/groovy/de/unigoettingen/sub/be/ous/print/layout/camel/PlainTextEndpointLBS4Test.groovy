/*
 * This file is part of the OUS Print Server, Copyright 2015 SUB Göttingen
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

import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

/**
 *
 * @author cmahnke
 */
class PlainTextEndpointLBS4Test  extends CamelTestSupport {
    
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint
    
    @Test
    public void testMessageCount() {
        //We've got 36 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(500)
        resultEndpoint.setResultWaitTime(20000)
        resultEndpoint.expectedMessageCount(2)
        assertMockEndpointsSatisfied();       
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:./target/generated-test-resources/hotfolder/lbs3/in?include=.*.print&noop=true&charset=ISO-8859-1")
                .to("plainText:.&pageSize=A5")
                .to("fop:application/pdf")
                .to('file:./target/?fileName=${file:name}-plain-lbs4.pdf')
                .to("mock:result");
            }
        };
    }

}
