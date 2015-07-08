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
import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.impl.UriEndpointComponent
import org.apache.camel.spi.UriParam

@Log4j
@TypeChecked
class AttachmentComponent extends UriEndpointComponent {
    @UriParam
    /** The desired page size of the result */
    String mimetype = null

    /**
     * Public constructor of the AttachmentComponent
     */
    public AttachmentComponent() {
        super(AttachmentEndpoint.class)
    }

    /**
     * Public constructor of the AttachmentComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(org.apache.camel.CamelContext, Class < ? extends Endpoint >)
     */
    public AttachmentComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
        super(context, AttachmentEndpoint.class)
    }

    /**
     * Public constructor of the AttachmentComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(Class < ? extends Endpoint >)
     */
    public AttachmentComponent(Class<? extends Endpoint> endpointClass) {
        super(AttachmentEndpoint.class)
    }

    /**
     * @see org.apache.camel.impl.DefaultComponent#createEndpoint(String, String, Map < String, Object >)
     * @throws Exception
     */
    @Override
    protected Endpoint createEndpoint(String uri,
                                      final String remaining, Map<String, Object> parameters) throws Exception {
        log.trace('Setup AttachmentProcessor')
        AttachmentProcessor ptp
        String mimetype = (String) parameters.get("mimetype")
        if (mimetype == null) {
            ptp = new AttachmentProcessor()
        } else {
            log.trace("Mimetype is ${mimetype}")
            ptp = new AttachmentProcessor()
        }
        return new AttachmentEndpoint(uri, this, ptp)
    }

    /**
     * @see org.apache.camel.impl.DefaultComponent#validateURI(String, String, Map < String, Object >)
     */
    @Override
    protected void validateURI(String uri, String path, Map<String, Object> parameters) {

    }

    /**
     * @see org.apache.camel.impl.DefaultComponent#validateParameters(String, Map < String, Object >, String)
     */
    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
        if (parameters.get("mimetype") == null) {
            log.warn('Mimetype not set, using Tika for detection!')
        }
    }
}
