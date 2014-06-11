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
* Generate XSL-FO files from ODF (Open / Libre Office)

## Requirements
This framework need just Java (Version 1.6 or higher) as runtime environment, to checkout and compile you also need Git and Maven.

## Installation and Usage
This package provides two main artifacts that can be used:
* The command line interface (CLI) for manual conversions and printing as part of the "layout-cli" Maven module
* The Server component for watching folders for new slips as part of the "print-server" Maven module

You need the other Maven modules as dependencies in your local Maven Repository in order to build the main artifacts. Just follow the steps below.

### Checkout from Git repository

Just run the following command to get a copy of the source code:
> git clone https:github.com/subugoe

### Compile & package

### Create documentaion
This readme file only provides a bird eye view over the framework, to get the whole picture generate the Maven site for this package. You just need to run the following command and look into ./target/site/index.html

>mvn site

# Development

This package provides a API documentation if you build the Maven site.

## Maven Modules

## XSL-FO Files