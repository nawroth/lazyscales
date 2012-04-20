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
package se.nawroth.scales.util;

import org.neo4j.graphdb.Node;

/**
 * Helper class to implement the {@link Entity} interface. Overrides
 * {@link #equals(Object)} and {@link #hashCode()} and uses the underlying
 * {@link Node} to calculate the values. It also requires {@link #toString()} to
 * be implemented by subclasses.
 * 
 * @author Anders Nawroth
 */
public abstract class EntityImpl implements Entity
{
    private final Node underlyingNode;

    /**
     * Wrap an {@link Entity} around a {@link Node}.
     * 
     * @param underlyingNode the underlying node
     */
    protected EntityImpl( final Node underlyingNode )
    {
        this.underlyingNode = underlyingNode;
    }

    @Override
    public final Node getUnderlyingNode()
    {
        return underlyingNode;
    }

    @Override
    public final boolean equals( final Object o )
    {
        return o instanceof Entity
               && getClass().equals( o.getClass() )
               && getUnderlyingNode().equals(
                       ( (Entity) o ).getUnderlyingNode() );
    }

    @Override
    public final int hashCode()
    {
        return getUnderlyingNode().hashCode();
    }

    @Override
    public abstract String toString();
}
