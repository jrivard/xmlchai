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
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class XmlElementW3c implements XmlElement
{
    /**
     * The actual wrapped w3c element instance.
     */
    private final org.w3c.dom.Element element;

    /**
     * The parent document of this element.  If null, this element is detached from its parent.
     */
    private XmlDocumentW3c xmlDocument;

    /**
     * A local lock instance used only when mutations are done on detached elements.
     */
    private Lock localLock;

    @SuppressFBWarnings( "FCCD_FIND_CLASS_CIRCULAR_DEPENDENCY" )
    XmlElementW3c( final org.w3c.dom.Element element, final XmlDocumentW3c xmlDocument )
    {
        this.element = Objects.requireNonNull( element );
        this.xmlDocument = xmlDocument;
    }

    private Lock getLock()
    {
        if ( xmlDocument != null )
        {
            return xmlDocument.getLock();
        }

        if ( localLock == null )
        {
            localLock = new ReentrantLock();
        }

        return localLock;
    }

    private void modificationCheck()
    {
        if ( xmlDocument != null && xmlDocument.getAccessMode() == AccessMode.IMMUTABLE )
        {
            throw new UnsupportedOperationException( "parent XmlDocument has modify mode set to immutable" );
        }
    }

    @Override
    public String getName()
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            return element.getTagName();
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public Optional<XmlElement> getChild( final String elementName )
    {
        final List<XmlElement> children = getChildren( elementName );
        if ( XmlChaiInternalUtils.isEmpty( children ) )
        {
            return Optional.empty();
        }
        return Optional.of( children.get( 0 ) );
    }

    @Override
    public List<String> getAttributeNames()
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            final List<String> returnValues = new ArrayList<>();
            final NamedNodeMap attributes = element.getAttributes();

            // get the number of nodes in this map
            final int numAttrs = attributes.getLength();

            for ( int i = 0; i < numAttrs; i++ )
            {
                final Attr attr = ( Attr ) attributes.item( i );

                final String attrName = attr.getNodeName();
                if ( !XmlChaiInternalUtils.isEmpty( attrName ) )
                {
                    returnValues.add( attrName );
                }

            }

            return returnValues.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList( returnValues );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public Optional<String> getAttribute( final String attribute )
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            final String attrValue = element.getAttribute( attribute );
            return XmlChaiInternalUtils.isEmpty( attrValue ) ? Optional.empty() : Optional.of( attrValue );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public List<XmlElement> getChildren()
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            final NodeList nodeList = element.getChildNodes();
            return XmlFactoryW3c.nodeListToElementList( nodeList, xmlDocument );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public List<XmlElement> getChildren( final String elementName )
    {
        Objects.requireNonNull( elementName );

        final Lock lock = getLock();
        lock.lock();
        try
        {
            final NodeList nodeList = element.getElementsByTagName( elementName );
            return XmlFactoryW3c.nodeListToElementList( nodeList, xmlDocument );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public Optional<String> getText()
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            final StringBuilder output = new StringBuilder();
            final NodeList nodeList = element.getChildNodes();
            if ( nodeList != null )
            {
                for ( int i = 0; i < nodeList.getLength(); i++ )
                {
                    final Node node = nodeList.item( i );
                    final short nodeType = node.getNodeType();
                    if ( nodeType == Node.TEXT_NODE )
                    {
                        final String text = node.getTextContent();
                        output.append( text == null ? "" : text.trim() );
                    }
                    else if ( nodeType == Node.CDATA_SECTION_NODE )
                    {
                        final String text = node.getTextContent();
                        output.append( text == null ? "" : text );
                    }
                }
            }

            return output.length() < 1 ? Optional.empty() : Optional.of( output.toString() );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void setAttribute( final String attributeName, final String value )
    {
        Objects.requireNonNull( attributeName );
        Objects.requireNonNull( value );

        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            element.setAttribute( attributeName, value );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void detach()
    {
        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            element.getParentNode().removeChild( element );
        }
        finally
        {
            lock.unlock();
        }
        xmlDocument = null;
    }

    @Override
    public void removeChildren()
    {
        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            final NodeList nodeList = element.getChildNodes();
            for ( final XmlElement child : XmlFactoryW3c.nodeListToElementList( nodeList, xmlDocument ) )
            {
                element.removeChild( ( ( XmlElementW3c ) child ).element );
                ( ( XmlElementW3c ) child ).xmlDocument = null;
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void removeChildren( final String elementName )
    {
        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            final NodeList nodeList = element.getElementsByTagName( elementName );
            for ( final XmlElement child : XmlFactoryW3c.nodeListToElementList( nodeList, xmlDocument ) )
            {
                element.removeChild( ( ( XmlElementW3c ) child ).element );
                ( ( XmlElementW3c ) child ).xmlDocument = null;
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void removeAttributes()
    {
        modificationCheck();
        final Lock lock = getLock();
        lock.lock();
        try
        {
            while ( element.getAttributes().getLength() > 0 )
            {
                final Node attribute = element.getAttributes().item( 0 );
                element.getAttributes().removeNamedItem( attribute.getNodeName() );
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void removeAttribute( final String attributeName )
    {
        Objects.requireNonNull( attributeName );

        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            element.removeAttribute( attributeName );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public XmlElement newChildElement( final String elementName )
    {
        Objects.requireNonNull( elementName );

        modificationCheck();

        final XmlElement newNode = xmlDocument.getFactory().newElement( elementName );
        attachElement( newNode );
        return newNode;
    }

    @Override
    public void attachElement( final XmlElement element )
    {
        Objects.requireNonNull( element );

        attachElement( Collections.singleton( element ) );
    }

    @Override
    public void attachElement( final Collection<XmlElement> elements )
    {
        Objects.requireNonNull( elements );

        modificationCheck();

        for ( final XmlElement element : elements )
        {
            if ( element.parent().isPresent() )
            {
                throw new IllegalStateException( "element named '" + element.getName() + "' already has an attached parent" );
            }
        }

        final Lock lock = getLock();
        lock.lock();
        try
        {
            for ( final XmlElement element : elements )
            {
                final org.w3c.dom.Element w3cElement = ( ( XmlElementW3c ) element ).element;
                this.element.getOwnerDocument().adoptNode( w3cElement );
                this.element.appendChild( w3cElement );
                ( ( XmlElementW3c ) element ).xmlDocument = xmlDocument;
                ( ( XmlElementW3c ) element ).localLock = null;
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void removeText()
    {
        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            this.element.setTextContent( null );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void setText( final String text )
    {
        Objects.requireNonNull( text );

        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            this.element.setTextContent( text );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void setComment( final List<String> textLines )
    {
        Objects.requireNonNull( textLines );

        modificationCheck();

        final Lock lock = getLock();
        lock.lock();
        try
        {
            final NodeList nodeList = element.getChildNodes();
            for ( int i = 0; i < nodeList.getLength(); i++ )
            {
                final Node node = nodeList.item( i );
                if ( node.getNodeType() == Node.COMMENT_NODE )
                {
                    element.removeChild( node );
                }
            }

            final DocumentBuilder documentBuilder = XmlFactoryW3c.getBuilder();
            final org.w3c.dom.Document document = documentBuilder.newDocument();

            final List<String> reversedList = new ArrayList<>( textLines );
            Collections.reverse( reversedList );
            for ( final String text : reversedList )
            {
                final org.w3c.dom.Comment textNode = document.createComment( text );
                this.element.getOwnerDocument().adoptNode( textNode );

                if ( element.hasChildNodes() )
                {
                    element.insertBefore( textNode, element.getFirstChild() );
                }
                else
                {
                    element.appendChild( textNode );
                }

            }
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public XmlElement copy()
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            final Node newNode = this.element.cloneNode( true );
            this.element.getOwnerDocument().adoptNode( newNode );
            return new XmlElementW3c( ( org.w3c.dom.Element ) newNode, null );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public Optional<XmlElement> parent()
    {
        final Lock lock = getLock();
        lock.lock();
        try
        {
            final Node parentElement = this.element.getParentNode();
            if ( !( parentElement instanceof org.w3c.dom.Element ) )
            {
                return Optional.empty();
            }
            return Optional.of( new XmlElementW3c( ( org.w3c.dom.Element ) parentElement, xmlDocument ) );
        }
        finally
        {
            lock.unlock();
        }
    }
}
