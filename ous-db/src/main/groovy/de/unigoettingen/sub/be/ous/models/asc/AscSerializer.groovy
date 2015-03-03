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

package de.unigoettingen.sub.be.ous.models.asc

import de.unigoettingen.sub.be.ous.models.asc.CommentLine
import de.unigoettingen.sub.be.ous.models.asc.Layout
import de.unigoettingen.sub.be.ous.models.asc.PositionLine

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 *
 * @author cmahnke
 */
@TypeChecked
//@CompileStatic
class AscSerializer extends Layout {
    def Layout layout = null
    
    AscSerializer (Layout layout) {
        this.layout = layout
    }
    
    public String serialze () {
        String asc
        if (layout == null) {
            throw new IllegalStateException('No Layout given!') 
        }
        asc = "\$ILN=${layout.getIln()}\n"
        asc <<= "\$LAYOUT=${layout.getLayoutFile()}\n"
        asc <<= "\$LANGUAGE=${layout.getLanguage()}\n"
        asc <<= ";lin kol ent att seq t len text\n"

        
        for (line in layout.getLine()) {
            if (line instanceof CommentLine) {
                asc <<= ";${((CommentLine) line).getText()}"
            } else if (line instanceof PositionLine) {
                asc <<= "${((PositionLine) line).getLine()} ${((PositionLine) line).getColumn()} ${((PositionLine) line).getEntity()} ${((PositionLine) line).getAttribute()} ${((PositionLine) line).getSequence()} ${((PositionLine) line).getType()} ${((PositionLine) line).getLength()} ${((PositionLine) line).getText()}"
            } else {
                throw new UnsupportedOperationException('Text layouts are not supported yet')
            }
        } 
        
        return asc
    }
    
}

