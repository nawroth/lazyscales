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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.ImpermanentGraphDatabase;

public class CategoryUtilTest
{
    /**
     * The navigation to use. Some ugliness here: We're actually testing testing
     * a class that extends the class we want to test here. However, the methods
     * we use are all declared as final. To fix this, create entities that don't
     * extend {@link NamedEntityImpl}.
     */
    private static final CategoryWithPropertiesUtil<Category, Item> NAVIGATION = CategoryItemNavigation.getInstance();

    static CategoryWithPropertiesUtil<Category, Item> getNavigation()
    {
        return NAVIGATION;
    }

    private GraphDb db;
    private Node rootNode;
    private Node mainCatNode;
    private Node directItemNode;

    /**
     * Lazily create the root category.
     * 
     * @return the root category
     */
    private Category getRootCategory()
    {
        Node referenceNode = db.getReferenceNode( CategoryTypes.CATEGORY );
        Category root = new Category( referenceNode, getNavigation() );
        return root;
    }

    @Before
    public void setUp() throws Exception
    {
        db = new GraphDb( new ImpermanentGraphDatabase() );
        Transaction tx = db.beginTx();
        Category root = getRootCategory();
        rootNode = root.getUnderlyingNode();
        Category main = new Category( db.createNode(), getNavigation(), root,
        "" );
        mainCatNode = main.getUnderlyingNode();
        Item directItem = new Item( db.createNode(), getNavigation(), main, "" );
        directItemNode = directItem.getUnderlyingNode();
        Category subCat1 = new Category( db.createNode(), getNavigation(),
                main, "subcat1" );
        @SuppressWarnings( "unused" )
        Item undirectItem = new Item( db.createNode(), getNavigation(),
                subCat1, "" );
        @SuppressWarnings( "unused" )
        Category subCatChild = new Category( db.createNode(), getNavigation(),
                subCat1, "" );
        tx.success();
        tx.finish();
    }

    @After
    public void tearDown() throws Exception
    {
        db.shutdown();
    }

    @Test
    public void testCategoryUtil()
    {
        assertEquals( rootNode, getRootCategory().getUnderlyingNode() );
    }

    @Test
    public void testCategoryFromNode()
    {
        Node referenceNode = db.getReferenceNode( CategoryTypes.CATEGORY );
        Category root = new Category( referenceNode, getNavigation() );
        assertEquals( rootNode, root.getUnderlyingNode() );
    }

    @Test
    public void testItemFromNode()
    {
        Node itemNode = db.getReferenceNode( CategoryTypes.CATEGORY,
                CategoryTypes.CATEGORY, CategoryTypes.ITEM );
        Item item = new Item( itemNode, getNavigation() );
        assertEquals( directItemNode, item.getUnderlyingNode() );
    }

    @Test
    public void testGetCategoryType()
    {
        RelationshipType type = getNavigation().getCategoryType();
        assertEquals( CategoryTypes.CATEGORY.name(), type.name() );
    }

    @Test
    public void testGetItemType()
    {
        RelationshipType type = getNavigation().getItemType();
        assertEquals( CategoryTypes.ITEM.name(), type.name() );
    }

    @Test
    public void testGetSubCategories()
    {
        Category rootCat = getRootCategory();
        Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
        assertEquals( mainCatNode, mainCat.getUnderlyingNode() );
    }

    @Test
    public void testGetParentCategories()
    {
        Category rootCat = getRootCategory();
        Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
        Category parentCat = getNavigation().getParentCategories( mainCat ).iterator().next();
        assertEquals( rootNode, parentCat.getUnderlyingNode() );
    }

    @Test
    public void testGetCategories()
    {
        Node itemNode = db.getReferenceNode( CategoryTypes.CATEGORY,
                CategoryTypes.CATEGORY, CategoryTypes.ITEM );
        Item item = new Item( itemNode, getNavigation() );
        Iterable<Category> cats = getNavigation().getCategories( item );
        Category firstCat = cats.iterator().next();
        assertEquals( mainCatNode, firstCat.getUnderlyingNode() );
    }

    @Test
    public void testGetDirectItems()
    {
        Category rootCat = getRootCategory();
        Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
        Item directiItem = getNavigation().getDirectItems( mainCat ).iterator().next();
        assertEquals( directItemNode, directiItem.getUnderlyingNode() );
    }

    @Test
    public void testGetAllItems()
    {
        Category rootCat = getRootCategory();
        Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
        int i = 0;
        for ( @SuppressWarnings( "unused" ) Item item : getNavigation().getAllItems(
                mainCat ) )
        {
            i++;
        }
        assertEquals( 2, i );
    }

    @Test
    public void testGetAllSubCategories()
    {
        Category rootCat = getRootCategory();
        int i = 0;
        for ( @SuppressWarnings( "unused" ) Category cat : getNavigation().getAllSubCategories(
                rootCat ) )
        {
            i++;
        }
        assertEquals( 3, i );
    }

    @Test
    public void testAddItem()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            new Item( db.createNode(), getNavigation(), mainCat, "" );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Item item : getNavigation().getAllItems(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 3, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testAddItemTwice()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            Item newItem = new Item( db.createNode(), getNavigation() );
            boolean wasAdded = getNavigation().addItem( mainCat, newItem );
            assertEquals( true, wasAdded );
            wasAdded = getNavigation().addItem( mainCat, newItem );
            assertEquals( false, wasAdded );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testAddItems()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            List<Item> items = new ArrayList<Item>();
            items.add( new Item( db.createNode(), getNavigation() ) );
            items.add( new Item( db.createNode(), getNavigation() ) );
            items.add( new Item( db.createNode(), getNavigation() ) );
            getNavigation().addItems( mainCat, items );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Item item : getNavigation().getAllItems(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 5, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testAddCategory()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            new Category( db.createNode(), getNavigation(), mainCat, "" );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Category category : getNavigation().getSubCategories(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 2, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testAddCategoryTwice()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            Category newCat = new Category( db.createNode(), getNavigation() );
            boolean wasLinked = getNavigation().addCategory( mainCat, newCat );
            assertEquals( true, wasLinked );
            wasLinked = getNavigation().addCategory( mainCat, newCat );
            assertEquals( false, wasLinked );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testAddCategories()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            List<Category> categories = new ArrayList<Category>();
            categories.add( new Category( db.createNode(), getNavigation() ) );
            categories.add( new Category( db.createNode(), getNavigation() ) );
            categories.add( new Category( db.createNode(), getNavigation() ) );
            getNavigation().addCategories( mainCat, categories );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Category category : getNavigation().getSubCategories(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 4, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testRemoveItem()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            Item directiItem = getNavigation().getDirectItems( mainCat ).iterator().next();
            getNavigation().removeItem( mainCat, directiItem );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Item item : getNavigation().getDirectItems(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 0, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testRemoveItemTwice()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            Item directiItem = getNavigation().getDirectItems( mainCat ).iterator().next();
            boolean wasRemoved = getNavigation().removeItem( mainCat,
                    directiItem );
            assertEquals( true, wasRemoved );
            wasRemoved = getNavigation().removeItem( mainCat, directiItem );
            assertEquals( false, wasRemoved );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void removeItemThatIsPartOfMultipleCategories()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            List<Item> items = new ArrayList<Item>();
            Item item1 = new Item( db.createNode(), getNavigation() );
            Item item2 = new Item( db.createNode(), getNavigation() );
            items.add( item2 );
            items.add( item1 );
            getNavigation().addItems( rootCat, items );
            getNavigation().addItems( mainCat, items );
            boolean wasRemoved = getNavigation().removeItem( mainCat, item1 );
            assertEquals( true, wasRemoved );
            wasRemoved = getNavigation().removeItem( mainCat, item2 );
            assertEquals( true, wasRemoved );
            boolean hasItems = getNavigation().getDirectItems( rootCat ).iterator().hasNext();
            assertEquals( true, hasItems );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testRemoveItems()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            List<Item> items = new ArrayList<Item>();
            Item directiItem = getNavigation().getDirectItems( mainCat ).iterator().next();
            items.add( directiItem );
            getNavigation().removeItems( mainCat, items );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Item item : getNavigation().getDirectItems(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 0, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testRemoveCategory()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            Category subCat = getNavigation().getSubCategories( mainCat ).iterator().next();
            getNavigation().removeCategory( mainCat, subCat );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Category category : getNavigation().getSubCategories(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 0, i );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testRemoveCategoryTwice()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            Category subCat = getNavigation().getSubCategories( mainCat ).iterator().next();
            boolean wasRemoved = getNavigation().removeCategory( mainCat,
                    subCat );
            assertEquals( true, wasRemoved );
            wasRemoved = getNavigation().removeCategory( mainCat, subCat );
            assertEquals( false, wasRemoved );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testRemoveCategories()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat ).iterator().next();
            List<Category> categories = new ArrayList<Category>();
            Category subCat = getNavigation().getSubCategories( mainCat ).iterator().next();
            categories.add( subCat );
            getNavigation().removeCategories( mainCat, categories );
            int i = 0;
            for ( @SuppressWarnings( "unused" ) Category category : getNavigation().getSubCategories(
                    mainCat ) )
            {
                i++;
            }
            assertEquals( 0, i );
        }
        finally
        {
            tx.finish();
        }
    }
}
