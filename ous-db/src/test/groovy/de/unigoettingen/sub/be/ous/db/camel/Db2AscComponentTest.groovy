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

package de.unigoettingen.sub.be.ous.db.camel

import de.unigoettingen.sub.be.ous.db.Db2AscTest

import groovy.transform.TypeChecked

import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.JndiRegistry
import org.apache.camel.test.junit4.CamelTestSupport

import org.junit.Test

import javax.sql.DataSource

/**
 * Created by cmahnke on 04.03.15.
 */
class Db2AscComponentTest extends CamelTestSupport {

    DataSource ds

    @EndpointInject(uri = 'mock:result')
    protected MockEndpoint resultEndpoint;

    @TypeChecked
    @Override
    protected JndiRegistry createRegistry() throws Exception {
        log.info('Setting up DataSource')
        ds = Db2AscTest.getDataSource()
        //Add DataSource to context
        JndiRegistry jndi = super.createRegistry()
        jndi.bind("jdbc/testDataSource", ds)
        return jndi
    }

    @Test
    @TypeChecked
    void testAsc2Db() {
        resultEndpoint.setMinimumResultWaitTime(300)
        resultEndpoint.setResultWaitTime(120000)
        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("timer:database?fixedRate=true&period=60000")
                        .to("db2asc:.?slip=1&iln=40&lang=de&dataSource=#jdbc/testDataSource")
                        .to("file:./target/test-classes/ous40_layout_001_du.asc")
                        .to('mock:result')
            }
        })
        resultEndpoint.expectedMessageCount(1)
        assertMockEndpointsSatisfied()
    }
}
