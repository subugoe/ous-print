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

package de.unigoettingen.sub.be.ous.print.util

import groovy.util.logging.Log4j
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

import javax.xml.transform.TransformerException
import javax.xml.transform.ErrorListener


/**
 * This Helper class is used to redirect output of an XSLT Processor to the logger
 * @author cmahnke
 */
@Log4j
@CompileStatic
class LogErrorListener implements ErrorListener {
    def fatal = false
        
    /**
     * @see ErrorListener#error(TransformerException)
     */
    void error(TransformerException exception) {
        log.error("XSLT Message: ", exception)
    }
    
    /**
     * @see ErrorListener#fatalError(TransformerException)
     */
    void fatalError(TransformerException exception) {
        this.fatal = true
        log.error("XSLT Message: ", exception)
    }
    
    /**
     * @see ErrorListener#warning(TransformerException)
     */
    void warning(TransformerException exception) {
        log.warn("XSLT Message: ", exception)
    }

}

