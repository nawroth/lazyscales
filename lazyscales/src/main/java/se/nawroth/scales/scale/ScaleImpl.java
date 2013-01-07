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

import static se.nawroth.scales.api.DiatonicInterval.UNISON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

import se.nawroth.scales.api.NamedInterval;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.util.NamedEntity;
import se.nawroth.scales.util.collection.CircularCollection;

/**
 * Default implementation of the {@link Scale} interface.
 * 
 * @author Anders Nawroth
 */
public final class ScaleImpl extends CircularCollection<NamedInterval, Node>
        implements NamedEntity<Scale>, Scale
{
    private static final int INITIAL_STRINGBUILDER_SIZE = 60;
    private static final String NAME = "name";

    /**
     * Create instance from the underlying {@link Node}.
     * 
     * @param node the underlying node
     */
    public ScaleImpl( final Node node )
    {
        super( NamedInterval.class, node );
    }

    /**
     * Get the start {@link Node} of this scale.
     * 
     * @return the start node
     */
    Node getStartNode()
    {
        return getStart();
    }

    @Override
    public boolean isEmpty()
    {
        return !getStart().getRelationships( Direction.OUTGOING ).iterator().hasNext();
    }

    @Override
    public String getName()
    {
        Object name = ScaleNavigation.getNavigation().getSingleItemRelationshipProperty(
                this, NAME );
        if ( name == null )
        {
            return null;
        }
        return String.valueOf( name );
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( INITIAL_STRINGBUILDER_SIZE );
        builder.append( getName() ).append( ": " ).append(
                UNISON.getShortName() ).append( " " );
        String previousName = null;
        for ( NamedInterval interval : asAbsoluteList() )
        {
            String shortName = interval.getShortName( previousName );
            builder.append( shortName )
                    .append( " " );
            previousName = shortName;
        }
        return builder.toString();
    }

    @Override
    public Iterator<NamedInterval> iterator()
    {
        return ScaleIterator.iterator( getStart(), false );
    }

    @Override
    public Iterator<NamedInterval> circularIterator()
    {
        return ScaleIterator.iterator( getStart(), true );
    }

    @Override
    public List<NamedInterval> asAbsoluteList()
    {
        NamedInterval current = null;
        List<NamedInterval> list = new ArrayList<NamedInterval>();
        for ( NamedInterval interval : this )
        {
            if ( current == null )
            {
                current = interval;
            }
            else
            {
                current = current.sum( interval );
            }
            list.add( current );
        }
        return list;
    }

    @Override
    public Node getUnderlyingNode()
    {
        return getStartNode();
    }

    @Override
    public int compareTo( final Scale other )
    {
        return getName().compareTo( other.getName() );
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    @Override
    public boolean equals( final Object obj )
    {
        return obj instanceof Scale
               && getName().equals( ( (Scale) obj ).getName() );
    }
}
