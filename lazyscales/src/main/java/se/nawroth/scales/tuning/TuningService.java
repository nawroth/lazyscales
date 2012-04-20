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

import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.Interval;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.note.NoteImpl;
import se.nawroth.scales.util.GraphDb;

/**
 * Perform operations on {@link Tuning}s.
 * 
 * @author Anders Nawroth
 */
public final class TuningService
{
    private final GraphDb graphDb;

    /**
     * Create the service.
     * 
     * @param graphDb the database to use
     */
    public TuningService( final GraphDb graphDb )
    {
        this.graphDb = graphDb;
    }

    /**
     * Create a new {@link Tuning} from {@link DiatonicInterval}s.
     * 
     * @param family the family it should belong to
     * @param name the name of the tuning
     * @param rootNote the root note of the tuning
     * @param intervals the intervals of the tuning
     * @return the created tuning
     */
    public Tuning newTuning( final TuningFamily family, final String name,
            final Note rootNote, final Interval... intervals )
    {
        Node node = graphDb.createNode();
        node.createRelationshipTo( ( (NoteImpl) rootNote ).getUnderlying(),
                TuningTypes.TUNING_ROOT_NOTE );
        Node intervalNode = graphDb.createNode();
        node.createRelationshipTo( intervalNode,
                TuningTypes.TUNING_INTERVAL_ROOT );
        Node current = intervalNode;
        for ( Interval interval : intervals )
        {
            Node next = graphDb.createNode();
            current.createRelationshipTo( next,
                    DiatonicInterval.getFromInterval( interval ) );
            current = next;
        }
        TuningImpl tuning = new TuningImpl( node );
        family.addTuning( tuning, name );
        return tuning;
    }

    /**
     * Create a new {@link Tuning} from another tuning, but with a different
     * root note and/or name.
     * 
     * @param family the family it should belong to
     * @param name the name of the tuning
     * @param rootNote the root note of the tuning
     * @param tuning the source tuning to use
     * @return the created tuning
     */
    public Tuning newTuning( final TuningFamily family, final String name,
            final Note rootNote, final Tuning tuning )
    {
        Node node = graphDb.createNode();
        node.createRelationshipTo( ( (NoteImpl) rootNote ).getUnderlying(),
                TuningTypes.TUNING_ROOT_NOTE );
        node.createRelationshipTo( ( (TuningImpl) tuning ).intervalRoot(),
                TuningTypes.TUNING_INTERVAL_ROOT );
        Tuning newTuning = new TuningImpl( node );
        family.addTuning( newTuning, name );
        return newTuning;
    }
}
