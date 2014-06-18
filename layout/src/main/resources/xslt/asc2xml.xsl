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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:print="http://www.sub.uni-goettingen.de/BE/OUS/print"
                xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xs xd" version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p>
                <xd:b>Created on:</xd:b> Mar 18, 2014</xd:p>
            <xd:p>
                <xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <xsl:param name="layoutFile"/>
    <xsl:param name="encoding" select="'ASCII'" as="xs:string"/>
    <xsl:include href="./lib/lib.xsl"/>
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <xsl:message>
            If this fails, there are special characters in the input file
            perl -pi -e 's/[\x10-\x1f]/ /g' file-name
        </xsl:message>
        <xsl:variable name="text" select="unparsed-text($layoutFile, $encoding)"/>
        <xsl:variable name="iln" select="replace($text, '^.*?\$ILN=(\d*).*$', '$1', 's')" as="xs:string"/>
        <xsl:variable name="layout" select="replace($text, '^.*?\$LAYOUT=(\d*).*$', '$1', 's')" as="xs:string"/>
        <xsl:variable name="language" select="replace($text, '^.*?\$LANGUAGE=(\w{2,3}).*$', '$1', 's')" as="xs:string"/>
        <!-- Check the type of layout -->
        <xsl:variable name="layout-type" as="xs:string">
            <xsl:choose>
                <xsl:when test="print:is-normal-layout($text)">
                    <xsl:value-of select="'normal'"/>
                </xsl:when>
                <xsl:when test="print:is-text-layout($text)">
                    <xsl:value-of select="'text'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message terminate="yes">Unknown layout type</xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <layout>
            <!-- Only applies to normal layouts -->
            <xsl:if test="$iln != '' and $layout != '' and $language !=''">
                <xsl:attribute name="iln" select="$iln"/>
                <xsl:attribute name="layout-file" select="$layout"/>
                <xsl:attribute name="language" select="$language"/>
            </xsl:if>
            <xsl:comment>
                <xsl:text>Generated from </xsl:text>
                <xsl:value-of select="$layoutFile"/>
            </xsl:comment>
            <xsl:for-each select="tokenize($text, $break)">
                <!-- Ommit lines starting with '$' or are empty -->
                <xsl:if test="not(starts-with(., '$')) and . != ''">
                    <line>
                        <xsl:choose>
                            <xsl:when test="matches(., '^\s*;.*$')">
                                <xsl:attribute name="comment" select="'true'"/>
                                <xsl:attribute name="text">
                                    <xsl:copy-of select="."/>
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- Check the type of layout -->
                                <xsl:choose>
                                    <xsl:when test="$layout-type = 'normal'">
                                        <!-- 
                                        Regex Groups:
                                            1) LIN: line
                                            2) KOL: column
                                            3) ENT: entry
                                            4) ATT: attribute
                                            5) SEQ: sequence
                                            6) T: type
                                            7) LEN: length
                                            8) TEXT: text
                                        -->
                                        <xsl:analyze-string select="." regex="^\s*(\d{{3}})\s*(\d{{3}})\s*(\d{{3}})\s*(\d{{3}})\s*(\d{{3}})\s*([\w^\d]{{1}})\s*(\d{{3}})(.*)$">
                                            <xsl:matching-substring>
                                                <xsl:attribute name="line" select="regex-group(1)"/>
                                                <xsl:attribute name="column" select="regex-group(2)"/>
                                                <xsl:attribute name="entity" select="regex-group(3)"/>
                                                <xsl:attribute name="attribute" select="regex-group(4)"/>
                                                <xsl:attribute name="sequence" select="regex-group(5)"/>
                                                <xsl:attribute name="type" select="regex-group(6)"/>
                                                <xsl:attribute name="length" select="regex-group(7)"/>
                                                <xsl:attribute name="text" select="regex-group(8)"/>
                                            </xsl:matching-substring>
                                        </xsl:analyze-string>
                                    </xsl:when>
                                    <xsl:when test="$layout-type = 'text'">
                                        <!--
                                        Regex Groups:
                                        1) file
                                        2) type
                                        3) nr
                                        4) language
                                        5) text
                                        -->
                                        <xsl:analyze-string select="." regex="^\s*(\d)\s*(\d{{3}})\s*(\d{{4}})\s*([\w^\d]{{2}})\s*(.*)$">
                                            <xsl:matching-substring>
                                                <xsl:attribute name="file" select="regex-group(1)"/>
                                                <xsl:attribute name="type" select="regex-group(2)"/>
                                                <xsl:attribute name="nr" select="regex-group(3)"/>
                                                <xsl:attribute name="language" select="regex-group(4)"/>
                                                <xsl:attribute name="text" select="regex-group(5)"/>
                                            </xsl:matching-substring>
                                        </xsl:analyze-string>
                                    </xsl:when>
                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                    </line>
                </xsl:if>
            </xsl:for-each>
        </layout>
    </xsl:template>
</xsl:stylesheet>
