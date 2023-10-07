package uk.ac.cam.ld558.oop.tick1;


import uk.ac.cam.ld558.oop.tick2.World;
import uk.ac.cam.ld558.oop.tick3.PatternFormatException;

import java.util.Arrays;

public class Pattern implements Comparable<Pattern> {

    private String name;
    private String author;
    private int width;
    private int height;
    private int startCol;
    private int startRow;
    private String cells;

    /*
    Declaration of necessary get methods for the above state corresponding to parameters
    necessary for the initialisation of a Game of Life configuration. Necessary for the
    espousing of information encapsulation to enable refactoring of classes without
    requiring alteration to fundamental underlying implementations or code. Minimise
    coupling/maximise immutability of state.
     */
    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public String getCells() {
        return cells;
    }


    public Pattern(String format) throws PatternFormatException {
        /*
        Constructor to intialise all fields provided some intial string input. Must be in a compatible format,
        otherwise throw an excpetion indicating that the expected format has been circumvented.
         */
        if (format==""){
            throw new PatternFormatException("Please specify a pattern");
        }
        String[] formatdecomp = format.split(":");
        if(formatdecomp.length!=7){
            throw new PatternFormatException(String.format("Invalid pattern format: Incorrect number of fields in pattern (found %d).",formatdecomp.length));
        }
            name = formatdecomp[0];
            author = formatdecomp[1];
            try {
                width = Integer.parseInt(formatdecomp[2]);
            }
            catch(NumberFormatException nfe) {
                throw new PatternFormatException(String.format("Invalid pattern format: Could not interpret the width field as a number ('%s' given).",formatdecomp[2]));
            }
            try {
                height = Integer.parseInt(formatdecomp[3]);
            }
            catch(NumberFormatException nfe) {
                throw new PatternFormatException(String.format("Invalid pattern format: Could not interpret the height field as a number ('%s' given).",formatdecomp[3]));
            }
            try {
                startCol = Integer.parseInt(formatdecomp[4]);
            }
            catch(NumberFormatException nfe) {
                throw new PatternFormatException(String.format("Invalid pattern format: Could not interpret the startX field as a number ('%s' given).",formatdecomp[4]));
            }
            try {
                startRow = Integer.parseInt(formatdecomp[5]);
            }
            catch(NumberFormatException nfe) {
                throw new PatternFormatException(String.format("Invalid pattern format: Could not interpret the startY field as a number ('%s' given).",formatdecomp[5]));
            }
            cells = formatdecomp[6];
        }


    public void initialise(World world) throws PatternFormatException {
        /*
        Initalise the word configuration of dead and alive cells provided the specification of initial live cells
        and the dimensions of the game board.
         */
        String[] cellsar = cells.split(" ");
        if(cellsar.length>=height-startCol+1){
            throw new PatternFormatException(String.format("Invalid pattern format: Malformed pattern '%s'", cells));
        }
        for (int i = 0; i < cellsar.length; i++) { //for each row of cells in the subset of the game board with cells that are alive
            if(cellsar[i].length()>width-startRow+1) {
                throw new PatternFormatException(String.format("Invalid pattern format: Malformed pattern '%s'", cells));
            }
            char[] subarr = cellsar[i].toCharArray(); //convert that row into a character array containing the 0 and 1 elements indicative of dead/alive
            for (int j = 0; j < subarr.length; j++) {//for each element in this character array of dead and alive cells
                if (subarr[j]!='1' && subarr[j]!='0'){
                    throw new PatternFormatException(String.format("Invalid pattern format: Malformed pattern '%s'", cells));
                }
                if (subarr[j] == '1') {//if the element is the character '1'
                    world.setCell(startCol + i ,startRow + j,true);//set the element in the world array that it is at a row equal to that of the subset initiation and after how many spaces it features in the subset(indicative of row and a column equal to the subset intiation add to its position along the character array to alive
                } else continue;
            }
        }

    }

    @Override
    public String toString(){
        //For debugging purposes.
        return (getName()+ " (" + getAuthor() + ")");
    }

    @Override
    public int compareTo(Pattern o) {
        /*
        Designate a new natural ordering as by alphabetical consideratoin of author of the given Game of Life's
        configuration na,e.
         */
        return name.compareTo(o.getName());
    }

}