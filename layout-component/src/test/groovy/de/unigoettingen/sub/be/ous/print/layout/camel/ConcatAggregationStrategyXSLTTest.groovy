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
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

import static org.apache.camel.language.mvel.MvelExpression.mvel

/**
 * Created by cmahnke on 18.06.15.
 */
class ConcatAggregationStrategyXSLTTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint


    @Test
    public void testMessageCount() {
        //We've got 2 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(500)
        resultEndpoint.setResultWaitTime(20000)
        //Only one result expected
        resultEndpoint.expectedMessageCount(1)
        assertMockEndpointsSatisfied()
    }

    @Override
    public boolean isUseDebugger() {
        // must enable debugger
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition definition, String id, String shortName) {
        // See http://camel.apache.org/debugger.html
        // this method is invoked before we are about to enter the given processor
        // from your Java editor you can just add a breakpoint in the code line below
        log.info("Before " + definition + " with body " + exchange.getIn().getBody());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:./target/generated-test-resources/hotfolder/acq4/in?include=acq40remdel.*.print&noop=true&charset=ISO-8859-1")
                        .convertBodyTo(String.class)
                        .setHeader('institute', mvel('request.headers.CamelFileName.replaceAll("^acq40rem.{3}(\\\\d{3})_.*$","$1")'))
                        .aggregate(simple('header.institute'), new ConcatAggregatingStrategy())
                        .completionTimeout(5000L)
                        .to("plainText:.&pageSize=A4")
                        .to('xslt:file:./src/main/resources/xslt/acq.xsl?saxon=true')
                        .to('file:./target/?fileName=institute-${header.institute}-' + this.class.getName() + '-plain-acq4.txt')
                        .to("mock:result")
            }
        };
    }
}
