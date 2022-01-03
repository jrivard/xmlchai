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

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class XmlDocumentW3c implements XmlDocument
{
    /**
     * The factory associated with this XmlDocument.
     */
    private final XmlFactoryW3c factory;

    /**
     * The wrapped w3c document instance.
     */
    private final org.w3c.dom.Document document;

    /**
     * The access mode used by this document instance.
     */
    private final AccessMode accessMode;

    /**
     * The lock used by this document and all attached {@link XmlElementW3c} instances.
     */
    private final Lock lock = new ReentrantLock();

    XmlDocumentW3c(
            final XmlFactoryW3c factory,
            final org.w3c.dom.Document document,
            final AccessMode mode
    )
    {
        this.factory = Objects.requireNonNull( factory );
        this.document = Objects.requireNonNull( document );
        this.accessMode = Objects.requireNonNull( mode );
    }

    Lock getLock()
    {
        return lock;
    }

    Document getW3cDocument()
    {
        return document;
    }


    XmlFactoryW3c getFactory()
    {
        return factory;
    }

    public AccessMode getModifyMode()
    {
        return accessMode;
    }

    @Override
    public XmlElement getRootElement()
    {
        lock.lock();
        try
        {
            return new XmlElementW3c( document.getDocumentElement(), this );
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public Optional<XmlElement> evaluateXpathToElement(
            final String xpathExpression
    )
    {
        final List<XmlElement> elements = evaluateXpathToElements( xpathExpression );
        if ( elements == null || elements.isEmpty() )
        {
            return Optional.empty();
        }
        return Optional.of( elements.iterator().next() );
    }

    @Override
    public List<XmlElement> evaluateXpathToElements(
            final String xpathExpression
    )
    {
        getLock().lock();
        try
        {
            final XPath xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
            final javax.xml.xpath.XPathExpression expression = xPath.compile( xpathExpression );
            final NodeList nodeList = ( NodeList ) expression.evaluate( document, XPathConstants.NODESET );
            return XmlFactoryW3c.nodeListToElementList( nodeList, this );
        }
        catch ( final XPathExpressionException e )
        {
            throw new IllegalStateException( "error evaluating xpath expression: " + e.getMessage() );
        }
        finally
        {
            getLock().unlock();
        }
    }

    @Override
    public XmlDocument copy()
    {
        getLock().lock();
        try
        {
            final org.w3c.dom.Document clonedNode = ( org.w3c.dom.Document ) document.cloneNode( true );
            return new XmlDocumentW3c( factory, clonedNode, accessMode );
        }
        finally
        {
            getLock().unlock();
        }
    }
}
