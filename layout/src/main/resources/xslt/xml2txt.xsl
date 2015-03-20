<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:print="http://www.sub.uni-goettingen.de/BE/OUS/print" exclude-result-prefixes="xs" version="2.0">
    <xsl:param name="encoding" as="xs:string" select="'UTF-8'"/>
    <xsl:param name="break" as="xs:string" select="'&#13;&#10;'"/>
    <xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <!-- Get the first and create leading empty lines  -->
        <xsl:variable name="start-line" select="xs:integer(min(//line[@xsi:type='positionLine']/@line|//entry/@line))" as="xs:integer"/>
        <xsl:value-of select="for $i in (1 to $start-line - 1) return $break"/>
        <!-- Create a list of empty lines -->
        <xsl:variable name="last-line" select="xs:integer(max(//line[@xsi:type='positionLine']/@line|//entry/@line))" as="xs:integer"/>
        <xsl:variable name="empty-lines" as="xs:integer*">
            <xsl:variable name="lines" select="//line[@xsi:type='positionLine']/@line|//entry/@line"/>
            <xsl:for-each select="1 to $last-line">
                <xsl:if test="$lines[xs:integer(@line) = .]">
                    <xsl:value-of select="."/>
                </xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <!-- Loop over the lines -->
        <xsl:for-each-group select="//entry|//line" group-by="@line">
            <xsl:sort order="ascending" select="number(@line)"/>
            <!-- Check if there are empty lines -->
            <xsl:variable name="pos" select="position()" as="xs:integer"/>
            <xsl:variable name="line" select="xs:integer(current-grouping-key())" as="xs:integer"></xsl:variable>
            <xsl:variable name="previous-line" select="current-group()[$pos - 1]"></xsl:variable>
            <xsl:choose>
                <!-- More than one text for this line -->
                <xsl:when test="count(current-group()) &gt; 1">
                    <xsl:for-each select="current-group()">
                        <xsl:sort order="ascending" select="number(@column)"/>
                        <xsl:variable name="text" select="concat(@text, @value)"></xsl:variable>
                        <xsl:choose>
                            <!-- First text on line add leading whitespace -->
                            <xsl:when test="position() = 1">
                                <xsl:value-of select="concat(print:space(xs:integer(@column)), $text, print:space(xs:integer(@length) - string-length(@text)))"
                                />
                            </xsl:when>
                            <!-- Last text on line, add break -->
                            <xsl:when test="position() = last()">
                                <xsl:variable name="pos" select="position()" as="xs:integer"/>
                                <!--
                                <xsl:variable name="leading-space"
                                    select="xs:integer(@column) - (xs:integer(current-group()[$pos - 1]/@column) + xs:integer(current-group()[$pos - 1][@length]))"
                                    as="xs:integer"/>
                                -->
                                <xsl:variable name="leading-space" select="print:calculate-space(., current-group()[$pos - 1])" as="xs:integer"/>
                                <xsl:value-of
                                    select="concat(print:space($leading-space), $text, print:space(xs:integer(@length) - string-length(@text)), $break)"/>
                            </xsl:when>
                            <!-- Some text in the middle -->
                            <xsl:otherwise>
                                <xsl:variable name="pos" select="position()" as="xs:integer"/>
                                <xsl:variable name="leading-space" select="print:calculate-space(., current-group()[$pos - 1])" as="xs:integer"/>
                                <xsl:value-of select="concat(print:space($leading-space), $text, print:space(xs:integer(@length) - string-length(@text)))"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </xsl:when>
                <!-- One text for this line -->
                <xsl:otherwise>
                    <xsl:value-of select="concat(print:space(xs:integer(@column)), @text, $break)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each-group>
    </xsl:template>
    <xsl:function name="print:space" as="xs:string">
        <xsl:param name="i" as="xs:integer"/>
        <xsl:value-of select="for $i in (1 to $i - 1) return ' '"/>
    </xsl:function>
    <xsl:function name="print:calculate-space" as="xs:integer">
        <xsl:param name="node" as="element()"/>
        <xsl:param name="previous-node" as="element()"/>
        <xsl:value-of select="xs:integer($node/@column) - (xs:integer($previous-node/@column) + xs:integer($previous-node/@length))"/>
    </xsl:function>
</xsl:stylesheet>
