package Exceptions;

public class InvalidNumberOfPlayersException extends Throwable {
    private int numberOfPlayers;

    public InvalidNumberOfPlayersException(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
}
