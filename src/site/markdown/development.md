Development
===========

# Introduction
This page describes the options for developers to use or extend the frame work. The framework is written in XSLT for the parsing parts and in Groovy for the required logic to handle the process itself. Make yourself familiar with both languages if you need to build your own logic. If you just want to create your own stunning layouts XSLT / XSL-FO will be enought. If you want to contribute, learn the usage of Git and GitHub. How to check out the sources from the repository and to compile is described in the [installation document](./installation.html). Groovy sources have a documentation in GroovyDoc format attached to them, check out the generated site for a human readable form. To understand how the sources work have a look at the unit tests, see the [`layout-test` module](./layout-test/) for the files of the unit tests.

# Needed tools

* To run the software you need [Java](http://java.com/en/) (Version 1.6 or higher) .
* To check out the sources you will need [Git](http://git-scm.com/).
* To build the binaries you will need [Maven](http://maven.apache.org/) (Version 3 and up prefered).
* To play around with XSLT and XSL-FO you need a decent XML editor, we recommend [OxygenXML](http://oxygenxml.com/).
* To play with the ODF feature you need ([Open](https://www.openoffice.org/) / [Libre Office](http://www.libreoffice.org/))

# Maven Modules
See the [overview document](./overview.html) for a shore description of the modules. 

# Dependencies
This is just a high level overview about the dependencies, have a look at the Maven dependencies (either in the project site or the project descriptors) for each sub module for a complete list including indirect dependencies and the used versions.

## Groovy
[Groovy](http://groovy.codehaus.org/) is a scripting language on top of the JVM, it's used for the code to glue the used components together.

## Apache FOP
[Apache FOP](http://xmlgraphics.apache.org/fop/) is a XSL-FO processor which is used to transform the XSL-FO files int PDF or Postscript.

## Apache Camel
[Apache Camel](http://camel.apache.org/) is routing framework which is used to implement the SUB PrintServer.

## Log4J and SLF4J
[Log4J](http://logging.apache.org/log4j/1.2/) and [SLF4J](http://www.slf4j.org/) are logging frameworks. Log4J is used by the this framework, some dependencies use SLF4J. The outputs of SLF4J are redirected to Log4J.

## Apache Commons
Apache Commons are a set of utility components. The following components are used directly in this project:

* [commons-cli](http://commons.apache.org/cli/) is a library to parse command line arguments.
* [commons-vfs](http://commons.apache.org/vfs/) is a library to handle virtual file systems, used for access to zipped files.

## Apache Tika
[Apache Tika](http://tika.apache.org/) is a toolkit to detect and analyze text contents and media types. Used to detect input types.

## Saxon
[Saxon](http://saxon.sourceforge.net/) is a XLST 2.0 capable XLST processor. It's used for the XSLT transformations.

# Adding your own features
If you want to add your own features, use the GitHub workflow: Clone the repository and sen a pull request if your feature is complete. Make sure that your feature is documented correctly:

* Groovy code should have GroovyDoc comments.
  * Groovy code should have unit tests.
* Style sheets should have documentation on each function.
* Describe the requirement which made your feature necessary.
* Provide a documentation how to use your feature.
* **All** provided files need to be under a free license.
