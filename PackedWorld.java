package uk.ac.cam.ld558.oop.tick2;

import uk.ac.cam.ld558.oop.tick1.Pattern;

import java.io.IOException;

public class PackedWorld extends World {
/*
Implementation of a world based on a long primitive type value, ideal for game configurations below an 8x8 board.
Live cells are characterised as a 1 in the long number and a dead cell as a 0.
 */

    private long world;

    public PackedWorld(Pattern x) throws Exception {
        super(x);
        // TODO: initialise world
        if (getPattern().getWidth() + getPattern().getHeight() > 16) {
            throw new Exception("The specified game board dimensions cannot be accommodated in this implementation of the Game of Life!");
        }
        getPattern().initialise(this);
    }
    public PackedWorld(String p) throws Exception {
        this(new Pattern(p));
    }
    public PackedWorld(PackedWorld pw) throws Exception{
        super(pw);
        this.world=pw.world;
    }

    @Override
    public boolean getCell(int col, int row) {
        //A cell value can be ascertained through conducting boolean logic
        //on the long. Shifting the long such that the cell in question's value
        //is the least significant bit then enables determination of whether
        //alive or dead.
        int position = col + getPattern().getHeight() * row;
        long check = (world & 1L << position) >>> position;
        return (check == 1);

    }

    @Override
    public void setCell(int col, int row, boolean value) {
        //Use of boolean logic for alteration of cell value.
        int position = col + getPattern().getHeight() * row;
        if (value) {
            world = world | 1L << position;
        } else {
            world = world & ~(1L << position);
        }
    }

    @Override
    protected void nextGenerationImpl() {
        //Computing the next generation of the Game of Life board
        //Cannot be done in place and hence a new long must be defined.
        //This is as altering any cells to update them would have implications
        //on the dead/alive characteristics of any yet unconsidered cells.
        long nextgen = 0L;
        for (int row = 0; row < getPattern().getHeight(); row++) {
            for (int col = 0; col < getPattern().getWidth(); col++) {
                if (computeCell(col, row)) {
                    int position = col + getPattern().getHeight() * row;
                    nextgen = nextgen | 1L << position;
                }
            }
        }
        world = nextgen;
    }

    @Override
    public PackedWorld clone() throws CloneNotSupportedException {
        PackedWorld clonepw = (PackedWorld)super.clone();
        clonepw.world = this.world;
        return clonepw;
    }

}


