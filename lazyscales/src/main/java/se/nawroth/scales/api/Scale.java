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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import se.nawroth.scales.util.NamedEntity;

/**
 * A scale, built from a series of intervals.
 * 
 * @author Anders Nawroth
 */
public interface Scale extends NamedEntity<Scale>, Collection<NamedInterval>
{
    /**
     * Get a circular {@link Iterator} over this {@link Scale}.
     * 
     * @return a circular iterator
     */
    Iterator<NamedInterval> circularIterator();

    /**
     * The scale as intervals relative to the tonic.
     * 
     * @return absolute intervals of the scale
     */
    List<NamedInterval> asAbsoluteList();
}
