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

package de.unigoettingen.sub.be.ous.db

import de.unigoettingen.sub.be.ous.models.asc.Layout
import de.unigoettingen.sub.be.ous.models.asc.PositionLine
import de.unigoettingen.sub.be.ous.models.asc.LanguageType
import de.unigoettingen.sub.be.ous.models.asc.TypeType

import groovy.sql.GroovyResultSet
import groovy.sql.Sql
import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import java.sql.Blob
import java.sql.SQLException
import javax.sql.DataSource

import org.apache.commons.io.IOUtils

/**
 *
 * @author cmahnke
 */
@Log4j
class Db2Asc {
    /** The database table */ 
    def static String DB_TABLE = 'lbs_report_layout'
    
    /** The column containing the layout definition */
    def static String DB_COLUMN = 'content'
    
    def Sql database
    
    def Integer number, iln
    
    def String language
    
    def Layout layout
    
    Db2Asc (DataSource ds, Integer number, Integer iln, String language) {
        this(new Sql(ds), number, iln, language)
    }
    
    Db2Asc (Sql database, Integer number, Integer iln, String language) {
        this.database = database
        this.iln = iln
        this.language = language
    }
    
        @TypeChecked
    void extract () {
        layout = new Layout()
        layout.setIln(String.format('%03d', iln))
        layout.setLayoutFile(String.format('%03d', number))
        layout.setLanguage(LanguageType.valueOf(language))
        parse(query())
    }
    
    protected InputStream query () {
        database.eachRow("select ${DB_COLUMN} from ${DB_TABLE} where number = ? and iln = ? and language_code = ?", [number, iln, language]) { GroovyResultSet it ->
            /*
            if (it.getBlob(DB_COLUMN) != null) {
                return it.getBlob(DB_COLUMN).getBinaryStream()
            */
            if (it.content instanceof Blob) {
                return ((Blob) it.content).getBinaryStream()
            } else {
                throw new SQLException('No matching layout found!')
            }
        }
    }
    
    protected void parse (InputStream is) {
        log.info("Got a InputStream with ${is.available()} Bytes")
        /* Line definition
        Byte 0: Row / Line (LIN)
        Byte 1: column (COL)
        Byte 2: entry (ENT)
        Byte 3: attribute (ATT)
        Byte 4: sequence (SEQ)
        Byte 5: type (T) (41hex = text, 45hex = date)
        Byte 6: Length of field in hex
        Byte 7: Length of follwing Text
         */
        int data = 0
        //This is probably the length
        byte[] header = new byte[8]
        data = is.read(header)
        /*
        int h1 = header[0] + header[1] + header[2] + header[3]
        int h2 = header[4] + header[5] + header[6] + header[7]
        println ';h1: ' + h1 + ' h2: ' + h2 
        */
        byte[] bytes = new byte[8]
    
        
        while(data != -1){
            data = is.read(bytes)
            List<String> l = new ArrayList<String>(bytes.length - 1)
            for (i in 0..bytes.length - 2) {
                //Byte s = Arrays.asList(bytes).get(i) & 0xff
                l.add(i, String.format('%03d', bytes[i] & 0xff))
            }
            PositionLine ps = new PositionLine()
            ps.setLine(l.get(0))
            ps.setColumn(l.get(1))
            ps.setEntity(l.get(2))
            ps.setAttribute(l.get(3))
            ps.setSequence(l.get(4))
            

            if (l.get(5) == '065') {
                ps.setType(TypeType.valueOf('T'))
            } else if (l.get(5) == '068') {
                ps.setType(TypeType.valueOf('A'))
            } else if (l.get(5) == '073') {
                ps.setType(TypeType.valueOf('I'))
            } else {
                throw new IllegalStateException("Unknown type: " + l.get(5))
            }
            ps.setLength(l.get(6))


            byte[] text = new byte[bytes[7] & 0xff]
            is.read(text)
            ps.setText(IOUtils.toString(text, 'ISO-8859-15'))
            layout.getLine().add(ps)
            //TODO: This isn't correct yet
            if (is.available() == 8) {
                break
            }
        }
    }
    
    Layout getLayout () {
        return layout
    }
    
}

