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
package se.nawroth.scales;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.nawroth.scales.api.DiatonicInterval.FOURTH;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_SECOND;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_THIRD;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_SECOND;

import java.util.Iterator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

import se.nawroth.scales.api.DiatonicInterval;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.fretboard.Fretboard;
import se.nawroth.scales.note.NoteRepository;
import se.nawroth.scales.note.Notes;

/**
 * Test for common use cases, bundled together in a mess.
 * 
 * @author Anders Nawroth
 */
public class LazyScalesTest
{
    private static LazyScales app;
    private static Services services;

    /**
     * Created an initialize the {@link LazyScales} instance.
     */
    @BeforeClass
    public static void setUpBeforeClass()
    {
        app = new LazyScales( "target/test-db" );
        app.start();
        app.init();
        services = app.getServices();
    }

    /**
     * Shut down the {@link LazyScales} instance.
     */
    @AfterClass
    public static void tearDownAfterClass()
    {
        app.stop();
    }

    /**
     * Lots of testing in here!
     */
    @Test
    public void general()
    {
        Transaction tx = services.beginTx();
        try
        {
            ScaleFamily rootScaleFamily = services.getScaleFamilyService().getRootScaleFamily();
            ScaleFamily diatonic = services.getScaleFamilyService().newScaleFamily(
                    rootScaleFamily, "Diatonic" );
            Scale ionian = services.getScaleService().newScale( diatonic,
                    "Ionian", MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND,
                    MAJOR_SECOND, MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND );

            /*

            auxiliary diminished
            whole-step/half-step
            1,2,b3,4,#4,#5,6,7

             */
            System.out.println( ionian );

            assertEquals( 6, ionian.size() );

            Scale dorian = services.getScaleService().newScale( diatonic,
                    ionian, 2, "Dorian" );
            System.out.println( dorian );
            services.getScaleService().newScale( diatonic, ionian, 7, "Locrian" );

            Note c = services.getNoteService().note( NoteRepository.C );
            Note a = services.getNoteService().note( NoteRepository.A );

            Notes notes = Notes.notes( c, ionian );
            System.out.println( notes );

            notes = Notes.notes( a, dorian );
            notes.setSharp();
            String notesSharp = notes.toString();
            System.out.println( notesSharp );
            notes.setFlat();
            String notesFlat = notes.toString();
            System.out.println( notesFlat );
            assertFalse( notesSharp.equals( notesFlat ) );

            notes = Notes.notes( a, ionian );
            notes.setSharp();
            Iterator<Note> iter = notes.circularIterator();
            for ( int i = 0; i < 20; i++ )
            {
                System.out.print( notes.toString( iter.next() ) + " " );
            }
            System.out.println();

            int scales = 0;
            System.out.println( diatonic );
            for ( Scale scale : diatonic.getScales() )
            {
                scales++;
                System.out.println( " " + scale );
            }
            assertEquals( 3, scales );

            TuningFamily standard = services.getTuningFamilyService().newTuningFamily(
                    services.getTuningFamilyService().getRootTuningFamily(),
                    "Standard tunings" );
            Tuning myTuning = services.getTuningService().newTuning( standard,
                    "E♭ tuning",
                    services.getNoteService().note( NoteRepository.E_FLAT ),
                    FOURTH, FOURTH, FOURTH, MAJOR_THIRD, FOURTH );
            services.getTuningService().newTuning( standard, "Standard tuning",
                    services.getNoteService().note( NoteRepository.E ),
                    myTuning );
            System.out.println( myTuning.getFamily() );
            for ( Tuning tuning : standard.getTunings() )
            {
                System.out.println( " " + tuning );
            }

            notes = Notes.notes( c, dorian );
            notes.setFlat();
            Fretboard neck = new Fretboard( myTuning, notes );
            System.out.println( notes );
            System.out.println( services.getFretboardService().printStrings(
                    neck, 17 ) );

            Scale searchResult = services.getScaleSearchService().find(
                    MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                    MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND );
            System.out.println( searchResult.getName() );
            searchResult = services.getScaleSearchService().find( MAJOR_SECOND,
                    MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                    MINOR_SECOND, MAJOR_SECOND );
            System.out.println( searchResult.getName() );

            DiatonicInterval[] phrygianIntervals = new DiatonicInterval[] {
                    MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                    MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND };

            // this one is actually the phrygian scale
            searchResult = services.getScaleSearchService().find(
                    phrygianIntervals );
            System.out.println( "Undefined scale: " + searchResult.getName() );

            // current convention is to return null for undef scales
            // - should be changed to empty list
            assertTrue( searchResult.getName() == null );

            // now, create the phrygian scale
            Scale phrygian = services.getScaleService().newScale( diatonic,
                    "Phrygian", phrygianIntervals );
            System.out.println( phrygian );

            searchResult = services.getScaleSearchService().find(
                    phrygianIntervals );
            System.out.println( "Previously undefined scale: "
                                + searchResult.getName() );
            assertEquals( searchResult, phrygian );
            assertTrue( searchResult.getName() != null );

            tx.success();
            System.out.println( "Finished general testing." );
        }
        finally
        {
            tx.finish();
        }
    }
}
