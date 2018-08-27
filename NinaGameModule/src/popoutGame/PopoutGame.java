package popoutGame;

import Exceptions.ColumnFullException;
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
    public Turn getParticipantTurn(int col, int turnType) {
        Turn turnMade;
        if(turnType == Turn.addDisk){
            turnMade = getParticipantTurn(col, turnType);
        } else {
            turnMade = getPopoutTurn(col);
        }

        if(turnMade != null) {
            checkForWinner(turnMade.getRow(), col, currentParticipant.getParticipantSymbol());

            if (!winnerFound) {
                changeCurrentParticipant();
                gameOver = (getPossibleColumn() == noMove);

            }
        }

        return turnMade;
    }

    private Turn getPopoutTurn(int col) {
        Turn turnMade = implementPopout(col);

        if(turnMade != null) { // turn was actually made
            // while there are tiles that can drop
            int currRow = gameBoard.getRows() -2;
            while(gameBoard.getTileSymbol(currRow,col) != gameBoard.getEmptyTile()){
                winners = new ArrayList<>();
                // drop a tile, check for winner
                // keep list of winners, in case of multi-way tie
                gameBoard.dropTile(currRow, col);
                checkForWinner(currRow+1,col,gameBoard.getTileSymbol(currRow+1,col));

                // with each one check for winner
                if(winnerFound){
                    winners.add(gameBoard.getTileSymbol(currRow+1,col));
                    winnerFound = false;
                }
            }

            if (winners.isEmpty()) {
                changeCurrentParticipant();
                //gameOver = (getPossibleColumn() == noMove);
            } else {
                gameOver = true;
            }
        } else {
            // turn was not made, couldn't pop out
        }

        return turnMade;
    }


//    private void takePopoutTurn(int col) {
//        Turn turnMade = implementPopout(col);
//
//        if(turnMade != null) { // turn was actually made
//            // while there are tiles that can drop
//            int currRow = gameBoard.getRows() -2;
//            while(gameBoard.getTileSymbol(currRow,col) != gameBoard.getEmptyTile()){
//                winners = new ArrayList<>();
//                // drop a tile, check for winner
//                // keep list of winners, in case of multi-way tie
//                gameBoard.dropTile(currRow, col);
//                checkForWinner(currRow+1,col,gameBoard.getTileSymbol(currRow+1,col));
//
//                // with each one check for winner
//                if(winnerFound){
//                    winners.add(gameBoard.getTileSymbol(currRow+1,col));
//                    winnerFound = false;
//                }
//            }
//
//            if (winners.size() == 0) {
//                changeCurrentParticipant();
//                //gameOver = (getPossibleColumn() == noMove);
//            } else {
//                gameOver = true;
//            }
//        } else {
//            // turn was not made, couldn't pop out
//        }
//    }

    private Turn implementPopout(int col) {
        Turn turnMade;
        if(gameBoard.getTileSymbol(gameBoard.getRows()-1,col) == currentParticipant.getParticipantSymbol()){
            gameBoard.popOutTile(col);

            turnMade = new Turn(gameBoard.getRows()-1, col, currentParticipant.getParticipantSymbol(), Turn.removeDisk);
        } else {
            // can't popout under this selection
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
