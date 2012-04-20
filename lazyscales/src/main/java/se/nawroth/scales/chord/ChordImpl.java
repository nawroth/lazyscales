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
package se.nawroth.scales.chord;

import java.util.Iterator;

import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.Chord;
import se.nawroth.scales.api.NamedInterval;
import se.nawroth.scales.util.collection.BaseCollection;

/**
 * Default implementation of the {@link Chord} interface.
 * 
 * @author Anders Nawroth
 */
public final class ChordImpl extends BaseCollection<NamedInterval, Node>
        implements Chord
{
    private static final String NAME = "name";

    protected ChordImpl( final Node start )
    {
        super( NamedInterval.class, start );
    }

    @Override
    public String getName()
    {
        Object name = ChordNavigation.getNavigation().getSingleItemRelationshipProperty(
                this, NAME );
        if ( name == null )
        {
            return null;
        }
        return String.valueOf( name );
    }

    @Override
    public Node getUnderlyingNode()
    {
        return getStart();
    }

    @Override
    public int compareTo( final Chord other )
    {
        return getName().compareTo( other.getName() );
    }

    @Override
    public Iterator<NamedInterval> iterator()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
