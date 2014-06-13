Print Server Overview
=====================

# Introduction
The new print server was designed as replacement for existing `ous_print_server` installations. It should provide the same capabilities and also provide some additional features.

# Requirements fulfilled
The following requirements were taken from the existing implementation.

## Watching file systems
* The program should be able to watch file systems.
* The Program should be able to fall back to a polling mechanism for whatching file systems.

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