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
package se.nawroth.scales.scale;

import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.util.CategoryImpl;

/**
 * Handle {@link ScaleFamily}s and their subfamilies and {@link Scale}s.
 * 
 * @author Anders Nawroth
 */
public final class ScaleFamilyImpl extends CategoryImpl<ScaleFamily, Scale>
        implements ScaleFamily
{
    /**
     * Get instance from the underlying {@link Node}.
     * 
     * @param underlyingNode the underlying node
     */
    public ScaleFamilyImpl( final Node underlyingNode )
    {
        super( underlyingNode, ScaleNavigation.getNavigation() );
    }

    ScaleFamilyImpl( final ScaleFamily parentFamily, final Node underlyingNode,
            final String name )
    {
        super( underlyingNode, ScaleNavigation.getNavigation(), parentFamily,
                name );
    }

    @Override
    public Iterable<ScaleFamily> getSubFamilies()
    {
        return ScaleNavigation.getNavigation().getSubCategories( this );
    }

    @Override
    public Iterable<Scale> getScales()
    {
        return ScaleNavigation.getNavigation().getDirectItems( this );
    }

    @Override
    public boolean addSubFamily( final ScaleFamily family )
    {
        return ScaleNavigation.getNavigation().addCategory( this, family );
    }

    @Override
    public boolean addScale( final Scale scale )
    {
        return ScaleNavigation.getNavigation().addItem( this, scale );
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
