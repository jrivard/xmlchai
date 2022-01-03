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
import java.util.Optional;

public class XmlDocument2Test
{
    private XmlDocument readXmlDocument() throws IOException
    {
        final InputStream xmlFactoryTestXmlFile = this.getClass().getResourceAsStream( "XmlDocument2.xml" );
        return XmlChai.getFactory().parse( xmlFactoryTestXmlFile, AccessMode.IMMUTABLE );
    }

    @Test
    public void testXpathToElements()
            throws Exception
    {
        final XmlDocument xmlDocument = readXmlDocument();

        Assertions.assertEquals( "RequiredQuestions", xmlDocument.getRootElement().getName() );

        final List<XmlElement> questions = xmlDocument.evaluateXpathToElements( "//Question" );
        Assertions.assertEquals( 2, questions.size() );
        Assertions.assertEquals( "How do you like your burger?", questions.get( 0 ).getText().get() );
        Assertions.assertEquals( "128", questions.get( 0 ).getAttribute("MaxLength" ).get() );
        Assertions.assertEquals( "4", questions.get( 0 ).getAttribute("MinLength" ).get() );
        Assertions.assertEquals( "What is your mother's maiden name?", questions.get( 1 ).getText().get() );
    }

    @Test
    public void testXpathToElements2()
            throws Exception
    {
        final XmlDocument xmlDocument = readXmlDocument();

        final List<XmlElement> questions = xmlDocument.evaluateXpathToElements( "//Question/display" );
        Assertions.assertEquals( 2, questions.size() );
        Assertions.assertEquals( "How do you like your burger?", questions.get( 0 ).getText().get() );
        Assertions.assertEquals( "What is your mother's maiden name?", questions.get( 1 ).getText().get() );
        final List<String> allAttrNames = questions.get( 0 ).getAttributeNames();
        Assertions.assertEquals( 2, allAttrNames.size() );
    }


    @Test
    public void testXpathToElements3()
            throws Exception
    {
        final XmlDocument xmlDocument = readXmlDocument();

        final XmlElement element1 = xmlDocument.evaluateXpathToElement( "//Question/display[1]" ).get();
        final Optional<String> langValue = element1.getAttribute( "xml:lang" );
        Assertions.assertTrue( langValue.isPresent() );
        Assertions.assertEquals( "en", langValue.get() );
    }
}
