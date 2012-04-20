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
package se.nawroth.scales.chord;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.NamedInterval;
import se.nawroth.scales.util.collection.BaseIterator;
import se.nawroth.scales.util.collection.IteratorInitializer;

public class ChordIterator extends BaseIterator<NamedInterval, Node>
{
    private static final IteratorInitializer<Node> INITIALIZER = new IteratorInitializer<Node>()
    {
        @Override
        public Node getCurrent( final Node start )
        {
            return start;
        }
    };

    protected ChordIterator( final Node start )
    {
        super( INITIALIZER, start );
    }

    @Override
    public boolean hasNext()
    {
        return getCurrent().hasRelationship( Direction.OUTGOING );
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
