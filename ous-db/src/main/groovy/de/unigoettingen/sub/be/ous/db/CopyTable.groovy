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

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.sql.DataSource

import org.apache.commons.dbcp.BasicDataSource

import org.apache.ddlutils.Platform
import org.apache.ddlutils.model.Column
import org.apache.ddlutils.model.Database
import org.apache.ddlutils.model.Table

import org.apache.log4j.Level

/**
 *
 * @author cmahnke
 */
@Log4j
class CopyTable {
    static Boolean quiet, verbose
    /*
    protected Table readTable (Database db, String table) {
        
    }
     */
   
    protected Database extractSchema (String dbName, Table table, DataSource input) {
        Platform platform = PlatformFactory.createNewPlatformInstance(input)
        Database readModel = platform.readModelFromDatabase(dbName)
        dataSource.close()
        Database writeModel = new Database()
        writeModel.setName(dbName)
        writeModel.addTable(table)
        input.close()
        return writeModel
    }
    
    protected void copyTable (String dbName, String tableName, DataSource input, DataSource output) {
        Table table = readModel.findTable(tableName)
        Database writeModel = extractSchema(dbName, table, input)
        Platform platformWrite = PlatformFactory.createNewPlatformInstance(output)
        platformWrite.createTables(writeModel, true, true)
        //Close connection
        output.close()

    }
    
    protected static String getInsertQuery (Table table) {
        def tableName = table.getName()
        def fields = ''
        def values = ''

        for (Column c in table.getColumns()) {
            def String name =  c.getName()
            fields += name + ", "
            values += "?" + name + ", "
        } 
        return "INSERT INTO ${tableName} (${fields[0..-3]}) VALUES (${values[0..-3]});"
    }
    
    protected static BasicDataSource setUpDataSource (URI jdbcUri) {
        BasicDataSource dataSource = new BasicDataSource()
        //TODO: add a Parser for different JDBC URIs
        /*
        dataSource.setUrl(DATASOURCE_IN_URI)
        dataSource.setUsername(DATASOURCE_IN_USER)
        dataSource.setPassword(DATASOURCE_IN_PASS)
        dataSource.setDriverClassName(DATASOURCE_IN_DRIVER)
        */
        return dataSource
    }
    
    static void main(args) {
        //Silence the logger
        log.setLevel(Level.ERROR)
        def cli = new CliBuilder(usage: 'java -jar ous-db.jar', posix: false)
        cli.i(longOpt: 'inUri', 'JDBC URI of the input database', args: 1)
        cli.o(longOpt: 'outUri', 'JDBC URI of the output database', args: 1)
        cli.h(longOpt: 'help', 'usage information')
        cli.q(longOpt: 'quiet', 'no output at all')
        cli.t(longOpt: 'table', 'table to copy', args: 1)
        cli.v(longOpt: 'verbose', 'verbose output')
        
        def opt = cli.parse(args)
        //******** Start of Option parsing
        if (opt.v) {
            verbose = true
            log.setLevel(Level.TRACE)
        } else {
            log.setLevel(Level.ERROR)
        }
        
        //Set quiet
        if (opt.q) {
            setQuiet()
        }
    }
    
}

