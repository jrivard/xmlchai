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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Factory for creating new XML objects.  Use {@link XmlChai#getFactory()} to obtain an instance.
 */
public interface XmlFactory
{
    /**
     * Hints to the XML Output processor.
     */
    enum OutputFlag
    {
        /**
         * Use Compact mode (no line-feeds or whitespace indentations) as opposed to default "Pretty Print" mode.
         */
        Compact,
    }

    /**
     * Parse an input stream into an {@link XmlDocument}.
     * @param inputStream value for {@code InputStream} containing XML data to parse.
     * @param accessMode declare if the returned XML document will be mutable or immutable.
     * @return a parsed XML document.
     * @throws NullPointerException if {@code inputStream} or {@code accessMode} is null.
     * @throws IOException if there is a stream or XML parsing error.
     */
    XmlDocument parse( InputStream inputStream, AccessMode accessMode )
            throws IOException;

    /**
     * Parse a String stream into an {@link XmlDocument}.
     * @param input value for {@code InputStream} containing XML data to parse.  Assumed to be in {@link java.nio.charset.StandardCharsets#UTF_8}.
     * @param accessMode declare if the returned XML document will be mutable or immutable.
     * @return a parsed XML document.
     * @throws NullPointerException if {@code inputStream} or {@code accessMode} is null.
     * @throws IOException if there is a stream or XML parsing error.
     */
    XmlDocument parseString( String input, AccessMode accessMode )
            throws IOException;

    /**
     * Output an XmlDocument to an output stream.
     * @param document document to be output.
     * @param outputStream output stream to write document to.
     * @param outputFlags one or more {@code OutputFlag}s to shape the output.
     * @throws NullPointerException if {@code document} or {@code outputStream} is null.
     * @throws IOException if there is a failure writing to the stream.
     */
    void output( XmlDocument document, OutputStream outputStream, OutputFlag... outputFlags )
            throws IOException;

    /**
     * Output an XmlDocument to a {@code String} using {@link java.nio.charset.StandardCharsets#UTF_8}.
     * @param document document to be output.
     * @param outputFlags one or more {@code OutputFlag}s to shape the output.
     * @return a String containing the serialized XML document.
     * @throws NullPointerException if {@code document} is null.
     * @throws IOException if there is a failure writing to the stream.
     */
    String outputString( XmlDocument document, OutputFlag... outputFlags )
            throws IOException;

    /**
     * Create a new XML document.  The newly created document will be mutable as defined by {@link AccessMode#MUTABLE}.
     * @param rootElementName the element name of the root Xml Node.
     * @return a newly created mutable document.
     * @throws NullPointerException if {@code rootElementName} is null.
     */
    XmlDocument newDocument( String rootElementName );

    /**
     * Create a new XML element.  Until attached to an element that is part of a document, this element will be mutable
     * as defined by {@link AccessMode#MUTABLE}.
     * @param elementName the newly created element's name.
     * @return a newly created element.
     * @throws NullPointerException if {@code elementName} is null.
     */
    XmlElement newElement( String elementName );

    /**
     * Fetch an instance of {@link XmlFactory}.
     * @return an instance of {@link XmlFactory}
     */
    static XmlFactory getFactory()
    {
        return XmlFactoryW3c.getW3cFactory();
    }
}
