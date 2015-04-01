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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs xd print" xmlns:print="http://www.sub.uni-goettingen.de/BE/OUS/print" version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>
                <xd:b>Created on:</xd:b> Mar 20, 2014</xd:p>
            <xd:p>
                <xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <xsl:output indent="yes"/>
    <xsl:param name="debugParam" select="'false'" as="xs:string"/>
    <xsl:param name="barcodeParam" select="'false'" as="xs:string"/>
    <xsl:param name="format" select="'A5'" as="xs:string"/>
    <xsl:param name="version" select="1.3"/>

    <!-- This is needed for Type casting -->
    <xsl:variable name="debug" select="if ($debugParam castable as xs:boolean) then xs:boolean($debugParam) else false()" as="xs:boolean"/>
    <xsl:variable name="barcode" select="if ($barcodeParam castable as xs:boolean) then xs:boolean($barcodeParam) else true()" as="xs:boolean"/>
    <xsl:variable name="template-info">
        <xsl:text>V</xsl:text>
        <xsl:value-of select="$version"/>
        <xsl:text> Layout: 001</xsl:text>
    </xsl:variable>
    <!--
    Definition of possible paper formats
    -->
    <xsl:variable name="paper-formats">
        <!-- A5 landscape -->
        <format name="A5" width="210mm" height="148mm" margin="13mm" orientation="LANDSCAPE"/>
        <!-- A4 portrait -->
        <format name="A4" width="210mm" height="297mm" margin="13mm" orientation="PORTRAIT"/>
    </xsl:variable>
    <!-- Sets the paper format the result is set to -->
    <!--
    <xsl:variable name="DEFAULTFORMAT" select="'A5'"/>
    <xsl:variable name="DEFAULTORIENTATION " select="'A5'"/>
    -->
    <!-- Debug text -->
    <xsl:variable name="debug-text">
        <xsl:text>Muster</xsl:text>
    </xsl:variable>
    <xsl:template match="/">
        <xsl:variable name="user-type" select="replace(//entry[@entity = '001' and @attribute = '004']/@value, '^(\d+)\s* .*$', '$1')"/>
        <xsl:variable name="loan-type" select="//entry[@entity = '005' and @attribute = '006']/@value"/>
        <xsl:variable name="loan-type-decription" select="//entry[@entity = '005' and @attribute = '106']/@value"/>
        <!-- Full user ID -->
        <xsl:variable name="user-id-full" select="(//entry[@entity = '001' and @attribute = '002']/@value)[1]"/>
        <!-- Truncated user ID -->
        <!-- User may contain X character, so \w isn' sufficient  -->
        <xsl:variable name="user-id" select="replace((//entry[@entity = '001' and @attribute = '002']/@value)[1], '^.*(\w{5})$', '$1')" as="xs:string"/>
        <xsl:variable name="location" select="//entry[@entity = '052' and @attribute = '005']/@value" as="xs:string"/>
        <xsl:variable name="target" select="//entry[@entity = '011' and @attribute = '004']/@value" as="xs:string"/>
        <xsl:variable name="user-title" select="//entry[@entity = '001' and @attribute = '010']/@value" as="xs:string"/>
        <xsl:variable name="user-name"
            select="concat((//entry[@entity = '001' and @attribute = '010']/@value)[1], (//entry[@entity = '001' and @attribute = '006']/@value)[1], ' ', (//entry[@entity = '001' and @attribute = '008']/@value)[1])"/>
        <xsl:variable name="user-firstname" select="(//entry[@entity = '001' and @attribute = '006']/@value)[1]" as="xs:string"/>
        <xsl:variable name="user-lastname" select="(//entry[@entity = '001' and @attribute = '008']/@value)[1]" as="xs:string"/>
        <xsl:variable name="user-initial" select="upper-case(substring((//entry[@entity = '001' and @attribute = '008']/@value)[1], 1, 1))" as="xs:string"/>
        <xsl:variable name="template-suffix"
            select="concat('|', upper-case(substring((//entry[@entity = '001' and @attribute = '006']/@value)[1], 1, 1)), upper-case(substring((//entry[@entity = '001' and @attribute = '008']/@value)[1], 1, 1)))"/>
        <xsl:variable name="master-name" select="upper-case($format)"/>
        <xsl:variable name="width" select="$paper-formats/format[@name = upper-case($format)]/@width" as="xs:string"/>
        <xsl:variable name="height" select="$paper-formats/format[@name = upper-case($format)]/@height" as="xs:string"/>
        <xsl:variable name="margin" select="$paper-formats/format[@name = upper-case($format)]/@margin" as="xs:string"/>
        <xsl:variable name="date" select="//entry[@entity = '098' and @attribute = '001']/@value" as="xs:string"/>
        <xsl:variable name="time" select="//entry[@entity = '098' and @attribute = '002']/@value" as="xs:string"/>
        <xsl:variable name="adress-extra" select="//entry[@entity = '012' and @attribute = '020']/@value" as="xs:string"/>
        <xsl:variable name="notabene" select="//entry[@entity = '001' and @attribute = '016']/@value"/>
        <xsl:variable name="volume" select="//entry[@entity = '004' and @attribute = '010']/@value" as="xs:string"/>

        <!-- Other fields -->
        <xsl:variable name="relocation" select="//entry[@entity = '004' and @attribute = '003']/@value" as="xs:string"/>
        <xsl:variable name="volume-free-text" select="//entry[@entity = '004' and @attribute = '011']/@value" as="xs:string"/>
        <xsl:variable name="loan-free-text" select="//entry[@entity = '005' and @attribute = '017']/@value"/>
        <xsl:variable name="co" select="//entry[@entity = '012' and @attribute = '014']/@value"/>

        <xsl:comment>
            <xsl:text>Some of the used variables:</xsl:text><xsl:text>&#xA;</xsl:text>
            <xsl:text>First name of the user: </xsl:text><xsl:value-of select="$user-firstname"/><xsl:text>&#xA;</xsl:text>
            <xsl:text>Last name of the user: </xsl:text><xsl:value-of select="$user-lastname"/><xsl:text>&#xA;</xsl:text>
            <xsl:text>Title of the user: </xsl:text><xsl:value-of select="$user-title"/><xsl:text>&#xA;</xsl:text>
            <xsl:text>Number of the user: </xsl:text><xsl:value-of select="$user-id-full"/><xsl:text>&#xA;</xsl:text>
            
            <xsl:text>Date: </xsl:text><xsl:value-of select="$date"/><xsl:text>&#xA;</xsl:text>
            <xsl:text>Time: </xsl:text><xsl:value-of select="$time"/><xsl:text>&#xA;</xsl:text>
        </xsl:comment>


        <!-- Document root -->
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="{$master-name}" page-width="{$width}" page-height="{$height}" margin="{$margin}">
                    <fo:region-body/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="{$master-name}">
                <fo:flow flow-name="xsl-region-body">
                    <!-- contents should be saved in here -->
                    <xsl:comment>Shelfmark</xsl:comment>
                    <xsl:variable name="shelfmark" select="//entry[@entity = '050' and @attribute = '013']/@value" as="xs:string"/>

                    <fo:block-container position="absolute" top="0mm" left="0mm" width="125mm">
                        <fo:block font-family="FreeSans" font-size="30pt">
                            <!-- We use XPath based logic here to be able to inject it from else were, like a better layouting system  -->
                            <xsl:copy-of
                                select="if (string-length($shelfmark) &lt; 17) then print:set-font-size(24) (: was 36:)
                                            else if (string-length($shelfmark) &lt; 23) then print:set-font-size(24) (: was 30:)
                                            else if (string-length($shelfmark) &lt; 34) then print:set-font-size(24) (: was 24:)
                                            else print:set-font-size(16)"/>
                            <xsl:value-of select="$shelfmark"/>
                        </fo:block>
                        <!-- Volume -->
                        <xsl:comment>Volume information and relocation</xsl:comment>
                        <fo:block font-family="FreeSans" font-size="14pt" text-align-last="justify">
                            <xsl:value-of select="$volume"/>
                            <fo:leader leader-pattern="space"/>
                            <xsl:value-of select="$relocation"/>
                        </fo:block>
                        <fo:block font-family="FreeSans" font-size="14pt">
                            <xsl:value-of select="$volume-free-text"/>
                        </fo:block>
                    </fo:block-container>

                    <fo:block-container position="absolute" left="95mm" top="25mm" width="30mm">
                        <xsl:comment>Date</xsl:comment>
                        <fo:block text-align="right" font-family="FreeSans" font-size="12pt">
                            <xsl:value-of select="$date"/>
                        </fo:block>
                        <xsl:comment>Time</xsl:comment>
                        <fo:block text-align="right" font-family="FreeSans" font-size="10pt" color="gray">
                            <xsl:value-of select="$time"/>
                        </fo:block>
                    </fo:block-container>
                    <!--
                    <xsl:comment>Heading</xsl:comment>
                    <fo:block-container position="absolute" top="25mm" left="0mm">
                        <fo:block font-family="FreeSans" font-size="20pt" font-weight="bold">Begleitzettel</fo:block>
                    </fo:block-container>
                    -->

                    <xsl:comment>Autor and title</xsl:comment>
                    <fo:block-container position="absolute" top="50mm" left="0mm">
                        <fo:block font-family="FreeSans" font-size="12pt">
                            <xsl:value-of select="//entry[@entity = '050' and @attribute = '003']/@value"/>
                        </fo:block>
                        <fo:block font-family="FreeSans" font-size="13pt">
                            <xsl:value-of select="//entry[@entity = '050' and @attribute = '002']/@value"/>
                        </fo:block>
                    </fo:block-container>
                    <xsl:comment>Barcode section</xsl:comment>
                    <fo:block-container position="absolute" top="68mm" left="0mm" width="120mm">
                        <fo:block padding="1mm">
                            <xsl:variable name="book-number" select="translate(//entry[@entity = '004' and @attribute = '004']/@value, ' ', '')"/>
                            <xsl:choose>
                                <xsl:when test="$barcode">
                                    <xsl:copy-of select="print:barcode($book-number, 'uniform', '70mm', '2cm', 'bottom')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$book-number"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </fo:block>
                    </fo:block-container>

                    <fo:block-container position="absolute" top="90mm" left="0mm" width="125mm">
                        <xsl:comment>Additional loan information</xsl:comment>
                        <xsl:if test="$loan-free-text != ''">
                            <xsl:comment>Additional info</xsl:comment>
                            <fo:block font-style="italic" font-family="FreeSans" font-size="12pt">
                                <xsl:value-of select="concat('Ausleihinfo: ', $loan-free-text)"/>
                            </fo:block>
                        </xsl:if>
                        <fo:block font-family="FreeSans" font-size="12pt" text-align-last="justify">
                            <xsl:comment>C/O Adress extra</xsl:comment>
                            <xsl:value-of select="$co"/>
                            <fo:leader leader-pattern="space"/>
                            <xsl:comment>Notabene</xsl:comment>
                            <xsl:value-of select="$notabene"/>
                        </fo:block>
                        <xsl:comment>Location</xsl:comment>
                        <fo:block font-family="FreeSans" font-size="12pt">
                            <xsl:value-of select="$location"/>
                        </fo:block>
                        <!--
                        <xsl:if
                            test="($user-type = '40' or $user-type = '15') or ($target != 'Zentralbib./Selbstabholbereich' and $target != 'Zentralbibliothek/Selbstabholbereich' and $target != 'Abholregal BB Kulturwiss.')">
                            <xsl:if test="$co != ''">
                                <xsl:comment>Add address for institute delivery service</xsl:comment>
                                <fo:block font-family="FreeSans" font-size="12pt">
                             <xsl:value-of select="concat('&#x2192;', $co)"/>
                                </fo:block>
                            </xsl:if>
                        </xsl:if>
                        -->
                    </fo:block-container>
                    <!-- This needs to be changed in A4 mode -->
                    <xsl:comment>Logo</xsl:comment>
                    <fo:block-container position="absolute" top="110mm" left="0mm">
                        <fo:block>
                            <xsl:comment>The Logo should span the whole width, which is 210 mm minus margin multiplied by two (210-13*2)</xsl:comment>
                            <fo:external-graphic src="../img/GAU-SUBlogo-einzeilig.svg" scaling="uniform" content-width="184mm"/>
                        </fo:block>
                    </fo:block-container>
                    <xsl:comment>Debug output, version etc</xsl:comment>
                    <fo:block-container reference-orientation="270" position="absolute" left="126mm" top="70mm" bottom="{print:scaleA5toA4('15mm')}"
                        text-align="right">
                        <fo:block font-family="FreeSans" font-size="5pt" color="grey">
                            <xsl:value-of select="$template-info"/>|<xsl:value-of
                                select="replace(//entry[@entity = '098' and @attribute = '002']/@value, ':', '')"/>
                            <xsl:value-of select="$template-suffix"/>
                        </fo:block>
                    </fo:block-container>
                    <!-- This needs to be changed in A4 mode -->
                    <!-- Check user Type and target 
                         If user type is 15 or delivery target is not "Zentralbibliothek/Selbstabholbereich" or "Abholregal BB Kulturwiss.", print the user name and id, otherwise anonymize
                    -->
                    <fo:block-container reference-orientation="90" position="absolute" top="0mm" left="130mm" bottom="{print:scaleA5toA4('15mm')}"
                        border-start-style="solid" border-color="grey" border-start-width="2pt">
                        <xsl:choose>
                            <!--
                            <xsl:when
                                test="($user-type = '40' or $user-type = '15' or $user-type = '30'  or $user-type = '35' or $user-type = '50'  or $user-type = '55') or ($target != 'Zentralbib./Selbstabholbereich' and $target != 'Zentralbibliothek/Selbstabholbereich' and $target != 'Abholregal BB Kulturwiss.' and $target != 'Zentralbibliothek / Selbstabholberei')">
                                -->
                            <xsl:when
                                test="($user-type = '40' or $user-type = '15') or ($target != 'Zentralbib./Selbstabholbereich' and $target != 'Zentralbibliothek/Selbstabholbereich' and $target != 'Abholregal BB Kulturwiss.' and $target != 'Zentralbibliothek / Selbstabholberei')">
                                <fo:block padding-top="2.5pt" font-family="FreeSans" font-size="24pt">
                                    <xsl:value-of select="concat($user-title, ' ', $user-lastname)"/>
                                </fo:block>
                                <fo:block padding-top="2.5pt" font-family="FreeSans" font-size="24pt">
                                    <xsl:value-of select="$user-firstname"/>
                                </fo:block>
                                <fo:block font-family="FreeSans" font-size="12pt" text-align-last="justify">
                                    <!--
                                    <xsl:comment>C/O Adress extra</xsl:comment>
                                    <xsl:value-of select="$co"/>
                                    -->
                                    <fo:leader leader-pattern="space"/>
                                    <!-- Output of user ID if not anonymized -->
                                    <fo:inline>
                                        <xsl:comment>User ID</xsl:comment>
                                        <xsl:value-of select="$user-id-full"/>
                                    </fo:inline>
                                </fo:block>
                            </xsl:when>
                            <xsl:otherwise>
                                <fo:block margin-top="4pt" padding-top="6pt" font-family="FreeSans" font-size="78pt" font-weight="bold">
                                    <xsl:value-of select="$user-id"/>
                                    <fo:inline padding-top="10pt" padding-left="3pt" padding-right="3pt" margin-top="2pt" padding-bottom="0pt"
                                        margin-bottom="0pt" border="4pt" border-style="solid">
                                        <xsl:value-of select="$user-initial"/>
                                    </fo:inline>
                                </fo:block>
                            </xsl:otherwise>
                        </xsl:choose>
                    </fo:block-container>

                    <!-- the is still some space for another block
                    <fo:block-container reference-orientation="90" position="absolute" top="0mm" left="159mm" bottom="{print:scaleA5toA4('15mm')}">
                    -->

                    <!-- This needs to be changed in A4 mode -->
                    <!-- Old values: right="24pt"  height="30pt" -->
                    <fo:block-container reference-orientation="90" position="absolute" top="0mm" right="8.5mm" height="11.5mm"
                        bottom="{print:scaleA5toA4('15mm')}">
                        <fo:block font-family="FreeSans" font-size="12pt">
                            <xsl:value-of select="concat('Ausleihtyp: ', $loan-type-decription, ' (', $loan-type, ') / Nutzertyp: ', $user-type)"/>
                        </fo:block>
                        <fo:block font-family="FreeSans" font-size="12pt" font-weight="bold">Ziel: <!-- Target -->
                            <xsl:comment>Target</xsl:comment>
                            <xsl:value-of select="$target"/>
                        </fo:block>
                        <!-- See http://stackoverflow.com/a/590335 -->
                        <fo:block margin-top="0.5mm" font-family="FreeSans" font-size="12pt">
                            <!-- Address extra -->
                            <xsl:comment>Address extra</xsl:comment>
                            <xsl:value-of select="$adress-extra"/>
                            <!-- Empty Space to get the empty block get rendered correctly -->
                            <fo:leader/>
                        </fo:block>
                        <!-- Date & and old Notabene -->
                        <xsl:comment>Date</xsl:comment>
                        <fo:block font-family="FreeSans" font-size="12pt" text-align-last="justify">
                            <!--
                            Notabene 
                            <xsl:value-of select="$notabene"/>
                            -->
                            <fo:leader leader-pattern="space"/>
                            <!-- Date -->
                            <xsl:comment>Date</xsl:comment>
                            <fo:inline font-weight="bold">
                                <xsl:value-of select="$date"/>
                            </fo:inline>
                        </fo:block>
                    </fo:block-container>
                    <!-- Prints a text accross the output PDF -->
                    <xsl:if test="$debug">
                        <fo:block-container position="absolute" top="0mm" left="0mm" z-index="10">
                            <fo:block padding="5mm">
                                <fo:instream-foreign-object scaling="uniform" width="154mm">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="210mm" height="148mm">
                                        <!-- Font was 'Optima' -->
                                        <text x="0" y="300" transform="rotate(-30)"
                                            style="font-size: 120pt; fill: grey; font-weight: bold; font-family: FreeSans;">
                                            <xsl:value-of select="$debug-text"/>
                                        </text>
                                    </svg>
                                </fo:instream-foreign-object>
                            </fo:block>
                        </fo:block-container>
                    </xsl:if>
                    <!--
                        Use this if you want a line to fold the A4 Page
                    <xsl:if test="$format = 'A4'">
                        <fo:block-container position="absolute" top="{print:calculate($paper-formats/format[@name = 'A4']/@height, '/', '2')}" border-start-style="solid" border-color="grey"
                            border-start-width="0.5pt"/>
                    </xsl:if>
                    -->
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:function name="print:calculate" as="xs:string">
        <xsl:param name="arg1" as="xs:string"/>
        <xsl:param name="operation" as="xs:string"/>
        <xsl:param name="arg2" as="xs:string"/>
        <xsl:variable name="unit" select="replace($arg1, '[\d\.]*?([^\d\.]+)', '$1')" as="xs:string"/>
        <!-- Use this if you  want to check if units match
        <xsl:if test="not(contains($arg2, $unit))">
            <xsl:message terminate="yes">Units doesn't match!</xsl:message>
        </xsl:if>
        -->
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
    <!-- This Function scales A5 landscape to A4 portrait, only bottom oriented values have to be recalculated -->
    <xsl:function name="print:scaleA5toA4" as="xs:string">
        <xsl:param name="bottom" as="xs:string"/>
        <xsl:choose>
            <xsl:when test="upper-case($format) = 'A4'">
                <!-- Bottom 0mm from A5 is half height of A6 plus margin -->
                <xsl:variable name="half-A4" select="print:calculate($paper-formats/format[@name = 'A4']/@height, '/', '2')"/>
                <xsl:value-of select="print:calculate($half-A4, '+', $bottom)"/>
            </xsl:when>
            <xsl:when test="upper-case($format) = 'A5'">
                <xsl:value-of select="$bottom"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">Unsupported Format <xsl:value-of select="$format"/></xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    <xsl:function name="print:set-font-size" as="attribute(font-size)">
        <xsl:param name="size" as="xs:integer"/>
        <xsl:attribute name="font-size" select="concat($size, 'pt')"/>
    </xsl:function>
    <xsl:function name="print:leader" as="element(fo:leader)" xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:leader leader-pattern="space"/>
    </xsl:function>
    <xsl:function name="print:barcode" as="element(fo:instream-foreign-object)" xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <xsl:param name="message"/>
        <xsl:param name="scaling"/>
        <xsl:param name="content-width"/>
        <xsl:param name="height"/>
        <xsl:param name="human-readable"/>
        <fo:instream-foreign-object scaling="{$scaling}" content-width="{$content-width}">
            <xsl:comment>Barcode, see http://barcode4j.sourceforge.net/2.1/fop-ext.html</xsl:comment>
            <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns" render-mode="svg">
                <xsl:attribute name="message">
                    <xsl:value-of select="$message"/>
                </xsl:attribute>
                <barcode:code39>
                    <!-- See http://barcode4j.sourceforge.net/2.1/barcode-xml.html -->
                    <barcode:height>
                        <xsl:value-of select="$height"/>
                    </barcode:height>
                    <barcode:human-readable>
                        <xsl:value-of select="$human-readable"/>
                    </barcode:human-readable>
                </barcode:code39>
            </barcode:barcode>
        </fo:instream-foreign-object>
    </xsl:function>
    <xsl:function name="print:replace-umlauts" as="xs:string">
        <xsl:param name="str" as="xs:string"/>
        <xsl:variable name="small" select="replace(replace(replace(replace($str,' ß','ss'),'ü','ue'),'ö','oe'),'ä','ae')"/>
        <xsl:variable name="result" select="replace(replace(replace($small,'Ü','Ue'),'Ö', 'Oe'),'Ä','Ae')"/>
        <xsl:value-of select="$result"/>
    </xsl:function>
</xsl:stylesheet>
