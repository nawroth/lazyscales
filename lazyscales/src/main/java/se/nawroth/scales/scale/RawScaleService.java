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

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.NamedInterval;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.scale.RawScale.RawScales;
import se.nawroth.scales.util.GraphDb;

/**
 * Perform operations on raw scales.
 * 
 * @author Anders Nawroth
 */
public final class RawScaleService
{
    private final GraphDb graphDb;

    /**
     * Create the service.
     * 
     * @param graphDb the database to use
     */
    public RawScaleService( final GraphDb graphDb )
    {
        this.graphDb = graphDb;
    }

    /**
     * Add a {@link Scale} as a {@link RawScale}.
     * 
     * @param scale the scale to add
     * @return <code>true</code> if the scale was added (i.e. did not already
     *         exist)
     */
    boolean addRawScale( final Scale scale )
    {
        int degree = scale.size() + 1; // intervals -> notes
        Node startNode = scale.getUnderlyingNode();
        Node currentNode = startNode;
        for ( NamedInterval namedInterval : scale )
        {
            DiatonicInterval interval = DiatonicInterval.getFromInterval( namedInterval );
            currentNode = currentNode.getSingleRelationship( interval,
                    Direction.OUTGOING ).getEndNode();
            if ( startNode.hasRelationship( RawTypes.RAW_SCALE,
                    Direction.INCOMING ) )
            {
                return false;
            }
        }
        getRawScaleReferenceNode( degree ).createRelationshipTo( startNode,
                RawTypes.RAW_SCALE );
        return true;
    }

    Iterable<Scale> getRawScales( final int degree )
    {
        return RawScale.NAVIGATION.getAllItems( new RawScale.RawScaleEntity(
                getRawScaleReferenceNode( degree ) ) );
    }

    private Node getRawScaleReferenceNode( final int degree )
    {
        return graphDb.getReferenceNode( RawTypes.RAW_SCALES,
                RawScales.getFromTones( degree ) );
    }
}
