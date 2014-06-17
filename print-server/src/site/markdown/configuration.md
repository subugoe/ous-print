Configuring the Print Server
============================

# Introduction
The print server is configured by Apache Camel route definitions in XML. These Routes need to have a start and an endpoint. You can also use switching and branching logic. Please refer to the [Camel configuration documentation](http://camel.apache.org/content-based-router.html).

# File format
Since the configuration is done in XML the configuration file start with an XML declaration, a root element and the namespace for Camel configurations
>`<?xml version="1.0" encoding="UTF-8"?>`

>`<routes xmlns="http://camel.apache.org/schema/spring">`

Without these the server can't parse the configuration and will fail at startup. Make sure to close the the root element at the end of the configuration file

>`</routes>`

# Routes
Route definitions start with the element `<route>`, they have one start point (element `<from>`) specified by an URI (attribute `@uri`) and several endpoints (element `<to>`) which will be called in the given order. For the options of the `file:` URI scheme consult the [Camel documentation](http://camel.apache.org/file2.html). The endpoint of the `layout` URI scheme is documented in the [`layout-component` module](../layout-component/).


## Example
This example takes files from the folder '`/pica/prod/prt/selbstabholbereich`', using a file pattern of '`sub4235_\d{16}_slip001.print`' and sends the to the layout component. the file is moved to '`../sich-selbstabhol`' afterwards.

The layout component is configured to use the XSL-FO file '`./xslfo/layout2fo.xsl`', the template file (layout definition) '`./ous40_layout_001_du.asc`' the output is configured as Postscript (PS).

The result delivered by the layout component is then sent to the printer '`bbk-test`'.

>`<route>`

>`    <from uri="file:/pica/prod/prt/selbstabholbereich?include=sub4235_\d{16}_slip001.print&amp;?move=../sich-selbstabhol"/>`

>`    <to uri="layout:.?xslfo=./xslfo/layout2fo.xsl&amp;template=./ous40_layout_001_du.asc&amp;outputFormat=PS"/>`

>`   <to uri="lpr://localhost/bbk-test"/>`

>`</route>`
