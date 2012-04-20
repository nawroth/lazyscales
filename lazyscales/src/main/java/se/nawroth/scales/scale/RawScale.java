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
package se.nawroth.scales.scale;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import se.nawroth.scales.api.Scale;
import se.nawroth.scales.util.CategoryUtil;
import se.nawroth.scales.util.Entity;

final class RawScale
{
    enum RawScales implements RelationshipType
    {
        ONE_TONE,
        TWO_TONE,
        THREE_TONE,
        FOUR_TONE,
        PENTATOINC,
        HEXATONIC,
        HEPTATONIC,
        OCTATONIC,
        NINE_TONE,
        TEN_TONE,
        ELEVEN_TONE,
        CHROMATIC;

        private static final RawScales[] RAW_SCALES = RawScales.values();

        static RawScales getFromTones( final int tones )
        {
            return RAW_SCALES[tones % RAW_SCALES.length];
        }

    }

    static final class RawScaleEntity implements Entity
    {
        private final Node node;

        RawScaleEntity( final Node node )
        {
            this.node = node;
        }

        @Override
        public Node getUnderlyingNode()
        {
            return node;
        }
    }

    static final CategoryUtil<RawScaleEntity, Scale> NAVIGATION;

    static
    {
        NAVIGATION = new CategoryUtil<RawScaleEntity, Scale>(
                RawTypes.RAW_SCALES, RawTypes.RAW_SCALE )
        {
            @Override
            protected Scale itemFromNode( final Node node )
            {
                return new ScaleImpl( node );
            }

            @Override
            protected RawScaleEntity categoryFromNode( final Node node )
            {
                return new RawScaleEntity( node );
            }
        };
    }

    private RawScale()
    {
        // no instantiation
    }
}
