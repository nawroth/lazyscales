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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.Interval;
import se.nawroth.scales.api.LatinInterval;
import se.nawroth.scales.api.Scale;

/**
 * Scale finding tools.
 * 
 * @author Anders Nawroth
 */
public final class ScaleSearchService
{
    private final RawScaleService rawScaleService;

    /**
     * Create the service.
     * 
     * @param rawScaleService the raw scale service to use
     */
    public ScaleSearchService( final RawScaleService rawScaleService )
    {
        this.rawScaleService = rawScaleService;
    }

    /**
     * Find a scale based on intervals. Will only find exact matches. Note that
     * the returned scale may be undefined, that is nameless.
     * 
     * @param intervals scale defined as a sequence of intervals.
     * @return null or Scale that matches the intervals
     */
    public Scale find( final Interval... intervals )
    {
        if ( intervals.length == 0 )
        {
            throw new IllegalArgumentException(
                    "There can't be a scale without any interval!" );
        }
        Iterable<Scale> rawScales = rawScaleService.getRawScales( intervals.length );
        for ( Scale scale : rawScales )
        {
            Iterator<? extends Interval> scaleIterator = scale.circularIterator();
            Node currentStart = scale.getUnderlyingNode();
            for ( int i = 0; i < intervals.length; i++ )
            {
                Scale concreteScale = new ScaleImpl( currentStart );
                Iterator<? extends Interval> concreteIterator = concreteScale.circularIterator();
                int count = 0;
                for ( Interval interval : intervals )
                {
                    if ( !interval.equals( concreteIterator.next() ) )
                    {
                        break;
                    }
                    count++;
                }
                if ( count == intervals.length )
                {
                    return new ScaleImpl( currentStart );
                }
                currentStart = currentStart.getSingleRelationship(
                        scaleIterator.next(), Direction.OUTGOING ).getEndNode();
            }
        }
        return null;
    }

    /**
     * Find a scale based on intervals. Will only find exact matches. Note that
     * the returned scale may be undefined, that is nameless.
     * 
     * @param intervals scale defined as a sequence of intervals.
     * @return null or Scale that matches the intervals
     */
    public Scale find( final Collection<? extends Interval> intervals )
    {
        Interval[] intervalArray = new Interval[intervals.size()];
        return find( intervals.toArray( intervalArray ) );
    }

    /**
     * Find a scale based on intervals. Will only find exact matches. Note that
     * the returned scale may be undefined, that is nameless.
     * 
     * @param intervals scale defined as a sequence of intervals.
     * @return null or Scale that matches the intervals
     */
    public Scale find( final Iterable<? extends Interval> intervals )
    {
        List<Interval> intervalList = new ArrayList<Interval>(
                LatinInterval.OCTAVE.getSemitones() );
        for ( Interval interval : intervals )
        {
            intervalList.add( interval );
        }
        Interval[] intervalArray = new Interval[intervalList.size()];
        return find( intervalList.toArray( intervalArray ) );
    }
}
