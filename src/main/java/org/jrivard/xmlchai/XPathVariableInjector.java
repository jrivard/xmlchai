/*
 * XML Chai Library
 * Copyright (c) 2021-2023 Jason D. Rivard
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.jrivard.xmlchai;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Internal helper to inject variables into xpath expressions.
 */
class XPathVariableInjector
{
    /**
     * A result set of used variables.
     */
    private final Set<String> unusedKeys = new HashSet<>();

    /**
     * XPath object to be used by the base class.
     */
    private final XPath xpath;

    XPathVariableInjector( final Map<String, String> suppliedParams )
    {
        xpath = XPathFactory.newInstance().newXPath();

        final Map<String, String> copiedParams = new HashMap<>( suppliedParams == null ? Collections.emptyMap() : suppliedParams );

        unusedKeys.addAll( copiedParams.keySet() );

        addInjectorToXPath( copiedParams );
    }

    private void addInjectorToXPath( final Map<String, String> params )
    {
        xpath.setXPathVariableResolver( variableName ->
        {
            final String key = variableName.getLocalPart();
            final String value = params.get( key );
            if ( value != null )
            {
                unusedKeys.remove( key );
            }
            return value;
        } );
    }

    public XPath getXPath()
    {
        return xpath;
    }

    public void throwIfParamsUnused()
    {
        if ( !unusedKeys.isEmpty() )
        {
            final String key = unusedKeys.iterator().next();
            throw new IllegalArgumentException( "xpath expression did not utilize variable $"
                    + key
                    + " for which a parameter value was included" );
        }
    }
}
