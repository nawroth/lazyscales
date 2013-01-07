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
package se.nawroth.scales.scale;

import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.util.GraphDb;
import se.nawroth.scales.util.NamedEntityImpl;

/**
 * Perform operations on {@link ScaleFamily}s.
 * 
 * @author Anders Nawroth
 */
public class ScaleFamilyService
{
    private final GraphDb graphDb;

    /**
     * Create the service.
     * 
     * @param graphDb the database to use
     */
    public ScaleFamilyService( final GraphDb graphDb )
    {
        this.graphDb = graphDb;
    }

    /**
     * Get the root {@link ScaleFamily}.
     * 
     * @return the root scale family
     */
    public final ScaleFamily getRootScaleFamily()
    {
        Node referenceNode = graphDb.getReferenceNode(
                ScaleTypes.SCALE_FAMILIES, ScaleTypes.SCALE_FAMILY );
        ScaleFamily root = new ScaleFamilyImpl( referenceNode );
        String name = root.getName();
        if ( name == null )
        {
            ScaleNavigation.getNavigation().setCategoryRelationshipProperty(
                    root, NamedEntityImpl.NAME, "Scale families" );
        }
        return root;
    }

    /**
     * Create a new {@link ScaleFamily}.
     * 
     * @param parentFamily the parent family
     * @param name the name of the family
     * @return the created scale family
     */
    public final ScaleFamily newScaleFamily( final ScaleFamily parentFamily,
            final String name )
    {
        Node node = graphDb.createNode();
        return new ScaleFamilyImpl( parentFamily, node, name );
    }
}
