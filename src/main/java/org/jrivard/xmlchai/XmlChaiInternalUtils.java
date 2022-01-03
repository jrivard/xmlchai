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

import java.util.Collection;

class XmlChaiInternalUtils
{
    static boolean isEmpty( final CharSequence value )
    {
        return value == null || value.length() == 0;
    }

    static <E extends Enum<E>> boolean enumArrayContainsValue( final E[] enumArray, final E enumValue )
    {
        if ( enumArray == null || enumArray.length == 0 )
        {
            return false;
        }

        for ( final E loopValue : enumArray )
        {
            if ( loopValue == enumValue )
            {
                return true;
            }
        }

        return false;
    }


    static <K> boolean isEmpty( final Collection<K> collection )
    {
        return collection == null || collection.isEmpty();
    }
}
