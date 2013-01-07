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
import se.nawroth.scales.util.CategoryImpl;

public final class ChordFamilyImpl extends CategoryImpl<ChordFamily, Chord>
        implements ChordFamily
{
    public ChordFamilyImpl( final Node underlyingNode )
    {
        super( underlyingNode, ChordNavigation.getNavigation() );
    }

    ChordFamilyImpl( final ChordFamily parentFamily, final Node underlyingNode,
            final String name )
    {
        super( underlyingNode, ChordNavigation.getNavigation(), parentFamily,
                name );
    }

    @Override
    public Iterable<ChordFamily> getSubFamilies()
    {
        return ChordNavigation.getNavigation().getSubCategories( this );
    }

    @Override
    public Iterable<Chord> getChords()
    {
        return ChordNavigation.getNavigation().getDirectItems( this );
    }

    @Override
    public boolean addSubFamily( final ChordFamily family )
    {
        return ChordNavigation.getNavigation().addCategory( this, family );
    }

    @Override
    public boolean addChord( final Chord chord )
    {
        return ChordNavigation.getNavigation().addItem( this, chord );
    }

    @Override
    public String toString()
    {
        return "ChordFamily [" + getName() + "]";
    }
}
