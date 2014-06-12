Installation and Usage
======================

# Checkout from Git repository

Just run the following command to get a copy of the source code:
> git clone https://github.com/subugoe/ous-print.git

# Compile & package
Not all unit tests are passing yet, therefore you need the following to build all modules:
> mvn -Dmaven.test.skip=true package

The resulting artifacts will be created in the subfolder 'target' in each of the modules. Refer to the module documentaion to learn how to use them,
