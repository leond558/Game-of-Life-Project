package uk.ac.cam.ld558.oop.tick5;

import uk.ac.cam.ld558.oop.tick1.Pattern;
import uk.ac.cam.ld558.oop.tick2.ArrayWorld;
import uk.ac.cam.ld558.oop.tick2.PackedWorld;
import uk.ac.cam.ld558.oop.tick2.World;
import uk.ac.cam.ld558.oop.tick3.PatternFormatException;
import uk.ac.cam.ld558.oop.tick3.PatternStore;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GUILife extends JFrame implements ListSelectionListener {

    private ArrayList<World> cachedWorlds = new ArrayList<World>();
    private World world;
    private PatternStore store;
    private GamePanel gamePanel;
    private JButton playButton = new JButton("Play");
    //Timer enables continutees execution of the next generation methods and updating of the
    //presentation of game state on the GUI.
    private Timer timer = new Timer(true);
    //Boolean corresponding to whether the simulation is taking effect or is currently paused.
    private boolean playing;

    public GUILife(PatternStore ps) throws Exception {
        //Forming the GUI from which the Game of Life simulation will be executable.
        super("Game of Life");
        store=ps;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024,768);
        //Three fundamental components to the GUI: the panel for pattern selection, the panel for
        //controlling evolution of the simulation and the panel for simulation visualisation.
        add(createPatternsPanel(),BorderLayout.WEST);
        add(createControlPanel(),BorderLayout.SOUTH);
        add(createGamePanel(),BorderLayout.CENTER);
        gamePanel.display(world);
    }

    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border tb = BorderFactory.createTitledBorder(etch,title);
        component.setBorder(tb);
    }

    private JPanel createGamePanel() {
        //Initialises the game panel.
        this.gamePanel = new GamePanel();
        addBorder(gamePanel,"Game Panel");
        return gamePanel;
    }

    private JPanel createPatternsPanel() {
        //Initialises the patterns panel
        JPanel patt = new JPanel();
        addBorder(patt,"Patterns");
        patt.setLayout(new BorderLayout());
        //The patterns presented in the panel are taken from the pattern store.
        JList<Object> jnames = new JList<Object>(store.getPatternsNameSorted().toArray());
        //Creates a listener object that enables determination of which and whether a
        //pattern has been selected.
        jnames.addListSelectionListener(this);
        //Enables the list of patterns to be scorlled through.
        JScrollPane pattscroller = new JScrollPane(jnames);
        patt.add(pattscroller);
        return patt;
    }

    private JPanel createControlPanel() {
        /*
        Creates a dashboard like interface that enables custom evolution of the simulation,
        with monitoring facilitated by listener objects with overriden methods for when a
        button is selected such to put into effect the desired action.
         */
        JPanel ctrl =  new JPanel();
        addBorder(ctrl,"Controls");
        ctrl.setLayout(new GridLayout(1,3));
        JButton backButton = new JButton("<< Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveBack();
            }
        });
        JButton forwardButton = new JButton("Forward >>");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runOrPause();
            }
        });
        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    moveForward();
                    playing = true;
                    runOrPause();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        ctrl.add(backButton);
        ctrl.add(playButton);
        ctrl.add(forwardButton);
        return ctrl;
    }

    private void runOrPause() {
        //If the simulation playing and a button is pressed, it is important to interrupt
        //the simulation and conduct the required action. Maintaining knowledge of the current
        //state enables for such interrupts.
        if (playing) {
            timer.cancel();
            playing=false;
            //Alters the button from stop to play when the simulation.
            playButton.setText("Play");
        }
        else {
            playing=true;
            playButton.setText("Stop");
            //Initalises a new timer for the execution of the program.
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        moveForward();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 100);
        }
    }
    private void moveBack(){
        //Moving the simulation back a generation.
        //If the world is not initialised then request a pattern from the user.
        if (world == null)
            System.out.println("Please select a pattern to play.");
        else {
            //If the previous generation has in fact been visited in the past(which is inherent given a non-null world)
            //then present the world from the cache as to obviate against re-computation.
            if (world.getGenerationCount() > 0) {
                world = cachedWorlds.get(world.getGenerationCount() - 1);
            }
            gamePanel.display(world);
        }
        playing = true;
        runOrPause();
    }

    private void moveForward() throws Exception {
        if (world == null)
            System.out.println("Please select a pattern to play.");
        //If the generation has already been visited, extract from the cache. Otherwise, generate it.
        else {
            if (world.getGenerationCount() < cachedWorlds.size()-1) {
                world = cachedWorlds.get(world.getGenerationCount() + 1);
            } else {
                world = copyWorld(true);
                world.nextGeneration();
                cachedWorlds.add(world);
            }
            gamePanel.display(world);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        //In the even that the pattern selected in the patterns panel changes i.e., the user
        //desires to simulate a new pattern then initialise the new world accordingly.
        JList<Pattern> list = (JList<Pattern>) e.getSource();
        Pattern p = list.getSelectedValue();
        // Based on size, create either a long based implementation or an array based implementation
        // from the pattern.
        if (p.getHeight() * p.getWidth() <= 64) {
            try {
                world = new PackedWorld(p);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                world = new ArrayWorld(p);
            } catch (PatternFormatException e1) {
                e1.printStackTrace();
            }
        }
        //Clear the cache, set world and put it into the non-empty cache.
        //Display the new world on the GUI.
        cachedWorlds.clear();
        cachedWorlds.add(world);
        gamePanel.display(world);
        playing = true;
        runOrPause();
    }

    private World copyWorld(boolean useCloning) throws Exception {
        //Method that enables switching between copy constructors and clones for the cache.
        //Enables optimisation for efficiency purposes.
        if (!useCloning) {
            if (world instanceof PackedWorld) {
                return new PackedWorld((PackedWorld) world);
            }
            return new ArrayWorld((ArrayWorld) world);
        }
        return world.clone();
    }

    public static void main(String[] args) throws Exception {
        try {

            PatternStore ps = new PatternStore(""); //Resource containing patterns to be inserted here.
            GUILife gui = new GUILife(ps);
            gui.setVisible(true);
        }
        catch (IOException io){
            System.out.println("Failed to load a pattern store!");
        }
    }
}