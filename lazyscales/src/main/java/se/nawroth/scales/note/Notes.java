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
package se.nawroth.scales.note;

import java.util.Iterator;

import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.util.collection.CircularCollection;

/**
 * Apply a {@link Scale} from a specific root {@link Note}.
 * 
 * @author Anders Nawroth
 */
public final class Notes extends CircularCollection<Note, Note>
{
    private static final int INITIAL_STRINGBUILDER_SIZE = 60;
    private final Scale scale;
    private boolean flat = true;

    /**
     * Creates notes for a scale.
     * 
     * @param startingNote the root note
     * @param scale the scale to use
     * @return the notes of the scale
     */
    public static Notes notes( final Note startingNote, final Scale scale )
    {
        return new Notes( startingNote, scale );
    }

    private Notes( final Note startingNote, final Scale scale )
    {
        super( Note.class, startingNote );
        if ( startingNote == null )
        {
            throw new IllegalArgumentException(
                    "The starting note can not be null" );
        }
        this.scale = scale;
    }

    /**
     * Set the note strings to use ♭ symbols.
     */
    public void setFlat()
    {
        this.flat = true;
    }

    /**
     * Set the note strings to use ♯ symbols.
     */
    public void setSharp()
    {
        this.flat = false;
    }

    /**
     * Check if ♭ or ♯ is used.
     * 
     * @return <code>true</code> if ♭ symbols are used.
     */
    public boolean isFlat()
    {
        return flat;
    }

    /**
     * Get the underlying scale.
     * 
     * @return the scale
     */
    public Scale getScale()
    {
        return scale;
    }

    /**
     * Get the starting note.
     * 
     * @return the starting note
     */
    public Note getStartingNote()
    {
        return getStart();
    }

    @Override
    public int size()
    {
        return scale.size();
    }

    @Override
    public boolean isEmpty()
    {
        // we always have a starting note!
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( INITIAL_STRINGBUILDER_SIZE );
        builder.append( scale.getName() ).append( " in " ).append(
                toString( getStart() ) ).append( ": " );
        for ( Note note : this )
        {
            builder.append( toString( note ) ).append( " " );
        }
        return builder.toString();
    }

    /**
     * Get the {@link Note} as a {@link String} using the current setting for
     * using flat/sharp notes.
     * 
     * @param note the note to convert to a string representation
     * @return a {@link String} representation using the correct ♭ or ♯ symbols.
     */
    public String toString( final Note note )
    {
        return note.toString( isFlat() );
    }

    @Override
    public Iterator<Note> circularIterator()
    {
        return NoteIterator.iterator( getStart(), scale.circularIterator() );
    }

    @Override
    public Iterator<Note> iterator()
    {
        return NoteIterator.iterator( getStart(), scale.iterator() );
    }
}
