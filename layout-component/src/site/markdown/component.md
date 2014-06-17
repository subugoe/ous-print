Layout Component
================

# Introduction
The Layouter can be used as component of [Apache Camel](http://camel.apache.org/). For general Information how to use components refer to the [Camel documentation](http://camel.apache.org/how-do-i-configure-endpoints.html).

# URI parameters
Note: Formats a described in the form of their definition in [FORMAT.groovy](../layout/groovydoc/de/unigoettingen/sub/be/ous/print/layout/FORMAT.html). If the paths given by the configuration the component will fail to start up.

## inputFormat
The format of the input file, defaults to TXT.

## outputFormat
The format of the output file, defaults to PDF currently.

## template
The template to be used, the format can be XML or TXT.

## xslfo
The XSL-FO file to be used.

## includePath
The include path for external content from XSL-FO.

## debugPath
A path where a copy of the generated files is stored.

## pageSize
The Page size, to be evaluated by the XSL-FO file. If you use your own XSL-FO you can ignore this.

# Example
In this example only the URI starting with 'layout:' is relevant. Note that you have to escape the ampersands ('&') in XML as '&amp;'.

>`<route>`

>`    <from uri="file:/pica/prod/prt/selbstabholbereich?include=sub4235_\d{16}_slip001.print&amp;?move=../sich-selbstabhol"/>`

>`    <to uri="layout:.?xslfo=./xslfo/layout2fo.xsl&amp;template=./ous40_layout_001_du.asc&amp;outputFormat=PS"/>`

>`   <to uri="lpr://localhost/bbk-test"/>`

>`</route>`
