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

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import javax.print.Doc
import javax.print.DocFlavor
import javax.print.DocPrintJob

import javax.print.PrintService
import javax.print.PrintServiceLookup
import javax.print.SimpleDoc
import javax.print.attribute.standard.Media

/**
 * Helper class for printers, static methods to find printers by name and getting
 * information about supported paper formats
 * @author cmahnke
 */
@CompileStatic
@TypeChecked
class PrinterUtil {
    /** Paper cut sequence in bytes */
    static byte[] cutSeq = [27, 105]
    
    /**
     * Gets a {@link javax.print.PrintService PrintService} by name
     * @param the name
     * @returns a {@link javax.print.PrintService PrintService}
     */
    public static PrintService getPrinter(String printerName) {
        PrintService p = null
        //Lookup Printer
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length > 0) {
            for (int i = 0; i < services.length; i++) {
                if (services[i].getName().equals(printerName))
                p = services[i];
            }
        }
        return p
    }

    /**
     * Gets a list of supported paper sizes for a given PrintService
     * @param printer the {@link javax.print.PrintService PrintService} to query
     * @returns a list of formats
     */
    public static List<String> getPaperSizes (PrintService printer) {
        List<String> mediaList = new ArrayList<String>()
        def media = printer.getSupportedAttributeValues(Media.class, null, null)
        for (m in media) {
            mediaList.add(m.toString())
        }
        return mediaList   
    }
    
    /**
     * Gets a list of names of available PrintServices for this system
     * @returns a list of printers
     */
    public static List<String> getPrinterNames () {
        List<String> printers = new ArrayList<String>()
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null)
        for (PrintService printer : printServices) {
            printers.add(printer.getName()) 
        }
        return printers
    }
    
    /**
     * Gets a list of available PrintServices for this system
     * @returns a list of {@link javax.print.PrintService PrintService}
     */
    public static List<PrintService> getPrinters () {
        List<PrintService> printers = new ArrayList<PrintService>()
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null)
        for (PrintService printer : printServices) {
            printers.add(printer) 
        }
        return printers
    }
    
    /**
     * Cuts paper on the given printer
     * @see http://stackoverflow.com/questions/19409456/thermal-receipt-printer-problems-with-auto-cut
     */
    @TypeChecked
    protected static void cut(String printer) {
        PrintService ps = PrinterUtil.getPrinter(printer)
        DocPrintJob job = ps.createPrintJob() 
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE
        Doc doc = new SimpleDoc(cutSeq, flavor, null)
        job.print(doc, null)
    }
}

