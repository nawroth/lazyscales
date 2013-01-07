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
package se.nawroth.scales.gui;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public enum Fonts
{
    SANS( "DejaVuSans" ),
    SANS_BOLD( "DejaVuSans-Bold" ),
    MONO( "DejaVuSansMono" ),
    MONO_BOLD( "DejaVuSansMono" );

    private Font font;
    private Map<Integer, Font> sizes = new HashMap<Integer, Font>( 4 );
    private final String fontName;

    private Fonts( String fontName )
    {
        this.fontName = fontName;
    }

    public synchronized Font font( int size )
    {
        if ( font == null )
        {
            font = getFont( fontName );
        }

        Font sizedFont = sizes.get( size );
        if ( sizedFont == null )
        {
            float floatSize = size;
            sizedFont = font.deriveFont( floatSize );
            sizes.put( Integer.valueOf( size ), sizedFont );
        }
        return sizedFont;
    }

    private static Font getFont( String name )
    {
        Font font = null;
        String file = "/fonts/" + name + ".ttf";
        try
        {
            InputStream stream = Fonts.class.getResourceAsStream( file );
            font = Font.createFont( Font.TRUETYPE_FONT, stream );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            System.err.println( name + " not loaded.  Using dialog font." );
            font = new Font( "Dialog", Font.PLAIN, 12 );
        }
        return font;
    }
}
