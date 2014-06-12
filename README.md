OUS Printing Tools (NG)
=======================

#Introduction

## Overview
This framework can be used to replace the ous_print_server with a implementation in Groovy, redirection rules are configured using XML.

## Features
* Convert ASC Files to XML
* Generate Parsers for generated slips
* Transform results of parsers to XSL-FO
* Printing of results
* Command line interface for conversions 
* Automatic hot folder for slips that should be converted and printed
* Uses Apache Camel, you can also mail and print the results 
* Generate XSL-FO files from ODF (Open / Libre Office)

## Requirements
This framework need just Java (Version 1.6 or higher) as runtime environment, to check out and compile you also need Git, Maven and a internet connection to resolve required artifacts.

##Terminology used in this document
* **Artifact**: A piece of self-contained software
* **Maven**: A Java build tool like Ant or even Make 
* **Maven site**: A generated documentation which contains Groovy docs, reports and written documentation, need to be generated since it's not available in HTML format.
* **XSL-FO**: E**X**stensable **S**tyles sheet **L**anguage **F**ormating **O**bjects, a XML representation of print layouts

## Installation and Usage
This package provides two main artifacts that can be used:
* The command line interface (CLI) for manual conversions and printing as part of the "layout-cli" Maven module
* The Server component for watching folders for new slips as part of the "print-server" Maven module

Both include a command line documentation of their parameters, if you use the argument '-?' or '--help'. Refer either to this output or to the generates sites for a list of supported arguments.

You need the other Maven modules as dependencies in your local Maven Repository in order to build the main artifacts. Just follow the steps below.

### Checkout from Git repository

Just run the following command to get a copy of the source code:
> git clone https:github.com/subugoe

### Compile & package
Not all unit tests are passing yet, therefore you need the following to build all modules:
> mvn -Dmaven.test.skip=true package

### Create documentation
This readme file only provides a bird eye view over the framework, to get the whole picture generate the Maven site for this package. You just need to run the following command and look into ./target/site/index.html

There are also some slides (in German) that reflect the current state of this framework.

>mvn site

# Development

This package provides a API documentation if you build the Maven site.

## Maven Modules
The following Maven Modules make up this framework:
* **util** - Utility functions used by several other modules
* **layout** - The core of this framework, contains the XSLT stylesheets and Groovy wrappers
* **layout-cli** - One of the main artifacts, a command line interface to work with the stylesheets, can convert and print
* **layout-component** - A Apache Camel component for the layout core, this way Camel can use the layout engine.
* **print-server** - A command line wrapper for Apache Camel, an act as a daemon to watch for and print slips
* **layout-test** - Contains test files for unit tests


## XSL-FO Files
The result of the layout process is determinate by XSL-FO files. Either use your own, change the provided one, or generate one using a ODF template. A description how to use ODF to create a XSL-FO file can be found in the Maven site.

## Contributing
Contributions are welcome, but since this code is in a working state, no support can be provided. 