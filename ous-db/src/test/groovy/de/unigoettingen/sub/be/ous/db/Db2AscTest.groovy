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

import de.unigoettingen.sub.be.ous.models.asc.AscSerializer
import groovy.sql.GroovyResultSet
import groovy.sql.Sql
import groovy.transform.TypeChecked
import groovy.util.logging.Log4j
import javax.sql.DataSource
import org.hsqldb.jdbc.JDBCDataSource
import org.junit.Before
import org.junit.Test

/**
 *
 * @author cmahnke
 */
@TypeChecked
@Log4j
class Db2AscTest {
    //Database settings
    static public String DATABASE = 'lbsdb'
    static public String TABLE = 'lbs_report_layout'
    static public String DB_URI = 'jdbc:hsqldb:file:./target/test-classes/database/lbsdb;shutdown=true' + DATABASE
    static public String USER = 'SA'
    static public String PASS = ''
    
    //Extraction settings
    static List<Integer> numbers = [1, 24] //TODO: Make 310 work
    static List<Integer> inls = [40]
    static List<String> languages = ['EN', 'DE']
    
    DataSource ds
    
    @Before
    @TypeChecked
    public void setup() {
        log.info('Setting up DataSource')
        ds = getDataSource()
        
    }
    
    public static DataSource getDataSource () {
        JDBCDataSource ds = new JDBCDataSource();
        ds.setUrl(DB_URI)
        ds.setUser(USER)
        ds.setPassword(PASS)
        return ds
    }

    @Test
    void testDatabase() {
        Sql database = new Sql(ds)
        database.eachRow("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE'") { row ->
            log.info("Got table ${row}")
        }
    }
    
    @Test
    void testExtract () {
        for (Integer number in numbers) {
            for (Integer inl in inls) {
                for (String language in languages) {
                    log.info("Testing Layout ${number}, for INL ${inl}, language ${language}")
                    Db2Asc d2a = new Db2Asc(ds.getConnection(), number, inl, language)
                    d2a.extract()
                    AscSerializer asr = new AscSerializer(d2a.getLayout())
                    log.info("Generated Layout:\n${asr.serialze()}")
                }
            }
        }
        
    }
	
}

