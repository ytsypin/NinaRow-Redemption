package Exceptions;

public class InvalidNumberOfColsException extends Throwable {
    private int colValue;
    public InvalidNumberOfColsException(int cols) {
        colValue = cols;
    }
}
