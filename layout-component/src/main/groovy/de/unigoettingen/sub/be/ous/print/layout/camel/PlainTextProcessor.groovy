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

import de.unigoettingen.sub.be.ous.print.layout.Layout.PageSize

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.Exchange
import org.apache.camel.Processor

import org.apache.commons.io.FilenameUtils

/**
 * Converts a plain text file into XSL-FO
 * See http://techdiary.bitourea.com/2014/05/using-camel-fop-to-convert-text.html
 * @author cmahnke
 */

@Log4j
@TypeChecked
//@CompileStatic
class PlainTextProcessor implements Processor  {
    /** The path to the XSL-FO template */
    def static URL XSL_FO_TEMPLATE = PlainTextProcessor.getClass().getResource('/xslfo/plaintext.fo')
    
    def PageSize pageSize
    
    /**
     * Creates a PlainTextProcessor
     */
    PlainTextProcessor(PageSize pageSize = PageSize.A4) {
        this.pageSize = pageSize
    }
    
    /**
     * Processes the contents of the given {@link org.apache.camel.Exchange Exchange}
     * @see {@link org.apache.camel.Processor#process(org.apache.camel.Exchange)}
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getIn().getBody(String.class)
        String fileName = FilenameUtils.getBaseName((String) exchange.getIn().getHeader(Exchange.FILE_NAME))
        String xslFoBody = getXslFo(body)
        exchange.getIn().setBody(xslFoBody)
        exchange.getIn().setHeader(Exchange.FILE_NAME, fileName + ".fo")
    }
    
    /**
     * Creates a XSL-FO String from the template file and the given String
     * @return String the resulting XSL-FO
     */
    //TODO: Include height and width here as well to simulate different paper sizes
    protected String getXslFo(String text) {
        Map<String, String> dimensions = PageSize.getDimension(pageSize)
        String fopTemplate = XSL_FO_TEMPLATE.getText('UTF-8')
        fopTemplate = fopTemplate.replace('{$width}', dimensions.get('width'))
        fopTemplate = fopTemplate.replace('{$height}', dimensions.get('hight'))
        fopTemplate = fopTemplate.replace('{$master-name}', dimensions.get('master-name'))
        fopTemplate = fopTemplate.replace('{$margin}', dimensions.get('margin'))
        return fopTemplate.replace('{$content}', text)
    }
}

