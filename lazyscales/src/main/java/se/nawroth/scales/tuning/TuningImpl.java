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
package se.nawroth.scales.tuning;

import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.note.NoteImpl;
import se.nawroth.scales.note.NoteIterator;
import se.nawroth.scales.scale.ScaleImpl;
import se.nawroth.scales.util.collection.BaseCollection;

/**
 * Default implementation of a {@link Tuning}.
 * 
 * @author Anders Nawroth
 */
public class TuningImpl extends BaseCollection<Note, Node> implements Tuning
{
    static final String NAME = "name";

    /**
     * Get instance from underlying node.
     * 
     * @param underlyingNode the underlying node
     */
    public TuningImpl( final Node underlyingNode )
    {
        super( Note.class, underlyingNode );
    }

    @Override
    public final Node getUnderlyingNode()
    {
        return getStart();
    }

    @Override
    public final int compareTo( final Tuning otherTuning )
    {
        return getName().compareTo( otherTuning.getName() );
    }

    @Override
    public final boolean equals( final Object obj )
    {
        return obj instanceof TuningImpl
               && getName().equals( ( (TuningImpl) obj ).getName() );
    }

    @Override
    public final int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public final boolean isEmpty()
    {
        return false;
    }

    @Override
    public final String toString()
    {
        return getName() + ": " + super.toString();
    }

    @Override
    public final TuningFamily getFamily()
    {
        return TuningFamilyImpl.NAVIGATION.getCategories( this ).iterator().next();
    }

    @Override
    public final String getName()
    {
        return (String) TuningFamilyImpl.NAVIGATION.getSingleItemRelationshipProperty(
                this, NAME );
    }

    @Override
    public final Iterator<Note> iterator()
    {
        return NoteIterator.iterator( rootNote(), ( new ScaleImpl(
                intervalRoot() ).iterator() ) );
    }

    private Note rootNote()
    {
        return new NoteImpl( getUnderlyingNode().getSingleRelationship(
                TuningTypes.TUNING_ROOT_NOTE, Direction.OUTGOING ).getEndNode() );
    }

    final Node intervalRoot()
    {
        return getUnderlyingNode().getSingleRelationship(
                TuningTypes.TUNING_INTERVAL_ROOT, Direction.OUTGOING ).getEndNode();
    }
}
