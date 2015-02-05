/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.unigoettingen.sub.be.ous.print.layout.camel

import de.unigoettingen.sub.be.ous.print.layout.FORMAT
import de.unigoettingen.sub.be.ous.print.layout.Layout

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.CamelContext
import org.apache.camel.Endpoint
import org.apache.camel.ResolveEndpointFailedException
import org.apache.camel.impl.UriEndpointComponent
import org.apache.camel.spi.UriParam

/**
 *
 * @author cmahnke
 */
@Log4j
@TypeChecked
class PlainTextComponent extends UriEndpointComponent {
    
    @UriParam
    /** The desired page size of the result */
    String pageSize = Layout.DEFAULT_PAGE_SIZE
    
	    /**
     * Public constructor of the PlainTextComponent
     */
    public PlainTextComponent () {
        super(PlainTextEndpoint.class)
    }
    
        /**
     * Public constructor of the LayoutComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(CamelContext, Class<? extends Endpoint>)
     */
    public PlainTextComponent(CamelContext context, Class<? extends Endpoint> endpointClass) {
        super(context, PlainTextEndpoint.class)
    }
       
    /**
     * Public constructor of the LayoutComponent
     * @see org.apache.camel.impl.UriEndpointComponent#UriEndpointComponent(Class<? extends Endpoint>)
     */
    public PlainTextComponent(Class<? extends Endpoint> endpointClass) {
        super(PlainTextEndpoint.class)
    }
    
    /**
     * @see org.apache.camel.impl.DefaultComponent#createEndpoint(String, String, Map<String,Object>)
     * @throws Exception
     */
    @Override
    protected Endpoint createEndpoint(String uri, final String remaining, Map<String, Object> parameters) throws Exception {
        log.trace('Setup PlainTextProcessor')
        PlainTextProcessor ptp = new PlainTextProcessor()
        return new PlainTextEndpoint(uri, this, ptp)
    }
    
     /**
     * @see org.apache.camel.impl.DefaultComponent#validateURI(String, String, Map<String,Object>)
     */
    @Override
    protected void validateURI(String uri, String path, Map<String,Object> parameters) {
        
    }
    
    /**
     * @see org.apache.camel.impl.DefaultComponent#validateParameters(String, Map<String,Object>, String)
     */
    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
        //TODO: check for valid pageSize
        if (parameters.get("pageSize") != null) {
            /*
            File f = Layout.getFile((String) parameters.get("includePath"))
            if (!f.exists()) {
                log.trace('Debug path ' + f.getAbsolutePath()  + 'couldn\'t be found')
                throw new ResolveEndpointFailedException('debug path doesn\'t exist')
            }
            */
        }
    }
    
}

