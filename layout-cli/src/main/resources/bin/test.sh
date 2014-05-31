#!/bin/sh
# BBK Test Printer 10.0.3.8

java -jar layout-cli-1.0-SNAPSHOT.jar -i ./sub473_2014030316384670_slip001.printed -j TXT -o ./sub473_2014030316384670_slip001.pdf -p PDF -t ./ous40_layout_001_du.asc -x ./layout2fo.xsl

# Print example
# java -jar layout-cli-1.0-SNAPSHOT.jar -i ./sub473_2014030316384670_slip001.printed -j TXT  -p PDF -t ./ous40_layout_001_du.asc -x ./layout2fo.xsl -prt bbk-test


#Example from Maven module (run mvn package first)

#java -jar ./target/layout-cli-1.0-SNAPSHOT.jar -i ../layout/src/test/resources/slips/sub473_2014030316384670_slip001.printed -j TXT -p PDF -t ../layout/src/test/resources/layouts/ous40_layout_001_du.asc -x ../layout/src/main/resources/xslt/layout2fo.xsl -prt PDFwriter -I ../layout/src/main/resources/xslt

#java -jar ./target/layout-cli-1.0-SNAPSHOT.jar -i ../layout/src/test/resources/slips/sub473_2014030316384670_slip001.printed -j TXT -p PDF -t ../layout/src/test/resources/layouts/ous40_layout_001_du.asc -x ../layout/src/main/resources/xslt/layout2fo.xsl -prt sub812 -I ../layout/src/main/resources/xslt -f A5

# LS1 A5 Printer
#java -jar ./target/layout-cli-1.0-SNAPSHOT.jar -i ../layout/src/test/resources/slips/sub473_2014030316384670_slip001.printed -j TXT -p PS -t ../layout/src/test/resources/layouts/ous40_layout_001_du.asc -x ../layout/src/main/resources/xslt/layout2fo.xsl -prt sub812 -f A4 -v -I ../layout/src/main/resources/xslt

#java -jar ./target/layout-cli-1.0-SNAPSHOT.jar -i ../layout/src/test/resources/slips/sub473_2014030316384670_slip001.printed -j TXT -p PS -t ../layout/src/test/resources/layouts/ous40_layout_001_du.asc -x ../layout/src/main/resources/xslt/layout2fo.xsl -o sub473_2014030316384670_slip001.ps -f A4 -v -I ../layout/src/main/resources/xslt

#Magazin Drucker
java -jar ./target/layout-cli-1.0-SNAPSHOT.jar -i ../layout/src/test/resources/slips/sub473_2014030316384670_slip001.printed -j TXT -p PS -t ../layout/src/test/resources/layouts/ous40_layout_001_du.asc -x ../layout/src/main/resources/xslt/layout2fo.xsl -prt sub473 -v -I ../layout/src/main/resources/xslt

