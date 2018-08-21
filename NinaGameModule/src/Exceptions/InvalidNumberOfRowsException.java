package Exceptions;

public class InvalidNumberOfRowsException extends Throwable {
    private int rowValue;
    public InvalidNumberOfRowsException(int rows) {
        rowValue = rows;
    }
}
