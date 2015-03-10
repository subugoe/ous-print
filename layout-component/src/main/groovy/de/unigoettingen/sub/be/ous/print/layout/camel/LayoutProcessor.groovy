/*
 * This file is part of the OUS Print Server, Copyright 2011, 2012 SUB Göttingen
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

import de.unigoettingen.sub.be.ous.print.layout.FORMAT
import de.unigoettingen.sub.be.ous.print.layout.Layout

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.camel.Property
import org.apache.camel.builder.xml.ResultHandlerFactory
import org.apache.camel.builder.xml.StringResultHandlerFactory

/**
 * This class implements the Processor interface to provide the capeabilities of
 * the layout engine to Apache Camel.
 * @author cmahnke
 */
@Log4j
@TypeChecked
class LayoutProcessor implements Processor {
    
    /** A path to write generated results to */
    URL debugPath = null
    
    /** The URLs of the template, the XSL-FO and the include path */
    URL template, xslfo, includePath = null
    
    /** The FORMAT to be read */
    FORMAT inputFormat = FORMAT.TEXT
    
    /** The FORMAT to write */
    FORMAT outputFormat = FORMAT.PDF
    
    /** The requested page size */
    String pageSize = null

    /** The required encoding */
    String encoding

    /**
     * Creates a LayoutProcessor
     */
    LayoutProcessor () {
      
    }
    
    /**
     * Creates a LayoutProcessor
     * @see {@link de.unigoettingen.sub.be.ous.print.layout.Layout} 
     */
    LayoutProcessor (FORMAT inputFormat, FORMAT outputFormat, URL xslfo, URL includePath, URL template, String pageSize, String encoding) {
        this()
        this.inputFormat = inputFormat
        this.outputFormat = outputFormat
        this.xslfo = xslfo
        this.includePath = includePath
        this.template = template
        this.pageSize = pageSize
        this.encoding = encoding
    }
    
    /**
     * Processes the contents of the given {@link org.apache.camel.Exchange Exchange}
     * @see {@link org.apache.camel.Processor#process(org.apache.camel.Exchange)}
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        Message input = exchange.getIn()
        log.info('Got request: ' + input.getMessageId())
        //Prepare Streams
        ByteArrayInputStream body = new ByteArrayInputStream(exchange.getIn().getBody(String.class).getBytes())
        OutputStream bos = new ByteArrayOutputStream()
        Layout l
        File temp = null
        String inputName = input.getHeader('CamelFileAbsolutePath', String.class)
        //Check if we get a body or just a file name
        if (body != null) {
            l = new Layout(inputFormat, body, outputFormat, bos, xslfo, includePath, template, System.getProperty('file.encoding'))
        } else {
            URL inputURL

            String absolutePath = input.getHeader('CamelFileAbsolutePath', String.class)

            if (absolutePath) {
                inputURL = new File(absolutePath).toURI().toURL()
            } else {
                temp = File.createTempFile("temp", ".txt");
                temp.deleteOnExit();
                new FileOutputStream(temp).write(input.getBody(String.class).getBytes())
                inputURL = temp.toURI().toURL()
            }
            inputName = inputURL.toString()
            log.trace('Using URL: ' + inputURL.toString())
            l = new Layout(inputFormat, inputURL.openStream(), outputFormat, bos, xslfo, includePath, template, encoding)
        }


        //Message input = exchange.getIn()
        //log.info('Got request: ' + input.getMessageId())
        //URL inputURL
        /*
        File temp = null
        String absolutePath = input.getHeader('CamelFileAbsolutePath', String.class)
        
        if (absolutePath) {
            inputURL = new File(absolutePath).toURI().toURL()
        } else {
            temp = File.createTempFile("temp", ".txt");
            temp.deleteOnExit();
            new FileOutputStream(temp).write(input.getBody(String.class).getBytes())
            inputURL = temp.toURI().toURL()
        }
        log.trace('Using URL: ' + inputURL.toString())
        
        //OutputStream bos = new ByteArrayOutputStream()
        Layout l = new Layout(inputFormat, inputURL.openStream(), outputFormat, bos, xslfo, includePath, template)
        */
        if (pageSize != null) {
            l.setPageSize(Layout.PageSize.fromString(pageSize))
            log.trace('Set page size ' + pageSize)
        }
        log.trace("Setup Layouter for input ${inputName} using Template " + template.toString() + ' include path ' + includePath.toString() + ' and XSL-FO ' + xslfo.toString())
        l.layout()
        
        if (debugPath != null && new File(debugPath.toURI()).exists()) {
            def fileName = debugPath.toString() + File.separator + inputName.replaceAll('^.*/([\\w\\.^/]*?)$', '$1') + '.pdf'
            log.trace('Writing debug output to ' + fileName)
            File f = new File(new URI(fileName))
            f.createNewFile()
            OutputStream outputStream = new FileOutputStream(f); 
            bos.writeTo(outputStream);
        }
        log.trace("Setting print job name to ${inputName}")
        exchange.getOut().setHeader('PrinterJobName', inputName)
        
        if (outputFormat == FORMAT.PDF) {
            exchange.getOut().setHeader('Content-Type', 'application/pdf')
        } else if (outputFormat == FORMAT.XSL || outputFormat == FORMAT.XSLFO) {
            exchange.getOut().setHeader('Content-Type', 'text/plain')
        } else if (outputFormat == FORMAT.PS) {
            exchange.getOut().setHeader('Content-Type', 'application/postscript')
        }
        log.trace('Content-Type set to ' + exchange.getOut().getHeader('Content-Type', String.class))
        
        if (bos.size() == 0) {
            log.error('Result of layouter is 0 Bytes long')
            throw new RuntimeException('Result is empty')
        }
        
        InputStream is = new ByteArrayInputStream(((ByteArrayOutputStream) bos).toByteArray())
        exchange.getOut().setBody(is)
        
        // propagate headers
        log.trace('Copying headers')
        exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders())
        
        if (temp != null) {
            temp.delete()
            log.trace('Deleted temporary file')
        }
    }

}

