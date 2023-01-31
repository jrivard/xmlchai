/*
 * XML Chai Library
 * Copyright (c) 2021 Jason D. Rivard
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class XmlDocumentTest
{
    private XmlDocument readXmlDocument() throws IOException
    {
        final InputStream xmlFactoryTestXmlFile = this.getClass().getResourceAsStream( "plant_catalog.xml" );
        return XmlChai.getFactory().parse( xmlFactoryTestXmlFile, AccessMode.IMMUTABLE );
    }

    @Test
    public void testXpathToElements()
            throws Exception
    {
        final XmlDocument xmlDocument = readXmlDocument();

        final List<XmlElement> results = xmlDocument.evaluateXpathToElements(
                "//PLANT[ZONE[text()=$0]]",
                List.of( "Annual" ) );

        Assertions.assertEquals( 8, results.size() );

        Assertions.assertThrows( IllegalArgumentException.class, () -> xmlDocument.evaluateXpathToElements(
                "//PLANT[ZONE[text()=$0]]",
                List.of( "Annual", "Extra Value" ) ) );

    }

}
