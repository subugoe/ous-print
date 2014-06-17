<?xml version="1.0" encoding="UTF-8"?>
<!--
/*
 * This file is part of the OUS print system, Copyright 2014 SUB Göttingen
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */
-->
<!-- Stupid Open / Libre Office uses their own namespaces for well known prefixes, this is why svg is aliased here: 
     svgoo is SVG from Libre / Open Office
     svg is the real one
     fooo  is SVG from Libre / Open Office
     fo is the real one
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="urn:oasis:names:tc:opendocument:xmlns:style:1.0" xmlns:draw="urn:oasis:names:tc:opendocument:xmlns:drawing:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0" xmlns:svgoo="urn:oasis:names:tc:opendocument:xmlns:svg-compatible:1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:barcode="http://barcode4j.krysalis.org/ns" xmlns:print="http://www.sub.uni-goettingen.de/BE/OUS/print" xmlns:gxsl="http://www.sub.uni-goettingen.de/BE/OUS/gXSL"
    xmlns:fooo="urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:svg="http://www.w3.org/2000/svg"
    xmlns:officeooo="http://openoffice.org/2009/office" exclude-result-prefixes="xd office text style draw svgoo fooo xlink officeooo" version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> May 14, 2014</xd:p>
            <xd:p><xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <xsl:output indent="yes"/>
    <!-- Generated xslt will be in the gxsl namespace, and is aliased for output -->
    <xsl:namespace-alias result-prefix="xsl" stylesheet-prefix="gxsl"/>
    <!-- This file contains the pages size, header, footer and styles -->
    <xsl:variable name="external-styles" select="document('./styles.xml', /)" as="document-node()"/>

    <xsl:variable name="APOS" as="xs:string">'</xsl:variable>
    <xsl:variable name="QUOT" as="xs:string">"</xsl:variable>
    <xsl:variable name="QUOTATIONMARKS" select="concat('[', $APOS, $QUOT, ']')" as="xs:string"/>
    <xsl:variable name="automatic-styles" select="//office:automatic-styles" as="element(office:automatic-styles)"/>
    <!-- Create a union of all named styles -->
    <xsl:variable name="styles" select="$automatic-styles//*[@style:name] | $external-styles//*[@style:name]" as="element()*"/>
    <xsl:variable name="reference-pattern" select="'\$\{entity\s*?=\s*?(\d{3})[\s,]*?attribute\s*?=\s*?(\d{3})\}'" as="xs:string"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="office:document-content|office:body">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="office:text">
        <gxsl:stylesheet version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <gxsl:output indent="yes"/>
            <gxsl:param name="debugParam" select="'false'" as="xs:string"/>
            <gxsl:variable name="debug" select="if ($debugParam castable as xs:boolean) then xs:boolean($debugParam) else false()" as="xs:boolean"/>

            <xsl:comment> Debug text </xsl:comment>
            <gxsl:variable name="debug-text">
                <gxsl:text>Muster</gxsl:text>
            </gxsl:variable>

            <gxsl:template match="/">
                <xsl:comment>Internal variables</xsl:comment>
                <!-- Collect internal variables -->
                <xsl:variable name="variables" as="node()*">
                    <xsl:for-each select="//svgoo:desc[matches(., $reference-pattern)]/text()">
                        <xsl:comment>Results of parsing <xsl:value-of select="."/></xsl:comment>
                        <xsl:analyze-string select="." regex="{$reference-pattern}">
                            <xsl:matching-substring>
                                <gxsl:variable>
                                    <xsl:attribute name="name" select="print:get-variable-name(.)"/>
                                    <xsl:attribute name="select" select="print:get-variable-path(.)"/>
                                    <xsl:attribute name="as" select="'xs:string'"/>
                                </gxsl:variable>
                                <!--
                                <gxsl:comment>Variable definition for expression <xsl:value-of select="."/> Value: <gxsl:value-of select="{concat('$', print:get-variable-name(.))}"></gxsl:value-of></gxsl:comment>
                                -->
                            </xsl:matching-substring>
                        </xsl:analyze-string>
                    </xsl:for-each>
                </xsl:variable>
                <!-- Select distinct variables to get rid of duplicates -->
                <!-- TODO, copy the comments as well | $variables/comment() -->
                <xsl:for-each select="distinct-values($variables/@select)">
                    <xsl:variable name="select" select="." as="xs:string"/>
                    <xsl:if test="$variables[@select = $select]/preceding-sibling::comment()">
                        <xsl:copy-of select="$variables[@select = $select]/preceding-sibling::comment()"/>
                    </xsl:if>
                    <xsl:copy-of select="$variables[@select = $select][1]"/>
                </xsl:for-each>
                <fo:root xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
                    <gxsl:comment>Contents go in here</gxsl:comment>
                    <fo:layout-master-set>
                        <fo:simple-page-master master-name="Master">
                            <!-- Page size -->
                            <xsl:attribute name="page-width" select="$external-styles//style:page-layout/style:page-layout-properties/@fooo:page-width"/>
                            <xsl:attribute name="page-height" select="$external-styles//style:page-layout/style:page-layout-properties/@fooo:page-height"/>
                            <!-- Margins -->
                            <xsl:attribute name="margin-top" select="$external-styles//style:page-layout/style:page-layout-properties/@fooo:margin-top"/>
                            <xsl:attribute name="margin-bottom" select="$external-styles//style:page-layout/style:page-layout-properties/@fooo:margin-bottom"/>
                            <xsl:attribute name="margin-left" select="$external-styles//style:page-layout/style:page-layout-properties/@fooo:margin-left"/>
                            <xsl:attribute name="margin-right" select="$external-styles//style:page-layout/style:page-layout-properties/@fooo:margin-right"/>
                            <fo:region-body region-name="xsl-region-body"/>
                            <xsl:if test="$external-styles//style:page-layout/style:header-style/style:header-footer-properties">
                                <fo:region-before region-name="xsl-region-before">
                                    <xsl:attribute name="extent" select="$external-styles//style:page-layout/style:header-style/style:header-footer-properties/@fooo:margin-bottom"/>
                                </fo:region-before>
                            </xsl:if>
                            <xsl:if test="$external-styles//style:page-layout/style:footer-style/style:header-footer-properties">
                                <fo:region-after region-name="xsl-region-after">
                                    <xsl:attribute name="extent" select="$external-styles//style:page-layout/style:footer-style/style:header-footer-properties/@fooo:margin-top"/>
                                </fo:region-after>
                            </xsl:if>
                        </fo:simple-page-master>
                    </fo:layout-master-set>
                    <fo:page-sequence master-reference="Master">
                        <!-- Header, see http://stackoverflow.com/questions/20657579/how-to-add-header-and-footer-for-every-pages-in-xsl-fo-to-generate-pdf -->
                        <xsl:if test="$external-styles//style:page-layout/style:header-style/style:header-footer-properties">
                            <fo:static-content flow-name="xsl-region-before">
                                <xsl:apply-templates select="$external-styles//office:master-styles/style:master-page/style:header"/>
                            </fo:static-content>
                        </xsl:if>
                        <!-- Footer -->
                        <xsl:if test="$external-styles//style:page-layout/style:footer-style/style:header-footer-properties">
                            <fo:static-content flow-name="xsl-region-after">
                                <xsl:apply-templates select="$external-styles//office:master-styles/style:master-page/style:footer"/>
                            </fo:static-content>
                        </xsl:if>
                        <!-- Main body -->
                        <fo:flow flow-name="xsl-region-body">
                            <xsl:apply-templates/>
                            <gxsl:if test="$debug">
                                <fo:block-container position="absolute" top="0mm" left="0mm" z-index="10">
                                    <fo:block padding="5mm">
                                        <fo:instream-foreign-object scaling="uniform" width="154mm">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="210mm" height="148mm">
                                                <!-- Font was 'Optima' -->
                                                <text x="0" y="300" transform="rotate(-30)" style="font-size: 120pt; fill: grey; font-weight: bold; font-family: FreeSans;">
                                                    <gxsl:value-of select="$debug-text"/>
                                                </text>
                                            </svg>
                                        </fo:instream-foreign-object>
                                    </fo:block>
                                </fo:block-container>
                            </gxsl:if>
                        </fo:flow>
                    </fo:page-sequence>
                </fo:root>
            </gxsl:template>
            <xsl:comment> Copy comment and processing instructions </xsl:comment>
            <gxsl:template match="comment()">
                <gxsl:comment>
                    <gxsl:value-of select="."/>
                </gxsl:comment>
            </gxsl:template>
            <gxsl:template match="processing-instruction()">
                <gxsl:processing-instruction>
                    <xsl:attribute name="name">{name(.)}</xsl:attribute>
                    <gxsl:value-of select="."/>
                </gxsl:processing-instruction>
            </gxsl:template>
            <xsl:comment>Functions</xsl:comment>
            <xsl:comment>Sets the font size for a given String</xsl:comment>
            <gxsl:function name="print:set-font-size" as="element(fo:inline)" xmlns:fo="http://www.w3.org/1999/XSL/Format">
                <gxsl:param name="size" as="xs:integer"/>
                <gxsl:param name="text" as="xs:string"/>
                <fo:inline>
                    <gxsl:attribute name="font-size" select="concat($size, 'pt')"/>
                    <gxsl:copy-of select="$text"/>
                </fo:inline>
            </gxsl:function>
            <gxsl:function name="print:show-if" as="element(fo:inline)" xmlns:fo="http://www.w3.org/1999/XSL/Format">
                <gxsl:param name="obj" as="node()*"/>
                <gxsl:param name="condition" as="xs:boolean"/>
                <fo:inline>
                    <gxsl:attribute name="visibility" select="if ($condition) then 'visible' else 'hidden'"/>
                    <gxsl:copy-of select="$obj"/>
                </fo:inline>
            </gxsl:function>
            <xsl:comment>returns a XSL-FO leader tag, use this for empty lines</xsl:comment>
            <gxsl:function name="print:leader" as="element(fo:leader)" xmlns:fo="http://www.w3.org/1999/XSL/Format">
                <fo:leader leader-pattern="space"/>
            </gxsl:function>
        </gxsl:stylesheet>
        <xsl:comment>Adds a barcode</xsl:comment>
        <gxsl:function name="print:barcode" as="element(fo:instream-foreign-object)" xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <gxsl:param name="message" as="xs:string"/>
            <gxsl:param name="scaling" as="xs:string"/>
            <gxsl:param name="content-width" as="xs:string"/>
            <gxsl:param name="height" as="xs:string"/>
            <gxsl:param name="human-readable" as="xs:string"/>
            <fo:instream-foreign-object>
                <gxsl:attribute name="scaling" select="$scaling"/>
                <gxsl:attribute name="content-width" select="$content-width"/>
                <gxsl:comment>Barcode, see http://barcode4j.sourceforge.net/2.1/fop-ext.html</gxsl:comment>
                <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns" render-mode="svg">
                    <gxsl:attribute name="message">
                        <gxsl:value-of select="$message"/>
                    </gxsl:attribute>
                    <barcode:code39>
                        <!-- See http://barcode4j.sourceforge.net/2.1/barcode-xml.html -->
                        <barcode:height>
                            <gxsl:value-of select="$height"/>
                        </barcode:height>
                        <barcode:human-readable>
                            <gxsl:value-of select="$human-readable"/>
                        </barcode:human-readable>
                    </barcode:code39>
                </barcode:barcode>
            </fo:instream-foreign-object>
        </gxsl:function>
    </xsl:template>
    <!-- Templates for styles -->
    <xsl:template match="style:header|style:footer">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Templates for layouts -->

    <xsl:template match="draw:frame">
        <fo:block-container position="absolute">
            <xsl:variable name="attributes" as="attribute()*">
                <xsl:apply-templates select="@*"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="$attributes[name(.) = 'reference-orientation']">
                    <xsl:copy-of select="$attributes except @top"/>
                    <!-- Rewrite top attribute top = top - width -->
                    <xsl:attribute name="top" select="print:calculate($attributes[name(.) = 'top'], '-', $attributes[name(.) = 'width'])"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$attributes"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates/>
        </fo:block-container>
    </xsl:template>

    <!-- One frame can contain multiple image representations, choose the SVG one and ignore others, otherwise apply image element -->
    <xsl:template match="draw:frame[child::draw:image]">
        <fo:block-container position="absolute">
            <xsl:apply-templates select="@*"/>
            <xsl:choose>
                <xsl:when test="./child::draw:image[ends-with(@xlink:href, '.svg')]">
                    <!-- TODO: create a function for this -->
                    <xsl:variable name="image" select="document(./child::draw:image[ends-with(@xlink:href, '.svg')]/@xlink:href, /)" as="document-node()"/>
                    <gxsl:comment>A SVG image (<xsl:value-of select="./child::draw:image[ends-with(@xlink:href, '.svg')]/@xlink:href"/>)</gxsl:comment>
                    <fo:block>
                        <fo:instream-foreign-object scaling="uniform">
                            <xsl:if test="@svgoo:width">
                                <xsl:attribute name="content-width" select="@svgoo:width"/>
                            </xsl:if>
                            <xsl:copy-of select="$image/*"/>
                        </fo:instream-foreign-object>
                    </fo:block>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
        </fo:block-container>
    </xsl:template>

    <!-- Shapes, currently unsupported, use at your own risk -->
    <!-- TODO: check if it works this way otherwise hande elements seperatly -->
    <xsl:template match="draw:ellipse|draw:rect|draw:line">
        <!-- TODO: map styles and size -->
        <gxsl:comment>A <xsl:value-of select="local-name(.)"/></gxsl:comment>
        <fo:block>
            <xsl:apply-templates select="@* except @svgoo:*" mode="fo"/>
            <fo:instream-foreign-object>
                <svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%">
                    <xsl:element name="{local-name(.)}" namespace="http://www.w3.org/2000/svg">
                        <xsl:apply-templates select="@svgoo:*" mode="svg"/>
                    </xsl:element>
                </svg>
            </fo:instream-foreign-object>
        </fo:block>
    </xsl:template>
    <xsl:template match="draw:custom-shape">
        <gxsl:comment>A polygon</gxsl:comment>
        <xsl:message>Polygons aren't supported</xsl:message>
        <!-- TODO: This doesn't work, and maybe never will -->
        <fo:instream-foreign-object>
            <svg xmlns="http://www.w3.org/2000/svg">
                <xsl:attribute name="viewBox" select="./draw:enhanced-geometry/@svgoo:viewBox"/>
                <polygon>
                    <xsl:attribute name="points" select="./draw:enhanced-geometry/@draw:enhanced-path"/>
                </polygon>
            </svg>
        </fo:instream-foreign-object>
    </xsl:template>
    <!-- Images, embed SVG images otherwise warn -->
    <xsl:template match="draw:image">
        <xsl:choose>
            <xsl:when test="ends-with(@xlink:href, '.svg')">
                <xsl:variable name="image" select="document(@xlink:href, /)" as="document-node()"/>
                <gxsl:comment>A SVG image (<xsl:value-of select="@xlink:href"/>)</gxsl:comment>
                <fo:block>
                    <xsl:apply-templates select="@*"/>
                    <fo:instream-foreign-object>
                        <xsl:copy-of select="$image/*"/>
                    </fo:instream-foreign-object>
                </fo:block>
            </xsl:when>
            <!-- TODO: Check if we can load the image as unparsed text, encode it to Base64 and add it inline by guessing the mime type -->
            <!--
            <xsl:when test="ends-with(@xlink:href, '.png')">   
                <xsl:variable name="image" select="unparsed-text(@xlink:href)"/>
                <fo:external-graphic src="url('data:imge/png;base64,<DATA BASE64 ENCODED HERE')"/>
            </xsl:when>
            -->
            <xsl:otherwise>
                <xsl:variable name="message">
                    Image Type of <xsl:value-of select="@xlink:href"/> not supported!
                </xsl:variable>
                <xsl:message select="$message"/>
                <gxsl:comment>If you expect an image here: <xsl:value-of select="$message"/></gxsl:comment>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- Text in XSL-FO mode -->
    <xsl:template match="text:p">
        <fo:block>
            <xsl:apply-templates select="@*"/>
            <!-- text:p's in frames with description -->
            <xsl:choose>
                <xsl:when test="parent::draw:text-box/following-sibling::svgoo:desc">
                    <xsl:copy-of select="print:el-parse(parent::draw:text-box/following-sibling::svgoo:desc/text())"/>
                    <gxsl:comment>If your style sheet fails here, you might have an XPath error inside the description</gxsl:comment>
                </xsl:when>
                <!-- text:p's without descriptions -->
                <xsl:otherwise>
                    <xsl:apply-templates select="@*"/>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
        </fo:block>
    </xsl:template>
    <!-- Text in SVG mode -->
    <xsl:template match="text:p" mode="svg">
        <svg:text xmlns:svg="http://www.w3.org/2000/svg">
            <xsl:if test="ancestor::draw:frame/@draw:transform">
                <xsl:apply-templates select="ancestor::draw:frame/@draw:transform" mode="svg"/>
            </xsl:if>
            <!-- 
            <xsl:variable name="style-attributes" as="attribute()*">
                <xsl:apply-templates select="ancestor-or-self::draw:frame//@* except ancestor::draw:frame/@draw:transform" mode="svg"/>
            </xsl:variable>
            <xsl:attribute name="style">
                <xsl:for-each select="$style-attributes">
                    <xsl:value-of select="concat(name(.), ': ' ,data(.), ';')"/>
                </xsl:for-each>
            </xsl:attribute>
            <xsl:apply-templates select="@*" mode="svg"/>
            -->
            <xsl:apply-templates select="ancestor-or-self::draw:frame//@* except ancestor::draw:frame/@draw:transform | @*" mode="svg"/>

            <!-- text:p's in frames with description -->
            <xsl:choose>
                <xsl:when test="parent::draw:text-box/following-sibling::svgoo:desc">
                    <xsl:copy-of select="print:el-parse(parent::draw:text-box/following-sibling::svgoo:desc/text())"/>
                    <gxsl:comment>If your style sheet fails here, you might have an XPath error inside the description</gxsl:comment>
                </xsl:when>
                <!-- text:p's without descriptions -->
                <xsl:otherwise>
                    <xsl:apply-templates select="@*" mode="svg"/>
                    <xsl:apply-templates mode="svg"/>
                </xsl:otherwise>
            </xsl:choose>
        </svg:text>
    </xsl:template>


    <xsl:template match="text:span" mode="#default">
        <fo:inline>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </fo:inline>
    </xsl:template>
    <xsl:template match="text:span" mode="svg">
        <svg:tspan>
            <xsl:apply-templates select="@*" mode="svg"/>
            <xsl:apply-templates/>
        </svg:tspan>
    </xsl:template>

    <!-- Lists -->
    <xsl:template match="text:list">
        <fo:list-block>
            <gxsl:comment>List are not really supported</gxsl:comment>
            <xsl:apply-templates/>
        </fo:list-block>
    </xsl:template>
    <xsl:template match="text:list-item">
        <fo:list-item>
            <fo:list-item-label>
                <fo:block/>
            </fo:list-item-label>
            <fo:list-item-body>
                <xsl:apply-templates/>
            </fo:list-item-body>
        </fo:list-item>
    </xsl:template>

    <xsl:template match="draw:text-box">
        <xsl:apply-templates select="@*"/>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- Attributes to match, this is mainly a mapping from one namespace to another.
         This way we can use a wildcard to catch unmaped attribute.
         There is one mode for XSL-FO results and one for SVG (not fully implemented yet)
    -->
    <!-- First Attributes in XSL-FO -->
    <xsl:template match="@svgoo:width|@svgoo:height|@draw:z-index">
        <xsl:attribute name="{local-name(.)}" select="."/>
    </xsl:template>

    <xsl:template match="@svgoo:y">
        <xsl:attribute name="top" select="."/>
    </xsl:template>
    <xsl:template match="@svgoo:x">
        <xsl:attribute name="left" select="."/>
    </xsl:template>

    <!-- Margins -->
    <xsl:template match="@fooo:margin-left|@fooo:margin-right|@fooo:margin-top|@fooo:margin-bottom">
        <xsl:attribute name="{local-name(.)}" select="."/>
    </xsl:template>
    <!-- Other FO attributes -->
    <xsl:template match="@fooo:text-align|@fooo:border|@fooo:padding|@fooo:clip|@fooo:min-height|@fooo:color|@fooo:background-color|@fooo:font-size|@fooo:font-weight|@fooo:font-style|@fooo:text-align"
        mode="#all">
        <xsl:attribute name="{local-name(.)}" select="."/>
    </xsl:template>

    <xsl:template match="@style:font-name">
        <xsl:variable name="font-name" select="."/>
        <xsl:attribute name="font-family" select="//style:font-face[@style:name = $font-name]/@svgoo:font-family"/>
    </xsl:template>
    <xsl:template match="@style:vertical-pos">
        <xsl:attribute name="vertical-align" select="."/>
    </xsl:template>
    <xsl:template match="@draw:transform">
        <!--
        <xsl:copy-of select="print:reference-orientation(.)"/>
        -->
        <xsl:copy-of select="print:transform(.)"/>
    </xsl:template>

    <!-- Wrapped styles, these contains additional styles -->
    <xsl:template match="@draw:style-name|@draw:text-style-name|@text:style-name|@style:parent-style-name|@style:list-style-name">
        <xsl:variable name="style-name" select="data(.)"/>
        <xsl:message>Resolving style <xsl:value-of select="$style-name"/></xsl:message>
        <xsl:apply-templates select="$styles[@style:name = $style-name]/@*" mode="#current"/>
    </xsl:template>

    <xsl:template match="@style:family" mode="#all">
        <xsl:apply-templates select="../style:text-properties/@*|../style:graphic-properties/@*|../style:paragraph-properties/@*" mode="#current"/>
    </xsl:template>

    <!-- Attributes to ignore -->
    <xsl:template
        match="@draw:name|@text:anchor-type|@style:name|@style:class|@style:page-layout-name|@office:version|@officeooo:*|@style:contextual-spacing|@text:number-lines|@text:line-number|@style:vertical-rel|@xlink:*|@draw:textarea-horizontal-align|@draw:textarea-vertical-align|@style:protect"/>
    <!-- Asian attributes to ignore -->
    <xsl:template
        match="@style:font-name-asian|@style:font-weight-asian|@style:font-size-asian|@style:font-style-asian|@style:font-pitch-asian|@style:font-family-asian|@style:font-family-generic-asian"/>
    <!-- Complex settings to ignore -->
    <xsl:template match="@style:font-name-complex|@style:font-size-complex|@style:font-weight-complex|@style:font-style-complex|@style:font-family-generic-complex"/>
    <!-- Font settings to ignore -->
    <xsl:template match="@fo:font-family|@style:font-family-generic"/>

    <!-- Wrap options, currently ignored -->
    <xsl:template match="@style:wrap|@style:number-wrapped-paragraphs|@style:wrap-contour|@draw:wrap-influence-on-position|@style:text-autospace">
        <xsl:message>Unmached attribute ignored: <xsl:value-of select="name(.)"/>, value: <xsl:value-of select="data(.)"/></xsl:message>
    </xsl:template>
    <!-- Other atriutes that may be mapped in the future -->
    <xsl:template match="@style:horizontal-pos|@style:horizontal-rel|@style:mirror">
        <xsl:message>Unmached attribute ignored: <xsl:value-of select="name(.)"/>, value: <xsl:value-of select="data(.)"/></xsl:message>
    </xsl:template>
    <!-- Attributes that might can be mapped to SVG -->
    <xsl:template
        match="@draw:luminance|@draw:contrast|@draw:red|@draw:green|@draw:blue|@draw:gamma|@draw:color-inversion|@draw:image-opacity|@draw:color-mode|@style:run-through|@style:flow-with-text">
        <xsl:message>Unmached attribute ignored: <xsl:value-of select="name(.)"/>, value: <xsl:value-of select="data(.)"/></xsl:message>
    </xsl:template>
    <!-- Stuff to map to SVG -->
    <xsl:template match="@style:vertical-pos|@draw:stroke|@draw:fill|@draw:fill-color|@fooo:font-weight" mode="svg">
        <xsl:attribute name="{local-name(.)}" select="."/>
    </xsl:template>

    <!-- SVG attributes -->
    <xsl:template match="@svgoo:stroke-color|@svgoo:x1|@svgoo:y1|@svgoo:x2|@svgoo:y2|@svgoo:x|@svgoo:y" mode="svg">
        <xsl:attribute name="{local-name(.)}" select="."/>
    </xsl:template>
    <xsl:template match="@draw:transform" mode="svg">
        <xsl:attribute name="{local-name(.)}" select="print:rewrite-transform(.)"/>
    </xsl:template>
    <!-- SVG Attributes to ignore in XSL-FO mode -->
    <xsl:template match="@draw:stroke|@svgoo:stroke-color|@draw:fill|@draw:fill-color"/>
    <!-- SVG Text -->
    <xsl:template match="text()" mode="svg">
        <xsl:value-of select="."/>
    </xsl:template>
    <!-- SVG Atributes to map -->
    <xsl:template match="@style:wrap|@style:number-wrapped-paragraphs|@style:wrap-contour|@draw:wrap-influence-on-position|@style:text-autospace|@fooo:text-align|@fooo:min-height|@style:font-name"
        mode="svg"/>

    <!-- Unmactched atibutes in svg mode -->
    <xsl:template match="@*" mode="svg">
        <xsl:message>Unmached attribute: <xsl:value-of select="name(.)"/>, value: <xsl:value-of select="data(.)"/></xsl:message>
    </xsl:template>
    <!-- All other attributes -->
    <xsl:template match="@*" mode="#default">
        <xsl:message terminate="yes">Unmached attribute: <xsl:value-of select="name(.)"/>, value: <xsl:value-of select="data(.)"/></xsl:message>
    </xsl:template>

    <!-- Other Stuff -->
    <!-- Catch template to find unmapped tags -->
    <xsl:template match="*">
        <xsl:message terminate="yes">Unmached element: <xsl:value-of select="name(.)"/></xsl:message>
    </xsl:template>
    <!-- Elements to ignore on direct match -->
    <xsl:template match="office:scripts|office:font-face-decls|office:automatic-styles|svgoo:desc|text:sequence-decls" mode="#all"/>
    <!-- Copy comment and processing instructions -->
    <xsl:template match="comment()">
        <xsl:comment><xsl:value-of select="."/></xsl:comment>
    </xsl:template>
    <xsl:template match="processing-instruction()">
        <xsl:processing-instruction name="{name(.)}">
            <xsl:value-of select="."/>
        </xsl:processing-instruction>
    </xsl:template>

    <!-- Functions -->
    <!-- Calculates a variable name for a expression -->
    <xsl:function name="print:get-variable-name" as="xs:string">
        <xsl:param name="str" as="xs:string"/>
        <xsl:variable name="entity" select="replace($str, concat('^', $reference-pattern, '$'), '$1')" as="xs:string"/>
        <xsl:variable name="attribute" select="replace($str, concat('^', $reference-pattern, '$'), '$2')" as="xs:string"/>
        <xsl:value-of select="concat('entity', $entity, 'attribute', $attribute)"/>
    </xsl:function>
    <!-- Calculates a XPath for a expression -->
    <xsl:function name="print:get-variable-path" as="xs:string">
        <xsl:param name="str" as="xs:string"/>
        <xsl:variable name="entity" select="replace($str, concat('^', $reference-pattern, '$'), '$1')" as="xs:string"/>
        <xsl:variable name="attribute" select="replace($str, concat('^', $reference-pattern, '$'), '$2')" as="xs:string"/>
        <xsl:value-of select="concat('(//entry[@entity = ', $APOS, $entity, $APOS, ' and @attribute = ', $APOS, $attribute, $APOS, ']/@value)[1]')"/>
    </xsl:function>
    <!-- This functions make up the expression language
         It consists of Atoms and Functions
         A Atom is something like 'entity=(\d*)[\s,]*?attribute=(\d*)' basicly a external reference
         Functions are XPath functions, note that these will be evaluated at runtime, errors won't be reported by this stylesheet
    -->
    <!-- Check if expression is atomic
         Expresions are atomic if the string only contains a reference
    -->
    <xsl:function name="print:el-is-atomic" as="xs:boolean">
        <xsl:param name="str" as="xs:string"/>
        <xsl:choose>
            <xsl:when test="matches($str, concat('^', $reference-pattern, '$'))">
                <xsl:copy-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    <!-- Check if expression is complex
         Expresions are complex if the string contains one ore more references and other things like functions
    -->
    <xsl:function name="print:el-is-complex" as="xs:boolean">
        <xsl:param name="str" as="xs:string"/>
        <xsl:choose>
            <xsl:when test="matches($str, concat('(', $reference-pattern, ')+'))">
                <xsl:copy-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <!-- Parses an expression -->
    <xsl:function name="print:el-parse" as="element(xsl:copy-of)">
        <xsl:param name="str" as="xs:string"/>
        <xsl:element name="xsl:copy-of">
            <xsl:attribute name="select">
                <xsl:choose>
                    <xsl:when test="print:el-is-atomic($str)">
                        <xsl:value-of select="concat('$', print:get-variable-name($str))"/>
                    </xsl:when>
                    <xsl:when test="print:el-is-complex($str)">
                        <xsl:analyze-string select="$str" regex="{$reference-pattern}">
                            <xsl:matching-substring>
                                <xsl:value-of select="concat('$', print:get-variable-name(.))"/>
                            </xsl:matching-substring>
                            <xsl:non-matching-substring>
                                <xsl:value-of select="."/>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:message terminate="yes">
                    Couldn't parse expression: <xsl:value-of select="$str"/>
                        </xsl:message>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </xsl:element>
    </xsl:function>

    <!-- Utility functions -->

    <!-- Interprets the @draw:transform attribute and generates XSL-FO attributes -->
    <xsl:function name="print:transform" as="attribute()*">
        <xsl:param name="str" as="xs:string"/>
        <xsl:variable name="rad" select="number(replace($str, '^.*rotate\s+\(([\d\.]+?)\).*$', '$1'))" as="xs:double"/>
        <xsl:variable name="left" select="replace($str, '.*translate\s*?\((.[^ ]*)\s*(.[^ ]*)\)', '$1')" as="xs:string"/>
        <xsl:variable name="top" select="replace($str, '.*translate\s*?\((.[^ ]*)\s*(.[^ ]*)\)', '$2')" as="xs:string"/>
        <xsl:attribute name="reference-orientation" select="round(print:rad-to-degree($rad))"/>
        <xsl:attribute name="left" select="$left"/>
        <xsl:attribute name="top" select="$top"/>
    </xsl:function>

    <!-- Takes a string and escapes charackters that should me matched literaly -->
    <xsl:function name="print:escape-regex" as="xs:string">
        <xsl:param name="str" as="xs:string"/>
        <xsl:value-of select="replace($str, '\.|\*|\?|\+|\^\|\$|\{|\}|\[|\]|\(|\)|\-|\\', '\\$1')"/>
    </xsl:function>
    <!-- Rewrite Transfor attribute from rad (Libre / Open Office) to degree (SVG) -->
    <xsl:function name="print:rewrite-transform">
        <!-- See http://mail-archives.apache.org/mod_mbox/incubator-ooo-dev/201208.mbox/%3C50269758.3090901@t-online.de%3E -->
        <xsl:param name="str"/>
        <xsl:analyze-string select="$str" regex="rotate\s+([\d\.]+?)">
            <xsl:matching-substring>
                <xsl:value-of select="concat('rotate (', print:rad-to-degree(number(replace(., 'rotate\s+\(([\d\.]+?)\)', '$1'))), ')')"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <xsl:value-of select="."/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:function>

    <!-- Converts radians to degrees -->
    <xsl:function name="print:rad-to-degree">
        <!-- See http://en.wikipedia.org/wiki/Radian#Conversions 
             And http://www.exslt.org/math/functions/constant/math.constant.template.xsl.html
        -->
        <xsl:param name="rad" as="xs:double"/>
        <xsl:variable name="pi" select="3.1415926535897932384626433832795028841971693993751" as="xs:double"/>
        <xsl:value-of select="$rad*(180 div $pi)"/>
    </xsl:function>

    <!-- Calculates some measurements -->
    <xsl:function name="print:calculate" as="xs:string">
        <xsl:param name="arg1" as="xs:string"/>
        <xsl:param name="operation" as="xs:string"/>
        <xsl:param name="arg2" as="xs:string"/>
        <xsl:variable name="unit" select="replace($arg1, '[\d\.]*?([^\d\.]+)', '$1')" as="xs:string"/>
        <!-- Use this if you  want to check if units match -->
        <xsl:if test="not(contains($arg2, $unit))">
            <xsl:message terminate="yes">Units doesn't match!</xsl:message>
        </xsl:if>
        <xsl:variable name="int1" select="number(replace($arg1, '([\d\.]+)[^\d]*', '$1'))" as="xs:double"/>
        <xsl:variable name="int2" select="number(replace($arg2, '([\d\.]+)[^\d]*', '$1'))" as="xs:double"/>
        <xsl:variable name="result" as="xs:double">
            <xsl:choose>
                <xsl:when test="$operation = '+'">
                    <xsl:value-of select="$int1 + $int2"/>
                </xsl:when>
                <xsl:when test="$operation = '-'">
                    <xsl:value-of select="$int1 - $int2"/>
                </xsl:when>
                <xsl:when test="$operation = '*'">
                    <xsl:value-of select="$int1 * $int2"/>
                </xsl:when>
                <xsl:when test="$operation = '/' or $operation = 'div'">
                    <xsl:value-of select="$int1 div $int2"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message terminate="yes">Unsupported operation!</xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="concat(string($result), $unit)"/>
    </xsl:function>

</xsl:stylesheet>
