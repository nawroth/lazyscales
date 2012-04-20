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

import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.helpers.collection.IterableWrapper;
import org.neo4j.kernel.Traversal;

/**
 * Helper class to simplify navigating category/item (parent/child) structures.
 * 
 * @author Anders Nawroth
 * 
 * @param <C> the category type
 * @param <I> the item type
 */
public abstract class CategoryUtil<C extends Entity, I extends Entity>
{
    private final RelationshipType categoryType;
    private final RelationshipType itemType;
    private final TraversalDescription allItemsInCategory;
    private final TraversalDescription allCategoriesInCategory;

    /**
     * Create instance from {@link RelationshipType}s.
     * 
     * @param category relationship type between categories
     * @param item relationship type from a category to its items
     */
    protected CategoryUtil( final RelationshipType category,
            final RelationshipType item )
    {
        this.categoryType = category;
        this.itemType = item;

        // set up traversers
        allCategoriesInCategory = Traversal.description().relationships(
                categoryType, OUTGOING ).evaluator(
                Evaluators.excludeStartPosition() );
        allItemsInCategory = allCategoriesInCategory.relationships( itemType,
                OUTGOING ).evaluator( new Evaluator()
        {
            @Override
            public Evaluation evaluate( final Path path )
            {
                if ( path.length() == 0 )
                {
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                }
                if ( path.lastRelationship().isType( itemType ) )
                {
                    return Evaluation.INCLUDE_AND_PRUNE;
                }
                else
                {
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                }
            }
        } );
    }

    /**
     * Instantiate a category element from the underlying {@link Node}.
     * 
     * @param node the underlying node
     * @return the category element
     */
    protected abstract C categoryFromNode( final Node node );

    /**
     * Instantiate an item element from the underlying {@link Node}.
     * 
     * @param node the underlying node
     * @return the item element
     */
    protected abstract I itemFromNode( final Node node );

    final RelationshipType getCategoryType()
    {
        return categoryType;
    }

    final RelationshipType getItemType()
    {
        return itemType;
    }

    /**
     * Get the sub-categories of a category.
     * 
     * @param category the category to start from
     * @return the sub-categories
     */
    public final Iterable<C> getSubCategories( final C category )
    {
        return getCategories( category.getUnderlyingNode(), categoryType,
                OUTGOING );
    }

    /**
     * Get the parent categories of a category.
     * 
     * @param category the category
     * @return the parent categories
     */
    public final Iterable<C> getParentCategories( final C category )
    {
        return getCategories( category.getUnderlyingNode(), categoryType,
                INCOMING );
    }

    /**
     * Get the categories of an item. Note that an item can belong to multiple
     * categories.
     * 
     * @param item the item
     * @return the categories
     */
    public final Iterable<C> getCategories( final I item )
    {
        return getCategories( item.getUnderlyingNode(), itemType, INCOMING );
    }

    private Iterable<C> getCategories( final Node node,
            final RelationshipType type, final Direction direction )
    {
        return new IterableWrapper<C, Relationship>( node.getRelationships(
                type, direction ) )
        {
            @Override
            protected C underlyingObjectToObject(
                    final Relationship relationship )
            {
                return categoryFromNode( relationship.getOtherNode( node ) );
            }
        };
    }

    /**
     * Get the direct items/children of a category, not looking into the
     * sub-categories.
     * 
     * @param category the category to start from
     * @return the items beloning to this category
     */
    public final Iterable<I> getDirectItems( final C category )
    {
        final Node node = category.getUnderlyingNode();
        return new IterableWrapper<I, Relationship>( node.getRelationships(
                itemType, OUTGOING ) )
        {
            @Override
            protected I underlyingObjectToObject(
                    final Relationship relationship )
            {
                return itemFromNode( relationship.getOtherNode( node ) );
            }
        };
    }

    /**
     * Get the items in a category, including items found in any sub-category.
     * 
     * @param category the category to start from
     * @return all items
     */
    public final Iterable<I> getAllItems( final C category )
    {
        final Node node = category.getUnderlyingNode();
        return new IterableWrapper<I, Node>(
                allItemsInCategory.traverse( node ).nodes() )
        {
            @Override
            protected I underlyingObjectToObject( final Node node )
            {
                return itemFromNode( node );
            }
        };
    }

    /**
     * Get all sub-categories of a category.
     * 
     * @param category the category to start from
     * @return all categories
     */
    public final Iterable<C> getAllSubCategories( final C category )
    {
        final Node node = category.getUnderlyingNode();
        return new IterableWrapper<C, Node>( allCategoriesInCategory.traverse(
                node ).nodes() )
        {
            @Override
            protected C underlyingObjectToObject( final Node node )
            {
                return categoryFromNode( node );
            }
        };
    }

    /**
     * Add an item to a category
     * 
     * @param category the category to add the item to
     * @param item the item to add
     * @return <code>true</code> if the item was added, false if it was already
     *         in this category
     */
    public final boolean addItem( final C category, final I item )
    {
        return addElement( category, item, itemType );
    }

    /**
     * Add multiple items to a category.
     * 
     * @param category the category to add items to
     * @param items the items to add
     * @return <code>true</code> if at least one change happened
     */
    public final boolean addItems( final C category, final Iterable<I> items )
    {
        return addElements( category, items, itemType );
    }

    /**
     * Add a sub-category to a category.
     * 
     * @param category the category to add a sub-category to
     * @param subCategory the sub-category to add
     * @return <code>true</code> if the sub-category was added, false if it was
     *         already a sub-category of the category
     */
    public final boolean addCategory( final C category, final C subCategory )
    {
        return addElement( category, subCategory, categoryType );
    }

    /**
     * Add multiple sub-categories to a category.
     * 
     * @param category the category to add sub-categories to
     * @param subCategories the sub-categories to add
     * @return <code>true</code> if at least one change happened
     */
    public final boolean addCategories( final C category,
            final Iterable<C> subCategories )
    {
        return addElements( category, subCategories, categoryType );
    }

    /**
     * Remove an item from a category.
     * 
     * @param category the category of the item
     * @param item the item to remove from the category
     * @return <code>true</code> if the item belonged to the category
     */
    public final boolean removeItem( final C category, final I item )
    {
        return removeElement( category, item, itemType );
    }

    /**
     * Remove multiple items from a category.
     * 
     * @param category the category to remove items from
     * @param items the items to remove
     * @return <code>true</code> if at least one change happened
     */
    public final boolean removeItems( final C category, final Iterable<I> items )
    {
        return removeElements( category, items, itemType );
    }

    /**
     * Remove a sub-category from a category.
     * 
     * @param category the category to remove a sub-category from
     * @param subCategory the sub-category to remove
     * @return <code>true</code> if the sub-category actually had the category
     *         as a parent category
     */
    public final boolean removeCategory( final C category, final C subCategory )
    {
        return removeElement( category, subCategory, categoryType );
    }

    /**
     * Remove multiple sub-categories from a category.
     * 
     * @param category the category to remove sub-categories from.
     * @param subCategories the sub-categories to remove
     * @return <code>true</code> if at least one change happened
     */
    public final boolean removeCategories( final C category,
            final Iterable<C> subCategories )
    {
        return removeElements( category, subCategories, categoryType );
    }

    protected final Relationship getRelationship( final Node from,
            final Node to, final RelationshipType type )
    {
        for ( Relationship rel : to.getRelationships( type, INCOMING ) )
        {
            if ( rel.getStartNode().equals( from ) )
            {
                return rel;
            }
        }
        return null;
    }

    private boolean addElement( final C category, final Entity element,
            final RelationshipType type )
    {
        Node categoryNode = category.getUnderlyingNode();
        Node elementNode = element.getUnderlyingNode();
        if ( getRelationship( categoryNode, elementNode, type ) != null )
        {
            return false;
        }
        categoryNode.createRelationshipTo( elementNode, type );
        return true;
    }

    private boolean addElements( final C category,
            final Iterable<? extends Entity> elements,
            final RelationshipType type )
    {
        boolean changed = false;
        for ( Entity element : elements )
        {
            changed |= addElement( category, element, type );
        }
        return changed;
    }

    private boolean removeElement( final C category, final Entity element,
            final RelationshipType type )
    {
        Node categoryNode = category.getUnderlyingNode();
        Node elementNode = element.getUnderlyingNode();
        Relationship rel = getRelationship( categoryNode, elementNode, type );
        if ( rel != null )
        {
            rel.delete();
            return true;
        }
        return false;
    }

    private boolean removeElements( final C category,
            final Iterable<? extends Entity> elements,
            final RelationshipType type )
    {
        boolean changed = false;
        for ( Entity element : elements )
        {
            changed |= removeElement( category, element, type );
        }
        return changed;
    }
}
