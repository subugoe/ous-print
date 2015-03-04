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

import groovy.sql.Sql
import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import java.sql.Blob
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import org.apache.commons.io.IOUtils

/**
 *
 * @author cmahnke
 */
@Log4j
class Db2Asc {
    /** The database table */
    static public String DB_TABLE = 'lbs_report_layout'

    /** The column containing the layout definition */
    static public String DB_COLUMN = 'content'

    Connection connection

    Integer number, iln

    String language

    Layout layout

    String table = DB_TABLE
    String column = DB_COLUMN

    /**
     * Creates a Db2Asc without database access, useful for parsing raw blobs
     * @param iln
     * @param number
     * @param language
     */

    Db2Asc(Integer iln, Integer number, String language) {
        this.iln = iln
        this.number = number
        this.language = language
    }

    Db2Asc(Sql database, Integer number, Integer iln, String language) {
        this(database.getConnection(), number, iln, language)
    }

    Db2Asc(Connection connection, Integer number, Integer iln, String language) {
        this.connection = connection
        this.number = number
        this.iln = iln
        this.language = language
    }

    @TypeChecked
    void extract() {
        layout = new Layout()
        layout.setIln(String.format('%03d', iln))
        layout.setLayoutFile(String.format('%03d', number))
        //Language code for german is broken
        if (language == 'DE') {
            layout.setLanguage(LanguageType.valueOf('DU'))
        } else {
            layout.setLanguage(LanguageType.valueOf(language))
        }
        ByteArrayInputStream is = query()
        parse(is)
    }

    public ByteArrayInputStream query() {
        if (connection == null) {
            throw new IllegalStateException('No database connection!')
        }
        String query = "select ${column} from ${table} where number = ? and iln = ? and language_code = ?"
        PreparedStatement ps = connection.prepareStatement(query)
        ps.setInt(1, number)
        ps.setInt(2, iln)
        ps.setString(3, language)

        ResultSet res = ps.executeQuery()
        if (!res.next()) {
            throw new IllegalStateException('Result set is empty')
        }
        //We need to copy the stream since it will be otherwise lost if the connection is closed, took some time to figure this out :(
        byte[] blob = IOUtils.toByteArray(res.getBlob('content').getBinaryStream())
        return new ByteArrayInputStream(blob)
    }

    /**
     * Parses a given database Blob
     * @param b
     */
    public void parse (Blob b) {
        byte[] blob = IOUtils.toByteArray(b.getBinaryStream())
        parse(ByteArrayInputStream(blob))
    }

    /**
     * Parses a given InputStream
     * @param is
     */
    public void parse(ByteArrayInputStream is) {
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


        while (data != -1) {
            data = is.read(bytes)
            List<String> l = new ArrayList<String>(bytes.length - 1)
            for (i in 0..bytes.length - 2) {
                l.add(i, String.format('%03d', bytes[i] & 0xff))
            }
            //Stop if there is trailing garbage
            //TODO: There are different form of Garbage, this doesn't work everytime yet
            if (l.get(0) == '032' && l.get(1) == '032' && l.get(2) == '032' && l.get(3) == '032' && l.get(4) == '032' && l.get(5) == '032') {
                break
            }
            PositionLine ps = new PositionLine()
            ps.setLine(l.get(0))
            ps.setColumn(l.get(1))
            ps.setEntity(l.get(2))
            ps.setAttribute(l.get(3))
            ps.setSequence(l.get(4))


            if (l.get(5) == '065') {
                //Fixed Text
                ps.setType(TypeType.valueOf('A'))
                log.trace('Type of entry: A (65) ')
            } else if (l.get(5) == '068') {
                //Date
                ps.setType(TypeType.valueOf('D'))
                log.trace('Type of entry: D (68) ')
            } else if (l.get(5) == '073') {
                //Integer
                ps.setType(TypeType.valueOf('I'))
                log.trace('Type of entry: I (73) ')
            } else if (l.get(5) == '077') {
                //Money
                ps.setType(TypeType.valueOf('M'))
                log.trace('Type of entry: M (77) ')
            } else if (l.get(5) == '084') {
                //Text
                ps.setType(TypeType.valueOf('T'))
                log.trace('Type of entry: T (84) ')
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

    Layout getLayout() {
        return layout
    }

}

