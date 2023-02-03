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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Represents a XML element.  XmlElement instances are threadsafe.
 */
public interface XmlElement
{
    /**
     * Get all attribute names of this element.
     * @return all attribute names of this element.
     */
    List<String> getAttributeNames();

    /**
     * Get the string attribute value of this element with the name of {@code attributeName}.
     * @param attributeName name of the attribute.
     * @return An {@link Optional} containing the attribute value, if the attribute exists and the value is a non-empty {@code String}.
     */
    Optional<String> getAttribute( String attributeName );

    /**
     * Get the direct child element of this element of the name {@code elementName}.  If there are multiple elements of the specified
     * {@code elementName}, the first one is returned.
     * @param elementName name of the element to get.
     * @return An {@link Optional} containing the element.
     * @throws NullPointerException if {@code elementName} is null.
     */
    Optional<XmlElement> getChild( String elementName );

    /**
     * Get all the direct child elements of this element.  If no such elements are present, an empty
     * list is returned.
     * @return A {@link List} containing the element(s), if any.
     */
    List<XmlElement> getChildren();

    /**
     * Get all the direct child elements of this element of the name {@code elementName}.  If no such elements are present, an empty
     * list is returned.
     * @param elementName name of the elements to get.
     * @return A {@link List} containing the element(s), if any.
     * @throws NullPointerException if {@code elementName} is null.
     */
    List<XmlElement> getChildren( String elementName );

    /**
     * Get the name of this element.
     * @return the name of this element.
     */
    String getName();

    /**
     * Get the contents of the child text elements of this element.  This includes plain text nodes (whitespace trimmed) and "CDATA"
     * nodes (not trimmed) concatenated into a single string value.
     * @return All direct text children of this element in a concatenated string.
     */
    Optional<String> getText();

    /**
     * Get the parent element if this element.  An element has no parent if it is not attached or is the root element.
     * @return The parent element, if any.
     */
    Optional<XmlElement> parent();

    /**
     * Set attribute value on current element.
     * @param attributeName name of the attribute.
     * @param value value to write to the attribute.
     * @throws NullPointerException if {@code attributeName} or {@code value} are null.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void setAttribute( String attributeName, String value );

    /**
     * Detach all the direct child elements, if any, from this element.  If those elements are not otherwise referenced by a client
     * they will become unreachable.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void removeChildren();

    /**
     * Detach all the direct child elements, if any, with the specified elementName from this element.
     * If those elements are not otherwise referenced by a client they will become unreachable.
     * @param elementName name of the element(s) to remove..
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void removeChildren( String elementName );

    /**
     * Remove all attribute values,if any on current element.
     * @throws NullPointerException if {@code attributeName} is null.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void removeAttributes();

    /**
     * Remove the attribute value on current element.
     * @param attributeName name of the attribute.
     * @throws NullPointerException if {@code attributeName} is null.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void removeAttribute( String attributeName );

    /**
     * Create a child element and attach it to this element.
     * @param elementName name of the element to create.
     * @return The newly created element.
     * @throws NullPointerException if {@code elementName} is null.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    XmlElement newChildElement( String elementName );

    /**
     * Attach an element as a child to this element.
     *
     * @param element an {@code XmlElement} without an attached parent
     * @throws NullPointerException if {@code element} is null.
     * @throws IllegalStateException if the element already has an attached parent.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void attachElement( XmlElement element );

    /**
     * Attach a collection of elements as children to this element.
     * @param elements a collection of {@code XmlElement}s each without an attached parent.
     * @throws NullPointerException if {@code elements} is null.
     * @throws IllegalStateException if any of the elements already has an attached parent.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void attachElement( Collection<XmlElement> elements );

    /**
     * Removes any and all text elements that are direct decendents of this element.  If this element has no existing text
     * element children, this method has no effect.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void removeText();

    /**
     * Remove all direct child text and CDATA elements from the current element and create a new text element with the supplied
     * {@code text} value.
     * @param text The text value to set.
     * @throws NullPointerException if {@code text} is null.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void setText( String text );

    /**
     * Replaces all current child comment nodes with a new comment element for each {@code String} in {@code textLines}.
     * @param textLines Lines of text, each of which will be added as a comment node.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void setComment( List<String> textLines );

    /**
     * Detaches this element from its parent.  If this element does not have a parent, no change is made.
     * @throws UnsupportedOperationException if the parent {@link XmlDocument}'s {@code AccessMode} is set to {@link AccessMode#IMMUTABLE}.
     */
    @ModifyOperation
    void detach();

    /**
     * Create and return a new instance of the current element and all of its descendants.  The returned copy instance
     * will not be attached to a parent element.
     * @return A new instance of the current element.
     */
    XmlElement copy();
}
