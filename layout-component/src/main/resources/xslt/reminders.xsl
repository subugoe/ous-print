<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs" version="2.0">
    <xsl:output indent="yes"/>
    <xsl:template match="fo:block">
        <fo:root xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="Master" page-width="21.001cm" page-height="29.7cm" margin-top="2cm" margin-bottom="2cm" margin-left="2cm"
                    margin-right="2cm">
                    <fo:region-body region-name="xsl-region-body"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="Master">
                <fo:flow flow-name="xsl-region-body">
                    <xsl:analyze-string select=".//text()[not(matches(., '^\s*$', 'sm'))]"
                        regex="&lt;address&gt;\s(.+?)\s&lt;/address&gt;\s&lt;reminder&gt;\s(.+?)\s&lt;/reminder&gt;" flags="sm">
                        <xsl:matching-substring>
                            <xsl:variable name="level" select="replace(regex-group(2), '.*zugeben.\s+(.+?)\s+Titel.*', '$1', 'sm')" as="xs:string"/>
                            <xsl:if test="contains($level, 'Dritte')">
                                <xsl:variable name="adress" select="replace(regex-group(2), '.*ol@sub.uni-goettingen.de\s*(.+)\s*Nutzernummer.*', '$1', 'sm')" as="xs:string"/>
                                <xsl:variable name="mail" select="regex-group(1)" as="xs:string"/>
                                <xsl:variable name="number" select="replace(regex-group(2), '.*Nutzernummer:\s(\w+).*', '$1', 'sm')" as="xs:string"/>
                                <xsl:variable name="title" select="replace(regex-group(2), '.*Titel\s+?:\s+?(.+?)[\r\n].*', '$1', 'sm')" as="xs:string"/>
                                <xsl:variable name="author">
                                    <xsl:if test="contains(regex-group(2), 'Autor')">
                                        <xsl:value-of select="replace(regex-group(2), '.*Autor\s+?:\s(.+?)[\r\n].*', '$1', 'sm')"/>
                                    </xsl:if>
                                </xsl:variable>
                                <xsl:variable name="shelfmark" select="replace(regex-group(2), '.*Signatur:\s(.+?)[\r\n].*', '$1', 'sm')" as="xs:string"/>
                                <xsl:variable name="date" select="translate(replace(regex-group(2), '.*Rueckgabedatum:\s([\w-]{10}).*', '$1', 'sm'), '-', '.')" as="xs:string"/>
                                <xsl:variable name="fee" select="replace(regex-group(2), '.*: EUR\s+(.+?)[\r\n].*', '$1', 'sm')" as="xs:string"/>
                                <xsl:variable name="text" select="replace(regex-group(2), '^\s*(.*)\s$', '$1', 'sm')" as="xs:string"/>

                                <fo:block page-break-after="always" font-family="Arial" font-size="12pt">
                                    <fo:block text-align-last="justify">Niedersächsische Staats- und Universitätsbibliothek <fo:leader leader-pattern="space"
                                        />Göttingen, <xsl:value-of select="format-dateTime(current-dateTime(), '[D].[M].[Y]')"/>
                                    </fo:block>
                                    <fo:block>SUB - Zentralbibliothek – Leihstelle</fo:block>
                                    <fo:block>Platz der Göttinger Sieben 1</fo:block>
                                    <fo:block text-align-last="justify">37070 Göttingen<fo:leader leader-pattern="space"/>ol@sub.uni-goettingen.de</fo:block>
                                    <fo:block margin-top="1cm">Mit Postzustellungsurkunde</fo:block>
                                    <fo:block margin-top="1cm" linefeed-treatment="preserve">
                                        <xsl:value-of select="$adress"/>
                                    </fo:block>
                                    <fo:block text-align="end">Nutzernummer: <xsl:value-of select="$number"/>
                                    </fo:block>
                                    <fo:block margin-top="1cm" font-size="13pt" font-weight="bold">Rückgabeverfügung und Dritte Mahnung </fo:block>
                                    <fo:block margin-top="0.5cm">Sie werden hiermit aufgefordert, der folgenden Verfügung nachzukommen:</fo:block>
                                    <fo:list-block provisional-distance-between-starts="0.4cm" provisional-label-separation="0.4cm" margin-top="2mm">
                                        <fo:list-item>
                                            <fo:list-item-label start-indent="4mm">
                                                <fo:block>1.</fo:block>
                                            </fo:list-item-label>
                                            <fo:list-item-body start-indent="9mm">
                                                <fo:block>Der folgende Band, dessen Leihfrist abgelaufen ist, ist gem. Par. 22 der Benutzungsordnung der
                                                    Niedersächsischen Staats- und Universitätsbibliothek Göttingen vom 18.12.1996 innerhalb von 14 Tagen nach
                                                    Zustellung dieses Bescheides zurückzugeben und die angefallenen Mahngebühren sind zu entrichten. Außerdem
                                                    werden Sie bis zur Rückgabe des Werkes bzw. einer evtl. Ersatzleistung von der Ausleihe
                                                    ausgeschlossen.</fo:block>
                                            </fo:list-item-body>
                                        </fo:list-item>
                                        <fo:list-item>
                                            <fo:list-item-label start-indent="4mm">
                                                <fo:block>2.</fo:block>
                                            </fo:list-item-label>
                                            <fo:list-item-body start-indent="9mm">
                                                <fo:block font-family="Arial" font-size="12pt">Für die Rückgabeverfügung wird die sofortige Vollziehung
                                                    angeordnet.</fo:block>
                                            </fo:list-item-body>
                                        </fo:list-item>
                                        <fo:list-item>
                                            <fo:list-item-label start-indent="4mm">
                                                <fo:block>3.</fo:block>
                                            </fo:list-item-label>
                                            <fo:list-item-body start-indent="9mm">
                                                <fo:block>
                                                    <fo:inline>Sollten Sie dieser Aufforderung nicht vollständig nachkommen, werden wir Ihnen die
                                                        Ersatzbeschaffungskosten samt Bearbeitungsgebühren in Form eines Leistungsbescheides in Rechnung
                                                        stellen.</fo:inline>
                                                </fo:block>
                                            </fo:list-item-body>
                                        </fo:list-item>
                                    </fo:list-block>

                                    <fo:block margin-top="0.5cm">Wir bitten Sie, den folgenden Band, dessen Leihfrist abgelaufen ist, umgehend zurückzugeben: </fo:block>
                                    <fo:block margin-top="0.5cm" font-weight="bold">
                                        <xsl:value-of select="$level"/>
                                    </fo:block>
                                    <fo:block margin-top="0.5cm">Titel:<fo:leader leader-pattern="space"/><xsl:value-of select="$title"/></fo:block>
                                    <fo:block>
                                        <xsl:choose>
                                            <xsl:when test="$author != ''"> Autor:<fo:leader leader-pattern="space"/><xsl:value-of select="$author"/></xsl:when>
                                            <xsl:otherwise>
                                                <fo:leader leader-pattern="space"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block>
                                    <fo:block>Signatur:<fo:leader leader-pattern="space"/><xsl:value-of select="$shelfmark"/></fo:block>

                                    <fo:block margin-top="0.5cm">Rückgabedatum: <xsl:value-of select="$date"/></fo:block>
                                    <fo:block margin-top="0.5cm">Mahngebühr (gilt nur für diese Mahnung): <xsl:value-of select="$fee"/> €</fo:block>
                                    <fo:block margin-top="1cm">Mit freundlichen Grüßen</fo:block>
                                    <fo:block>Ihre SUB</fo:block>
                                    <fo:block font-size="11pt">
                                        <fo:footnote>
                                            <fo:inline/>
                                            <fo:footnote-body>
                                                <fo:block keep-together.within-column="always">
                                                    <fo:block>Dieses Schreiben wurde maschinell erstellt und bedarf daher keiner Unterschrift.</fo:block>
                                                    <fo:block>Beachten Sie die beiliegenden Hinweise nebst der abschließenden Rechtsbehelfsbelehrung.</fo:block>
                                                    <fo:block> Nord LB, BLZ 25050000, Kto.106032725, Verw.-Z.:Gebühren/Nutzernummer</fo:block>
                                                </fo:block>
                                            </fo:footnote-body>
                                        </fo:footnote>
                                    </fo:block>
                                </fo:block>
                                <fo:block page-break-after="always">
                                    <!-- Adress -->
                                    <fo:block-container position="absolute" font-family="Arial" font-size="10pt" top="11mm" height="22mm"
                                        linefeed-treatment="preserve">
                                        <fo:block>
                                            <xsl:value-of select="$adress"/>
                                        </fo:block>
                                    </fo:block-container>
                                    <fo:block-container position="absolute" font-family="Arial" font-size="11pt" top="230mm" width="50mm" height="30mm"
                                        text-align="center">
                                        <fo:block>Niedersächsische Staats-</fo:block>
                                        <fo:block>und Universitätsbibliothek</fo:block>
                                        <fo:block>- Ausleihe -</fo:block>
                                        <fo:block>Geschäftsstelle der Benutzungsabteilung</fo:block>
                                        <fo:block>Platz der Göttinger Sieben 1</fo:block>
                                        <fo:block>37073 Göttingen</fo:block>
                                    </fo:block-container>
                                </fo:block>
                            </xsl:if>
                        </xsl:matching-substring>
                    </xsl:analyze-string>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template match="text()"/>
</xsl:stylesheet>
