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
package se.nawroth.scales.util;

import static org.neo4j.graphdb.Direction.INCOMING;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * Extends the {@link CategoryUtil} with functionality to handle
 * {@linkplain PropertyContainer properties}.
 * 
 * @author Anders Nawroth
 * 
 * @param <C> the category type
 * @param <I> the item type
 */
public abstract class CategoryWithPropertiesUtil<C extends Entity, I extends Entity>
        extends CategoryUtil<C, I>
{
    /**
     * Create instance from {@link RelationshipType}s.
     * 
     * @param category relationship type between categories
     * @param item relationship type from a category to its items
     */
    protected CategoryWithPropertiesUtil( final RelationshipType category,
            final RelationshipType item )
    {
        super( category, item );
    }

    /**
     * Add a sub-category to a category and add a key/value pair to the
     * {@link Relationship}. Note that the key is a {@link String} and the value
     * is a Neo4j {@linkplain PropertyContainer#setProperty(String, Object)
     * property value}.
     * 
     * @param category the category to add a sub-category to
     * @param subCategory the sub-category to add
     * @param key the key of the value
     * @param value the value to set on the relationship
     * @return <code>true</code> if the sub-category was added, false if it was
     *         already a sub-category of the category
     */
    public final boolean addCategory( final C category, final C subCategory,
            final String key, final Object value )
    {
        return addElement( category, subCategory, getCategoryType(), key, value );
    }

    /**
     * Get the property values from a category {@link Relationship}s. This
     * methods handles the case where a category can have multiple parent
     * categories.
     * 
     * @param category the category to get the property from
     * @param key the key of the property
     * @return the values of the property
     */
    public final Iterable<Object> getCategoryRelationhipProperty(
            final C category, final String key )
    {
        return getProperty( getCategoryType(), category, key );
    }

    /**
     * Get a single property value from a category {@link Relationship}s. When
     * using this method, a category may only have a single parent category.
     * 
     * @param category the category to get the property from
     * @param key the key of the property
     * @return the value of the property
     */
    public final Object getSingleCategoryRelationshipProperty(
            final C category, final String key )
    {
        return getSingleProperty( getCategoryType(), category, key );
    }

    /**
     * Add an item to a category, also adding a key/value pair to the
     * {@link Relationship} from the category to the item. Note that the key is
     * a {@link String} and the value is a Neo4j
     * {@linkplain PropertyContainer#setProperty(String, Object) property value}
     * .
     * 
     * @param category the category to add the item to
     * @param item the item to add
     * @param key the key of the value
     * @param value the value to set on the relationship
     * @return <code>true</code> if the item was added, false if it was already
     *         in this category
     */
    public final boolean addItem( final C category, final I item,
            final String key, final Object value )
    {
        return addElement( category, item, getItemType(), key, value );
    }

    private boolean addElement( final C category, final Entity element,
            final RelationshipType type, final String key, final Object value )
    {
        Node categoryNode = category.getUnderlyingNode();
        Node elementNode = element.getUnderlyingNode();
        Boolean created = false;
        Relationship rel = getRelationship( categoryNode, elementNode, type );
        if ( rel == null )
        {
            rel = categoryNode.createRelationshipTo( elementNode, type );
            created = true;
        }
        rel.setProperty( key, value );
        return created;
    }

    /**
     * Get the property values from a item {@link Relationship}s. This methods
     * handles the case where an item can have multiple categories.
     * 
     * @param item the item to get the property from
     * @param key the key of the property
     * @return the values of the property
     */
    public final Iterable<Object> getItemRelationshipProperty( final I item,
            final String key )
    {
        return getProperty( getItemType(), item, key );
    }

    /**
     * Get a single property value from an item {@link Relationship}s. When
     * using this method, an item may only have a single category.
     * 
     * @param item the item to get the property from
     * @param key the key of the property
     * @return the value of the property
     */
    public final Object getSingleItemRelationshipProperty( final I item,
            final String key )
    {
        return getSingleProperty( getItemType(), item, key );
    }

    private Iterable<Object> getProperty( final RelationshipType type,
            final Entity entity, final String key )
    {
        List<Object> values = new ArrayList<Object>();
        for ( Relationship rel : entity.getUnderlyingNode().getRelationships(
                type, INCOMING ) )
        {
            Object value = rel.getProperty( key, null );
            if ( value != null )
            {
                values.add( value );
            }
        }
        return values;
    }

    private Object getSingleProperty( final RelationshipType type,
            final Entity entity, final String key )
    {
        Node node = entity.getUnderlyingNode();
        Relationship rel;
        try
        {
            rel = node.getSingleRelationship( type, INCOMING );
        }
        catch ( NotFoundException nfe )
        {
            throw new IllegalStateException(
                    node
                            + " should only have a single incoming relationship of type "
                            + type
                            + " but has at least two such relationships." );
        }
        if ( rel == null )
        {
            return null;
        }
        return rel.getProperty( key, null );
    }

    /**
     * Set the value of a property on a category {@link Relationship}. Note that
     * the key is a {@link String} and the value is a Neo4j
     * {@linkplain PropertyContainer#setProperty(String, Object) property value}
     * .
     * 
     * @param category the category to set the value for
     * @param key the key of the property
     * @param value the value of the property
     */
    public final void setCategoryRelationshipProperty( final C category,
            final String key, final Object value )
    {
        setProperty( getCategoryType(), category, key, value );
    }

    /**
     * Set the value of a property on an item {@link Relationship}. Note that
     * the key is a {@link String} and the value is a Neo4j
     * {@linkplain PropertyContainer#setProperty(String, Object) property value}
     * .
     * 
     * @param item the item to set the value for
     * @param key the key of the property
     * @param value the value of the property
     */
    public final void setItemRelationshipProperty( final I item,
            final String key, final Object value )
    {
        setProperty( getItemType(), item, key, value );
    }

    private void setProperty( final RelationshipType type, final Entity entity,
            final String key, final Object value )
    {
        Node node = entity.getUnderlyingNode();
        Relationship rel = node.getSingleRelationship( type, INCOMING );
        if ( rel == null )
        {
            throw new IllegalStateException( "Incoming relationship of type ["
                                             + type + "] must exist." );
        }
        rel.setProperty( key, value );
    }
}
