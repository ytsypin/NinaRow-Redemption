package Exceptions;

public class IDDuplicateException extends Throwable {
    private short idNum;

    public IDDuplicateException(short idNum) {
        this.idNum = idNum;
    }
}
