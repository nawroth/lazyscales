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

import static se.nawroth.scales.api.DiatonicInterval.MINOR_SECOND;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.LatinInterval;
import se.nawroth.scales.api.NamedInterval;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.api.Types;
import se.nawroth.scales.scale.ScaleFamilyService;
import se.nawroth.scales.scale.ScaleService;
import se.nawroth.scales.util.GraphDb;

/**
 * Operations on {@link Note}s.
 * 
 * @author Anders Nawroth
 */
public final class NoteService
{
    private static final String SHARP_NAME = "sharp name";
    private static final String FLAT_NAME = "flat name";
    private final GraphDb graphDb;
    private final ScaleService scaleService;
    private final ScaleFamilyService scaleFamilyService;

    /**
     * Create the service.
     * 
     * @param graphDb the database to use
     * @param scaleService the scale service to use
     * @param scaleFamilyService the scale family service to use
     */
    public NoteService( final GraphDb graphDb, final ScaleService scaleService,
            final ScaleFamilyService scaleFamilyService )
    {
        this.graphDb = graphDb;
        this.scaleService = scaleService;
        this.scaleFamilyService = scaleFamilyService;
    }

    /**
     * Get the concrete {@link Note} from a note representation.
     * 
     * @param noteRepresentation note to get
     * @return the actual note
     */
    public Note note( final NoteRepository noteRepresentation )
    {
        Node cNode = graphDb.getReferenceNode( NoteTypes.NOTES );
        Note c = new NoteImpl( cNode );
        return c.getFromInterval( noteRepresentation.interval() );
    }

    /**
     * Get the interval between two {@link Note}s.
     * 
     * @param first the starting {@link Note}
     * @param second the target {@link Note}
     * @return the interval between the {@link Note}s
     */
    public static NamedInterval findInterval( final Note first,
            final Note second )
    {
        if ( first.equals( second ) )
        {
            return DiatonicInterval.UNISON;
        }
        for ( Relationship rel : ( (NoteImpl) first ).getUnderlying().getRelationships(
                Direction.OUTGOING ) )
        {
            Node other = rel.getEndNode();
            if ( other.equals( ( (NoteImpl) second ).getUnderlying() )
                 && DiatonicInterval.isDiatonicInterval( rel.getType() ) )
            {
                return DiatonicInterval.valueOf( rel.getType().name() );
            }
        }
        return null;
    }

    /**
     * Initialize the database with {@link Note}s. This should be executed once
     * for every new database.
     */
    public void setupNotes()
    {
        Node notesNode = noteReferenceNode();
        if ( notesNode.hasRelationship( LatinInterval.SEMITONE ) )
        {
            return;
        }
        // create circle with note names by using semitones
        Queue<String> sharpNames = new LinkedList<String>(
                Arrays.asList( new String[] { "C", "C♯", "D", "D♯", "E", "F",
                        "F♯", "G", "G♯", "A", "A♯", "B" } ) );
        Queue<String> flatNames = new LinkedList<String>(
                Arrays.asList( new String[] { "C", "D♭", "D", "E♭", "E", "F",
                        "G♭", "G", "A♭", "A", "B♭", "B" } ) );
        Node currentNode = notesNode;
        while ( !sharpNames.isEmpty() )
        {
            currentNode.setProperty( SHARP_NAME, sharpNames.poll() );
            currentNode.setProperty( FLAT_NAME, flatNames.poll() );
            if ( !sharpNames.isEmpty() )
            {
                Node nextNode = graphDb.createNode();
                currentNode.createRelationshipTo( nextNode,
                        LatinInterval.SEMITONE );
                currentNode = nextNode;
            }
        }
        currentNode.createRelationshipTo( notesNode, LatinInterval.SEMITONE );

        // add other intervals
        while ( !currentNode.hasRelationship( DiatonicInterval.MINOR_SECOND,
                Direction.OUTGOING ) )
        {
            for ( DiatonicInterval interval : DiatonicInterval.values() )
            {
                if ( interval.equals( DiatonicInterval.UNISON ) )
                {
                    continue;
                }
                Node searchNode = currentNode;
                for ( int i = 1; i <= interval.getSemitones(); i++ )
                {
                    searchNode = searchNode.getSingleRelationship(
                            LatinInterval.SEMITONE, Direction.OUTGOING ).getEndNode();
                }
                currentNode.createRelationshipTo( searchNode, interval );
            }
            currentNode = currentNode.getSingleRelationship(
                    LatinInterval.SEMITONE, Direction.OUTGOING ).getEndNode();
        }

        // setup chromatic scale
        ScaleFamily rootScaleFamily = scaleFamilyService.getRootScaleFamily();
        ScaleFamily chromaticFamily = scaleFamilyService.newScaleFamily(
                rootScaleFamily, "Chromatic" );
        Scale chromatic = scaleService.newScale( chromaticFamily, "Chromatic",
                MINOR_SECOND, MINOR_SECOND, MINOR_SECOND, MINOR_SECOND,
                MINOR_SECOND, MINOR_SECOND, MINOR_SECOND, MINOR_SECOND,
                MINOR_SECOND, MINOR_SECOND, MINOR_SECOND, MINOR_SECOND );
        graphDb.getReferenceNode().createRelationshipTo(
                chromatic.getUnderlyingNode(), Types.CHROMATIC_SCALE );
    }

    private Node noteReferenceNode()
    {
        return graphDb.getReferenceNode( NoteTypes.NOTES );
    }

    /**
     * Get the reference {@link Note}, which is C.
     * 
     * @return a C
     */
    public Note referenceNote()
    {
        return new NoteImpl( noteReferenceNode() );
    }
}
