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

package de.unigoettingen.sub.be.ous.print.server

import de.unigoettingen.sub.be.ous.print.layout.camel.LayoutProcessor

import groovy.transform.TypeChecked
import groovy.util.logging.Log4j

import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.RoutesDefinition
import org.apache.camel.main.Main
import org.apache.camel.model.RoutesDefinition

import org.apache.commons.vfs2.FileChangeEvent
import org.apache.commons.vfs2.FileListener
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.VFS
import org.apache.commons.vfs2.impl.DefaultFileMonitor

import org.apache.log4j.Level

import org.xml.sax.SAXParseException

/**
 * Apache Camel based print server main class. Takes some command line arguments to
 * boot up a camel instance. Configuration is done via XML file. Ths configuration
 * can be watched during runtime to reload new routes or shut down old ones.
 * @author cmahnke
 */
@Log4j
//@TypeChecked
class PrintServer extends Main {
    //pattern taken from https://camel.apache.org/running-camel-standalone-and-have-it-keep-running.html
    
    /** The Camel main class */
    private Main main = null
    /** the URL of the configuration file */
    static protected URL config = null
    /** The raw argument Strings */
    protected String[] args;
    /** Container for boolean command line parameters */
    static protected Boolean verbose, quiet, watch, noDeamon = false
 
    /**
     * Main method of the SUB PrintServer
     * Takes commandline arguments, parses them and boots up a Camel Context
     */
    public static void main(String[] args) throws Exception {
        //Silence the logger
        log.setLevel(Level.ERROR)
        def cli = new CliBuilder(usage: 'java -jar printServer.jar', posix: false)
        cli.h(longOpt: 'help', 'usage information')
        cli.c(longOpt: 'config', 'route configuration', args: 1)
        cli.q(longOpt: 'quiet', 'no output at all')
        cli.nd(longOpt: 'no-daemon', 'do not start as deamon')
        cli.v(longOpt: 'verbose', 'verbose output')
        cli.w(longOpt: 'watch', 'reload configuration after change')
        
        def opt = cli.parse(args)
        //******** Start of Option parsing
        if (opt.v) {
            verbose = true
            log.setLevel(Level.TRACE)
        } else {
            log.setLevel(Level.ERROR)
        }
        
        if (opt.q) {
            quiet = true
            log.setLevel(Level.OFF)
        }
        
        //No options at all
        if (!opt) {
            cli.usage()
            return
        }
        
        //Watch option
        if (opt.w) {
            watch = true
        }
        
        //No daemon option
        if (opt.nd) {
            noDeamon = true
        }
       
        //Help option
        if(opt.h) {
            cli.usage()
            log.trace('Help requested')
            return
        }
        
        //Configuration file
        if (opt.c) {
            String dir = System.getProperty("user.dir") + File.separator + opt.c
            
            config = new File(dir).toURI().toURL()
            log.trace('Configuration: ' + config.toString() + ' (generated from ' + dir + ')')
        } else {
            println 'No routes definition given, exiting!'
            System.exit(2)
        }
        
        PrintServer ps = new PrintServer(args);
        ps.boot();
    }
    
    /**
     * A protected constructor used for unit tests
     * @param args, the command line arguments
     */    
    protected PrintServer (String[] args) {
        this.args = args
    }
    
    /**
     * A protected constructor used for unit tests
     * @param url, the configuration file
     * @param watch, if changes should be watched
     */
    protected PrintServer (URL config, Boolean watch = false) {
        this.config = config
        this.watch = watch
    }
    
    /**
     * The startup method, starts the camel context
     */
    @TypeChecked
    protected void boot() throws Exception {
        // create a Main instance
        main = new Main();
        // enable hangup support so you can press ctrl + c to terminate the JVM
        if (!noDeamon) {
            main.enableHangupSupport()
        }
        
        //Configuration
        if (config != null) {
            RouteBuilder rb
            try {
                rb = new PrintServerRouteBuilder(config)
            } catch (UnmarshalException ue) {
                println 'Couldn\'t parse route definitions, probably a XML problem, have you encoded the \'&\' in the URIs as \'&amp;\'?'
                System.exit(1)
            } catch (SAXParseException se) {
                println 'Couldn\'t parse route definitions, probably a XML problem, have you encoded the \'&\' in the URIs as \'&amp;\'?'
                System.exit(1)
            }
            //Add the loaded routes
            main.addRouteBuilder(rb);
        } else {
            println 'Using internal routes'
        }
        //File watcher
        FileSystemManager fsManager = VFS.getManager()
        FileObject listenConfig = fsManager.resolveFile(config)
 
        //TODO: Finish this, reloading of routes doesn't work yet
        DefaultFileMonitor fm = new DefaultFileMonitor(new ConfigFileListener(new DefaultCamelContext()))
        fm.setRecursive(false)
        fm.addFile(listenConfig)
        fm.start()
        
        
        //Start
        main.run()
        
        // run until you terminate the JVM
        println 'Starting Camel. Use ctrl + c to terminate the JVM.\n'
        //See http://saltnlight5.blogspot.de/2013/08/getting-started-with-apache-camel-using.html
        
        
    }
    
}

