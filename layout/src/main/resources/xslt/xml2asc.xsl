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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl" exclude-result-prefixes="xs xd"
    version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2014</xd:p>
            <xd:p><xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>

    <xsl:output method="text" encoding="ASCII"/>
    <xsl:include href="./lib/lib.xsl"/>
    <xsl:param name="output-heading" select="false()" as="xs:boolean"/>
    <xsl:template match="/">
        <xsl:if test="/layout/@iln and /layout/@layout and /layout/@language">
            <xsl:copy-of select="concat('$ILN=', /layout/@iln, $break)"/>
            <xsl:copy-of select="concat('$LAYOUT=', /layout/@layout, $break)"/>
            <xsl:copy-of select="concat('$LANGUAGE=', /layout/@language, $break)"/>
        </xsl:if>
        <!-- Check if input is mixed -->
        <xsl:if test="//@file and //@entity">
            <xsl:message terminate="yes">File contains mixed normal and text layout</xsl:message>
        </xsl:if>
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="line">
        <xsl:choose>
            <xsl:when test="@comment and not(starts-with(@text,';'))">
                <xsl:copy-of select="concat('; ', @text, $break)"/>
            </xsl:when>
            <xsl:when test="@comment">
                <xsl:copy-of select="concat(@text, $break)"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- Check layout type -->
                <xsl:choose>
                    <xsl:when test="not(@file)">
                        <xsl:if test="$output-heading and not(preceding-sibling::line[not(@comment)])">
                            <xsl:copy-of select="concat($heading, $break)"/>
                        </xsl:if>
                        <xsl:copy-of select="concat(' ', @line, ' ', @column, ' ', @entity, ' ', @attribute, ' ', @sequence, ' ', @type, ' ', @length, ' ', @text, $break)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="concat(@file, ' ', @type, ' ', @nr, ' ', @language, ' ', @text, $break)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
