Layout Test Resource Bundle
===========================

# Introduction
This package contains all files used by the unit tests of other modules, they are packaged into a Zip file when you install this module. And will be exploded into the folder '`./target/generated-test-resources`' of the modules using the if the test phase is run.

# The files
The files are saved in '`./src/test/resources`', all paths below are relative to this location
## Slips
Slips are saved in the directory '`./hotfolder-in`'

## Templates
There are three different types of layouts some in ASC format, without breaking characters ('`./layout`'), some with breaking characters ('`./layout-broken`') and some in XML format ('`./layout-xml`').

## Other files
There a single slip in '`./test-slips`' which has all fields filled.