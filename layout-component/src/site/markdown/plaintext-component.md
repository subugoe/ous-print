Plain Text Component
================

# Introduction
The Plain Text component can be used with [Apache Camel](http://camel.apache.org/). For general Information how to use components refer to the [Camel documentation](http://camel.apache.org/how-do-i-configure-endpoints.html).

# URI parameters

## pageSize
The Page size, to be evaluated by the XSL-FO file. If you use your own XSL-FO you can ignore this.

# Example
In this example only the URI starting with 'plainText:' is relevant. Note that you have to escape the ampersands ('&') in XML as '&amp;'.

>`<route>`

>`    <from uri="file:./target/generated-test-resources/hotfolder/lbs3/in?include=.*.print&amp;noop=true&charset=ISO-8859-1"/>`

>`    <to uri="plainText:.&amp;pageSize=A5"/>`

>`    <to uri="fop:application/pdf"/>`

>`    <to uri="file:./target/?fileName=${file:name}.pdf"/>`

>`</route>`
