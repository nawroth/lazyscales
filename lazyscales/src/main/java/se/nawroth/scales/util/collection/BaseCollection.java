/**
 * Copyright (c) 2002-2012 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.nawroth.scales.util.collection;

import java.util.AbstractCollection;
import java.util.Iterator;

/**
 * Class to handle a collection of nodes.
 * 
 * @author Anders Nawroth
 * @param <T> the exposed type
 * @param <U> the underlying type
 */
public abstract class BaseCollection<T, U> extends AbstractCollection<T>
{
    private final Class<T> elementClass;
    private final U start;

    /**
     * Create instance from the exposed element type and the underlying start
     * element.
     * 
     * @param elementType the type of exposed elements
     * @param start the underlying start element
     */
    protected BaseCollection( final Class<T> elementType, final U start )
    {
        this.elementClass = elementType;
        this.start = start;
    }

    /**
     * Get the class of the elements.
     * 
     * @return the class of the elements
     */
    protected final Class<T> getElementClass()
    {
        return elementClass;
    }

    @Override
    public int size()
    {
        int size = 0;
        Iterator<T> iterator = this.iterator();
        while ( iterator.hasNext() )
        {
            iterator.next();
            size++;
        }
        return size;
    }

    /**
     * Get the start element.
     * 
     * @return the start element
     */
    protected final U getStart()
    {
        return start;
    }
}
