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
package se.nawroth.scales.scale;

import java.util.Arrays;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.Interval;
import se.nawroth.scales.api.LatinInterval;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.util.GraphDb;

/**
 * Create and delete scales.
 * 
 * @author Anders Nawroth
 */
public final class ScaleService
{
    private static final String NAME = "name";
    private final GraphDb graphDb;
    private final ScaleSearchService scaleSearchService;
    private final RawScaleService rawScaleService;

    /**
     * Create the service.
     * 
     * @param graphDb the database to use
     * @param scaleSearchService the scale search service to use
     * @param rawScaleService the raw scale service to use
     */
    public ScaleService( final GraphDb graphDb,
            final ScaleSearchService scaleSearchService,
            final RawScaleService rawScaleService )
    {
        this.graphDb = graphDb;
        this.scaleSearchService = scaleSearchService;
        this.rawScaleService = rawScaleService;
    }

    /**
     * Create a new {@link Scale}.
     * 
     * @param family the family of the scale
     * @param name the name of the scale
     * @param intervals the intervals that define the scale
     * @return the created scale
     */
    public Scale newScale( final ScaleFamily family, final String name,
            final Interval... intervals )
    {
        int sum = scaleSum( intervals );
        if ( sum % LatinInterval.OCTAVE.getSemitones() != 0 )
        {
            throw new IllegalArgumentException(
                    "The sum of the scale must be a multiple of twelve semitones, but was: ["
                            + sum + "]" );
        }
        Scale scale;
        // check if the scale already exists
        Scale search = scaleSearchService.find( intervals );
        if ( search == null )
        {
            Node newStartNode = graphDb.createNode();
            Node currentNode = newStartNode;
            Iterator<Interval> iter = Arrays.asList( intervals )
                    .iterator();
            while ( iter.hasNext() )
            {
                // enforce DiatonicInterval for scales
                DiatonicInterval interval = DiatonicInterval.getFromInterval( iter.next() );
                Node newNode = iter.hasNext() ? graphDb.createNode()
                        : newStartNode;
                currentNode.createRelationshipTo( newNode, interval );
                currentNode = newNode;
            }
            scale = linkScale( family, name, newStartNode );
            rawScaleService.addRawScale( scale );
        }
        else
        {
            scale = linkScale( family, name, search.getUnderlyingNode() );
        }
        return scale;
    }

    /**
     * Create a {@link Scale} from the intervals of another scale.
     * 
     * @param family the family of the scale
     * @param name the name of the scale
     * @return the created scale
     */
    public Scale newScale( final ScaleFamily family, final Scale sourceScale,
            final int degree, final String name )
    {
        Node node = ( (ScaleImpl) sourceScale ).getStartNode();
        for ( int i = 1; i < degree; i++ )
        {
            node = node.getRelationships( Direction.OUTGOING )
                    .iterator()
                    .next()
                    .getEndNode();
        }
        return linkScale( family, name, node );
    }

    /**
     * Create a set of modal {@link Scale}s from the given intervals.
     * 
     * @param family the family of the scales
     * @param names the names of the scales
     * @return the first created scale
     */
    public Scale newScales( final ScaleFamily family, final String[] names,
            final Interval... intervals )
    {
        if ( names.length < 1 )
        {
            throw new IllegalArgumentException(
                    "There has to be at least one scale name." );
        }
        Scale baseScale = newScale( family, names[0], intervals );
        for ( int i = 1; i < names.length; i++ )
        {
            newScale( family, baseScale, i + 1, names[i] );
        }
        return baseScale;
    }

    /**
     * Delete a {@link Scale} from the database. Note that the name of the scale
     * is needed, to distinguish it from other scales using the same intervals.
     * 
     * @param scale the scale to delete
     * @param name the name of the scale
     */
    public static void deleteScale( final Scale scale, final String name )
    {
        // TODO remember: check for other uses!
        throw new UnsupportedOperationException(
                "Delete operation is not implemented for scales yet." );
    }

    /**
     * Create the link to add the scale.
     * 
     * @param family the family of the scale
     * @param name the name of the scale
     * @param newStartNode the start node of the scale
     * @return the new scale
     */
    private static Scale linkScale( final ScaleFamily family,
            final String name, final Node newStartNode )
    {
        Scale scale = new ScaleImpl( newStartNode );
        ScaleNavigation.getNavigation()
                .addItem( family, scale, NAME, name );
        return scale;
    }

    /**
     * Calculate the semitone sum of a scale.
     * 
     * @param intervals the intervals to sum
     * @return the sum of the interval semitones
     */
    private static int scaleSum( final Interval... intervals )
    {
        int sum = 0;
        for ( Interval interval : intervals )
        {
            sum += interval.getSemitones();
        }
        return sum;
    }
}
