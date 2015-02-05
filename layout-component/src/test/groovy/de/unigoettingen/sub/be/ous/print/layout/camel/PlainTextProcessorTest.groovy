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
import org.apache.camel.ShutdownRunningTask

import org.junit.Test

/**
 * This Test file should test if all files are converted into XSL-FO
 * @author cmahnke
 */
class PlainTextProcessorTest extends CamelTestSupport {
    
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;
    
    @Test
    public void testMessageCount() {
        //We've got 36 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(1000)
        resultEndpoint.setResultWaitTime(10000)
        resultEndpoint.expectedMessageCount(36)
        assertMockEndpointsSatisfied();       
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                PlainTextProcessor processor = new PlainTextProcessor()
                from("file:./target//generated-test-resources/hotfolder/in?noop=true&include=.*.print&charset=Cp850")
                .shutdownRunningTask(ShutdownRunningTask.CompleteAllTasks)
                .process(processor)
                .to("mock:result")
            }
        };
    }
	
}

