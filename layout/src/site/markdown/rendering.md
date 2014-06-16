Rendering process
=================

# Introduction
This document describes how Layouts are rendered. And how to influence the process. The redering is done by [Apache FOP](http://xmlgraphics.apache.org/fop/). This is a Java library to convert XSL-FO into PDF, Postscript and various other formats. Since it is only with the usual Postscript and PDF fonts(Standard 14 fonts), it fall back to fonts known to the system if another font is specified. Therefore the redering can be done with bundled fonts.

# XSL-FO 
XSL-FO is a XML based style sheet language for XML document formatting. It has a lot of features to describe the desired layout of a page object (either printed or in Formats like PDF or Postscript). Have a look at the [Wikipedia](http://en.wikipedia.org/wiki/XSL_Formatting_Objects) for a broader description. There are several tutorials on the web which can help you learn XSL-FO. Also have a look at the bundled style sheet (`./src/main/resources/xslt/layout2fo.xsl`).

# Font settings
to change the Font settings of the engine, you need to heck out the source code, make your changes and recompile. the fonts used in this package are downloaded during the build process and placed under `./src/main/resources/fonts`, if you want your own fonts place them there. The fonts need also be registered at Apache Fop. This is done within the file `./src/main/resources/xconf/fop.xconf`. Note that you have to add each font to each desired renderer, this is need if you want to use the font in PDF as well as PostScript. You can also do substitutions in there.

For more details please refer to the [documentation](http://xmlgraphics.apache.org/fop/trunk/fonts.html).

## Default font substitutions
The [GNU FreeFonts](https://www.gnu.org/software/freefont/) are bundled with this software package. The following mapping is configured:

<table>
    <tr>
        <td>From font</td><td>To font</td>
    </tr>
    <tr>
        <td>Arial</td><td>FreeSans</td>
    </tr>
    <tr>
        <td>LiberationSans</td><td>FreeSans</td>
    </tr>
    <tr>
        <td>Times</td><td>FreeSerif</td>
    </tr>
    <tr>
        <td>Times New Roman</td><td>FreeSerif</td>
    </tr>
    <tr>
        <td>LiberationSerif</td><td>FreeSerif</td>
    </tr>
</table>

# Page sizes
You will get the best results if you style sheet emits exactly the same page size your printer is configured to. To make this work, it is possible to set the page size either from the route definition of the SUB PrintServer or the command line interface and hand it over to the style sheet. If you create your own style sheet, you are responsible to handle this parameter.