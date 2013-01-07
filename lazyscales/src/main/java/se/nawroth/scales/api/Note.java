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
package se.nawroth.scales.api;

/**
 * A musical note. Has methods for getting other {@link Note}s by interval.
 * 
 * @author Anders Nawroth
 */
public interface Note
{
    /**
     * Get the {@link Note} to which the <code>interval</code> leads.
     * 
     * @param interval distance to other {@link Note}
     * @return {@link Note} at the specified interval distance
     */
    Note getFromInterval( Interval interval );

    /**
     * Get the {@link Note} from which the <code>interval</code> leads to this
     * one.
     * 
     * @param interval distance from other {@link Note}
     * @return {@link Note} at the specified backwards interval distance
     */
    Note getFromIntervalReversed( Interval interval );

    /**
     * Get a string representation of the {@link Note}, using ♭ or ♯ symbols
     * according to the <code>flat</code> parameter.
     * 
     * @param flat decides if ♭ or ♯ symbols should be used
     * @return representation using ♭ or ♯ symbols
     */
    String toString( boolean flat );
}
