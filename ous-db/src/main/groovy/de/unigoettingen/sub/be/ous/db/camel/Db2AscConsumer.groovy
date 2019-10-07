/*
 * This file is part of the OUS Print Server, Copyright 2019 SUB Göttingen
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
import org.apache.camel.impl.DefaultConsumer
import org.apache.camel.impl.ProcessorEndpoint
import org.apache.commons.lang.NotImplementedException

@CompileStatic
class Db2AscConsumer extends DefaultConsumer {
    Db2AscConsumer (ProcessorEndpoint endpoint) {
        throw new NotImplementedException("Db2AscConsumer isn't implemented yet")
    }
}
