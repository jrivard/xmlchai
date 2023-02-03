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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XmlElementTest
{
    @Test
    public void testSetText()
    {
        final XmlElement xmlElement = XmlFactory.getFactory().newElement( "TEST" );
        Assertions.assertFalse( xmlElement.getText().isPresent() );

        xmlElement.setText( "text1" );
        Assertions.assertTrue( xmlElement.getText().isPresent() );
        Assertions.assertEquals( "text1", xmlElement.getText().get() );

        xmlElement.setText( "text2" );
        Assertions.assertTrue( xmlElement.getText().isPresent() );
        Assertions.assertEquals( "text2", xmlElement.getText().get() );
    }

    @Test
    public void testRemoveText()
    {
        final XmlElement xmlElement = XmlFactory.getFactory().newElement( "TEST" );
        Assertions.assertFalse( xmlElement.getText().isPresent() );

        xmlElement.setText( "text1" );
        Assertions.assertTrue( xmlElement.getText().isPresent() );
        Assertions.assertEquals( "text1", xmlElement.getText().get() );


        xmlElement.removeText(  );
        Assertions.assertFalse( xmlElement.getText().isPresent() );
    }

    @Test
    public void testGetParent()
    {
        final XmlElement xmlElement = XmlFactory.getFactory().newElement( "TEST" );
        Assertions.assertFalse( xmlElement.parent().isPresent() );

        final XmlElement childXmlElement = XmlFactory.getFactory().newElement( "TEST-CHILD" );
        xmlElement.attachElement( childXmlElement );
        Assertions.assertTrue( childXmlElement.parent().isPresent() );
    }

    @Test
    public void testRemoveAttributes()
    {
        final XmlElement xmlElement = XmlFactory.getFactory().newElement( "TEST" );
        xmlElement.setAttribute( "a", "1" );
        xmlElement.setAttribute( "b", "2" );
        xmlElement.setAttribute( "c", "3" );

        Assertions.assertEquals( 3, xmlElement.getAttributeNames().size() );

        xmlElement.removeAttributes();
        Assertions.assertEquals( 0, xmlElement.getAttributeNames().size() );
    }

    @Test
    public void testRemoveChildren()
    {
        final XmlElement xmlElement = XmlFactory.getFactory().newElement( "TEST" );
        xmlElement.newChildElement( "a" );
        xmlElement.newChildElement( "b" );
        xmlElement.newChildElement( "c" );

        Assertions.assertEquals( 3, xmlElement.getChildren().size() );

        xmlElement.removeChildren();
        Assertions.assertEquals( 0, xmlElement.getChildren().size() );
    }
}
