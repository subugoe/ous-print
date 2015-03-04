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

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.ResolveEndpointFailedException
import org.apache.camel.impl.UriEndpointComponent
import org.apache.camel.spi.UriParam

import javax.sql.DataSource

/**
 * Created by cmahnke on 04.03.15.
 */
@Log4j
@TypeChecked
class Db2AscComponent extends UriEndpointComponent {
    @UriParam
    String table

    @UriParam
    String column

    @UriParam
    String lang

    @UriParam
    Integer slip

    @UriParam
    Integer iln

    @UriParam
    DataSource dataSource


    /**
     * Public constructor of the Db2AscComponent
     */
    public Db2AscComponent () {
        super(Db2AscEndpoint.class)
    }

    /**
     * Public constructor of the Db2AscComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(org.apache.camel.CamelContext, Class<? extends Endpoint>)
     */
    public Db2AscComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
        super(context, Db2AscEndpoint.class)
    }

    /**
     * Public constructor of the Db2AscComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(Class<? extends Endpoint>)
     */
    public Db2AscComponent(Class<? extends Endpoint> endpointClass) {
        super(Db2AscEndpoint.class)
    }

    /**
     * @see org.apache.camel.impl.DefaultComponent#createEndpoint(String, String, Map<String,Object>)
     * @throws Exception
     */
    @Override
    protected Endpoint createEndpoint(String uri, final String remaining, Map<String, Object> parameters) throws Exception {
        log.trace('Setup Db2AscProcessor')
        if (parameters.get("lang") != null) {
            lang = ((String) parameters.get("lang")).toUpperCase()
            log.trace("Lang set to ${lang}")
        }

        if (parameters.get("iln") != null) {
            iln = Integer.parseInt((String) parameters.get("iln"))
            log.trace("ILN set to ${iln}")
        }

        if (parameters.get("slip") != null) {
            slip = Integer.parseInt((String) parameters.get("iln"))
            log.trace("Slip set to ${slip}")
        }

        //Database settings

        if (parameters.get("table") != null) {
            table = ((String) parameters.get("table"))
            log.trace("Table set to ${table}")
        }
        if (parameters.get("column") != null) {
            column = ((String) parameters.get("column"))
            log.trace("Column set to ${column}")
        }

        if (parameters.get("dataSource") != null) {
            String dataSourceName = ((String) parameters.get("dataSource"))
            dataSource = resolveAndRemoveReferenceParameter(parameters, "dataSource", DataSource.class)
            if (dataSource == null) {
                throw new ResolveEndpointFailedException('dataSource couldn\'t be resolved')
            }
            log.trace("dataSource set to ${dataSourceName}")
        }
        Db2AscProcessor d2ap
        if (table == null && column == null) {
            d2ap = new Db2AscProcessor(iln, slip, lang, dataSource)
        } else {
            d2ap = new Db2AscProcessor(iln, slip, lang, table, column, dataSource)
        }
        log.trace('Returning configured Db2AscEndpoint')
        return new Db2AscEndpoint(uri, this, d2ap)
    }

    /**
     * @see org.apache.camel.impl.DefaultComponent#validateURI(String, String, Map<String,Object>)
     */
    @Override
    protected void validateURI(String uri, String path, Map<String,Object> parameters) {
        if (!parameters.get('iln')) {
            log.error('Endpoint not correctly configured: iln')
            throw new ResolveEndpointFailedException('iln not set')
        }
        if (!parameters.get('slip')) {
            log.error('Endpoint not correctly configured: slip')
            throw new ResolveEndpointFailedException('slip not set')
        }
        if (!parameters.get('lang')) {
            log.error('Endpoint not correctly configured: lang')
            throw new ResolveEndpointFailedException('lang not set')
        }
        if (!parameters.get('dataSource')) {
            log.warn('Endpoint probably not correctly configured, no dataSource given!')
        }

    }

    /**
     * @see org.apache.camel.impl.DefaultComponent#validateParameters(String, Map<String,Object>, String)
     */
    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {

    }

}
