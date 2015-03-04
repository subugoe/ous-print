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

import de.unigoettingen.sub.be.ous.db.Db2Asc
import de.unigoettingen.sub.be.ous.models.asc.AscSerializer
import groovy.transform.TypeChecked
import groovy.util.logging.Log4j
import org.apache.camel.Exchange
import org.apache.camel.Processor

import javax.sql.DataSource

/**
 * Created by cmahnke on 04.03.15.
 */
@Log4j
@TypeChecked
class Db2AscProcessor implements Processor {
    Integer iln
    Integer number
    String language
    //Database settings
    DataSource ds
    String table
    String column

    /**
     * Creates a Db2AscProcessor
     */
    Db2AscProcessor(Integer iln, Integer number, String language, DataSource ds) {
        this(iln, number, language, Db2Asc.DB_TABLE, Db2Asc.DB_COLUMN, ds)
    }

    /**
     * Creates a Db2AscProcessor
     */
    Db2AscProcessor(Integer iln, Integer number, String language, String table, String column, DataSource ds) {
        log.warn("Creating new Db2AscProcessor")
        this.iln = iln
        this.number = number
        this.language = language
        this.table = table
        this.column = column
        this.ds = ds
    }

    /**
     * Processes the contents of the given {@link org.apache.camel.Exchange Exchange}
     * @see {@link org.apache.camel.Processor#process(org.apache.camel.Exchange)}
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.trace('Processing Exchange')
        ByteArrayInputStream bis = exchange.getIn(ByteArrayInputStream.class)
        Db2Asc d2a
        if (bis != null && bis.available() > 0) {
            log.trace('Parsing ByteArrayInputStream, should be content of a Blob')
            d2a = new Db2Asc(iln, number, language)
            d2a.parse(bis)
        } else {
            log.trace('Using direct database access')
            d2a = new Db2Asc(ds.getConnection(), number, iln, language)
            d2a.table = table
            d2a.column = column
            d2a.extract()
        }
        AscSerializer asr = new AscSerializer(d2a.getLayout())
        exchange.getOut()setBody(asr.serialze())
    }
}
