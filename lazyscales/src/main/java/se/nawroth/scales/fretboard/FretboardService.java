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
package se.nawroth.scales.fretboard;

import org.neo4j.graphdb.Direction;

import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.Types;
import se.nawroth.scales.scale.ScaleImpl;
import se.nawroth.scales.util.GraphDb;

/**
 * Perform operations on {@link Fretboard}s.
 * 
 * @author Anders Nawroth
 */
public final class FretboardService
{
    private final Scale chromaticScale;

    /**
     * Create the fretboard service.
     * 
     * @param graphDb the graphdb to use
     */
    public FretboardService( final GraphDb graphDb )
    {
        chromaticScale = new ScaleImpl(
                graphDb.getReferenceNode().getSingleRelationship(
                        Types.CHROMATIC_SCALE, Direction.OUTGOING ).getEndNode() );
    }

    /**
     * Print a fretboard to a {@link String}.
     * 
     * @param fretboard the fretboard to use
     * @param frets the number of frets on the fretboard
     * @return the fretboard in text format
     */
    public String printStrings( final Fretboard fretboard, final int frets )
    {
        return fretboard.printStrings( frets, chromaticScale );
    }

    /**
     * Get the strings of the fretboard.
     * 
     * @param fretboard the fretboard to use
     * @return the strings of the fretboard
     */
    public Iterable<Iterable<Note>> getStrings( final Fretboard fretboard )
    {
        return fretboard.getStrings( chromaticScale );
    }
}
