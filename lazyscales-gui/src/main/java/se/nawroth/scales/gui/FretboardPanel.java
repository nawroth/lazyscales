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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Iterator;

import javax.swing.JPanel;

import se.nawroth.scales.Services;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.fretboard.Fretboard;
import se.nawroth.scales.note.Notes;

public class FretboardPanel extends JPanel
{
    private static final Color CURRENT_NOTE_COLOR = Color.black;
    /**
     * org.pushingpixels.substance.api.colorscheme.OliveColorScheme.mainMidColor
     */
    static final Color PINNED_NOTE_COLOR = new Color( 165, 174, 129 );
    private static final Color TUNING_NOTE_FONT_COLOR = Color.lightGray;
    private static final Color TUNING_NOTE_CIRCLE_COLOR = Color.lightGray;
    /**
     * org.pushingpixels.substance.api.colorscheme.OliveColorScheme.
     * mainUltraLightColor
     */
    private static final Color DOT_COLOR = new Color( 205, 212, 182 );
    private static final Color STRINGS_FRETS_COLOR = Color.darkGray;
    private static final Color STARTING_NOTE_BGCOLOR = Color.lightGray;
    private static final Color PINNED_STARTING_NOTE_BGCOLOR = new Color( 216,
            216, 216 );
    private static final int CIRCLE_DIVIDER = 20;
    private static final int STROKE_DIVIDER = 330;
    private static final int PANEL_WIDTH_MULTIPLIER = 2;
    private static final int FONT_CONSTANT = -6;
    private static final int FONT_DIVIDER = 26;
    private static final int FONT_HEIGHT_FACTOR = 3;
    private static final Fonts FONT = Fonts.SANS_BOLD;
    private static final int DEFAULT_NUMBER_OF_STRINGS = 6;
    private static final int DEFAULT_NUMBER_OF_FRETS = 16;
    private static final int ORIGIN_X = 6;
    private static final int MAXIMUM_X = 94;
    private static final int ORIGIN_Y = 10;
    private static final int MAXIMUM_Y = 90;
    private int numberOfStrings;
    private int numberOfFrets;
    private int yStep;
    private int xStep;
    private Font font;
    private FontMetrics fontMetrics;
    private int fontHeight;
    private int originX;
    private int maximumX;
    private int originY;
    private int maximumY;
    private int strokeWidth;
    private int circleSize;
    private int circleHalfSize;
    private int xMax;
    private int yMax;
    private int yMid;
    private int xHalfStep;
    private int dotSize;
    private int dotHalfSize;
    private Tuning tuning;
    private Note[] tuningNotes = new Note[20];
    private Services services;
    private Notes notes;
    private Notes pinnedNotes;
    private Stroke[] strokes = new Stroke[30];

    public FretboardPanel( Services services )
    {
        super();
        this.services = services;
        numberOfStrings = DEFAULT_NUMBER_OF_STRINGS;
        numberOfFrets = DEFAULT_NUMBER_OF_FRETS;
    }

    private void calculateSizes()
    {
        originY = getHeight() * ORIGIN_Y / 100;
        maximumY = getHeight() * MAXIMUM_Y / 100;
        yStep = ( maximumY - originY ) / ( numberOfStrings - 1 );
        originX = getWidth() * ORIGIN_X / 100;
        maximumX = getWidth() * MAXIMUM_X / 100;
        xStep = ( maximumX - originX ) / ( numberOfFrets - 1 );
        xMax = originX + ( numberOfFrets - 1 ) * xStep;
        yMax = originY + ( numberOfStrings - 1 ) * yStep;

        // TODO calculate length(w or h)/number(frets or strings) and
        // pick the one which is lower only. (or factor the other
        // one in just a bit)
        int sizeFactor = (int) Math.sqrt( ( PANEL_WIDTH_MULTIPLIER * getWidth() )
                                          * getHeight() );

        int fontSize = sizeFactor / FONT_DIVIDER;
        fontSize += FONT_CONSTANT;
        setupFont( fontSize );

        strokeWidth = sizeFactor / STROKE_DIVIDER;

        circleSize = sizeFactor / CIRCLE_DIVIDER;
        circleHalfSize = circleSize / 2;

        dotSize = circleSize / 2;
        dotHalfSize = dotSize / 2;

        yMid = ( yMax + originY ) / 2 - dotHalfSize;
        xHalfStep = xStep / 2 + dotHalfSize;
    }

    private void setupFont( int fontSize )
    {
        font = FONT.font( fontSize );
        setFont( font );
        fontMetrics = getFontMetrics( font );
        fontHeight = fontMetrics.getHeight() / FONT_HEIGHT_FACTOR;
    }

    private Stroke getStroke( int width )
    {
        if ( strokes[width] == null )
        {
            strokes[width] = new BasicStroke( strokeWidth );
        }
        return strokes[width];
    }

    @Override
    public void paint( Graphics g )
    {
        super.paint( g );
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint( Color.black );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        calculateSizes();

        g2.setStroke( getStroke( strokeWidth ) );

        paintEmptyFretboard( g2 );

        if ( notes == null || tuning == null )
        {
            return;
        }

        numberOfStrings = tuning.size();
        boolean isFlat = notes.isFlat();

        Iterator<Iterable<Note>> stringIter = getFretboardStringsIterator( notes );

        Iterator<Iterable<Note>> pinnedStringIter = null;
        Note startNote = notes.getStartingNote();
        Note pinnedStartNote = null;
        if ( pinnedNotes != null )
        {
            pinnedStringIter = getFretboardStringsIterator( pinnedNotes );
            pinnedStartNote = pinnedNotes.getStartingNote();
        }

        for ( int currentString = 0; currentString < numberOfStrings; currentString++ )
        {
            Iterator<Note> noteIter = stringIter.next()
                    .iterator();
            Iterator<Note> pinnedNoteIter = null;
            if ( pinnedStringIter != null )
            {
                pinnedNoteIter = pinnedStringIter.next()
                        .iterator();
            }
            for ( int currentFret = 0; currentFret < numberOfFrets; currentFret++ )
            {
                Note note = noteIter.next();
                Note pinnedNote = null;
                if ( pinnedNoteIter != null )
                {
                    pinnedNote = pinnedNoteIter.next();
                }
                NotePinnedState state = NotePinnedState.getState( note,
                        pinnedNote );
                if ( state != NotePinnedState.NONE )
                {
                    Color circleColor = null, fontColor = null, bgColor = null;
                    Note currentNote = null;
                    if ( state == NotePinnedState.ONLY_CURRENT )
                    {
                        currentNote = note;
                        circleColor = CURRENT_NOTE_COLOR;
                        fontColor = CURRENT_NOTE_COLOR;
                        bgColor = STARTING_NOTE_BGCOLOR;
                    }
                    if ( state == NotePinnedState.ONLY_PINNED )
                    {
                        currentNote = pinnedNote;
                        circleColor = PINNED_NOTE_COLOR;
                        fontColor = PINNED_NOTE_COLOR;
                        bgColor = PINNED_STARTING_NOTE_BGCOLOR;
                    }
                    else if ( state == NotePinnedState.BOTH )
                    {
                        currentNote = note;
                        circleColor = CURRENT_NOTE_COLOR;
                        fontColor = CURRENT_NOTE_COLOR;
                        bgColor = STARTING_NOTE_BGCOLOR;
                    }
                    String str = currentNote.toString( isFlat );
                    int xPos = originX + currentFret * xStep;
                    int yPos = originY + currentString * yStep;
                    if ( currentNote.equals( startNote )
                         || currentNote.equals( pinnedStartNote ) )
                    {
                        g2.setPaint( bgColor );
                    }
                    else
                    {
                        g2.setPaint( getBackground() );
                    }
                    int circleX = xPos - circleHalfSize;
                    int circleY = yPos - circleHalfSize;
                    if ( state == NotePinnedState.ONLY_CURRENT
                         ^ pinnedNotes != null )
                    {
                        g2.fillOval( circleX, circleY, circleSize, circleSize );
                    }
                    else
                    {
                        g2.fillRoundRect( circleX, circleY, circleSize,
                                circleSize, circleHalfSize, circleHalfSize );
                    }

                    g2.setPaint( circleColor );

                    if ( state == NotePinnedState.ONLY_CURRENT
                         ^ pinnedNotes != null )
                    {
                        g2.drawOval( circleX, circleY, circleSize, circleSize );
                    }
                    else
                    {
                        g2.drawRoundRect( circleX, circleY, circleSize,
                                circleSize, circleHalfSize, circleHalfSize );
                    }

                    g2.setPaint( fontColor );
                    g2.drawString( str, xPos - fontMetrics.stringWidth( str )
                                        / 2, yPos + fontHeight );
                }
                else if ( currentFret == 0 )
                {
                    String str = tuningNotes[currentString].toString( isFlat );
                    int xPos = originX + currentFret * xStep;
                    int yPos = originY + currentString * yStep;
                    g2.setPaint( getBackground() );
                    int circleX = xPos - circleHalfSize;
                    int circleY = yPos - circleHalfSize;
                    g2.fillOval( circleX, circleY, circleSize, circleSize );
                    g2.setPaint( TUNING_NOTE_CIRCLE_COLOR );
                    g2.drawOval( circleX, circleY, circleSize, circleSize );
                    g2.setPaint( TUNING_NOTE_FONT_COLOR );

                    g2.drawString( str, xPos - fontMetrics.stringWidth( str )
                                        / 2, yPos + fontHeight );
                }
            }
        }
    }

    private Iterator<Iterable<Note>> getFretboardStringsIterator(
            Notes scaleNotes )
    {
        Fretboard neck = new Fretboard( tuning, scaleNotes );
        Iterable<Iterable<Note>> stringNotes = services.getFretboardService()
                .getStrings( neck );
        Iterator<Iterable<Note>> stringIter = stringNotes.iterator();
        return stringIter;
    }

    private void paintEmptyFretboard( Graphics2D g2 )
    {
        g2.setPaint( STRINGS_FRETS_COLOR );
        for ( int y = 0; y < numberOfStrings; y++ )
        {
            int yPos = originY + y * yStep;
            g2.drawLine( originX, yPos, xMax, yPos );
        }
        for ( int x = 0; x < numberOfFrets; x++ )
        {
            int xPos = originX + x * xStep;
            g2.drawLine( xPos, originY, xPos, yMax );
        }
        g2.setPaint( DOT_COLOR );
        for ( int f = 3; f < numberOfFrets; f++ )
        {
            int fretMod = f % 12;
            if ( fretMod == 3 || fretMod == 5 || fretMod == 7 || fretMod == 9 )
            {
                int xPos = originX + f * xStep - xHalfStep;
                g2.fillOval( xPos, yMid, dotSize, dotSize );
            }
            else if ( fretMod == 0 )
            {
                int xPos = originX + f * xStep - xHalfStep;
                g2.fillOval( xPos, yMid + yStep, dotSize, dotSize );
                g2.fillOval( xPos, yMid - yStep, dotSize, dotSize );
            }
        }
    }

    public void setTuning( Tuning tuning )
    {
        this.tuning = tuning;
        int size = tuning.size();
        for ( Note note : tuning )
        {
            tuningNotes[--size] = note;
        }
    }

    public void setNotes( Notes notes )
    {
        this.notes = notes;
    }

    public void setNumberOfFrets( int frets )
    {
        this.numberOfFrets = frets;
    }

    public void pinCurrentScale()
    {
        pinnedNotes = notes;
    }

    public void unpinCurrentScale()
    {
        pinnedNotes = null;
    }

    public Notes getPinnedScale()
    {
        return pinnedNotes;
    }

    private enum NotePinnedState
    {
        ONLY_CURRENT,
        ONLY_PINNED,
        BOTH,
        NONE;

        static NotePinnedState getState( Note current, Note pinned )
        {
            if ( current == null )
            {
                return pinned == null ? NotePinnedState.NONE
                        : NotePinnedState.ONLY_PINNED;
            }
            else
            {
                return pinned == null ? NotePinnedState.ONLY_CURRENT
                        : NotePinnedState.BOTH;
            }
        }
    }
}
