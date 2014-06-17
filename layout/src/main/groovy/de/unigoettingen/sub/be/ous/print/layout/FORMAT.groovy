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

package de.unigoettingen.sub.be.ous.print.layout

import groovy.transform.TypeChecked
import groovy.transform.CompileStatic

/**
 * This Enum is used to parse the supported input and output formats
 * @author cmahnke
 */
@CompileStatic
@TypeChecked
enum FORMAT {
    ASC('ASC'), ASCXML('ASCXML'), XML('XML'),/* ODF('ODF'),*/ PDF('PDF'), PS('PS'), XSLFO('XSLFO'), XSL('XSL'), TEXT('TXT'), UNKNOWN('UNKNOWN')
    
    /** The name of this FORMAT */
    String name
        
    FORMAT(String name) { 
        this.name = name 
    }
        
    /**
     * Get a FORMAT for a given String
     * @returns FORMAT the format or null
     */ 
    public static FORMAT fromString(String format) {
        if (format != null) {
            for (FORMAT f : FORMAT.values()) {
                if (format.equalsIgnoreCase(f.name)) {
                    return f
                }
            }
        }
        return FORMAT.UNKNOWN
    }

    /**
     * Get a String representations of all formats
     * @returns String the formats
     */ 
    public static String getFormats() {
        def formats = ""
        for (FORMAT f : values()) {
            formats + f.name + " "
        }
        return formats
    }
        
}

