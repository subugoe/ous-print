/*
 * This file is part of the OUS Print Server, Copyright 2015 SUB Göttingen
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

package de.unigoettingen.sub.be.ous.print.layout.camel

import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.test.junit4.CamelTestSupport

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import org.joda.time.format.ISODateTimeFormat

import org.junit.Test

class TimerTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint

    protected DateTime dt
    protected String cronExpression
    protected String fileName

    final static String CHARSET = 'ISO-8859-1'
    final static String REMINDER_LOCATION = './target/test-classes/'
    final static String REMINDER =
            """<address>
riedel@sub.uni-goettingen.de
</address>
<reminder>
SUB / Historisches Gebaeude
Papendiek 14
37073  Goettingen
ol@sub.uni-goettingen.de


Technische Buchbearbeitung  HG
SUB / Historisches Geb~ude
Papendiek 14
37073 G_ttingen
Nutzernummer: 00071719237

Wir bitten Sie, folgende Positionen mit abgelaufener Leihfrist umgehend zu
verlaengern oder zurueckzugeben.

Dritte Mahnung

Titel   :  Historische, Kurtze, Einfaltige und Ordentli
Autor   : Letzner, Johann
Signatur: HG-MAG:8 H HAS NASS 7160

Rueckgabedatum: 24-03-2015

Mahngebuehr (gilt nur fuer diese Mahnung): EUR     0.00

Mit freundlichem Gruss
Ihre SUB Goettingen

Hinweise zum Verlaengern finden Sie auf unserer Website unter
http://www.sub.uni-goettingen.de/ausleihen-verlaengern/ausleihen-vor-ort-verlaengern/#c204

Auf die 1. Mahnung folgt eine 2. bzw. 3. Mahnung nach jeweils
sieben Tagen und ist mit weiteren Gebuehren verbunden. Die Fristsetzung
nach der dritten Mahnung betraegt 14 Tage.

Die Mahngebuehren betragen fuer die
1. Mahnung    2,00 Euro je Band,
2. Mahnung    5,00 Euro je Band, zuzueglich 2,00 Euro fuer die erste Mahnung,
3. Mahnung   10,00 Euro je Band, zuzueglich 7,00 Euro fuer die erste und zweite Mahnung,
insgesamt also 17.00 Euro.

Die Mahngebuehren sind an einer unserer Servicetheken zu entrichten
oder auf das u. g. Konto zu ueberweisen.

Gem. Paragraph 2 der Gebuehrenordnung fuer die Bibliotheken des Landes
Niedersachsen vom 29.02.1996 (Nds. GVBl. S.45, zuletzt geaendert am 16.11.2004)
nebst Gebuehrenverzeichnis (Nds. GVBl. S. 678) in Verbindung mit Par. 23 der
Benutzungsordnung der Niedersaechsischen Staats- und Universitaetsbibliothek
Goettingen (SUB) (BenO) v. 18.12.1996, in Kraft getreten durch
Veroeffentlichung in den Amtlichen Mitteilungen der Georg-August-Universitaet
am 3.2.1997.
Bei Buchverlust ist gemaess Par. 7 Abs. 3 der BenO Ersatz zu leisten.
Bei Ersatzbeschaffung durch die Bibliothek faellt eine Beschaffungsgebuehr von
5,00 Euro an. Hinzu kommt fuer jedes Buch eine Einarbeitungsgebuehr von
15,00 Euro.
Bei Schliessfachschluesselverlust betraegt die Fristsetzung nach der zweiten
Mahnung 14 Tage. Danach fallen Kosten in Hoehe von 39,00 Euro fuer den
Austausch des Schliesszylinders inkl. Bearbeitungsgebuehr an.

Rechtsbehelfsbelehrung:
Gegen diesen Bescheid kann innerhalb eines Monats nach Bekanntgabe Klage beim
Verwaltungsgericht Goettingen erhoben werden.

Hinweis:
In Niedersachsen koennen Sie unmittelbar nach Bekanntgabe des Bescheids Klage
beim zustaendigen Verwaltungsgericht erheben, ohne dass es einer nochmaligen
Ueberpruefung durch uns bedarf. Es wird dringend empfohlen, sich zur Abklaerung
moeglicher Unstimmigkeiten mit uns in Verbindung zu setzen. Beachten Sie bitte,
dass hierdurch die in der Rechtsbehelfsbelehrung genannte Klagefrist unberuehrt
bleibt. Die Klage hat keine aufschiebende Wirkung (Par. 80 Abs. 2 Nr. 1 u. 4 der
Verwaltungsgerichtsordnung i.V.m. Par. 64 Abs. 4 Nieders. SOG).

NLB, BLZ 25050000, Kto. 106032725, IBAN: DE49250500000106032725,
BIC: NOLADE2HXXX, Verw.-Z. Gebuehren/NUTZERNUMMER


























</reminder>
"""

    @Override
    void doPreSetup() {
        //Set up cron expression
        java.util.Date juDate = new Date()
        dt = new DateTime(juDate).plusSeconds(10)
        cronExpression = cronTime(dt)
        //Create test file
        DateTimeFormatter fileNameFmt = new DateTimeFormatterBuilder()
                .appendLiteral('ous_040_email_reminders_')
                .appendDayOfMonth(2)
                .appendMonthOfYear(2).appendLiteral('.rem')
                .toFormatter()
        fileName = dt.toString(fileNameFmt)
        new File(REMINDER_LOCATION + fileName).write(REMINDER, CHARSET)
    }

    // See http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger
    public static String cronTime (DateTime date) {
        DateTimeFormatter cronFmt = new DateTimeFormatterBuilder()
                .appendSecondOfMinute(2).appendLiteral('+')
                .appendMinuteOfHour(2).appendLiteral('+')
                .appendHourOfDay(2).appendLiteral('+')
                .appendDayOfMonth(2).appendLiteral('+')
                .appendMonthOfYear(2).appendLiteral('+?')
                .toFormatter()
        return date.toString(cronFmt)
    }

    @Test
    public void testMessageCount() {
        DateTime startDate = new DateTime(new Date())
        log.info("Start date: ${startDate.toString(ISODateTimeFormat.basicDateTime())}, Cron syntax: ${cronTime(startDate)}")
        resultEndpoint.setMinimumResultWaitTime(500)
        resultEndpoint.setResultWaitTime(60000)
        resultEndpoint.expectedMessageCount(1)
        assertMockEndpointsSatisfied()
    }

    @Override
    public boolean isUseDebugger() {
        // must enable debugger
        return true;
    }

    @Override
    protected void debugBefore(Exchange exchange, Processor processor, ProcessorDefinition definition, String id, String shortName) {
        // See http://camel.apache.org/debugger.html
        // this method is invoked before we are about to enter the given processor
        // from your Java editor you can just add a breakpoint in the code line below
        log.info("Before " + definition + " with body " + exchange.getIn().getBody());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //See https://camel.apache.org/quartz2.html
                from("quartz2://testTimer?cron=${cronExpression}")
                        .from("file:${REMINDER_LOCATION}?fileName=ous_040_email_reminders_\${date:now:ddMM}.rem&noop=true&charset=${CHARSET}")
                        .to("plainText:.&pageSize=A4")
                        .to('xslt:file:./src/main/resources/xslt/reminders.xsl?saxon=true')
                        .to('file:./target/?fileName=reminders-' + this.class.getName() + '.xsl')
                        .to("fop:application/pdf")
                        .to('file:./target/?fileName=reminders-' + this.class.getName() + '.pdf')
                        .to("mock:result")
            }
        };
    }
}
