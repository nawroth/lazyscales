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
package se.nawroth.scales.tuning;

import org.neo4j.graphdb.RelationshipType;

import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;

/**
 * {@link RelationshipType}s used by the graphdb.
 * 
 * @author Anders Nawroth
 */
enum TuningTypes implements RelationshipType
{
    /**
     * Points from a tuning family to a tuning.
     */
    TUNING,
    /**
     * A tuning family inside of a tuning family.
     */
    TUNING_FAMILY,
    /**
     * Entry point to the {@link TuningFamily}s.
     */
    TUNING_FAMILIES,
    /**
     * Points to the root note of a {@link Tuning}.
     */
    TUNING_ROOT_NOTE,
    /**
     * Points to the root for the intervals of a {@link Tuning}.
     */
    TUNING_INTERVAL_ROOT;
}
