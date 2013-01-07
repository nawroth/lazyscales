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
package se.nawroth.scales.api;

import se.nawroth.scales.util.NamedEntity;

/**
 * Group {@link Tuning}s together.
 * 
 * @author Anders Nawroth
 */
public interface TuningFamily extends NamedEntity<TuningFamily>
{
    /**
     * Get families beloning to this family.
     * 
     * @return the subfamilies.
     */
    Iterable<TuningFamily> getSubFamilies();

    /**
     * Get the tuning belonging to this family.
     * 
     * @return the tunings of this family
     */
    Iterable<Tuning> getTunings();

    /**
     * Add a family to this.
     * 
     * @param family the family to add
     * @return <code>true</code> if the family was added, <code>false</code> if
     *         it already belonged to this family
     */
    boolean addSubFamily( final TuningFamily family );

    /**
     * Add a Tuning to this family and give it a name.
     * 
     * @param tuning the tuning to add
     * @param name the name of the tuning
     * @return <code>true</code> if the tuning was added, <code>false</code> if
     *         it already belonged to this family.
     */
    boolean addTuning( final Tuning tuning, final String name );
}
