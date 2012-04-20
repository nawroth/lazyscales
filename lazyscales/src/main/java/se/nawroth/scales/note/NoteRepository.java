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
package se.nawroth.scales.note;

import static se.nawroth.scales.api.DiatonicInterval.DIMINISHED_FIFTH;
import static se.nawroth.scales.api.DiatonicInterval.FIFTH;
import static se.nawroth.scales.api.DiatonicInterval.FOURTH;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_SECOND;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_SEVENTH;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_SIXTH;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_THIRD;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_SECOND;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_SEVENTH;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_SIXTH;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_THIRD;
import static se.nawroth.scales.api.DiatonicInterval.UNISON;
import se.nawroth.scales.api.Interval;

/**
 * Notes defined through the interval from C.
 * 
 * @author Anders Nawroth
 */
public enum NoteRepository
{
    C( UNISON ),
    C_SHARP( MINOR_SECOND ),
    D( MAJOR_SECOND ),
    D_SHARP( MINOR_THIRD ),
    E( MAJOR_THIRD ),
    F( FOURTH ),
    F_SHARP( DIMINISHED_FIFTH ),
    G( FIFTH ),
    G_SHARP( MINOR_SIXTH ),
    A( MAJOR_SIXTH ),
    A_SHARP( MINOR_SEVENTH ),
    B( MAJOR_SEVENTH ),
    D_FLAT( MINOR_SECOND ),
    E_FLAT( MINOR_THIRD ),
    G_FLAT( DIMINISHED_FIFTH ),
    A_FLAT( MINOR_SIXTH ),
    B_FLAT( MINOR_SEVENTH );

    private final Interval interval;

    private NoteRepository( final Interval interval )
    {
        this.interval = interval;
    }

    /**
     * The interval from C.
     * 
     * @return interval from C.
     */
    public Interval interval()
    {
        return interval;
    }
}
