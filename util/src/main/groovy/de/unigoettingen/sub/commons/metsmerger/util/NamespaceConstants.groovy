/*
 * This file is part of the METS Merger, Copyright 2011, 2012 SUB Göttingen
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


package de.unigoettingen.sub.commons.metsmerger.util

import groovy.transform.TypeChecked
import javax.xml.namespace.NamespaceContext

/**
 * This class contains some namespace URIs and helper methods
 * @author cmahnke
 */
@TypeChecked
class NamespaceConstants implements NamespaceContext {
    /** The XML namespace URO */
    static String XML_NAMESPACE = 'http://www.w3.org/XML/1998/namespace'
    /** The METS namespace URI */
    static String METS_NAMESPACE = 'http://www.loc.gov/METS/'
    /** The TEI namespace URI */
    static String TEI_NAMESPACE = 'http://www.tei-c.org/ns/1.0'
    /** The MODS namespace URI */
    static String MODS_NAMESPACE = 'http://www.loc.gov/mods/v3'
    /** The DC namespace URI */
    static String DC_NAMESPACE = 'http://purl.org/dc/elements/1.1/'
    /** The XLink namespace URI */
    static String XLINK_NAMESPACE = 'http://www.w3.org/1999/xlink'
    /** The XML Schema namespace URI */
    static String XS_NAMESPACE = 'http://www.w3.org/2001/XMLSchema'
    /** The XSLT namespace URI */
    static String XSLT_NAMESPACE = 'http://www.w3.org/1999/XSL/Transform'
    //Our own namespaces
    /** The Goobi namespace URI */
    static String GOOBI_NAMESPACE = 'http://meta.goobi.org/v1.5.1/'
    /** The GDZ namespace URI */
    static String GDZ_NAMESPACE = 'http://gdz.sub.uni-goettingen.de/'
    /** The DFG-Viewer namespace URI */
    static String DV_NAMESPACE = 'http://dfg-viewer.de/'

    /** A {@link java.utilMap Map} containing the prefix and the namespace URI */
    static Map NAMESPACES_WITH_PREFIX = [
            'xml': XML_NAMESPACE,
            'mets': METS_NAMESPACE,
            'tei': TEI_NAMESPACE,
            'mods': MODS_NAMESPACE,
            'dc': DC_NAMESPACE,
            'xlink': XLINK_NAMESPACE,
            'xs': XS_NAMESPACE,
            'xsl': XSLT_NAMESPACE,
            //Our own namespaces
            'goobi': GOOBI_NAMESPACE,
            'gdz': GDZ_NAMESPACE,
            'dv': DV_NAMESPACE
    ]

    //Map<String, String>
    /** A {@link java.utilMap Map} containing the namespace URI as key and the prefix as value */
    static Map PREFIX_WITH_NAMESPACES = [:]
    static {

        NAMESPACES_WITH_PREFIX.each() { key, value -> PREFIX_WITH_NAMESPACES[value] = key }
    }

    /** A {@link java.utilMap Map} containing the namespace URI and the location of the schema */
    static Map SCHEMA_LOCATIONS = [:]
    static {
        SCHEMA_LOCATIONS[METS_NAMESPACE] = 'http://www.loc.gov/standards/mets/version17/mets.v1-7.xsd'
        SCHEMA_LOCATIONS[XSLT_NAMESPACE] = 'http://www.w3.org/2007/schema-for-xslt20.xsd'
        SCHEMA_LOCATIONS[XLINK_NAMESPACE] = 'http://www.loc.gov/standards/mets/xlink.xsd'
        SCHEMA_LOCATIONS[DC_NAMESPACE] = 'http://dublincore.org/schemas/xmls/simpledc20021212.xsd'
    }

    /**
     * Gets the schema location for a prefix
     * @param prefix the namemace prefix
     * @return the schema location
     */
    static String getSchemaForPrefix (prefix) {
        return SCHEMA_LOCATIONS.get(NAMESPACES_WITH_PREFIX.get(prefix))
    }

    /**
     * Gets the Schema location for a given namespace URI
     * @param uri the namemace URI
     * @return the schema location
     */
    static String getSchemaLoactionForNamespace (uri) {
        return SCHEMA_LOCATIONS.get(uri)
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
     */
    @Override
    String getNamespaceURI(String prefix) {
        return NAMESPACES_WITH_PREFIX[prefix]
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
     */
    @Override
    String getPrefix(String namespaceURI) {
        return NAMESPACES_WITH_PREFIX[namespaceURI]
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
     */
    @Override
    Iterator getPrefixes(String namespaceURI) {
        (Iterator) [getPrefix(namespaceURI)]
    }

}