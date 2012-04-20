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
package se.nawroth.scales.gui;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

public class MonoList extends JList
{

    public MonoList( ListModel dataModel )
    {
        super( dataModel );
        initLooks();
    }

    private void initLooks()
    {
        setFont( Fonts.MONO.font( 14 ) );
        getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION );
        setBorder( UIManager.getBorder( "Tree.editorBorder" ) );
    }
}
