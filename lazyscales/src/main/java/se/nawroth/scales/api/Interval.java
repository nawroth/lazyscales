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
package se.nawroth.scales.api;

import org.neo4j.graphdb.RelationshipType;

/**
 * Defines an interval representation that includes the number of semitones and
 * a corresponding {@link RelationshipType} used in the database.
 * 
 * @author Anders Nawroth
 */
public interface Interval extends RelationshipType
{
    /**
     * The number of semitones this {@link Interval} has.
     * 
     * @return the number of semitones
     */
    int getSemitones();

    /**
     * Compare {@link Interval}s in a safe way.
     * 
     * @param other the other {@link Interval}
     * @return <code>true</code> if both {@link Interval}s have the same number
     *         of semitones.
     */
    boolean equivalent( Interval other );
}
