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
 * Created by cmahnke on 09.06.15.
 */
class ConcatAggregatingStrategyTest extends CamelTestSupport {
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
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:./target/generated-test-resources/hotfolder/lbs3/in?include=.*.print&noop=true&charset=Cp850")
                        .aggregate(constant('true'), new ConcatAggregatingStrategy('\n'))
                        .completionTimeout(500L)
                        .to("plainText:.&pageSize=A5")
                        .to("fop:application/pdf")
                        .to('file:./target/?fileName=${file:name}-' + this.class.getName() + '-plain-lbs4.pdf')
                        .to("mock:result");
            }
        };
    }
}
