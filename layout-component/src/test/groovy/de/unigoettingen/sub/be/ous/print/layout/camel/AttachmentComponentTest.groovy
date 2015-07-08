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

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.test.junit4.CamelTestSupport

import org.junit.After
import org.junit.Test

import org.jvnet.mock_javamail.Mailbox

import static org.apache.camel.language.mvel.MvelExpression.mvel

@TypeChecked
class AttachmentComponentTest extends CamelTestSupport {
    static String ADDRESS = 'lbs-team@sub.uni-goettingen.de'

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint


    @Test
    public void testMessageCount() {
        Mailbox.clearAll()
        //We've got 6 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(500)
        resultEndpoint.setResultWaitTime(40000)
        resultEndpoint.expectedMessageCount(6)
        assertMockEndpointsSatisfied()
        assertTrue(assertMailCount(ADDRESS, 6))
    }

    static boolean assertMail(String address) {
        Mailbox mailbox = Mailbox.get(address)
        if (mailbox.size() > 0 && mailbox.get(0) != null) {
            return true
        }
        return false
    }

    static boolean assertMailCount(String address, int count) {
        Mailbox mailbox = Mailbox.get(address)
        if (mailbox.size() == count) {
            return true
        }
        return false
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
                //See ConcatAggregatingStrategyTest for more documentation

                from("file:./target/generated-test-resources/hotfolder/lbs3/in?include=.*.print&noop=true&charset=Cp850")
                        .setHeader('printQueue', mvel('request.headers.CamelFileName.replaceAll("^(\\\\w*?)_.*$","$1")'))
                        .aggregate(simple('header.printQueue'), new ConcatAggregatingStrategy())
                        .completionTimeout(5000L)
                        .to("plainText:.&pageSize=A5")
                        .to("fop:application/pdf")
                        .to('file:./target/?fileName=${header.printQueue}-' + this.class.getName() + '-plain-lbs4.pdf')
                        .to('attachment:.&mimetype=application/pdf')
                        .to('smtp://localhost?to=' + ADDRESS)
                        .to("mock:result")
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        Mailbox.clearAll()
    }
}
