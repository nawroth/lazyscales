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

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * A simplified wrapper around the Neo4j graph database. It always adds a
 * shutdown hook for the database, in case the program is aborted. It also adds
 * a different handling of reference nodes on top of the Neo4j engine.
 * 
 * @author Anders Nawroth
 */
public final class GraphDb
{
    private final GraphDatabaseService underlying;

    /**
     * Wrap a simplified graph database around a real
     * {@link GraphDatabaseService}.
     * 
     * @param graphDb the database implementation
     */
    public GraphDb( final GraphDatabaseService graphDb )
    {
        this.underlying = graphDb;
        registerShutdownHook( graphDb );
    }

    /**
     * Create a new node.
     * 
     * @return the new node
     */
    public Node createNode()
    {
        return underlying.createNode();
    }

    /**
     * Get the reference node.
     * 
     * @return the reference node
     */
    public Node getReferenceNode()
    {
        return underlying.getReferenceNode();
    }

    /**
     * Get a sub-reference node, following the path of {@link RelationshipType}s
     * given. If the sub-reference node does not exist, it will be created.
     * 
     * @param types the relationship types to follow, in the given order, to
     *            arrive at the sub-reference node
     * @return the sub-reference node
     */
    public Node getReferenceNode( final RelationshipType... types )
    {
        Node node = underlying.getReferenceNode();
        for ( RelationshipType type : types )
        {
            if ( type == null )
            {
                throw new IllegalArgumentException( "Type can not be null." );
            }
            if ( node.hasRelationship( type, Direction.OUTGOING ) )
            {
                node = node.getSingleRelationship( type, Direction.OUTGOING ).getEndNode();
            }
            else
            {
                Node newNode = underlying.createNode();
                node.createRelationshipTo( newNode, type );
                node = newNode;
            }
        }
        return node;
    }

    /**
     * Shut down the database.
     */
    public void shutdown()
    {
        underlying.shutdown();
    }

    /**
     * Start a database transaction.
     * 
     * @return a new, thread-confined, database transaction
     */
    public Transaction beginTx()
    {
        return underlying.beginTx();
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
}
