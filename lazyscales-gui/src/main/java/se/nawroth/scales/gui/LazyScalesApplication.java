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

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.miginfocom.swing.MigLayout;

import org.neo4j.kernel.impl.util.FileUtils;

import se.nawroth.scales.LazyScales;
import se.nawroth.scales.Services;
import se.nawroth.scales.api.LatinInterval;
import se.nawroth.scales.api.Note;
import se.nawroth.scales.api.Scale;
import se.nawroth.scales.api.ScaleFamily;
import se.nawroth.scales.api.Tuning;
import se.nawroth.scales.api.TuningFamily;
import se.nawroth.scales.note.Notes;

public class LazyScalesApplication
{
    private JFrame frmLazyscales;
    private LazyScales lazy;
    private JTree scaleFamilyTree;
    private Services services;
    private DefaultListModel scaleListData;
    private JComboBox noteCombo;
    private FretboardPanel fretboardPanel;
    private DefaultComboBoxModel noteComboData;
    private JRadioButton flatRadio;
    private JRadioButton sharpRadio;
    private JPanel sharpFlatPanel;
    private final Map<String, Note> noteMap = new HashMap<String, Note>( 12 );
    private JList scaleList;
    private JTree tuningFamilyTree;
    private JList tuningList;
    private DefaultListModel tuningListData;
    private Tuning previousTuning;
    private Note previousNote;
    private Scale previousScale;
    private JLabel scaleNotesLabel;
    private boolean previousIsFlat;
    private JCheckBox pinCheckBox;
    private JSlider fretsSlider;
    private JLabel pinnedScaleNotesLabel;

    /**
     * Launch the application.
     */
    public static void main( String[] args )
    {
        EventQueue.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    JFrame.setDefaultLookAndFeelDecorated( true );
                    JDialog.setDefaultLookAndFeelDecorated( true );
                    System.setProperty( "sun.awt.noerasebackground", "true" );
                    UIManager.setLookAndFeel( new LazySubstanceSkin() );
                    LazyScalesApplication window = new LazyScalesApplication();
                    window.frmLazyscales.setVisible( true );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * Create the application.
     */
    public LazyScalesApplication()
    {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmLazyscales = new JFrame();
        frmLazyscales.setIconImage( Toolkit.getDefaultToolkit()
                .getImage(
                        LazyScalesApplication.class.getResource( "/LazyIcon.png" ) ) );
        frmLazyscales.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                if ( lazy != null )
                {
                    lazy.stop();
                }
            }
        } );
        Package pckg = LazyScalesApplication.class.getPackage();
        String version = pckg.getImplementationVersion();
        if ( version == null )
        {
            version = "";
        }
        frmLazyscales.setTitle( "LazyScales " + version );
        frmLazyscales.setBounds( 100, 100, 971, 599 );
        frmLazyscales.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        String appdir = System.getProperty( "user.home" );
        if ( appdir == null || appdir.isEmpty() )
        {
            appdir = System.getProperty( "user.dir" );
        }

        File dir = new File( appdir + File.separator + ".LazyScales"
                             + File.separator + "db" );

        if ( dir.exists() )
        {
            try
            {
                FileUtils.deleteRecursively( dir );
            }
            catch ( IOException e1 )
            {
                e1.printStackTrace();
            }
        }

        lazy = new LazyScales( dir.getAbsolutePath() );
        lazy.start();
        lazy.init();
        services = lazy.getServices();
        DataImport.populateDb( services );
        initializeScaleFamilyTree();
        initializeScaleList();
        initializeTuningFamilyTree();
        initializeTuningList();
        initializeSharpFlat();
        initializeNoteCombo();

        // TODO this could need some cleanup
        TreeModel tuningData = tuningFamilyTree.getModel();
        Object[] tuningFamilyPath = new Object[2];
        tuningFamilyPath[0] = tuningData.getRoot();
        tuningFamilyPath[1] = tuningData.getChild( tuningFamilyPath[0], 0 );
        if ( ( (TuningFamily) ( (DefaultMutableTreeNode) tuningFamilyPath[1] ).getUserObject() ).getName()
                .equals( "Standard tunings" ) )
        {
            tuningFamilyTree.setSelectionPath( new TreePath( tuningFamilyPath ) );
        }
        tuningList.setSelectedIndex( 1 );

        scaleNotesLabel = new LazyLabel();
        frmLazyscales.getContentPane()
                .add( scaleNotesLabel, "cell 3 2 2 1" );

        pinnedScaleNotesLabel = new LazyLabel();
        pinnedScaleNotesLabel.setForeground( FretboardPanel.PINNED_NOTE_COLOR );
        frmLazyscales.getContentPane()
                .add( pinnedScaleNotesLabel, "cell 3 3 2 1" );

        fretboardPanel = new FretboardPanel( services );
        frmLazyscales.getContentPane()
                .add( fretboardPanel, "cell 1 4 4 1,shrinkx 0,grow" );

        fretsSlider = new JSlider();
        fretsSlider.setToolTipText( "Set the number of frets to use." );
        fretsSlider.setFont( Fonts.SANS.font( 12 ) );
        fretsSlider.addChangeListener( new ChangeListener()
        {
            @Override
            public void stateChanged( ChangeEvent ce )
            {
                fretboardPanel.setNumberOfFrets( fretsSlider.getValue() );
                fretboardPanel.repaint();
            }
        } );
        frmLazyscales.getContentPane()
                .add( fretsSlider, "cell 0 4,alignx center,growy" );
        fretsSlider.setValue( 16 );
        fretsSlider.setMinorTickSpacing( 1 );
        fretsSlider.setMaximum( 24 );
        fretsSlider.setMinimum( 6 );
        fretsSlider.setOrientation( SwingConstants.VERTICAL );

        pinCheckBox = new JCheckBox( "Pin" );
        pinCheckBox.setEnabled( false );
        pinCheckBox.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                if ( pinCheckBox.isSelected() )
                {
                    fretboardPanel.pinCurrentScale();
                    Notes pinned = fretboardPanel.getPinnedScale();
                    pinnedScaleNotesLabel.setText( pinned.toString() );
                }
                else
                {
                    fretboardPanel.unpinCurrentScale();
                    pinnedScaleNotesLabel.setText( "" );
                    if ( scaleList.getSelectedIndex() == -1
                         || tuningList.getSelectedIndex() == -1 )
                    {
                        pinCheckBox.setEnabled( false );
                    }
                }
                fretboardPanel.repaint();
            }
        } );
        pinCheckBox.setFont( Fonts.SANS.font( 12 ) );
        pinCheckBox.setVerticalAlignment( SwingConstants.BOTTOM );
        pinCheckBox.setHorizontalAlignment( SwingConstants.RIGHT );
        frmLazyscales.getContentPane()
                .add( pinCheckBox, "cell 2 2 1 2,alignx right,aligny bottom" );
    }

    private void initializeScaleFamilyTree()
    {
        ScaleFamily rootScaleFamily = services.getScaleFamilyService()
                .getRootScaleFamily();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                rootScaleFamily );
        addScaleFamilyChildren( rootNode );
        frmLazyscales.getContentPane()
                .setLayout(
                        new MigLayout(
                                "",
                                "[24.00][35.00][86.00][200:254.00,grow][200px:176.00,grow,shrinkprio 30]",
                                "[80px:n][160px:n][19.00][][240px:n,grow]" ) );
        scaleFamilyTree = new FamilyTree( rootNode );
        frmLazyscales.getContentPane()
                .add( scaleFamilyTree, "cell 0 0 3 2,grow" );
        scaleFamilyTree.addTreeSelectionListener( new TreeSelectionListener()
        {
            @Override
            public void valueChanged( TreeSelectionEvent tse )
            {
                TreePath newLeadSelectionPath = tse.getNewLeadSelectionPath();
                if ( newLeadSelectionPath != null )
                {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) newLeadSelectionPath.getLastPathComponent();
                    ScaleFamily scaleFamily = (ScaleFamily) node.getUserObject();
                    updateScaleList( scaleFamily );
                }
            }
        } );
    }

    private void initializeTuningFamilyTree()
    {
        TuningFamily rootTuningFamily = services.getTuningFamilyService()
                .getRootTuningFamily();
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(
                rootTuningFamily );
        addTuningFamilyChildren( rootNode );
        tuningFamilyTree = new FamilyTree( rootNode );
        frmLazyscales.getContentPane()
                .add( tuningFamilyTree, "cell 4 0,grow" );
        tuningFamilyTree.addTreeSelectionListener( new TreeSelectionListener()
        {
            @Override
            public void valueChanged( TreeSelectionEvent tse )
            {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getNewLeadSelectionPath()
                        .getLastPathComponent();
                TuningFamily tuningFamily = (TuningFamily) node.getUserObject();
                updateTuningList( tuningFamily );
            }
        } );
    }

    private void updateScaleList( ScaleFamily scaleFamily )
    {
        scaleListData.clear();
        for ( Scale scale : scaleFamily.getScales() )
        {
            scaleListData.addElement( scale );
        }
        if ( scaleListData.size() > 0 )
        {
            scaleList.setSelectedIndex( 0 );
        }
        else
        {
            scaleNotesLabel.setText( "" );
            if ( !pinCheckBox.isSelected() )
            {
                pinCheckBox.setEnabled( false );
            }
        }
        scaleList.requestFocus();
    }

    private void updateTuningList( TuningFamily tuningFamily )
    {
        tuningListData.clear();
        for ( Tuning tuning : tuningFamily.getTunings() )
        {
            tuningListData.addElement( tuning );
        }
        if ( tuningListData.size() > 0 )
        {
            tuningList.setSelectedIndex( 0 );
        }
    }

    private void initializeScaleList()
    {
        scaleListData = new DefaultListModel();
        scaleList = new MonoList( scaleListData );
        scaleList.addListSelectionListener( new ListSelectionListener()
        {
            @Override
            public void valueChanged( ListSelectionEvent selEvent )
            {
                scaleOrNoteOrTuningOrSignChange();
            }
        } );
        frmLazyscales.getContentPane()
                .add( scaleList, "cell 3 0 1 2,grow" );
    }

    private void scaleOrNoteOrTuningOrSignChange()
    {
        int selectedScaleIndex = scaleList.getSelectedIndex();
        if ( selectedScaleIndex == -1 )
        {
            return;
        }
        Scale scale = (Scale) scaleListData.elementAt( selectedScaleIndex );

        int selectedNoteIndex = noteCombo.getSelectedIndex();
        if ( selectedNoteIndex == -1 )
        {
            return;
        }
        Note startingNote = noteMap.get( noteCombo.getItemAt( selectedNoteIndex ) );

        boolean isFlat = flatRadio.isSelected();
        Notes scaleNotes = Notes.notes( startingNote, scale );
        if ( isFlat )
        {
            scaleNotes.setFlat();
        }
        else
        {
            scaleNotes.setSharp();
        }

        scaleNotesLabel.setText( scaleNotes.toString() );

        int selectedTuningIndex = tuningList.getSelectedIndex();
        if ( selectedTuningIndex == -1 )
        {
            return;
        }
        Tuning tuning = (Tuning) tuningListData.elementAt( selectedTuningIndex );

        if ( scale.equals( previousScale )
             && startingNote.equals( previousNote )
             && tuning.equals( previousTuning ) && isFlat == previousIsFlat )
        {
            return;
        }

        previousTuning = tuning;
        previousNote = startingNote;
        previousScale = scale;
        previousIsFlat = isFlat;

        fretboardPanel.setTuning( tuning );
        fretboardPanel.setNotes( scaleNotes );
        fretboardPanel.repaint();
        pinCheckBox.setEnabled( true );
    }

    private void initializeTuningList()
    {
        tuningListData = new DefaultListModel();
        tuningList = new MonoList( tuningListData );
        frmLazyscales.getContentPane()
                .add( tuningList, "cell 4 1,grow" );
        tuningList.addListSelectionListener( new ListSelectionListener()
        {
            @Override
            public void valueChanged( ListSelectionEvent selEvent )
            {
                scaleOrNoteOrTuningOrSignChange();
            }
        } );
    }

    private void initializeSharpFlat()
    {
        ButtonGroup sharpFlatGroup = new ButtonGroup();
        sharpFlatPanel = new JPanel();
        sharpFlatPanel.setBorder( UIManager.getBorder( "Tree.editorBorder" ) );

        frmLazyscales.getContentPane()
                .add( sharpFlatPanel,
                        "flowx,cell 0 2 2 2,alignx left,aligny bottom" );
        flatRadio = new JRadioButton( "♭" );
        flatRadio.setFont( Fonts.SANS.font( 16 ) );
        sharpFlatPanel.add( flatRadio );
        sharpFlatGroup.add( flatRadio );
        sharpRadio = new JRadioButton( "♯" );
        sharpRadio.setFont( Fonts.SANS.font( 16 ) );
        sharpFlatPanel.add( sharpRadio );
        sharpFlatGroup.add( sharpRadio );
        sharpFlatGroup.setSelected( flatRadio.getModel(), true );
        flatRadio.addChangeListener( new ChangeListener()
        {
            @Override
            public void stateChanged( ChangeEvent e )
            {
                sharpFlatUpdated();
            }
        } );
    }

    private void initializeNoteCombo()
    {
        noteComboData = new DefaultComboBoxModel();
        noteCombo = new JComboBox( noteComboData );
        noteCombo.setFont( Fonts.SANS.font( 16 ) );
        noteCombo.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                scaleOrNoteOrTuningOrSignChange();
            }
        } );

        frmLazyscales.getContentPane()
                .add( noteCombo, "flowx,cell 2 2 1 2,alignx left,aligny bottom" );
        refreshNoteCombo();
    }

    private void sharpFlatUpdated()
    {
        refreshNoteCombo();
        scaleOrNoteOrTuningOrSignChange();
    }

    private void refreshNoteCombo()
    {
        int selectedIndex = noteCombo.getSelectedIndex();
        noteComboData.removeAllElements();
        Note note = services.getNoteService()
                .referenceNote();
        Note currentNote = note;
        noteMap.clear();
        do
        {
            String name = noteName( currentNote );
            noteMap.put( name, currentNote );
            noteComboData.addElement( name );
            currentNote = currentNote.getFromInterval( LatinInterval.SEMITONE );
        }
        while ( !currentNote.equals( note ) );
        if ( selectedIndex != -1 )
        {
            noteCombo.setSelectedIndex( selectedIndex );
        }
    }

    private String noteName( final Note note )
    {
        return note.toString( flatRadio.isSelected() );
    }

    private <T> void addTuningFamilyChildren( DefaultMutableTreeNode rootNode )
    {
        TuningFamily family = (TuningFamily) rootNode.getUserObject();
        for ( TuningFamily child : family.getSubFamilies() )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( child );
            rootNode.add( node );
            addTuningFamilyChildren( node );
        }
    }

    private void addScaleFamilyChildren( DefaultMutableTreeNode rootNode )
    {
        ScaleFamily family = (ScaleFamily) rootNode.getUserObject();
        for ( ScaleFamily child : family.getSubFamilies() )
        {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode( child );
            rootNode.add( node );
            addScaleFamilyChildren( node );
        }
    }
}
