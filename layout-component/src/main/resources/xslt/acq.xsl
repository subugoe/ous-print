<?xml version="1.1" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xs" version="2.0">
    <!-- 
        This is \f (form feed) character class in Perl regular expresions 
        We need to define this document as XML 1.1 to use this.
    -->
    <xsl:variable name="form-feed" select="'&#8;'" as="xs:string"/>
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="*">
        <xsl:copy>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="//fo:block[@break-after]">
        <xsl:value-of select="$form-feed"/>
    </xsl:template>
    <xsl:template match="//fo:block[@white-space-treatment]">
        <xsl:apply-templates select="text()"/>
    </xsl:template>
    <xsl:template match="fo:layout-master-set"/>
    <xsl:template match="text()">
        <!-- Aus dem alten Script kommentiert
         # Wenn das Feld lokale Spezifikationen (DBS) nicht ausgefuellt ist, wird eine zusaetzliche Leerzeile erzeugt.
         # Diese besteht aus Tab (011) Tab (011) Zeilenumbruch (015) + (012)
         /usr/bin/perl -i -p -e's/\011\011\015\012/\ /g' ${DIR_JPRT}acq40supfor${Inst}_$(date '+%Y%m%d')_slip100_$(date '+%y%m%d').doc
         # Ersetzung ~| durch Seitenumbruch ist nicht im Layout
         # Ersetzung Seitenumbruch + Leerzeile + Leerzeile durch Seitenumbruch nicht notwendig, wird schon in der XML Konvetierung gemacht
         # Entfernung den letzten Seitenumbruch in der Datei nicht notwendig, wird schon in der XML Konvetierung gemacht
        -->
        <xsl:if test="not(matches(., '^\s*$'))">
            <xsl:variable name="text" select="replace(., '&#11;&#11;&#15;&#12;', ' ')" as="xs:string"/>
            <xsl:value-of select="$text"/>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
