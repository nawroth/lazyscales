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
package se.nawroth.scales.api;


/**
 * Semitone constants for some special intervals.
 * 
 * @author Anders Nawroth
 */
public enum LatinInterval implements Interval
{
    SEMITONE( 1 ),
    WHOLE_TONE( 2 ),
    TRITONE( 6 ),
    OCTAVE( 12 );

    private final int semitones;

    private LatinInterval( final int steps )
    {
        semitones = steps;
    }

    @Override
    public int getSemitones()
    {
        return semitones;
    }

    @Override
    public boolean equivalent( final Interval other )
    {
        return other.getSemitones() == semitones;
    }
}
