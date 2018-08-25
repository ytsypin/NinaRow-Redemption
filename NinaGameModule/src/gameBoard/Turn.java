package gameBoard;

import java.io.Serializable;

public class Turn implements Serializable {
    public static int addDisk = 0;
    public static int removeDisk = 1;

    private int participantSymbol;
    private int row;
    private int col;
    private int turnType;

    public Turn(int row, int col, int participantSymbol, int turnType){
        this.row = row;
        this.col = col;
        this.participantSymbol = participantSymbol;
        this.turnType = turnType;
    }

    public int getCol() {
        return col;
    }

    public int getRow() { return row; }

    public int getTurnType() {
        return turnType;
    }

    public int getParticipantSymbol(){
        return participantSymbol;
    }
}
