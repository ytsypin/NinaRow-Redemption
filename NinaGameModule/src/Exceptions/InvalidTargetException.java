package Exceptions;

public class InvalidTargetException extends Throwable {
    private int nValue;
    public InvalidTargetException(int n) {
        nValue = n;
    }

    public int getnValue() {
        return nValue;
    }
}
