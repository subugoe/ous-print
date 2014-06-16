How it works
============

#Introduction
This documents describes how the layout engine is working.

# Layouts
To understand how this is working you need to understand how a layout is defined. The layouts (ASC files) are described in a formats which is designed to place the head of a printer at a certain position identified by line and column. Definitions of lines are ordered by line number and column, the same line can be addressed multiple times for different columns. The output has a maximum length of text taken either from the database or a external text file (depending of the definition of entity and attribute). Each text can be prefixed for output. Comments start with „;“. These Files are encoded in ASCII, but the might be encoding errors. These are will be silently fixed in the command line wrapper, the style sheets can’t deal with them.

There are two different types of layouts, which use the same file association (.asc):
* Normal layouts are used by the software directly
* Text layouts are included texts in normal layouts (these aren’t covered completely yet)

## Sample for a normal layout:


> 010 005 004 004 000 A 015

>; Entleiher:

> 010 049 001 004 000 A 013 Nutzertyp:

> 010 064 001 002 000 A 013

### Column Meaning
1. LIN: line
2. KOL: column
3. ENT: entry
4. ATT: attribute
5. SEQ: sequence
6. T: type
7. LEN: length
8. TEXT: text

## Sample for a text layout:

> 0 400 0118 DU Rechtsbehelfsbelehrung:

> 0 400 0119 DU Gegen diesen Bescheid kann innerhalb eines Monats nach Bekanntgabe Klage

> 0 400 0120 DU erhoben werden.

### Column Meaning
1. File
2. Type
3. Nr
4. Language
5. Text

# The style sheets
## asc2xml.xsl
The style sheet asc2xml.xsl creates a XML representation of those files which the can be either be transformed to a parser of resulting files or back into ASC files. The XML representation of the ASC files ist straight forward, every line becomes an element, the different columns are transformed into attributes. It supports both, the normal and the text layout format.
### Parameters
* encoding: The encoding to read the file (default: ASCII)
* layout: the file to read

## xml2asc.xsl
The style sheet xml2asc.xsl transfers a XML representation of an ASC file back into text format, this can be used by style sheets that generate ASC files in XML format.

### Parameters:
* output-heading: Should a field definition be outputted (default: false)

## xml2parser.xsl
The style sheet xml2parser.xsl creates a XSLT based parser for generated text files (slips). It's better to convert the gerated files into UTF-8 before passing the to the stylesheet. The Groovy wrapper does this automaticly.

## layout2fo.xsl
The style sheet layout2fo.xsl generates a XSL-FO file from a XML representation of a slip. This is later passed to [Apache FOP](http://xmlgraphics.apache.org/fop/) to generate a PDF or Postscript file from it.
### Parameters:
* debug: print a text across the generated Layout, which reads "Muster"
* barcode: should the barcode of a book be included

## odf2xslfo.xsl
The style sheet odf2xslfo.xsl can be used to generate a XSL-FO file from ODF ([Open](https://www.openoffice.org/) / [Libre Office](http://www.libreoffice.org/)), see [the documentation](./odf.html) of this feature.

# Groovy wrapper classes

The stylesheets are encapsulated in Groovy wrapper classes. For more details on the refer to the generated [GroovyDocs](./groovydoc/index.html).

