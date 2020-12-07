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

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import org.apache.camel.AsyncCallback
import org.apache.camel.CamelExchangeException
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultConsumer
import org.apache.camel.impl.ScheduledBatchPollingConsumer

/**
 * Created by cmahnke on 04.03.15.
 */
@CompileStatic
class Db2AscConsumer extends DefaultConsumer {
    private final Db2AscEndpoint d2ae

    /*
    public Db2AscConsumer(Db2AscEndpoint d2ae) {
        super(d2ae)
        this.d2ae = d2ae
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        if (d2ae.getConsumer() == null) {
            throw new CamelExchangeException("No consumers available on endpoint: " + endpoint, exchange);
        } else {
            d2ae.getConsumer().getProcessor().process(exchange);
        }

    }
*/

}
