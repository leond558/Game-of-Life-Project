package uk.ac.cam.ld558.oop.tick3;
/*
An exception to be thrown in the instance that the string provided for Game of Life
initialisation is not in the desired format compatible with thde program.
 */
public class PatternFormatException extends Exception {
    public PatternFormatException(String msg) {
        super(msg);
    }
}