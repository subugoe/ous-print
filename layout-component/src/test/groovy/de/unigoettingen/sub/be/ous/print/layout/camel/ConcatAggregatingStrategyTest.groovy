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
import org.apache.camel.Expression
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultMessage
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.test.junit4.CamelTestSupport

import static org.apache.camel.language.mvel.MvelExpression.mvel

import static org.mockito.Mockito.*

import org.junit.Test

/**
 * Created by cmahnke on 09.06.15.
 */
class ConcatAggregatingStrategyTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint


    @Test
    public void testMessageCount() {
        //We've got 6 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(500)
        resultEndpoint.setResultWaitTime(20000)
        //Only one result expected
        resultEndpoint.expectedMessageCount(6)
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

    @Test
    public void testEl () {
        Expression mvel = mvel('request.headers.CamelFileName.replaceAll("^(\\\\w*?)_.*$","$1")')

        //Mock exchange.in.headers.get('CamelFileName')
        Exchange ex = mock(Exchange.class)
        Message m = new DefaultMessage()

        m.setHeader('CamelFileName', 'sub100_2014060313043579_slip001.print')

        when(ex.getIn()).thenReturn(m)
        when(ex.getContext()).thenReturn(this.context())

        String result = mvel.evaluate(ex, String.class)
        log.info('Result of expression is ' + result)
        assertTrue(result == 'sub100')
    }


    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:./target/generated-test-resources/hotfolder/lbs3/in?include=.*.print&noop=true&charset=Cp850")
                        /* convert body to UTF-8
                        <convertBodyTo type="java.lang.String" charset="UTF-8"/>
                         */
                        /* first set a header
                        <setHeader headerName="printQueue">
                            <mvel>request.headers.CamelFileName.replaceAll("^(\\w*?)_.*$","$1")</mvel>
                        </setHeader>
                        */
                        .setHeader('printQueue', mvel('request.headers.CamelFileName.replaceAll("^(\\\\w*?)_.*$","$1")'))
                        /*
                        aggregate using this header, set the startegyRef using a bean binding via Spring or in the Main method, see the print server
                        <aggregate strategyRef="aggregatorStrategy" completionTimeout="20000" forceCompletionOnStop="true">
                            <correlationExpression>
                                <simple>header.printQueue</simple>
                            </correlationExpression>
                            ...
                        </aggregate>
                        */
                        .aggregate(simple('header.printQueue'), new ConcatAggregatingStrategy())
                        .completionTimeout(5000L)
                        .to("plainText:.&pageSize=A5")
                        .to("fop:application/pdf")
                        .to('file:./target/?fileName=${header.printQueue}-' + this.class.getName() + '-plain-lbs4.pdf')
                        .to("mock:result")
            }
        };
    }
}
