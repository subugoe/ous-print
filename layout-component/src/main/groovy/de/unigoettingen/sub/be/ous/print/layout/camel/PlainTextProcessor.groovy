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

import de.unigoettingen.sub.be.ous.print.layout.Layout
import de.unigoettingen.sub.be.ous.print.layout.Layout.PageSize

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.Exchange
import org.apache.camel.Processor

import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang.StringEscapeUtils
/**
 * Converts a plain text file into XSL-FO
 * See http://techdiary.bitourea.com/2014/05/using-camel-fop-to-convert-text.html
 * @author cmahnke
 */

@Log4j
@TypeChecked
class PlainTextProcessor implements Processor  {
    /** The path to the XSL-FO template */
    def static URL XSL_FO_TEMPLATE = PlainTextProcessor.getClass().getResource('/xslfo/plaintext.fo')
    

    /** Th page size
    def PageSize pageSize = Layout.DEFAULT_PAGE_SIZE
    
    enum Dimensions {
        HEIGHT('height'), WIDTH('width'), MARGIN('margin'), ORIENTATION('orientation')
    }
        
        /** Page dimensions */
        /*
         * <!-- A5 landscape -->
         * <format name="A5" width="210mm" height="148mm" margin="13mm" orientation="LANDSCAPE"/>
         * <!-- A4 portrait -->
         * <format name="A4" width="210mm" height="297mm" margin="13mm" orientation="PORTRAIT"/>
         */
        static Map<PageSize, Map<String, String>> dimensions = [:]
        static {
            dimensions.put(PageSize.A4, ['height':'297mm', 'width': '210mm', 'margin': '13mm', 'orientation': 'PORTRAIT'])
            dimensions.put(PageSize.A5, ['height':'148mm', 'width': '210mm', 'margin': '13mm', 'orientation': 'LANDSCAPE'])
        }
    
    
    /**
     * Creates a PlainTextProcessor
     */
    PlainTextProcessor(String pageSize) {
        this.pageSize = PageSize.fromString(pageSize)
        if (PageSize.fromString(pageSize) != null) {
            this.pageSize = PageSize.fromString(pageSize)
            log.warn("Page size set to to ${Layout.DEFAULT_PAGE_SIZE}")
        }
    }
    
    /**
     * Creates a PlainTextProcessor
     */
    PlainTextProcessor() {
        log.warn("Page size not given! defaulting to ${Layout.DEFAULT_PAGE_SIZE}")
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
        exchange.getOut().setBody(xslFoBody)
        log.trace('Copying headers')
        exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders())
        //Setting our header
        exchange.getOut().setHeader(Exchange.FILE_NAME, fileName + ".fo")
    }
    
    /**
     * Creates a XSL-FO String from the template file and the given String
     * @return String the resulting XSL-FO
     */
    protected String getXslFo(String text) {
        text = StringEscapeUtils.escapeXml(text)
        String height = dimensions.get(pageSize).get('height')
        String width = dimensions.get(pageSize).get('width')
        String masterName = pageSize.name()
        String margin = dimensions.get(pageSize).get('margin')
        
        log.trace("Setting XSLT Parameters for ${masterName}, width: ${width}, height ${height}")
        String fopTemplate = XSL_FO_TEMPLATE.getText('UTF-8')
        fopTemplate = fopTemplate.replace('${width}', width)
        fopTemplate = fopTemplate.replace('${height}', height)
        fopTemplate = fopTemplate.replace('${master-name}', masterName)
        fopTemplate = fopTemplate.replace('${margin}', margin)
        return fopTemplate.replace('${content}', text)
    }
}

