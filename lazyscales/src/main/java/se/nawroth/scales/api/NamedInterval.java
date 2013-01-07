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
 * An {@link Interval} that also has a name.
 * 
 * @author Anders Nawroth
 */
public interface NamedInterval extends Interval
{
    /**
     * Get a {@link String} representation of the interval.
     * 
     * @return the {@link String} representation of the interval.
     */
    String getShortName();

    /**
     * Get a {@link String} representation of the interval, using information
     * about the previous interval.
     * 
     * @param previous String representation of the previous note in a scale.
     * @return the {@link String} representation of the interval.
     */
    String getShortName( String previous );

    /**
     * Get the sum of two {@link Interval}s. Note that an octave could be
     * returned as a Unison etc, depending on the implementation..
     * 
     * @param interval the other interval
     * @return the sum of the intervals
     */
    NamedInterval sum( Interval interval );
}
