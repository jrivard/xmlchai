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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

@SuppressFBWarnings( "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY" )
// FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY is incorrectly detected on this class
class XmlFactoryW3c implements XmlFactory
{
    /**
     * Singleton for the standard W3C XmlFactory.
     */
    private static final XmlFactoryW3c W3C_FACTORY = new XmlFactoryW3c();

    /**
     * Character set used the ChaiLibrary for parsing/outputting strings.
     */
    private static final Charset XML_STRING_CHARSET = StandardCharsets.UTF_8;

    private XmlFactoryW3c()
    {
    }

    static XmlFactoryW3c getW3cFactory()
    {
        return W3C_FACTORY;
    }

    @Override
    @SuppressFBWarnings( value = "XXE_DOCUMENT" )
    // XXE_DOCUMENT suppressing XXE warning as appropriate builder features are set in getBuilder() method
    public XmlDocument parse( final InputStream inputStream, final AccessMode accessMode )
            throws IOException
    {
        try
        {
            final DocumentBuilder builder = getBuilder();
            final org.w3c.dom.Document inputDocument = builder.parse( inputStream );
            return new XmlDocumentW3c( this, inputDocument, accessMode );
        }
        catch ( final Exception e )
        {
            throw new IOException( "error parsing xml data: " + e.getMessage(), e );
        }
    }

    @Override
    public XmlDocument parseString( final String input, final AccessMode accessMode )
            throws IOException
    {
        Objects.requireNonNull( input );
        Objects.requireNonNull( accessMode );

        try ( ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( input.getBytes( XML_STRING_CHARSET ) ) )
        {
            return parse( byteArrayInputStream, accessMode );
        }
    }

    @SuppressFBWarnings( "EXS_EXCEPTION_SOFTENING_NO_CONSTRAINTS" )
    static DocumentBuilder getBuilder()
    {
        try
        {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
            dbFactory.setFeature( "http://apache.org/xml/features/disallow-doctype-decl", true );
            dbFactory.setValidating( false );
            dbFactory.setXIncludeAware( false );
            dbFactory.setExpandEntityReferences( false );
            return dbFactory.newDocumentBuilder();
        }
        catch ( final ParserConfigurationException e )
        {
            throw new IllegalArgumentException( "unable to generate dom xml builder: " + e.getMessage(), e );
        }
    }

    @Override
    public void output( final XmlDocument document, final OutputStream outputStream, final OutputFlag... outputFlags )
            throws IOException
    {
        Objects.requireNonNull( document );
        Objects.requireNonNull( outputStream );

        final boolean compact = XmlChaiInternalUtils.enumArrayContainsValue( outputFlags, OutputFlag.Compact );

        final Lock lock = ( ( XmlDocumentW3c ) document ).getLock();
        lock.lock();
        try
        {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature( XMLConstants.FEATURE_SECURE_PROCESSING, true );
            factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_DTD, "" );
            factory.setAttribute( XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "" );

            final Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty( OutputKeys.INDENT, compact ? "no" : "yes" );
            transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
            transformer.setOutputProperty( OutputKeys.ENCODING, XML_STRING_CHARSET.toString() );

            transformer.transform( new DOMSource( ( ( XmlDocumentW3c ) document ).getW3cDocument() ), new StreamResult( outputStream ) );
        }
        catch ( final TransformerException e )
        {
            throw new IOException( "error loading xml transformer: " + e.getMessage(), e );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public String outputString( final XmlDocument document, final OutputFlag... outputFlags )
            throws IOException
    {
        Objects.requireNonNull( document );

        try ( ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream() )
        {
            output( document, byteArrayOutputStream, outputFlags );
            return byteArrayOutputStream.toString( XML_STRING_CHARSET.name() );
        }
    }

    static List<XmlElement> nodeListToElementList(
            final XmlFactory xmlFactory,
            final NodeList nodeList,
            final XmlDocumentW3c xmlDocumentW3c
            )
    {
        if ( nodeList != null )
        {
            final int length = nodeList.getLength();
            if ( length > 0 )
            {
                final List<XmlElement> returnList = new ArrayList<>();
                for ( int i = 0; i < length; i++ )
                {
                    final Node node = nodeList.item( i );
                    if ( node.getNodeType() == Node.ELEMENT_NODE )
                    {
                        returnList.add( new XmlElementW3c( ( org.w3c.dom.Element ) node, xmlFactory, xmlDocumentW3c ) );
                    }
                }
                return Collections.unmodifiableList( returnList );
            }
        }
        return Collections.emptyList();
    }

    @Override
    public XmlDocument newDocument( final String rootElementName )
    {
        Objects.requireNonNull( rootElementName );

        final DocumentBuilder documentBuilder = getBuilder();
        final org.w3c.dom.Document document = documentBuilder.newDocument();
        document.setXmlStandalone( true );
        final org.w3c.dom.Element rootElement = document.createElement( rootElementName );
        document.appendChild( rootElement );
        return new XmlDocumentW3c( this, document, AccessMode.MUTABLE );
    }

    @Override
    public XmlElement newElement( final String elementName )
    {
        final DocumentBuilder documentBuilder = getBuilder();
        final org.w3c.dom.Document document = documentBuilder.newDocument();
        final org.w3c.dom.Element element = document.createElement( elementName );
        return new XmlElementW3c( element, this, null );
    }
}
