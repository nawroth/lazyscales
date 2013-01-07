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
package se.nawroth.scales.tuning;

import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.util.GraphDb;
import se.nawroth.scales.util.NamedEntityImpl;

/**
 * Perform operations on {@link TuningFamily}s.
 * 
 * @author Anders Nawroth
 */
public final class TuningFamilyService
{
    private final GraphDb graphDb;

    /**
     * Create the service.
     * 
     * @param graphDb the database to use
     */
    public TuningFamilyService( final GraphDb graphDb )
    {
        this.graphDb = graphDb;
    }

    /**
     * Get the root {@link TuningFamily}.
     * 
     * @return the root tuning family
     */
    public TuningFamily getRootTuningFamily()
    {
        Node referenceNode = graphDb.getReferenceNode(
                TuningTypes.TUNING_FAMILIES, TuningTypes.TUNING_FAMILY );
        TuningFamilyImpl root = new TuningFamilyImpl( referenceNode );
        String name = root.getName();
        if ( name == null )
        {
            TuningFamilyImpl.NAVIGATION.setCategoryRelationshipProperty( root,
                    NamedEntityImpl.NAME, "Tuning families" );
        }
        return root;
    }

    /**
     * Create a new tuning family.
     * 
     * @param parentFamily the parent family
     * @param name the name of the tuning family
     * @return the created tuning family
     */
    public TuningFamily newTuningFamily( final TuningFamily parentFamily,
            final String name )
    {
        Node node = graphDb.createNode();
        return new TuningFamilyImpl( parentFamily, node, name );
    }
}
