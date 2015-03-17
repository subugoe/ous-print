/*
 * This file is part of the OUS print system, Copyright 2014 SUB Göttingen
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

package de.unigoettingen.sub.be.ous.print.layout.cli

import groovy.util.logging.Log4j
import groovy.transform.TypeChecked

import java.awt.print.PageFormat
import java.awt.print.PrinterJob

import javax.print.Doc
import javax.print.DocFlavor
import javax.print.DocPrintJob
import javax.print.PrintService
import javax.print.SimpleDoc
import javax.print.attribute.HashPrintRequestAttributeSet
import javax.print.attribute.PrintRequestAttributeSet
import javax.print.attribute.standard.Copies
import javax.print.attribute.standard.JobName

import javax.xml.transform.TransformerConfigurationException

import org.apache.log4j.Level

import org.apache.pdfbox.printing.PDFPrinter
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage

import de.unigoettingen.sub.be.ous.print.layout.Layout
import de.unigoettingen.sub.be.ous.print.layout.FORMAT
import de.unigoettingen.sub.be.ous.print.util.PrinterUtil

import java.nio.charset.Charset

/**
 * Main class for command line interface
 * @author cmahnke
 */
@Log4j
class Main {
    /** Variuos boolean options */
    static Boolean check, verbose, quiet, cut = false
    /** The URLs of input and output files */
    static URL inputUrl, outputUrl, template, include, xslfo = null
    /** The File to save the output to */
    static File outputFile
    /** The ByteArrayOutputStream to cache the output to */
    static ByteArrayOutputStream output = new ByteArrayOutputStream()
    /** The InputStream to read the input from */
    static InputStream input = null
    /** The given input format */
    static FORMAT inFormat = FORMAT.TEXT
    /** The desired output format */
    static FORMAT outFormat = FORMAT.PDF
    /** Contains parameters for the style sheets */
    static Map<String, String> params = [:]
    /** Name of the printer to use */
    static String printer = null
    /** The page size to use */
    static Layout.PageSize pageSize = Layout.DEFAULT_PAGE_SIZE
    /** The encoding to be used */
    static String encoding = System.getProperty('file.encoding')
    
    /**
     * Sets options passed on commandline Use the help command to get a list.
     */
    static void main(args) {
        //Silence the logger
        log.setLevel(Level.ERROR)
        def cli = new CliBuilder(usage: 'java -jar ous-layout.jar', posix: false)
	cli.jc(longOpt: 'charset', 'prints the used Java char set')
        cli.c(longOpt: 'cut', 'signals the printer to cut the paper (only with prt)')
        cli.D(args:2, valueSeparator:'=', argName:'property=value', 'use value for given property for XSLT')
        cli.e(longOpt: 'encoding', 'the encoding to be used used, defaults to ' + System.getProperty('file.encoding') + ' (Note: this is probably not what you want!)', args:1)
        cli.f(longOpt: 'format', 'page format, either A4 or A5, defaults to ' + Layout.DEFAULT_PAGE_SIZE, args: 1)
        cli.h(longOpt: 'help', 'usage information')
        cli.i(longOpt: 'input', 'input file', args: 1)
        cli.I(longOpt: 'include-directory', 'directory for external FO objects', args: 1)
        cli.j(longOpt: 'input-format', 'input file format', args: 1)
        cli.lf(longOpt: 'list-formats', 'print available formats')
        cli.lp(longOpt: 'list-printer', 'list available printers (with -v also media sizes)')
        cli.o(longOpt: 'output', 'output file', args: 1)
        cli.p(longOpt: 'output-format', 'output file format', args: 1)
        cli.prt(longOpt: 'printer', 'send result to printer (only for PDF / PS output)', args: 1)
        cli.q(longOpt: 'quiet', 'no output at all')
        cli.t(longOpt: 'template', 'template / parser for printing', args: 1)
        cli.v(longOpt: 'verbose', 'verbose output')
        cli.x(longOpt: 'xsl-fo', 'XSL-FO to be used', args: 1)
        
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
        
        //No options
        if (!opt) {
            cli.usage()
            return
        }
        
        //List printers
        if (opt.lp) {
            for (PrintService p in PrinterUtil.getPrinters()) {
                List<String> media = PrinterUtil.getPaperSizes(p)
                String printer = 'Available Printer: ' + p.getName()
                if (opt.v) {
                    printer = printer + ' (Page formats: ' + media.join(', ') + ')'
                }
                println printer
            }
            return
        }

        //Print help
        if (opt.h) {
            cli.usage()
            log.trace('Help requested')
            return
        }
        
        //Print charset
        if (opt.jc) {
            println "File encoding: " + System.getProperty("file.encoding")
            println "Default charset: " + java.nio.charset.Charset.defaultCharset().name()
            log.trace('Charset requested')
            return
        }
        
        //Print formats
        if (opt.lf) {
            println "This program supports a number of input formats:"
            println "ASC: A text file containing a layout definition"
            println "ASCXML: A XML file containing a layout definition"
            println "TXT: A printed text file - input only"
            println "     Or a text file to create a layout from"
            println "XML: A parsed text file - input only"
            println "XSL: A XSLT based parser for printed files - output only"
            println "XSLFO: A XSL FO Layout for a printed file (also set a parser / template -t and a XSL-FO stylesheet -x) - output only"
            println "PDF: A PDF file for a printed file (also set a parser / template -t and a XSL-FO stylesheet -x) - output only"
            println "PS: A Postscript file for a printed file (also set a parser / template -t and a XSL-FO stylesheet -x) - output only"
            log.trace('Format list requested')
            return
        }
        
        //Input file
        if (opt.i) {
            inputUrl = new File(opt.i).toURI().toURL()
            log.trace('Input: ' + inputUrl.toString())
            try {
                input = inputUrl.openStream()
            } catch (FileNotFoundException e) {
                println 'Input File not found!'
                log.warn('Input File not found!:', e)
                System.exit(3)
            }
        }
        
        if (opt.prt) {
            printer = opt.prt
            if (outFormat != FORMAT.PDF) {
                println 'Output format needs to be PDF for printing'
                System.exit(10)
            }
            if (!PrinterUtil.getPrinterNames().contains(printer)) {
                println 'Printer ' + printer + ' not known to system'
                System.exit(11)
            }
            log.trace('Printer set to: ' + printer)
        }
        
        if (opt.c) {
            if (printer == null) {
                println 'Cutting the paper only works with a printer'
                System.exit(12)
            }
            cut = true
            log.trace('Cutting enabled')
        }
        
        //No output method specified
        if (!opt.o && !opt.prt) {
            println 'Either set output file (-o) or printer (-prt)'
            cli.usage()
            System.exit(2)
        } else if (opt.o) {
            outputFile = new File(opt.o)
            log.trace('Output: ' + outputFile.toString())
        }
        
        //Template or parser
        if (opt.t) {
            template = new File(opt.t).toURI().toURL()
            log.trace('Template: ' + template.toString())
        }
        
        //Include directory
        if (opt.I) {
            String dir = System.getProperty("user.dir") + File.separator + opt.I
            include =  new File(dir).toURI().toURL()
            log.trace('Include directory: ' + include.toString())
        }
        
        //XSL-FO file
        if (opt.x) {
            String dir = System.getProperty("user.dir") + File.separator + opt.x
            xslfo = new File(dir).toURI().toURL()
            log.trace('XSL-FO: ' + xslfo.toString())
        }
        
        //Page size
        //TODO: check if opt.f is valid && Layout.PageSize.fromString(opt.f) != null
        if (opt.f) {
            pageSize = Layout.PageSize.fromString(opt.f)
            log.trace('Set page size to ' + pageSize.toString())
        } else {
            log.trace('PageSize set to ' + pageSize.toString() + ' (Default)')
        }

        //Encoding
        if (opt.e) {
            encoding = opt.e
            log.trace('Set encoding to ' + encoding)
        } else {
            log.trace('Using default encoding ' + encoding)
        }
        /*
        else if (opt.f) {
        log.warn('Unknown page size to ' + opt.f)
        }
         */
        
        //******** End of Option parsing
        
        //******** Validate input
        
        //Check if there is something to work with

        if (input == null || output == null) {
            println "Either no input file or output file given, see help:"
            cli.usage()
            System.exit(1)
        }
        
        if ((outFormat == FORMAT.PDF || outFormat == FORMAT.PS) && (xslfo == null || template == null)) {
            println "PDF / PS Output requires a template / parser and a XSL-FO file"
            cli.usage()
            System.exit(1)
        }

        
        //Check formats
        if (opt.j && FORMAT.fromString(opt.j) != null) {
            inFormat = FORMAT.fromString(opt.j)
        } else if (opt.j) {
            log.trace('Can\'t parse input format' + opt.j)
            println 'Format must be one of ' + FORMAT.getFormats()
            System.exit(5)
        }
        if (opt.p && FORMAT.fromString(opt.p) != null) {
            outFormat = FORMAT.fromString(opt.p)
        } else if (opt.p) {
            log.info('Can\'t parse output format' + opt.p)
            println 'Format must be one of ' + FORMAT.getFormats()
            System.exit(5)
        }
        
        //parse XSLT params
        if (opt.D) {
            for(i in (0..opt.Ds.size() - 1).step(2)) {
                params[opt.Ds.get(i)] = opt.Ds.get(i + 1)
            }
            params.each() { key,  value ->
                log.trace('XSLT Param ' + key + ' is set to ' + value)
            }
        }

        //Validate charset
        if (encoding != System.getProperty('file.encoding')) {
            Boolean found = false
            for (c in Charset.availableCharsets().keySet()) {
                if (encoding == c) {
                    found = true
                    break
                }
            }
            if (!found) {
                log.info("Charset ${encoding} not found, exiting")
                println "Encoding ${encoding} not supported"
                System.exit(6)
            }

        }

        
        start()
    }
    
    /**
     * Processes the given files and save or prints them
     */
    @TypeChecked
    static void start () {
        //output = new FileOutputStream(outputFile)
        Layout l = new Layout(inFormat, input, outFormat, (OutputStream) output, xslfo, include, template, encoding)
        l.setParams(params)
        if (pageSize != Layout.DEFAULT_PAGE_SIZE) {
            l.setPageSize(pageSize)
        }
        try {
            l.layout()
        } catch (TransformerConfigurationException tce) {
            log.error('Error while transforming', tce)
            println 'Couldn\'t transform file' 
            System.exit(30)
        }
        
        if (outputFile != null) {
            save(outputFile, output)
        }
        
        //TODO Remove Postscript Output here
        if (outFormat == FORMAT.PDF && printer != null && output != null) {
            //Send to given printer
            //See http://stackoverflow.com/q/18636622
            PDDocument pddocument = PDDocument.load(new ByteArrayInputStream(((ByteArrayOutputStream) output).toByteArray()))
            Boolean rotate = false
            
            if (rotate) {
                /*
                Old API (PDFBox 1.8)
                List<PDPage> pageList = pddocument.getDocumentCatalog().getAllPages()
                New API (PDFBox 2.0)
                Iterator<PDPage> pageIterator = pddocument.getDocumentCatalog().getPages()
                */
                for (page in pddocument.getDocumentCatalog().getPages()) {
                    ((PDPage) page).setRotation(90)
                }
            }
            PrintService p = PrinterUtil.getPrinter(printer)
            if (p == null) {
                println 'Printer not found: ' + printer
                System.exit(20)
            }
    
            PrinterJob job = PrinterJob.getPrinterJob()
            job.setJobName(inputUrl.toString())
            job.setPrintService(p)
            //PDPageable pdpageable = new PDPageable(pddocument)
            //There is a bug in PdfBox: https://issues.apache.org/jira/browse/PDFBOX-2021
 
            log.trace('PageSize set to ' + pageSize.toString())
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(new Copies(1));
            printRequestAttributeSet.add(Layout.PageSize.getMediaSizeName(pageSize))
            printRequestAttributeSet.add(Layout.PageSize.getOrientationRequested(pageSize));
            
            
            PageFormat pf = job.getPageFormat(printRequestAttributeSet)
            log.trace('Got a PageFormat for the requested page (Format:' + pageSize.toString() + ')')
            log.trace('Height:' + pf.getHeight())
            log.trace('ImageableHeight:' + pf.getImageableHeight())
            log.trace('ImageableWidth:' + pf.getImageableWidth())
            log.trace('Width:' + pf.getWidth())
            log.trace('Orientation:' + pf.getOrientation())
            
            job.defaultPage(pf)
            
            //Paper paper = Layout.PageSize.getPaper(pageSize)
            
            PDFPrinter pdprinter = new PDFPrinter(pddocument)
            //PDFPrinter pdprinter = new PDFPrinter(pddocument, Scaling.SCALE_TO_FIT, Orientation.AUTO, paper)
            pdprinter.silentPrint(job)
            job = null
            
            //The is a Bug in PDF Box, orientation isn't set correctly, we jus define a bigger page
            //See http://pk345.blogspot.de/2012/11/pdfbox-landscape-printing-problem-and.html
            /*
            try {
            pddocument.silentPrint(job)
            } catch (javax.print.PrintException pe) {
            log.error('Couldn\'t print', pe)
            println 'Couldn\'t print result'
            System.exit(21)
            }
             */
            println 'Generated file send to Printer ' + printer
            if (cut) {
                PrinterUtil.cut(printer)
                println 'Sended paper cut signal'
            }
        
        } else if (outFormat == FORMAT.PS && printer != null && output != null) {
            //TODO: Finish this
            PrintService ps = PrinterUtil.getPrinter(printer)
            InputStream is = new ByteArrayInputStream(((ByteArrayOutputStream) output).toByteArray())
            Doc doc = new SimpleDoc(is, DocFlavor.INPUT_STREAM.POSTSCRIPT, null)
            HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet()
            attr.add(new Copies(1))
            attr.add(new JobName(inputUrl.toString(), null))
            
            DocPrintJob pj = ps.createPrintJob()
            println 'Generated file send to Printer ' + printer
            pj.print(doc, attr)
        
        }
        
        
        output.close()
    }
    
    /**
     * Turns the logger off
     */
    @TypeChecked
    protected static void setQuiet() {
        quiet = true
        log.setLevel(Level.OFF)
    }
    
    /**
     * Saves the given ByteArrayOutputStream as given file
     * @param f, the File to safe to
     * @param baos, the ByteArrayOutputStream to safe
     */
    @TypeChecked
    public static void save (File f, ByteArrayOutputStream baos) {
        //Get the required OutputStream
        OutputStream output
        if (f.getName() == '') {
            setQuiet()
            output = System.out
        } else {
            if (!f.exists()) {
                f.createNewFile();
            }
            output = new FileOutputStream(f)
        }

        try {
            baos.writeTo(output)
        } catch (IOException e) {
            println 'IO Error, Maybe the file can\'t be written to (check name and permissions)!'
            log.warn('Output error:', e)
            System.exit(2)
        }
        
    }

    //TODO: Check error handling from http://stackoverflow.com/questions/16035739/how-to-access-the-status-of-the-printer

}
