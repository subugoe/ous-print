Installation and Usage
======================

# Checkout from Git repository

Just run the following command to get a copy of the source code:
> git clone https://github.com/subugoe/ous-print.git

# Compile & package
Not all unit tests are passing yet, therefore you need the following to build all modules:
> mvn -Dmaven.test.skip=true package

The resulting artifacts will be created in the subfolder 'target' in each of the modules. Refer to the module documentation to learn how to use them.

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
 