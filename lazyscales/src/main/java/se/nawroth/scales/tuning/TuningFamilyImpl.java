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

import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.util.CategoryImpl;
import se.nawroth.scales.util.CategoryWithPropertiesUtil;

/**
 * Default implementation of {@link TuningFamily}.
 * 
 * @author Anders Nawroth
 */
public final class TuningFamilyImpl extends CategoryImpl<TuningFamily, Tuning>
        implements TuningFamily
{
    static final CategoryWithPropertiesUtil<TuningFamily, Tuning> NAVIGATION;

    static
    {
        NAVIGATION = new CategoryWithPropertiesUtil<TuningFamily, Tuning>(
                TuningTypes.TUNING_FAMILY, TuningTypes.TUNING )
        {
            @Override
            protected TuningFamily categoryFromNode( final Node node )
            {
                return new TuningFamilyImpl( node );
            }

            @Override
            protected Tuning itemFromNode( final Node node )
            {
                return new TuningImpl( node );
            }
        };
    }

    /**
     * Create instance from the underlying {@link Node}.
     * 
     * @param underlyingNode the underlying node
     */
    public TuningFamilyImpl( final Node underlyingNode )
    {
        super( underlyingNode, NAVIGATION );
    }

    TuningFamilyImpl( final TuningFamily parentFamily,
            final Node underlyingNode, final String name )
    {
        super( underlyingNode, NAVIGATION, parentFamily, name );
    }

    @Override
    public Iterable<TuningFamily> getSubFamilies()
    {
        return NAVIGATION.getSubCategories( this );
    }

    @Override
    public Iterable<Tuning> getTunings()
    {
        return NAVIGATION.getDirectItems( this );
    }

    @Override
    public boolean addSubFamily( final TuningFamily family )
    {
        return NAVIGATION.addCategory( this, family );
    }

    @Override
    public boolean addTuning( final Tuning tuning, final String name )
    {
        return NAVIGATION.addItem( this, tuning, TuningImpl.NAME, name );
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
