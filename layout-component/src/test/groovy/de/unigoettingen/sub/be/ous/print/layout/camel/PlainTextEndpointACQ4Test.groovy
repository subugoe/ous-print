package de.unigoettingen.sub.be.ous.print.layout.camel

import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Test

/**
 * Created by cmahnke on 09.04.15.
 */
class PlainTextEndpointACQ4Test extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint

    @Test
    public void testMessageCount() {
        //We've got 1 Test files - This takes some time
        resultEndpoint.setMinimumResultWaitTime(500)
        resultEndpoint.setResultWaitTime(200000)
        resultEndpoint.expectedMessageCount(1)
        assertMockEndpointsSatisfied()
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:./target/generated-test-resources/hotfolder/acq4/in?include=.*.print&noop=true&charset=ISO-8859-1")
                        .to("plainText:.&pageSize=A5")
                        .to("fop:application/pdf")
                        .to('file:./target/?fileName=${file:name}-' + this.class.getName() + '-plain-lbs4.pdf')
                        .to("mock:result");
            }
        };
    }
}
