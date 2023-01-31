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

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Method annotation indicating if a method will mutate data.  If the XML
 * data is part of an {@link XmlDocument} that is set to {@link AccessMode#IMMUTABLE},
 * any call to that method will result in an {@link UnsupportedOperationException}.
 */
@Target( ElementType.METHOD )
public @interface ModifyOperation
{
}
