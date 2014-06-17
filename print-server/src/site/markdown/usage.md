Usage of SUB PrintServer
========================

# Introduction
This document describes the capabilities and features of the command line interface.

# Basics
Since the program is compiled as a Java program you need to pass it's name to the Java interpreter like this.
> `java -jar print-server-1.0-SNAPSHOT.jar`

Note that the version suffix (`1.0-SNAPSHOT`) can change in later versions.

To get a help text from the command line just use the argument `-h` or `--help`.
> `java -jar print-server-1.0-SNAPSHOT.jar --help`

# Arguments
Most arguments can be passed using either a short name or a long one. Short arguments a prefixed by a single dash character (`-`), long ones by a double dash (`--`).

## Help
Prints a online help text.
 
* **Short form**: -h 
* **Long form**: --help

## Configuration
Sets the routes definition file to be used. See the [configuration page](./configuration.html) for details.

* **Short form**: -c
* **Long form**: --config

## Non daemon
Disable daemon mode, used for debuging.

* **Short form**: -nd
* **Long form**: --no-daemon
  
## Watch for changes
If to reload configuration after a change.

* **Short form**: -w
* **Long form**: --watch

## Quiet mode
Print no output at all.
        
* **Short form**: -q
* **Long form**: --quiet


## Verbose mode
Sets verbose output.
        
* **Short form**: -v
* **Long form**: --verbose

#Examples

    java -jar print-server-1.0-SNAPSHOT.jar -c routes.xml

Starts the SUB PrintServer with the given configuration file and sends messages to the console. 

    nohup java -jar print-server-1.0-SNAPSHOT.jar -c routes.xml &

Starts the SUB PrintServer with the given configuration file in the background and sends output to '`nohup.out`'. The process will not be terminated at logout.