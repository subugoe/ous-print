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


import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor

import org.apache.tika.detect.DefaultDetector
import org.apache.tika.detect.Detector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MimeTypes

import javax.activation.DataHandler
import javax.mail.util.ByteArrayDataSource

/**
 * Created by cmahnke on 08.07.15.
 */
@Log4j
@CompileStatic
class AttachmentProcessor implements Processor {
    protected String mimetype = null
    private static final Detector DETECTOR = new DefaultDetector(MimeTypes.getDefaultMimeTypes());

    public AttachmentProcessor(String mimetype) {
        this.mimetype = mimetype
    }

    public AttachmentProcessor() {

    }

    @Override
    public void process(Exchange exchange) {
        Message m = exchange.getIn()
        byte[] file = m.getBody(byte[].class)
        String fileId = m.getHeader("CamelFileName", String.class)
        if (mimetype == null) {
            //mimetype = Layout.guessContentType(new File(m.getHeader("CamelFileAbsolutePath", String.class)).toURL())
            TikaInputStream tis = null
            try {
                tis = TikaInputStream.get(m.getBody(InputStream.class))
                final Metadata metadata = new Metadata()
                mimetype = DETECTOR.detect(tis, metadata).toString()
            } finally {
                if (tis != null) {
                    tis.close()
                }
            }
        }
        m.addAttachment(fileId, new DataHandler(new ByteArrayDataSource(file, mimetype)))
    }
}
