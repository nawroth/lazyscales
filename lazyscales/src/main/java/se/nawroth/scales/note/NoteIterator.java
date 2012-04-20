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

import java.util.Iterator;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.Interval;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.util.collection.BaseIterator;
import se.nawroth.scales.util.collection.IteratorInitializer;

/**
 * Get the {@link Note}s from a series of {@link Interval}s and a starting
 * {@link Note}.
 * 
 * @author Anders Nawroth
 */
public final class NoteIterator extends BaseIterator<Note, Note>
{
    private final Iterator<? extends Interval> intervals;
    private static final IteratorInitializer<Note> INITIALIZER = new IteratorInitializer<Note>()
    {
        @Override
        public Note getCurrent( final Note start )
        {
            return null;
        }
    };

    /**
     * Create an instance from a starting {@link Note} and
     * {@link DiatonicInterval}s.
     * 
     * @param startingNote the root note to use
     * @param intervals the intervals to use
     * @return a iterator from the root note using the intervals given
     */
    public static NoteIterator iterator( final Note startingNote,
            final Iterator<? extends Interval> intervals )
    {
        return new NoteIterator( startingNote, intervals, 0 );
    }

    /**
     * Create an instance from a starting {@link Note} and
     * {@link DiatonicInterval}s.
     * 
     * @param startingNote the root note to use
     * @param intervals the intervals to use
     * @param index the starting position in the intervals to use
     * @return a iterator from the root note using the intervals given
     */
    public static NoteIterator iterator( final Note startingNote,
            final Iterator<? extends Interval> intervals, final int index )
    {
        return new NoteIterator( startingNote, intervals, index );
    }

    private NoteIterator( final Note startingNote,
            final Iterator<? extends Interval> intervals, final int index )
    {
        super( INITIALIZER, startingNote, index );
        this.intervals = intervals;
    }

    @Override
    public boolean hasNext()
    {
        return getCurrent() == null || intervals.hasNext();
    }

    @Override
    public Note next()
    {
        if ( getCurrent() == null )
        {
            setCurrent( getStart() );
        }
        else
        {
            setCurrent( getCurrent().getFromInterval( intervals.next() ) );
        }
        return getCurrent();
    }
}
