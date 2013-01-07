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

import java.util.Iterator;

/**
 * Base class for iterators with an exposed type and another underying type.
 * Initialization of the iterator is delegated to a {@link IteratorInitializer}.
 * 
 * @author Anders Nawroth
 * @param <T> the exposed type
 * @param <U> the underlying type
 */
public abstract class BaseIterator<T, U> implements Iterator<T>, Iterable<T>
{
    private final U start;
    private U current = null;

    /**
     * Create instance from an initializer, the exposed element type and the
     * underlying start element.
     * 
     * @param initializer used to initialize the iterator
     * @param start the underlying start element
     * @param index the element to start iterating from
     */
    protected BaseIterator( final IteratorInitializer<U> initializer,
            final U start, final int index )
    {
        if ( start == null )
        {
            throw new IllegalArgumentException(
                    "The start element must not be null." );
        }
        this.start = start;
        this.current = initializer.getCurrent( start );
        move( index );
    }

    /**
     * Create instance from an initializer and a start element.
     * 
     * @param initializer used to initialize the iterator
     * @param start the start element
     */
    protected BaseIterator( final IteratorInitializer<U> initializer,
            final U start )
    {
        this( initializer, start, 0 );
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

    /**
     * Set the current element
     * 
     * @param current the current element
     */
    protected final void setCurrent( final U current )
    {
        this.current = current;
    }

    /**
     * Get the current element.
     * 
     * @return the current element
     */
    protected final U getCurrent()
    {
        return current;
    }

    @Override
    public final Iterator<T> iterator()
    {
        return this;
    }

    @Override
    public final void remove()
    {
        unsupported();
    }

    /**
     * Utility method to throw an {@link UnsupportedOperationException} stating
     * that the class is immutable.
     */
    protected final void unsupported()
    {
        throw new UnsupportedOperationException(
                this.getClass().getSimpleName()
                        + " is immutable, no mutating operations are allowed." );
    }

    /**
     * Move the internal position to the element pointed out by the
     * <code>index</code> parameter.
     * 
     * @param index
     */
    private void move( final int index )
    {
        for ( int i = 0; i < index; i++ )
        {
            next();
        }
    }
}
