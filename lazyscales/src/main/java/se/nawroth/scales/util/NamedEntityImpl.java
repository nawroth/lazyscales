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
package se.nawroth.scales.util;

import org.neo4j.graphdb.Node;

/**
 * Helper class to extend {@link EntityImpl} for {@link NamedEntity}s with a
 * category/item navigation.
 * 
 * @author Anders Nawroth
 * 
 * @param <T> the category type
 * @param <U> the item type
 */
@SuppressWarnings( "rawtypes" )
public abstract class NamedEntityImpl<T extends NamedEntity, U extends NamedEntity>
        extends EntityImpl
{
    /**
     * The property key used for names.
     */
    public static final String NAME = "name";
    private final CategoryWithPropertiesUtil<T, U> navigation;

    /**
     * Create instance from an underlying {@link Node} and a navigation helper.
     * 
     * @param underlyingNode the underlying node
     * @param navigation the navigation helper to use
     */
    protected NamedEntityImpl( final Node underlyingNode,
            final CategoryWithPropertiesUtil<T, U> navigation )
    {
        super( underlyingNode );
        this.navigation = navigation;
    }

    /**
     * Get the name of this element.
     * 
     * @return the name
     */
    public abstract String getName();

    /**
     * Get the navigation helper.
     * 
     * @return the navigation helper in use
     */
    protected final CategoryWithPropertiesUtil<T, U> getNavigation()
    {
        return navigation;
    }
}
