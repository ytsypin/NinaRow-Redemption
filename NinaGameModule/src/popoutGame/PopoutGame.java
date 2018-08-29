package popoutGame;

import Exceptions.CantPopoutException;
import Exceptions.ColumnFullException;
import gameBoard.NinaBoard;
import gameBoard.Participant;
import gameBoard.Turn;
import javafx.collections.ObservableList;
import regularGame.RegularGame;

import java.util.ArrayList;

public class PopoutGame extends RegularGame {
    public PopoutGame(int n, ObservableList<Participant> readyParticipants, int rows, int cols) {
        super(n, readyParticipants, rows, cols);
    }

    @Override
    public void takeParticipantTurn(int col, int turnType) throws ColumnFullException {
        if(turnType == Turn.addDisk){
            super.takeParticipantTurn(col, turnType);
        } else {
//            takePopoutTurn(col);
        }
    }


    @Override
    public Turn getParticipantTurn(int col, int turnType) throws ColumnFullException, CantPopoutException {
        Turn turnMade;
        if(turnType == Turn.addDisk){
            turnMade = super.getParticipantTurn(col, turnType);
        } else {
            turnMade = getPopoutTurn(col);
        }

        if(turnMade == null){
            throw new CantPopoutException();
        } else {
            if(turnType == Turn.removeDisk) {
                boolean winnersFound = false;

                int currRow = turnMade.getRow()-2;

                while (gameBoard.getTileSymbol(currRow, col) != NinaBoard.getEmptyTile()) {
                    int currentTile = gameBoard.getTileSymbol(currRow,col);
                    gameBoard.dropTile(currRow, col);

                    checkForWinner(currRow, col, currentTile);

                    if (winnerFound) {
                        winners.add(gameBoard.getTileSymbol(currRow, col));
                        winnersFound = true;
                        winnerFound = false;
                    }
                    currRow--;
                }

                if (winnersFound) {
                    winnerFound = true;
                }
            } else {
                checkForWinner(turnMade.getRow(), col, currentParticipant.getParticipantSymbol());

                if (!winnerFound) {
                    gameOver = (getPossibleColumn() == noMove);
                }

            }
        }

        return turnMade;
    }

    private Turn getPopoutTurn(int col) {
        Turn turnMade = implementPopout(col);

        return turnMade;
    }


    private Turn implementPopout(int col) {
        Turn turnMade;
        if(gameBoard.getTileSymbol(gameBoard.getRows()-1,col) == currentParticipant.getParticipantSymbol()){
            gameBoard.popOutTile(col);

            turnMade = new Turn(gameBoard.getRows()-1, col, currentParticipant.getParticipantSymbol(), Turn.removeDisk);
        } else {
            turnMade = null;
        }

        return turnMade;
    }

    @Override
    public void takeBotTurn() {
        // TODO
    }

    @Override
    public int getGameType() {
        return super.popoutGame;
    }
}
