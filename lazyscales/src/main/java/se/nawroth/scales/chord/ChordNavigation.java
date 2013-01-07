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
package se.nawroth.scales.chord;

import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.Chord;
import se.nawroth.scales.api.ChordFamily;
import se.nawroth.scales.util.CategoryWithPropertiesUtil;

final class ChordNavigation
{
    private static final CategoryWithPropertiesUtil<ChordFamily, Chord> NAVIGATION;

    static
    {
        NAVIGATION = new CategoryWithPropertiesUtil<ChordFamily, Chord>(
                ChordTypes.CHORD_FAMILY, ChordTypes.CHORD )
        {
            @Override
            protected ChordFamily categoryFromNode( final Node node )
            {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            protected Chord itemFromNode( final Node node )
            {
                return new ChordImpl( node );
            }
        };
    }

    static CategoryWithPropertiesUtil<ChordFamily, Chord> getNavigation()
    {
        return NAVIGATION;
    }

    private ChordNavigation()
    {
        // no instantiation
    }
}
