Using the command line interface (CLI)
======================================

# Introduction
This document describes the capabilities and features of the command line interface.

# Basics
Since the program is compiled as a Java program you need to pass it's name to the Java interpreter like this.
> `java -jar layout-cli-1.0-SNAPSHOT.jar`

Note that the version suffix (`1.0-SNAPSHOT`) can change in later versions.

To get a help text from the command line just use the argument `-h` or `--help`.
> `java -jar layout-cli-1.0-SNAPSHOT.jar --help`

# Arguments
Most arguments can be passed using either a short name or a long one. Short arguments a prefixed by a single dash character (`-`), long ones by a double dash (`--`).

## Style sheet properties
Sets property name and value separated by '='. Uses a value for a given property for XSLT

* **Only Short form**: -D

## Format
Sets the page format, either A4 or A5.
        
* **Short form**: -f
* **Long form**: --format

## Help
Prints a online help text.
 
* **Short form**: -h 
* **Long form**: --help

## Input file
Sets the input file.
        
* **Short form**: -i
* **Long form**: --input

## Include path
Sets a directory for external FO objects.
        
* **Short form**: -I 
* **Long form**: --include-directory

## Input format
Sets the input file format.

* **Short form**:
* **Long form**:        

## Available formats 
Prints available formats

* **Short form**: -lf
* **Long form**: --list-formats

## Available printers
Lists available printers (with -v also media sizes).

* **Short form**: -lp
* **Long form**: --list-printers

## Output File
Sets the output file.

* **Short form**: -o
* **Long form**: --output

## Output format
Sets the output file format.
        
* **Short form**: -p
* **Long form**: --output-format

## Printer
Send result to printer (only for PDF / PS output).
        
* **Short form**: -prt
* **Long form**: --printer

## Quiet mode
Print no output at all.
        
* **Short form**: -q
* **Long form**: --quiet

## Template file
Sets the template / parser for printing
        
* **Short form**: -t 
* **Long form**: --template

## Verbose mode
Sets verbose output.
        
* **Short form**: -v
* **Long form**: --verbose

## XSL-FO
Sets the XSLT to transform into XSL-FO to be used.

* **Short form**: -x
* **Long form**: --xsl-fo

#Examples

    java -jar layout-cli-1.0-SNAPSHOT.jar -i ./sub473_2014030316384670_slip001.printed -j TXT  -p PDF -t ./ous40_layout_001_du.asc -x ./layout2fo.xsl -prt bbk-test
    
Reads a printed Slip ('`sub473_2014030316384670_slip001.printed`') in text format, parse it using the given template ('`ous40_layout_001_du.asc`'), transform it using the XSLT ('`layout2fo.xsl`') into PDF and sends it to the printer ('`bbk-test`').


    java -jar ./target/layout-cli-1.0-SNAPSHOT.jar -i ./resources/slips/sub473_2014030316384670_slip001.printed -j TXT -p PS -t ./ous40_layout_001_du.asc -x ./xslt/layout2fo.xsl -o sub473_2014030316384670_slip001.ps -f A4 -v -I ../resources/xslt
    
Reads a printed Slip ('`./resources/slips/sub473_2014030316384670_slip001.printed`') in text format, parse it using the given template ('`ous40_layout_001_du.asc`'), transform it using the XSLT ('`./xslt/layout2fo.xsl`'), using a include directory ('`../resources/xslt`') into Postscript in page size A4 and save the result ('`sub473_2014030316384670_slip001.ps`').
