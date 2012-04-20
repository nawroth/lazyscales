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

import org.neo4j.graphdb.RelationshipType;

/**
 * Definition of the basic {@link Interval}s from a diatonic perspective. This
 * representation is used for {@link Scale} storage and also contains string
 * representations of the {@link NamedInterval}s.
 * 
 * @author Anders Nawroth
 * @see <a href="http://en.wikipedia.org/wiki/Diatonic_scale">Diatonic scale at
 *      Wikipedia</a>
 */
public enum DiatonicInterval implements NamedInterval
{
    UNISON( 0, "T" ),
    MINOR_SECOND( 1, "♭2" ),
    MAJOR_SECOND( 2, "2", "♭♭3" ),
    MINOR_THIRD( 3, "♭3" ),
    MAJOR_THIRD( 4, "3", "♭4" ),
    FOURTH( 5, "4" ),
    DIMINISHED_FIFTH( 6, "♯4", "♭5" ),
    FIFTH( 7, "5", "♭♭6" ),
    MINOR_SIXTH( 8, "♯5", "♭6" ),
    MAJOR_SIXTH( 9, "6", "♭♭7" ),
    MINOR_SEVENTH( 10, "♭7" ),
    MAJOR_SEVENTH( 11, "7" );

    private final int semitones;
    private final String shortName;
    private final String altName;

    private static final DiatonicInterval[] INTERVALS = DiatonicInterval.values();

    private DiatonicInterval( final int steps, final String name )
    {
        semitones = steps;
        shortName = name;
        altName = null;
    }

    private DiatonicInterval( final int steps, final String name,
            final String alternateName )
    {
        semitones = steps;
        shortName = name;
        altName = alternateName;
    }

    @Override
    public int getSemitones()
    {
        return semitones;
    }

    @Override
    public String getShortName()
    {
        return shortName;
    }

    @Override
    public String getShortName( String previous )
    {
        if ( altName == null || previous == null || previous.length() < 1 )
        {
            return shortName;
        }
        String prevLast = previous.substring( previous.length() - 1 );
        if ( shortName.endsWith( prevLast ) )
        {
            return altName;
        }
        else
        {
            return shortName;
        }
    }

    @Override
    public String toString()
    {
        return getShortName();
    }

    // implements Interval interface
    @Override
    public boolean equivalent( final Interval other )
    {
        return other.getSemitones() == semitones;
    }

    /**
     * A convenient way to get a {@link DiatonicInterval}.
     * 
     * @param steps the number of semitone steps in the interval
     * @return the corresponding {@link DiatonicInterval}
     */
    public static DiatonicInterval getFromSemitones( final int steps )
    {
        return INTERVALS[steps % INTERVALS.length];
    }

    /**
     * Get the {@link DiatonicInterval} from an {@link Interval}, replacing any
     * intervals larger than or equal to an octave with the corresponding
     * smaller interval.
     * 
     * @param interval the interval to translate
     * @return the corresponding {@link DiatonicInterval}
     */
    public static DiatonicInterval getFromInterval( final Interval interval )
    {
        return getFromSemitones( interval.getSemitones()
                                 % LatinInterval.OCTAVE.getSemitones() );
    }

    /**
     * Check if a {@link RelationshipType} is a {@link DiatonicInterval} as
     * well.
     * 
     * @param relType the {@link RelationshipType} to check
     * @return <code>true</code> if the {@link RelationshipType} represents a
     *         {@link DiatonicInterval}
     */
    public static boolean isDiatonicInterval( final RelationshipType relType )
    {
        for ( DiatonicInterval interval : values() )
        {
            if ( interval.name()
                    .equals( relType.name() ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public DiatonicInterval sum( final Interval interval )
    {
        int newSemitones = semitones + interval.getSemitones();
        newSemitones %= LatinInterval.OCTAVE.getSemitones();
        return DiatonicInterval.getFromSemitones( newSemitones );
    }
}
