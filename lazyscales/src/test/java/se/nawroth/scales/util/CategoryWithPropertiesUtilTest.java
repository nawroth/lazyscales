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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.ImpermanentGraphDatabase;

public class CategoryWithPropertiesUtilTest
{
    private static final String A_CATEGORY = "a category";
    private static final String INTEGERTEST = "integertest";
    private static final String SUBCAT_CHILD = "subcat-child";
    private static final Integer INT_FIVE = Integer.valueOf( 5 );
    private static final String AN_ITEM = "an item";
    private static final String UNDIRECT_ITEM = "undirectItem";
    private static final String MAIN_CATEGORY = "main";
    private static final String DIRECT_ITEM = "directItem";
    private static final String THE_ROOT_CATEGORY = "The root category";
    private static final CategoryWithPropertiesUtil<Category, Item> NAVIGATION = CategoryItemNavigation.getInstance();
    private GraphDb db;

    static CategoryWithPropertiesUtil<Category, Item> getNavigation()
    {
        return NAVIGATION;
    }

    /**
     * Lazily create the root category.
     * 
     * @return the root category
     */
    private Category getRootCategory()
    {
        Node referenceNode = db.getReferenceNode( CategoryTypes.CATEGORY );
        Category root = new Category( referenceNode, getNavigation() );
        String name = root.getName();
        if ( name == null )
        {
            getNavigation().setCategoryRelationshipProperty( root,
                    NamedEntityImpl.NAME, THE_ROOT_CATEGORY );
        }
        return root;
    }

    @Before
    public void setUp() throws Exception
    {
        db = new GraphDb( new ImpermanentGraphDatabase() );
        Transaction tx = db.beginTx();
        Category root = getRootCategory();
        Category main = new Category( db.createNode(), getNavigation(), root,
                MAIN_CATEGORY );
        @SuppressWarnings( "unused" ) Item directItem = new Item(
                db.createNode(), getNavigation(), main, DIRECT_ITEM );
        Category subCat1 = new Category( db.createNode(), getNavigation(),
                main, "subcat1" );
        @SuppressWarnings( "unused" ) Item undirectItem = new Item(
                db.createNode(), getNavigation(), subCat1, UNDIRECT_ITEM );
        @SuppressWarnings( "unused" ) Category subCatChild = new Category(
                db.createNode(), getNavigation(), subCat1, SUBCAT_CHILD );
        tx.success();
        tx.finish();
    }

    @After
    public void tearDown() throws Exception
    {
        db.shutdown();
    }

    @Test
    @Ignore
    public void testCategoryWithPropertiesUtil()
    {
        fail( "Not yet implemented" );
    }

    @Test
    public void testAddCategoryKeyValue()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            // doesn't link the category
            Category newCategory = new Category( db.createNode(),
                    getNavigation() );
            boolean wasCreated = getNavigation().addCategory( mainCat,
                    newCategory, INTEGERTEST, INT_FIVE );
            assertEquals( true, wasCreated );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleCategoryRelationshipProperty(
                            newCategory, INTEGERTEST ) );
            // links the category
            newCategory = new Category( db.createNode(), getNavigation(),
                    mainCat, A_CATEGORY );
            wasCreated = getNavigation().addCategory( mainCat, newCategory,
                    INTEGERTEST, INT_FIVE );
            assertEquals( false, wasCreated );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleCategoryRelationshipProperty(
                            newCategory, INTEGERTEST ) );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testGetCategoryRelationhipProperty()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            getNavigation().setCategoryRelationshipProperty( mainCat,
                    INTEGERTEST, INT_FIVE );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getCategoryRelationhipProperty( mainCat,
                            INTEGERTEST )
                            .iterator()
                            .next() );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSettingPropertyValueToObject()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            getNavigation().setCategoryRelationshipProperty( mainCat,
                    INTEGERTEST, new Object() );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSettingPropertyValueToNull()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            getNavigation().setCategoryRelationshipProperty( mainCat,
                    INTEGERTEST, null );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test( expected = IllegalStateException.class )
    public void testSettingPropertyWithMissingRelationship()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            @SuppressWarnings( "unused" ) Category mainCat = getNavigation().getSubCategories(
                    rootCat )
                    .iterator()
                    .next();
            // doesn't link the category
            Category newCategory = new Category( db.createNode(),
                    getNavigation() );
            getNavigation().setCategoryRelationshipProperty( newCategory,
                    INTEGERTEST, INT_FIVE );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testGetSingleCategoryRelationshipProperty()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            getNavigation().setCategoryRelationshipProperty( mainCat,
                    INTEGERTEST, INT_FIVE );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleCategoryRelationshipProperty(
                            mainCat, INTEGERTEST ) );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test( expected = IllegalStateException.class )
    public void testGetSingleCategoryRelationshipPropertyWithMultipleParents()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            // links the category
            Category newCategory = new Category( db.createNode(),
                    getNavigation(), mainCat, A_CATEGORY );
            getNavigation().setCategoryRelationshipProperty( newCategory,
                    INTEGERTEST, INT_FIVE );
            getNavigation().addCategory( rootCat, newCategory );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleCategoryRelationshipProperty(
                            newCategory, INTEGERTEST ) );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    public void testAddItemKeyValue()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            // doesn't link the item
            Item newItem = new Item( db.createNode(), getNavigation() );
            boolean wasCreated = getNavigation().addItem( mainCat, newItem,
                    INTEGERTEST, INT_FIVE );
            assertEquals( true, wasCreated );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleItemRelationshipProperty( newItem,
                            INTEGERTEST ) );
            // links the item
            newItem = new Item( db.createNode(), getNavigation(), mainCat,
                    AN_ITEM );
            wasCreated = getNavigation().addItem( mainCat, newItem,
                    INTEGERTEST, INT_FIVE );
            assertEquals( false, wasCreated );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleItemRelationshipProperty( newItem,
                            INTEGERTEST ) );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    @Ignore
    public void testGetItemRelationshipProperty()
    {
        fail( "Not yet implemented" );
    }

    @Test
    public void testGetSingleItemRelationshipProperty()
    {
        Transaction tx = db.beginTx();
        try
        {
            Category rootCat = getRootCategory();
            Category mainCat = getNavigation().getSubCategories( rootCat )
                    .iterator()
                    .next();
            // links the item
            Item newItem = new Item( db.createNode(), getNavigation(), mainCat,
                    AN_ITEM );
            getNavigation().setItemRelationshipProperty( newItem, INTEGERTEST,
                    INT_FIVE );
            assertEquals(
                    INT_FIVE,
                    getNavigation().getSingleItemRelationshipProperty( newItem,
                            INTEGERTEST ) );
        }
        finally
        {
            tx.finish();
        }
    }

    @Test
    @Ignore
    public void testSetCategoryRelationshipProperty()
    {
        fail( "Not yet implemented" );
    }

    @Test
    @Ignore
    public void testSetItemRelationshipProperty()
    {
        fail( "Not yet implemented" );
    }
}
