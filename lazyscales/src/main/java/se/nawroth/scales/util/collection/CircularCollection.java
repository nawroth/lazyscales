/**
 * Copyright (c) 2002-2013 "Neo Technology,"
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

import java.util.Collection;
import java.util.Iterator;

/**
 * Class that extends normal collections by adding a circular iterator method.
 * 
 * @author Anders Nawroth
 * @param <T> the exposed type
 * @param <U> the underlying type
 */
public abstract class CircularCollection<T, U> extends BaseCollection<T, U>
{
    /**
     * Create instance from the exposed element type and the underlying start
     * element.
     * 
     * @param elementType the type of exposed elements
     * @param start the underlying start element
     */
    protected CircularCollection( final Class<T> elementType, final U start )
    {
        super( elementType, start );
    }

    /**
     * A circular {@link Iterator} over this {@link Collection}.
     * 
     * @return the iterator instance
     */
    public abstract Iterator<T> circularIterator();
}
