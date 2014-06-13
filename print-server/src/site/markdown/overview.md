Print Server Overview
=====================

# Introduction
The new print server was designed as replacement for existing `ous_print_server` installations. It should provide the same capabilities and also provide some additional features.

#Implementation
The new print server uses [Apache Camel](http://camel.apache.org/) as its base. This way you can use all [components of Camel](http://camel.apache.org/components.html) using the route [XML syntax](http://camel.apache.org/configuring-camel.html). See the [configuration page](./configuration.html) for examples.

# Requirements fulfilled
The following requirements were taken from the existing implementation.

## Watching file systems
* The program should be able to watch file systems.
* The Program should be able to fall back to a polling mechanism for watching file systems.

## Detection of print type and printer
* The print type and the printer should be determinate fro the name of the file.

## Mailing
* The program should be able to send send mails.
* The return address of the mails should be configurable.

## Printing
* Printing should be program native - no need to call lpr commands.
* the Java print API (and thus CUPS) should be supported.

## Printing redirection
* Prints for specific printers should be redirected according to types.

# Additional requirements
* The program should be distinct from it's configuration (safety).
* Configuration should be update-able during runtime.
* Monitoring should be possible.