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

import se.nawroth.scales.util.NamedEntity;

/**
 * Group chords together in a hierarchy.
 * 
 * @author Anders Nawroth
 */
public interface ChordFamily extends NamedEntity<ChordFamily>
{
    /***
     * Get all families belonging to this family.
     * 
     * @return all subfamilies
     */
    Iterable<ChordFamily> getSubFamilies();

    /**
     * Get all {@link Chord}s belonging to this family.
     * 
     * @return scales in this family
     */
    Iterable<Chord> getChords();

    /**
     * Add a new family as belonging to this.
     * 
     * @param family a family that should be owned by this.
     * @return <code>true</code> if the family was added, <code>false</code> if
     *         it already was a subfamily of this family.
     */
    boolean addSubFamily( final ChordFamily family );

    /**
     * Add a new scale as belonging to this.
     * 
     * @param chord a chord that should be owned by this family.
     * @return <code>true</code> if the chord was added, <code>false</code> if
     *         it already was a chord included in this family.
     */
    boolean addChord( final Chord chord );
}
