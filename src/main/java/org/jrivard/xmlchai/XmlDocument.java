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

import java.util.List;
import java.util.Optional;

/**
 * Represents a mutable Document containing an {@link XmlElement} reference.  <code>XmlDocument</code> instances
 * are thread-safe.
 */
public interface XmlDocument
{
    /**
     * Get the top level root element for the document.
     * @return The top level root element for the document.
     */
    XmlElement getRootElement();

    /**
     * Execute the xpath query and return the first matching element, if any.
     * @param xpathExpression A valid xpath expression.
     * @return Return the first matching element, if any.
     * @throws NullPointerException if the {@code xpathExpression} is null.
     */
    Optional<XmlElement> evaluateXpathToElement( String xpathExpression );

    /**
     * Execute the xpath query and return all the matching elements, if any.
     * @param xpathExpression A valid xpath expression.
     * @return Return all the matching elements, if any.
     * @throws NullPointerException if the {@code xpathExpression} is null.
     */
    List<XmlElement> evaluateXpathToElements( String xpathExpression );

    /**
     * Make a copy of the entire document.  The {@code AccessMode} of the copied
     * document will be {@link AccessMode#MUTABLE}.
     * @return A new copy of the current document.
     */
    XmlDocument copy();

    /**
     * Get the access mode of this document.
     * @return The access mode of this document.
     */
    AccessMode getAccessMode();

}
