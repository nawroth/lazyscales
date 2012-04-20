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
package se.nawroth.scales.scale;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.NamedInterval;
import se.nawroth.scales.util.collection.CircularIterator;
import se.nawroth.scales.util.collection.IteratorInitializer;

final class ScaleIterator extends CircularIterator<NamedInterval, Node>
{
    private static final IteratorInitializer<Node> INITIALIZER = new IteratorInitializer<Node>()
    {
        @Override
        public Node getCurrent( final Node start )
        {
            return start;
        }
    };

    public static ScaleIterator iterator( final Node node )
    {
        return new ScaleIterator( node, false );
    }

    public static ScaleIterator iterator( final Node node,
            final boolean isCircular )
    {
        return new ScaleIterator( node, isCircular );
    }

    public static ScaleIterator iterator( final Node node, final int index,
            final boolean isCircular )
    {
        return new ScaleIterator( node, index, isCircular );
    }

    private ScaleIterator( final Node node )
    {
        this( node, false );
    }

    private ScaleIterator( final Node node, final boolean isCircular )
    {
        super( INITIALIZER, node, isCircular );
    }

    private ScaleIterator( final Node node, final int index,
            final boolean isCircular )
    {
        super( INITIALIZER, node, index, isCircular );
    }

    @Override
    public boolean hasNext()
    {
        if ( !getCurrent().hasRelationship( Direction.OUTGOING ) )
        {
            return false;
        }
        if ( isCircular() )
        {
            return true;
        }
        return !getStart().equals( getNextRelationship().getEndNode() );
    }

    @Override
    public NamedInterval next()
    {
        Relationship rel = getNextRelationship();
        setCurrent( rel.getEndNode() );
        return getInterval( rel );
    }

    private Relationship getNextRelationship()
    {
        return getCurrent().getRelationships( Direction.OUTGOING ).iterator().next();
    }

    private static NamedInterval getInterval( final Relationship rel )
    {
        return DiatonicInterval.valueOf( rel.getType().name() );
    }
}
