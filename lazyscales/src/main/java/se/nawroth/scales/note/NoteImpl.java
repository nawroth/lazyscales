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
package se.nawroth.scales.note;

import static se.nawroth.scales.api.DiatonicInterval.UNISON;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.Interval;
import se.nawroth.scales.api.Note;

/**
 * Default implementation of the {@link Note} interface.
 * 
 * @author Anders Nawroth
 */
public class NoteImpl implements Note
{
    private static final String SHARP_NAME = "sharp name";
    private static final String FLAT_NAME = "flat name";
    private final Node underlying;

    /**
     * Crate a {@link Note} from an underlying {@link Node}.
     * 
     * @param node the underlying node
     */
    public NoteImpl( final Node node )
    {
        this.underlying = node;
    }

    /**
     * Get the underlying {@link Node}.
     * 
     * @return the underlying {@link Node}
     */
    public final Node getUnderlying()
    {
        return underlying;
    }

    @Override
    public final Note getFromInterval( final Interval interval )
    {
        if ( UNISON.equivalent( interval ) )
        {
            return this;
        }
        return new NoteImpl( underlying.getSingleRelationship( interval,
                Direction.OUTGOING ).getEndNode() );
    }

    @Override
    public final Note getFromIntervalReversed( final Interval interval )
    {
        if ( UNISON.equivalent( interval ) )
        {
            return this;
        }
        return new NoteImpl( underlying.getSingleRelationship( interval,
                Direction.INCOMING ).getStartNode() );
    }

    @Override
    public final String toString()
    {
        return toStringFlat();
    }

    @Override
    public final String toString( final boolean flat )
    {
        if ( flat )
        {
            return toStringFlat();
        }
        else
        {
            return toStringSharp();
        }
    }

    @Override
    public final int hashCode()
    {
        return getUnderlying().hashCode();
    }

    @Override
    public final boolean equals( final Object obj )
    {
        if ( obj instanceof NoteImpl )
        {
            return getUnderlying().equals( ( (NoteImpl) obj ).getUnderlying() );
        }
        return false;
    }

    private String toStringFlat()
    {
        return (String) underlying.getProperty( FLAT_NAME );
    }

    private String toStringSharp()
    {
        return (String) underlying.getProperty( SHARP_NAME );
    }
}
