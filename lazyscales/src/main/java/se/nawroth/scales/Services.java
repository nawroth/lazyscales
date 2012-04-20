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
package se.nawroth.scales;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.fretboard.Fretboard;
import se.nawroth.scales.fretboard.FretboardService;
import se.nawroth.scales.note.NoteService;
import se.nawroth.scales.scale.RawScaleService;
import se.nawroth.scales.scale.ScaleFamilyService;
import se.nawroth.scales.scale.ScaleSearchService;
import se.nawroth.scales.scale.ScaleService;
import se.nawroth.scales.tuning.TuningFamilyService;
import se.nawroth.scales.tuning.TuningService;
import se.nawroth.scales.util.GraphDb;

/**
 * A simple dependency resolving class, using manual constructor injection. It
 * also gives access to database transaction control.
 * 
 * @author Anders Nawroth
 */
public final class Services
{
    private String dir;
    private GraphDb graphDb;
    private ScaleService scaleService;
    private NoteService noteService;
    private ScaleFamilyService scaleFamilyService;
    private TuningService tuningService;
    private TuningFamilyService tuningFamilyService;
    private ScaleSearchService scaleSearchService;
    private RawScaleService rawScaleService;
    private FretboardService fretboardService;

    /**
     * Prepare for services with database located at location.
     * 
     * @param location location of the database
     */
    Services( final String location )
    {
        this.dir = location;
        this.graphDb = null;
    }

    /**
     * Prepare for testing of services using a given database.
     * 
     * @param graphDatabase the underlying database to use
     */
    Services( final GraphDatabaseService graphDatabase )
    {
        graphDb = new GraphDb( graphDatabase );
    }

    /**
     * Start the database server. Other services are started lazily as needed.
     */
    void start()
    {
        if ( graphDb == null )
        {
            graphDb = new GraphDb( new EmbeddedGraphDatabase( dir ) );
        }
    }

    /**
     * Stop the database and invalidate all services.
     */
    void stop()
    {
        graphDb.shutdown();
        graphDb = null;
        scaleService = null;
        noteService = null;
        scaleFamilyService = null;
        tuningService = null;
        tuningFamilyService = null;
        scaleSearchService = null;
        rawScaleService = null;
        fretboardService = null;
    }

    /**
     * Get the embedded database instance.
     * 
     * @return
     */
    private GraphDb getGraphDb()
    {
        return graphDb;
    }

    /**
     * Start a database transaction. Note that a transaction must be marked as
     * {@link Transaction#success() successful} and {@link Transaction#finish()
     * finished} in order to persist data. Data can be read outside of
     * transactions, but any data that is not yet committed will be invisible.
     * 
     * @return a Neo4j transaction
     */
    public Transaction beginTx()
    {
        return getGraphDb().beginTx();
    }

    /**
     * Get the {@link Scale} service.
     * 
     * @return the current {@link ScaleService} instance
     */
    public ScaleService getScaleService()
    {
        if ( scaleService == null )
        {
            scaleService = new ScaleService( getGraphDb(),
                    getScaleSearchService(), getRawScaleService() );
        }
        return scaleService;
    }

    /**
     * Get the {@link Note} service.
     * 
     * @return the current {@link NoteService} instance.
     */
    public NoteService getNoteService()
    {
        if ( noteService == null )
        {
            noteService = new NoteService( getGraphDb(), getScaleService(),
                    getScaleFamilyService() );
        }
        return noteService;
    }

    /**
     * Get the {@link ScaleFamily} service.
     * 
     * @return the current {@link ScaleFamilyService} instance.
     */
    public ScaleFamilyService getScaleFamilyService()
    {
        if ( scaleFamilyService == null )
        {
            scaleFamilyService = new ScaleFamilyService( getGraphDb() );
        }
        return scaleFamilyService;
    }

    /**
     * Get the {@link TuningFamily} service.
     * 
     * @return the current {@link TuningFamilyService} instance
     */
    public TuningFamilyService getTuningFamilyService()
    {
        if ( tuningFamilyService == null )
        {
            tuningFamilyService = new TuningFamilyService( getGraphDb() );
        }
        return tuningFamilyService;
    }

    /**
     * Get the {@link Tuning} service.
     * 
     * @return the current {@link TuningService} instance
     */
    public TuningService getTuningService()
    {
        if ( tuningService == null )
        {
            tuningService = new TuningService( getGraphDb() );
        }
        return tuningService;
    }

    /**
     * Get the {@link Scale} searching service.
     * 
     * @return the current {@link ScaleSearchService} instance
     */
    public ScaleSearchService getScaleSearchService()
    {
        if ( scaleSearchService == null )
        {
            scaleSearchService = new ScaleSearchService( getRawScaleService() );
        }
        return scaleSearchService;
    }

    /**
     * Get the {@link RawScale} service.
     * 
     * @return the current {@link RawScaleService} instance
     */
    RawScaleService getRawScaleService()
    {
        if ( rawScaleService == null )
        {
            rawScaleService = new RawScaleService( getGraphDb() );
        }
        return rawScaleService;
    }

    /**
     * Get the {@link Fretboard} service.
     * 
     * @return the current {@link FretboardService} instance
     */
    public FretboardService getFretboardService()
    {
        if ( fretboardService == null )
        {
            fretboardService = new FretboardService( getGraphDb() );
        }
        return fretboardService;
    }
}
