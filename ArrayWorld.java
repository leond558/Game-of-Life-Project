package uk.ac.cam.ld558.oop.tick2;

import uk.ac.cam.ld558.oop.tick1.Pattern;
import uk.ac.cam.ld558.oop.tick3.PatternFormatException;

import java.lang.reflect.Array;

public class ArrayWorld extends World {
    //The world is stored as a boolean matrix where True corresponds to an alive cell.
    private boolean[][] world;
    //Rows composed of exclusively dead cells can all be represented by a reference
    //to an identical false array for memory efficiency purposes.
    private boolean[] deadRow;



    public ArrayWorld(Pattern p) throws PatternFormatException {
        //Initialisation of the array implementation of the world
        //provided some initial pattern
        //Calling the parent constructor of the general World class
        super(p);
        //Form the world
        world = new boolean[getHeight()][getWidth()];
        //Initalise the world given the initalise method in the pattern class
        getPattern().initialise(this);
        deadRow = new boolean[getPattern().getWidth()];
        //If a row is completely dead, maximise efficiency through passing a
        //reference to the general dead array
        for (int i = 0; i < world.length; i++) {
            boolean questiondead = true;
            for (int j = 0; j < world[i].length; j++) {
                if (world[i][j]) {
                    questiondead = false;
                }
            }
            if (questiondead) {

                world[i] = deadRow;
            }
        }
    }

    public ArrayWorld(String p) throws PatternFormatException {
        this(new Pattern(p));
    }

    public ArrayWorld(ArrayWorld aw) throws PatternFormatException {
        //Copy constructor for a deep clone.
        super(aw);
        world = new boolean[getPattern().getHeight()][getPattern().getWidth()];
        for (int i = 0; i < getPattern().getHeight(); i++) {
            if (aw.world[i] == aw.deadRow) {
                world[i] = aw.deadRow;
            }
            else {
                world[i] = aw.world[i].clone();
            }
        }
        deadRow = aw.deadRow;
        for (int i = 0; i < getPattern().getHeight(); i++) {
            System.out.println(world[i]);
        }
    }

    @Override
    public boolean getCell(int col, int row) {
        if (row < 0 || row >= getPattern().getHeight()) {
            return false;
        }
        if (col < 0 || col >= getPattern().getWidth()) {
            return false;
        }
        return world[row][col];
    }

    @Override
    public void setCell(int col, int row, boolean value) {
        if ((row > -1 && row < getPattern().getHeight()) && (col > -1 && col < getPattern().getWidth())) {
            world[row][col] = value;
        }
    }

    @Override
    protected void nextGenerationImpl() {
        //Update the world between successive generations through computing the truth value
        //of a subsequent cell given the Game of Life rules.
        boolean[][] nextGen = new boolean[world.length][];
        for (int y = 0; y < world.length; ++y) {
            nextGen[y] = new boolean[world[y].length];
            for (int x = 0; x < world[y].length; ++x) {
                boolean nextCell = computeCell(x, y);
                nextGen[y][x] = nextCell;
            }
        }
        world = nextGen;
    }

    @Override
    public ArrayWorld clone() throws CloneNotSupportedException {
        //Deep clone implementation.
        ArrayWorld cloneaw = (ArrayWorld) super.clone();
        boolean[][] worldd = new boolean[getPattern().getHeight()][getPattern().getWidth()];
        cloneaw.world = worldd;
        //The cloned world must have a reference to the dead array as opposed to that of its
        //original counterpart:
        for (int i = 0; i < getPattern().getHeight(); i++) {
            if (world[i] == deadRow) {
                worldd[i] = deadRow;
            }
            else {
                for (int j = 0; j < getPattern().getWidth() ; j++) {
                    worldd[i][j] = world[i][j];
                }
            }
        }
        return cloneaw;
    }

}
