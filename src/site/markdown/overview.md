Overview
========

# Introduction
This document provides an overview about the modules of the OUS print framework. It also tries to lead you to the right documentation for your questions and needs.

# What?
This framework allows you to get better prints from your OUS 3 (and probably 4) library system. It can read your layout definitions to enable to interpret generated print files. These can then be transformed to beautiful PDF or Postscript layouts to be printed. You have the full power of XSLT 2.0 and thus XPath 2.0 to spice up your print layouts. And there is even more! You can also mail the results. Everything that is "printed" by your library system can be changed!

# I want!
## To have beautiful prints
Since this is a framework, not a finished product, you need a bit of knowledge in how this works first. Please read the documentation of the 'layout' module first. After the refer to the 'layout-cli' module to conduct your test. After that use the 'print-server'.

## To add or have a feature
Just use the GitHub workflow, which is forking the repository, add your changes, send a pull request.

# Modules

The following Maven Modules make up this framework:
## 'util'
Utility functions used by several other modules. Only relevant for developers.
## 'layout'
The core of this framework, contains the XSLT stylesheets and Groovy wrappers. [Go there](./layout/), if you want to understand how this works.
## 'layout-cli' 
One of the main artifacts, a command line interface to work with the stylesheets, can convert and print. [Go there](./layout-cli/), if you want to do conversions manually.
## 'layout-component'
A Apache Camel component for the layout core, this way Camel can use the layout engine. Only relevant for developers.
## 'print-server'
The SUB PrintServer, a command line wrapper for Apache Camel, can act as a daemon to watch for and print slips. [Go there](./print-server/), if you want the conversion ant printing as a automatic background task.
# 'layout-test'
Contains test files for unit tests. Only relevant for developers.

# Development

Just run the following command to get a copy of the source code:
> git clone https://github.com/subugoe/ous-print.git

### Compile & package
Not all unit tests are passing yet, therefore you need the following to build all modules:
> mvn -Dmaven.test.skip=true package

### Create documentation
This readme file only provides a bird eye view over the framework, to get the whole picture generate the Maven site for this package. You just need to run the following command and look into ./target/site/index.html

There are also some slides (in German) that reflect the current state of this framework.

>mvn site

