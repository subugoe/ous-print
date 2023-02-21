Installation and Usage
======================

# Checkout from Git repository

Just run the following command to get a copy of the source code:
> git clone https://github.com/subugoe/ous-print.git

# Compile & package
Not all unit tests are passing yet, therefore you need the following to build all modules:
> mvn -Dmaven.test.skip=true package

On platforms that use their own encoding (Solaris, Windows, MacOS) you may need to set UTF-8 as platform encoding.
You can do this by seting your own .mavenrc file (on Solaris and MacOS)
> echo "MAVEN_OPTS=' -Dfile.encoding=UTF-8'" > ~/.mavenrc
If you are working on MacOS with a current JDK from Oraclte (not Apple) make sure it's used by Maven
> echo "JAVA_HOME=`/usr/libexec/java_home`" >> ~/.mavenrc

The resulting artifacts will be created in the subfolder 'target' in each of the modules. Refer to the module documentation to learn how to use them.

## Installing the test data module

To make sure the test data module is present during development make sure you have the module installed: 

> mvn install -pl layout-test

**You need a internet connection to build this software since dependencies will be downloaded. Without internet connection the build will fail!**

# Installation
The compiled artifacts can be copied to your server, into a empty directory. Make sure you copy the depending files, like the configuration of routes, stylesheets and images as well. The best way is to place the CLI interface and the SUB PrintServer next to each other, this way you can test the settings of the server manually.

## Example directory layout
Bullet points represent files, points with subitems are directories.

* layout-cli-1.0-SNAPSHOT.jar
* print-server-1.0-SNAPSHOT.jar
* routes.xml
* lib
  * lib.xsl 
* img
  * GAU-SUBlogo-einzeilig.svg
* xslfo
  * layout2fo.xsl

# Running on Solaris
Make sure that you start java with the right encoding setting (-Dfile.encoding=UTF-8) otherwise umlauts will be missing

Example:
> java -Dfile.encoding=UTF-8 -jar print-server-1.0-SNAPSHOT.jar -c test-routes.xml