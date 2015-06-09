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

import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy

/**
 * A AggregationStratagy that joins the Bodies of given Exchanges
 */
class ConcatAggregatingStrategy implements AggregationStrategy {
    String joinStr = ""

    ConcatAggregatingStrategy (String str) {
        if (str != null) {
            joinStr = str
        }
    }

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange
        }

        String oldBody = oldExchange.getIn().getBody(String.class)
        String newBody = newExchange.getIn().getBody(String.class)
        newExchange.getIn().setBody(oldBody + joinStr + newBody)
        return newExchange
    }
}
