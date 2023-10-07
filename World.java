package uk.ac.cam.ld558.oop.tick2;

import uk.ac.cam.ld558.oop.tick1.Pattern;
import uk.ac.cam.ld558.oop.tick3.PatternFormatException;

import java.io.IOException;

public abstract class World implements Cloneable{
    private int generation;
    private Pattern pattern;

    //The following methods must be provided with a concrete implementation
    //dependent on whether an array or long based Game of Life configuration
    //is preferable.
    public abstract boolean getCell(int col, int row);
    public abstract void setCell(int col, int row, boolean value);
    protected abstract void nextGenerationImpl();

    public int getGenerationCount(){
        return generation;
    }

    public void nextGeneration(){
        nextGenerationImpl();
        generation++;
    }

    protected void incrementGenerationCount() {
        generation++;
    }
    protected Pattern getPattern(){
        return pattern;
    }
    public int getHeight() {
        return pattern.getHeight();
    }

    public int getWidth() {
        return pattern.getWidth();
    }

    protected int countNeighbours(int col, int row) {
        int NeighbourCount = 0;
        if ((row > -1 && row < getHeight()) && (col > -1 && col < getWidth())) {
            for (int i = row - 1; i < row + 2; i++) {
                if (i < getHeight() && i > -1) {
                    for (int j = col - 1; j < col + 2; j++) {
                        if (j < getWidth() && j > -1) {
                            if (getCell(j, i)) {
                                NeighbourCount++;
                            }
                        }
                    }
                }
            }
        }
        if (getCell(col, row)) {
            NeighbourCount -= 1;
        }
        return (NeighbourCount);
    }
    protected boolean computeCell(int col, int row){
        // liveCell is true if the cell at position (col,row) in world is live
        boolean liveCell = getCell(col, row);

        // neighbours is the number of live neighbours to cell (col,row)
        int neighbours = countNeighbours(col, row);

        // we will return this value at the end of the method to indicate whether
        // cell (col,row) should be live in the next generation
        //A live cell with less than two neighbours dies (underpopulation)
        //A live cell with two or three neighbours lives (a balanced population)
        //A live cell with with more than three neighbours dies (overcrowding)
        boolean nextCell = false;
        if (liveCell) {
            nextCell = (neighbours < 2 | neighbours > 3) ? false : true;
        }
        //A dead cell with exactly three live neighbours comes alive
        if (liveCell == false & neighbours == 3) {
            nextCell = true;
        }
        return nextCell;
    }


    public World(Pattern x) throws PatternFormatException {
        pattern = x;
        generation = 0;
    }

    public World(World w){
        pattern = w.getPattern();
        generation = w.getGenerationCount();
    }
    @Override
    public World clone() throws CloneNotSupportedException {
            World clonedw = (World) super.clone();
            clonedw.pattern = getPattern();
            clonedw.generation = generation;
            return clonedw;

    }

}
