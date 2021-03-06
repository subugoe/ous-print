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
    xmlns:print="http://www.sub.uni-goettingen.de/BE/OUS/print" xmlns:gxsl="http://www.sub.uni-goettingen.de/BE/OUS/gXSL"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" exclude-result-prefixes="xd" version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2014</xd:p>
            <xd:p><xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <!-- Generated xslt will be in the gxsl namespace, and is aliased for output -->
    <xsl:namespace-alias result-prefix="xsl" stylesheet-prefix="gxsl"/>
    <xsl:param name="encoding" select="'ASCII'"/>
    <!-- This controls if regular expressions should always match the whole line ^.*$ -->
    <xsl:param name="whole-line" as="xs:boolean" select="true()"/>
    <!-- LBS4 seem to use other line Endings the LBS3, so we will have &#xD; at the end of broken strings -->
    <xsl:param name="clear-xD" as="xs:boolean" select="true()"/>
    <xsl:include href="./lib/lib.xsl"/>
    <xsl:output indent="yes" encoding="UTF-8"/>
    <xsl:template match="/">
        <gxsl:stylesheet version="2.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xsl:comment>Inline Comments are taken from the layout definition ASC file.</xsl:comment>
            <gxsl:param name="input"/>
            <gxsl:param name="encoding" as="xs:string">
                <xsl:attribute name="select">'<xsl:value-of select="$encoding"/>'</xsl:attribute>
            </gxsl:param>
            <gxsl:variable name="break">
                <xsl:attribute name="select">
                    <xsl:text>'</xsl:text>
                    <xsl:value-of select="$break"/>
                    <xsl:text>'</xsl:text>
                </xsl:attribute>
            </gxsl:variable>
            <gxsl:output indent="yes" encoding="UTF-8"/>
            <gxsl:template match="/">
                <gxsl:variable name="text" select="unparsed-text($input, $encoding)"/>
                <gxsl:variable name="lines" select="tokenize($text, $break)"/>
                <layout>
                    <xsl:apply-templates/>
                </layout>
            </gxsl:template>
            <!-- Our own replacer, returns an empty string if no match found -->
            <xsl:comment>This is our own replacer which returns an empty string if no match found</xsl:comment>
            <gxsl:function name="print:replace" as="xs:string">
                <gxsl:param name="input"/>
                <gxsl:param name="pattern"/>
                <gxsl:param name="replacement"/>
                <gxsl:variable name="replaced" select="replace($input, $pattern, $replacement)" as="xs:string"/>
                <gxsl:choose>
                    <gxsl:when test="$input = $replaced">
                        <gxsl:value-of select="''"/>
                    </gxsl:when>
                    <gxsl:otherwise>
                        <gxsl:value-of select="$replaced"/>
                    </gxsl:otherwise>
                </gxsl:choose>
            </gxsl:function>
        </gxsl:stylesheet>
    </xsl:template>
    <xsl:function name="print:generate-positions" as="xs:integer*">
        <xsl:param name="start" as="xs:double"/>
        <xsl:param name="ident" as="xs:double"/>
        <xsl:param name="length" as="xs:double"/>
        <!-- We need to cast from s:double to xs:integer -->
        <xsl:variable name="output-start" select="xs:integer($start + $ident)" as="xs:integer"/>
        <xsl:copy-of select="for $x in ($output-start to $output-start + xs:integer($length)) return $x"/>
    </xsl:function>

    <xsl:template match="line[not(@comment)]">
        <!-- TODO: Add support for type I, M and R - aligned right -->
        <xsl:choose>
            <!-- Filter empty lines -->
            <xsl:when test="@entity != '000' or @attribute != '000' or @sequence != '000'">
                <!-- ident, lenght of preceding text, plus 1 for the following space, if there is text -->
                <xsl:variable name="ident" as="xs:integer">
                    <xsl:choose>
                        <xsl:when test="matches(@text, '^\s*$')">
                            <xsl:value-of select="0"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="string-length(@text)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <!-- column, the start column -->
                <xsl:variable name="column" select="number(@column)" as="xs:double"/>
                <!-- length, the length of the output-->
                <xsl:variable name="length" select="number(@length)" as="xs:double"/>
                <!-- Check if the positions are overwritten by other outputs -->
                <!-- Get the line number -->
                <xsl:variable name="line" select="number(@line)" as="xs:double"/>
                <!-- output-start, position where the output starts -->
                <xsl:variable name="output-start" select="xs:integer($column + $ident)" as="xs:integer"/>
                <!-- Calculate all positions, from $output-start to $output-start + $length -->
                <xsl:variable name="positions" select="print:generate-positions($column, $ident, $length)" as="xs:integer*"/>
                <xsl:variable name="id" select="generate-id(.)"/>
                <!-- This defines the possible size of empty @text, which can overfloat to the left -->
                <xsl:variable name="left-overflow" as="xs:integer">
                    <xsl:choose>
                        <xsl:when test="matches(@text, '\s*')">
                            <xsl:value-of select="string-length(@text)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="0"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:for-each select="//line[@line = $line and not(generate-id(.) = $id)]">
                    <!-- Check if this isn't a text layout -->
                    <xsl:if test="@file">
                        <xsl:message terminate="yes"> This parser generator doesn't support text layouts yet! </xsl:message>
                    </xsl:if>
                    <!-- Check if the line contains external data -->
                    <xsl:if test="@entity != '000' or @attribute != '000' or @sequence != '000'">
                        <xsl:variable name="other-positions" select="print:generate-positions(number(@column), string-length(@text) + 1, number(@length))"
                            as="xs:integer*"/>
                        <xsl:if test="not(empty(distinct-values($positions[. = $other-positions])))">
                            <xsl:message>E<xsl:value-of select="@entity"/>A<xsl:value-of select="@attribute"/>: Warning: area over written by other definitions!
                                Line <xsl:value-of select="$line"/>, column: <xsl:value-of select="$column"/> Offending entry: entity '<xsl:value-of
                                    select="@entity"/>', attribute '<xsl:value-of select="@attribute"/>', sequence: '<xsl:value-of select="@sequence"/>'
                            </xsl:message>
                        </xsl:if>
                    </xsl:if>
                </xsl:for-each>
                <!-- Generate Regex -->
                <xsl:variable name="regex" as="xs:string*">
                    <!-- Start -->
                    <xsl:text>'</xsl:text>
                    <xsl:if test="$whole-line">
                        <xsl:text>^</xsl:text>
                    </xsl:if>
                    <!-- Stuff at the beginning to ignore -->
                    <xsl:value-of select="concat('.{', $column + $ident - 1, '}')"/>
                    <!-- length, the result should be in here -->
                    <xsl:value-of select="concat('(.{0,', $length - $ident, '})')"/>
                    <!-- The rest -->
                    <xsl:value-of select="'.*'"/>
                    <!-- End -->
                    <xsl:if test="$whole-line">
                        <xsl:text>$</xsl:text>
                    </xsl:if>
                    <xsl:text>'</xsl:text>
                </xsl:variable>
                <entry>
                    <!-- Copy definitions, useful for a full reconstruction of the slip later -->
                    <xsl:attribute name="length" select="@length"/>
                    <xsl:attribute name="line" select="@line"/>
                    <xsl:attribute name="column" select="@column"/>
                    <xsl:attribute name="text" select="@text"/>
                    <xsl:attribute name="entity" select="@entity"/>
                    <xsl:attribute name="attribute" select="@attribute"/>
                    <xsl:attribute name="sequence" select="@sequence"/>
                    <xsl:attribute name="type" select="@type"/>
                    <gxsl:variable name="line">
                        <xsl:attribute name="select">
                            <xsl:choose>
                                <xsl:when test="$clear-xD">
                                    <xsl:value-of select="concat('translate($lines[', $line ,'], ')"/>
                                    <xsl:text>'&#xD;', '')</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat('$lines[', $line ,']')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                    </gxsl:variable>
                    <xsl:choose>
                        <!-- Contains whitespace that might gets overriden -->
                        <xsl:when test="$left-overflow != 0">
                            <xsl:comment>This contains the raw string, including possible leading whitespace</xsl:comment>
                            <gxsl:variable name="raw-value" as="xs:string">
                                <xsl:attribute name="select">
                                    <xsl:value-of select="concat('print:replace($line , ', string-join($regex, ''))"/>
                                    <xsl:text>, '$1')</xsl:text>
                                </xsl:attribute>
                            </gxsl:variable>
                            <xsl:comment>This is used to filter possible leading whitespace</xsl:comment>
                            <gxsl:variable name="value">
                                <xsl:attribute name="select">
                                    <xsl:text>replace($raw-value, '^\s{0,</xsl:text>
                                    <xsl:value-of select="$left-overflow"/>
                                    <xsl:text>}(.+)$', '$1')</xsl:text>
                                </xsl:attribute>
                            </gxsl:variable>
                            <gxsl:attribute name="value">
                                <gxsl:value-of select="$value"/>
                            </gxsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <gxsl:attribute name="value">
                                <xsl:attribute name="select">
                                    <xsl:value-of select="concat('print:replace($line , ', string-join($regex, ''))"/>
                                    <xsl:text>, '$1')</xsl:text>
                                </xsl:attribute>
                            </gxsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                </entry>
                <xsl:message>Generated Parser for entity '<xsl:value-of select="@entity"/>', attribute '<xsl:value-of select="@attribute"/>'</xsl:message>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="//line[@comment]">
        <xsl:comment>
            <xsl:value-of select="@text"/>
        </xsl:comment>
    </xsl:template>
</xsl:stylesheet>
