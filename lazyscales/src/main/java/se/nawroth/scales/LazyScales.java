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
package se.nawroth.scales;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * LazyScales handles the life cycle of the server,
 * 
 * @author Anders Nawroth
 */
public final class LazyScales
{
    private final Services services;

    /**
     * Create a LazyScales server. In case the database doesn't already exist,
     * an empty database will be created.
     * 
     * @param location the filesystem location of the database
     */
    public LazyScales( final String location )
    {
        this.services = new Services( location );
    }

    /**
     * Create a LazySales server for testing, using a given underlying database.
     * 
     * @param graphDb the underlying database to use.
     */
    public LazyScales( final GraphDatabaseService graphDb )
    {
        this.services = new Services( graphDb );
    }

    /**
     * Start the LazyScales server.
     */
    public void start()
    {
        services.start();
    }

    /**
     * Initialize the database at first startup. Note: {@link #start()} needs to
     * be called first.
     */
    public void init()
    {
        Transaction tx = services.beginTx();
        try
        {
            services.getNoteService().setupNotes();
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    /**
     * Stop the server.
     */
    public void stop()
    {
        services.stop();
    }

    /**
     * Get the available services.
     * 
     * @return the services
     */
    public Services getServices()
    {
        return services;
    }
}
