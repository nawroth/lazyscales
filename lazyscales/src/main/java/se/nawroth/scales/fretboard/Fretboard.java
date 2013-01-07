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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import se.nawroth.scales.api.LatinInterval;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.note.NoteService;
import se.nawroth.scales.note.Notes;

/**
 * Creates a logical fretboard representation. A {@link Fretboard} is created
 * from a {@link Tuning} and {@link Notes}.
 * 
 * @author Anders Nawroth
 */
public class Fretboard
{
    private static final int FIRST_FRET = 0;
    private static final int SEVENTH_FRET = 7;
    private static final int FIFTH_FRET = 5;
    private static final int INITIAL_STRINGBUILDER_SIZE = 1024;
    private static final int TAB_WIDTH = 6;
    private static final String[] TAB_SPACES;

    private final Tuning tuning;
    private final Notes notes;

    static
    {
        final String spaces = "                      ";
        TAB_SPACES = new String[TAB_WIDTH];
        for ( int i = 0; i < TAB_WIDTH; i++ )
        {
            TAB_SPACES[i] = spaces.substring( 0, i + 1 );
        }
    }

    /**
     * Create a fretboard from a tuning and notes.
     * 
     * @param tuning the tuning of the strings
     * @param notes the notes to show on the fretboard
     */
    public Fretboard( final Tuning tuning, final Notes notes )
    {
        this.tuning = tuning;
        this.notes = notes;
    }

    /**
     * Get the strings of the fretboard.
     * 
     * @param chromaticScale chromatic scale to use
     * @return the strings of the fretboard
     */
    final Iterable<Iterable<Note>> getStrings( final Scale chromaticScale )
    {
        Deque<Iterable<Note>> res = new ArrayDeque<Iterable<Note>>();
        for ( Note baseNote : tuning )
        {
            res.addFirst( new ScaleComparer( Notes.notes( baseNote,
                    chromaticScale ), notes ) );
        }
        return res;
    }

    /**
     * Print a fretboard to a {@link String}.
     * 
     * @param frets the number of frets on the fretboard
     * @param chromaticScale chromatic scale to use
     * @return the fretboard in text format
     */
    final String printStrings( final int frets, final Scale chromaticScale )
    {
        StringBuilder s = new StringBuilder( INITIAL_STRINGBUILDER_SIZE );
        for ( Iterable<Note> string : getStrings( chromaticScale ) )
        {
            Iterator<Note> stringIterator = string.iterator();
            for ( int i = 0; i <= frets; i++ )
            {
                Note next = stringIterator.next();
                if ( next == null )
                {
                    int fret = i % LatinInterval.OCTAVE.getSemitones();
                    char c;
                    switch ( fret )
                    {
                    case FIRST_FRET:
                        c = '+';
                        break;
                    case FIFTH_FRET:
                    case SEVENTH_FRET:
                        c = '|';
                        break;
                    default:
                        c = '-';
                        break;
                    }
                    s.append( c );
                    // as long as it's normal chars, this should be safe
                    s.append( TAB_SPACES[TAB_WIDTH - 1] );
                }
                else
                {
                    String noteStr = next.toString( notes.isFlat() );
                    s.append( noteStr );
                    s.append( TAB_SPACES[TAB_WIDTH - noteStr.length()] );
                }
            }
            s.append( "\n" );
        }
        return s.toString();
    }

    /**
     * Helper class to keep two {@link Note} iterators in sync. The base notes
     * are returned when they match the comparison iterator. In other cases,
     * <code>null</code> is returned. The comparison iterator sets the "tempo"
     * of the iteration.
     * 
     * @author Anders Nawroth
     */
    private static class ScaleComparer implements Iterable<Note>
    {
        private final Iterator<Note> baseIterator;
        private final Iterator<Note> comparisonIterator;

        /**
         * Compare two {@link Note} iterators.
         * 
         * @param baseNotes the notes that will be returned
         * @param comparisonNotes the notes that keep track of the position of
         *            the iterator
         */
        public ScaleComparer( final Notes baseNotes, final Notes comparisonNotes )
        {
            baseIterator = baseNotes.circularIterator();
            comparisonIterator = comparisonNotes.circularIterator();
            Note baseStartNote = baseNotes.iterator().next();
            // bring iterators in sync
            int index = 0;
            int maxSemitones = 0;
            int maxIndex = 0;
            for ( Note note : comparisonNotes )
            {
                index++;
                int semitones = NoteService.findInterval( baseStartNote, note ).getSemitones();
                if ( semitones > maxSemitones )
                {
                    maxIndex = index;
                    maxSemitones = semitones;
                }
            }
            for ( int i = 0; i < maxIndex; i++ )
            {
                comparisonIterator.next();
            }
        }

        @Override
        public Iterator<Note> iterator()
        {
            return new ComparingIterator();
        }

        /**
         * Class to perform the actual iteration over the {@link Note}s. Returns
         * the {@link Note} on hits and <code>null</code> on misses.
         * 
         * @author Anders Nawroth
         */
        private class ComparingIterator implements Iterator<Note>
        {
            private Note compNote = comparisonIterator.next();

            @Override
            public boolean hasNext()
            {
                return true;
            }

            @Override
            public Note next()
            {
                Note baseNote = baseIterator.next();
                if ( baseNote.equals( compNote ) )
                {
                    compNote = comparisonIterator.next();
                    return baseNote;
                }
                return null;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        }
    }
}
