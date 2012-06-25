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
package se.nawroth.scales.gui;

import static se.nawroth.scales.api.DiatonicInterval.FIFTH;
import static se.nawroth.scales.api.DiatonicInterval.FOURTH;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_SECOND;
import static se.nawroth.scales.api.DiatonicInterval.MAJOR_THIRD;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_SECOND;
import static se.nawroth.scales.api.DiatonicInterval.MINOR_THIRD;

import org.neo4j.graphdb.Transaction;

import se.nawroth.scales.Services;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.note.NoteRepository;

public class DataImport
{

    static void populateDb( Services services )
    {
        Transaction tx = services.beginTx();
        try
        {
            ScaleFamily rootScaleFamily = services.getScaleFamilyService()
                    .getRootScaleFamily();

            ScaleFamily diatonic = services.getScaleFamilyService()
                    .newScaleFamily( rootScaleFamily, "Diatonic" );
            ScaleFamily majorModes = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Major Scale Modes" );
            String[] modes = new String[] { "Ionian", "Dorian", "Phrygian",
                    "Lydian", "Mixolydian", "Aeolian", "Locrian" };
            services.getScaleService()
                    .newScales( majorModes, modes, MAJOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND );

            ScaleFamily melodicMinor = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Melodic Minor Modes" );
            modes = new String[] { "Ascending Melodic Minor", "Phrygian ♮6",
                    "Lydian ♯5", "Lydian ♭7", "Mixolydian ♭6",
                    "Half-diminished", "Super Locrian" };
            services.getScaleService()
                    .newScales( melodicMinor, modes, MAJOR_SECOND,
                            MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND );

            ScaleFamily harmonicMinor = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Harmonic Minor Modes" );
            modes = new String[] { "Harmonic Minor", "Locrian ♯6", "Ionian ♯5",
                    "Dorian ♯4", "Phrygian Dominant", "Lydian ♯2",
                    "Ultralocrian" };
            services.getScaleService()
                    .newScales( harmonicMinor, modes, MAJOR_SECOND,
                            MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_THIRD, MINOR_SECOND );

            ScaleFamily harmonicMajorModes = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Harmonic Major Modes" );
            modes = new String[] { "Harmonic Major", "Dorian ♭5",
                    "Phrygian ♭4", "Lydian ♭3", "Mixolydian ♭2",
                    "Lydian Augmented ♯2", "Locrian ♭♭7" };
            services.getScaleService()
                    .newScales( harmonicMajorModes, modes, MAJOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_THIRD, MINOR_SECOND );

            ScaleFamily naepolitanMajorModes = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Naepolitan Modes" );
            modes = new String[] { "Naepolitan", "Leading Whole-tone",
                    "Lydian Augmented Dominant", "Lydian Dominant ♭6",
                    "Major Locrian", "Semilocrian ♭4", "Superlocrian ♭♭3" };
            services.getScaleService()
                    .newScales( naepolitanMajorModes, modes, MINOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND );

            ScaleFamily naepolitanMinorModes = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Naepolitan Minor Modes" );
            modes = new String[] { "Naepolitan Minor", "Lydian ♯6",
                    "Mixolydian Augmented", "Hungarian Gypsy",
                    "Locrian Dominant", "Ionian ♯2", "Ultralocrian ♭♭3" };
            services.getScaleService()
                    .newScales( naepolitanMinorModes, modes, MINOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_THIRD, MINOR_SECOND );

            ScaleFamily doubleHarmonicModes = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Double Harmonic Modes" );
            modes = new String[] { "Double Harmonic Major", "Lydian ♯2 ♯6",
                    "Ultraphrygian", "Hungarian Minor", "Oriental",
                    "Ionian Augmented ♯2", "Locrian ♭♭3 ♭♭7" };
            services.getScaleService()
                    .newScales( doubleHarmonicModes, modes, MINOR_SECOND,
                            MINOR_THIRD, MINOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_THIRD, MINOR_SECOND );

            ScaleFamily hungarianModes = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Hungarian Scale Modes" );
            modes = new String[] { "Hungarian", "Superlocrian ♭♭6 ♭♭7",
                    "Harmonic Minor ♭5", "Superlocrian ♯6", "Melodic Minor ♯5",
                    "Dorian ♭9 ♯11", "Lydian Augmented ♯3" };
            services.getScaleService()
                    .newScales( hungarianModes, modes, MINOR_THIRD,
                            MINOR_SECOND, MAJOR_SECOND, MINOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_SECOND );

            ScaleFamily enigmatic = services.getScaleFamilyService()
                    .newScaleFamily( diatonic, "Enigmatic Scale" );
            services.getScaleService()
                    .newScale( enigmatic, "Enigmatic (ascending)",
                            MINOR_SECOND, MINOR_THIRD, MAJOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND,
                            MINOR_SECOND );
            services.getScaleService()
                    .newScale( enigmatic, "Enigmatic (descending)",
                            MINOR_SECOND, MINOR_THIRD, MINOR_SECOND,
                            MINOR_THIRD, MAJOR_SECOND, MINOR_SECOND,
                            MINOR_SECOND );

            ScaleFamily symmetric = services.getScaleFamilyService()
                    .newScaleFamily( rootScaleFamily, "Symmetric" );

            services.getScaleService()
                    .newScale( symmetric, "Whole Tone", MAJOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND );

            services.getScaleService()
                    .newScales( symmetric,
                            new String[] { "Diminished", "Octatonic" },
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MAJOR_SECOND, MINOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND );

            services.getScaleService()
                    .newScales(
                            symmetric,
                            new String[] { "Augmented", "Augmented (inverse)" },
                            MINOR_THIRD, MINOR_SECOND, MINOR_THIRD,
                            MINOR_SECOND, MINOR_THIRD, MINOR_SECOND );

            ScaleFamily blues = services.getScaleFamilyService()
                    .newScaleFamily( rootScaleFamily, "Blues" );

            services.getScaleService()
                    .newScale( blues, "Blues Scale", MINOR_THIRD, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_SECOND, MINOR_THIRD,
                            MAJOR_SECOND );

            ScaleFamily pentatonic = services.getScaleFamilyService()
                    .newScaleFamily( rootScaleFamily, "Pentatonic" );
            ScaleFamily minorPentatonic = services.getScaleFamilyService()
                    .newScaleFamily( pentatonic, "Minor Pentatonic Modes" );
            modes = new String[] { "Minor Pentatonic", "Major Pentatonic",
                    "Suspended Pentatonic", "Blues Minor Pentatonic",
                    "Blues Major Pentatonic" };
            services.getScaleService()
                    .newScales( minorPentatonic, modes, MINOR_THIRD,
                            MAJOR_SECOND, MAJOR_SECOND, MINOR_THIRD,
                            MAJOR_SECOND );
            ScaleFamily japanesePentatonic = services.getScaleFamilyService()
                    .newScaleFamily( pentatonic, "Japanese Pentatonic Scales" );
            services.getScaleService()
                    .newScale( japanesePentatonic, "Hirajōshi", MAJOR_THIRD,
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_THIRD,
                            MINOR_SECOND );
            services.getScaleService()
                    .newScale( japanesePentatonic, "Hirajōshi (Sachs)",
                            MINOR_SECOND, MAJOR_THIRD, MINOR_SECOND,
                            MAJOR_THIRD, MAJOR_SECOND );
            services.getScaleService()
                    .newScale( japanesePentatonic, "Hirajōshi (Kosta & Payne)",
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_THIRD,
                            MINOR_SECOND, MAJOR_THIRD );
            services.getScaleService()
                    .newScale( japanesePentatonic, "Miyako-bushi",
                            MINOR_SECOND, MAJOR_THIRD, MAJOR_SECOND,
                            MINOR_SECOND, MAJOR_THIRD );

            ScaleFamily bebop = services.getScaleFamilyService()
                    .newScaleFamily( rootScaleFamily, "Bebop" );

            services.getScaleService()
                    .newScale( bebop, "Bebop Major", MAJOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND );

            services.getScaleService()
                    .newScale( bebop, "Bebop Dominant", MAJOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MAJOR_SECOND, MINOR_SECOND, MINOR_SECOND,
                            MINOR_SECOND );

            services.getScaleService()
                    .newScale( bebop, "Bebop Dorian", MAJOR_SECOND,
                            MINOR_SECOND, MINOR_SECOND, MINOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND,
                            MAJOR_SECOND );

            services.getScaleService()
                    .newScale( bebop, "Bebop Minor", MAJOR_SECOND,
                            MINOR_SECOND, MAJOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MINOR_SECOND );

            services.getScaleService()
                    .newScale( bebop, "Bebop Locrian", MINOR_SECOND,
                            MAJOR_SECOND, MAJOR_SECOND, MINOR_SECOND,
                            MINOR_SECOND, MINOR_SECOND, MAJOR_SECOND,
                            MAJOR_SECOND );

            TuningFamily guitarTunings = services.getTuningFamilyService()
                    .newTuningFamily( services.getTuningFamilyService()
                            .getRootTuningFamily(), "Standard guitar tunings" );
            Tuning myTuning = services.getTuningService()
                    .newTuning( guitarTunings, "E♭ tuning",
                            services.getNoteService()
                                    .note( NoteRepository.E_FLAT ), FOURTH,
                            FOURTH, FOURTH, MAJOR_THIRD, FOURTH );
            services.getTuningService()
                    .newTuning( guitarTunings, "Standard guitar tuning",
                            services.getNoteService()
                                    .note( NoteRepository.E ), myTuning );
            TuningFamily bassTunings = services.getTuningFamilyService()
                    .newTuningFamily( services.getTuningFamilyService()
                            .getRootTuningFamily(), "Standard bass tunings" );
            services.getTuningService()
                    .newTuning( bassTunings, "Standard bass tuning",
                            services.getNoteService()
                                    .note( NoteRepository.E ), FOURTH, FOURTH,
                            FOURTH );
            services.getTuningService()
                    .newTuning( bassTunings, "Fifths bass tuning",
                            services.getNoteService()
                                    .note( NoteRepository.C ), FIFTH, FIFTH,
                            FIFTH );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

}
