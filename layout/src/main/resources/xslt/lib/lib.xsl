<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    xmlns:print="http://www.sub.uni-goettingen.de/BE/OUS/print" exclude-result-prefixes="xs xd" version="2.0">
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Mar 18, 2014</xd:p>
            <xd:p><xd:b>Author:</xd:b> cmahnke</xd:p>
            <xd:p/>
        </xd:desc>
    </xd:doc>
    <!-- Line endings are encoded as LF (dec: 10, hex: 0a, Regex: \n) -->
    <xsl:variable name="break" select="'&#10;'" as="xs:string"/>
    <xsl:variable name="heading">
        <xsl:text>;LIN KOL ENT ATT SEQ T LEN TEXT</xsl:text>
    </xsl:variable>
    <xsl:variable name="text-layout-pattern" select="concat('^([\$;].*?', print:regex-for-break($break), ')\s*\d\s*\d{3}.*$')" as="xs:string"/>
    <xsl:variable name="normal-layout-pattern" select="concat('^([\$;].*?', print:regex-for-break($break), ')\s*\d{3}\s*\d{3}.*$')" as="xs:string"/>
    <xsl:function name="print:regex-for-break" as="xs:string">
        <xsl:param name="break" as="xs:string"></xsl:param>
        <xsl:choose>
            <!-- 
            Remember:
            DOS & Windows: \r\n 0D0A (hex), 13,10 (decimal)
            Unix & Mac OS X: \n, 0A, 10
            Macintosh (OS 9): \r, 0D, 13
            See: http://stackoverflow.com/questions/6539801/reminder-r-n-or-n-r
            -->
            <xsl:when test="$break = '&#10;'">
                <xsl:value-of select="'\n'"/>
            </xsl:when>
            <xsl:when test="$break = '&#13;'">
                <xsl:value-of select="'\r'"/>
            </xsl:when>
            <xsl:when test="$break = '&#13;&#10;'">
                <xsl:value-of select="'\r\n'"/>
            </xsl:when>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="print:is-text-layout" as="xs:boolean">
        <xsl:param name="layout" as="xs:string"/>
        <xsl:copy-of select="matches($layout, $text-layout-pattern, 's')"/>
    </xsl:function>
    <xsl:function name="print:is-normal-layout" as="xs:boolean">
        <xsl:param name="layout" as="xs:string"/>
        <xsl:copy-of select="matches($layout, $normal-layout-pattern, 's')"/>
    </xsl:function>
    <xsl:template match="text()|comment()|processing-instruction()"/>
</xsl:stylesheet>
