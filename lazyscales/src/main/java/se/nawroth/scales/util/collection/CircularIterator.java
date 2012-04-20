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

/**
 * Handle unsupported mutating operations.
 * 
 * @author Anders Nawroth
 * @param <T> the exposed type
 * @param <U> the underlying type
 */
public abstract class CircularIterator<T, U> extends BaseIterator<T, U>
{
    private final boolean circular;

    /**
     * Check if the iterator is circular.
     * 
     * @return <code>true</code> if this iterator is circular
     */
    public final boolean isCircular()
    {
        return circular;
    }

    /**
     * Create instance from an initializer, the exposed element type and the
     * underlying start element.
     * 
     * @param initializer used to initialize the iterator
     * @param start the underlying start element
     * @param index the element to start iterating from
     * @param circular <code>true</code> if this iterator instance should be
     *            circular
     */
    protected CircularIterator( final IteratorInitializer<U> initializer,
            final U start, final int index, final boolean circular )
    {
        super( initializer, start, index );
        this.circular = circular;
    }

    /**
     * Create instance from an initializer, the exposed element type and the
     * underlying start element.
     * 
     * @param initializer used to initialize the iterator
     * @param start the underlying start element
     * @param circular <code>true</code> if this iterator instance should be
     *            circular
     */
    protected CircularIterator( final IteratorInitializer<U> initializer,
            final U start, final boolean circular )
    {
        super( initializer, start );
        this.circular = circular;
    }
}
