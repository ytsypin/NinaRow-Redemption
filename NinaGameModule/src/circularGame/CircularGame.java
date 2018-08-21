package circularGame;

import gameBoard.Participant;
import gameBoard.Turn;
import javafx.collections.ObservableList;
import regularGame.RegularGame;

public class CircularGame extends RegularGame {

    private boolean winnerFound;

    public CircularGame(int n, ObservableList<Participant> readyParticipants, int rows, int cols) {
        super(n, readyParticipants, rows, cols);
    }

    @Override
    public void takeParticipantTurn(int col, int turnType) {
        Turn turnMade = implementTurn(col);

        checkForWinner(turnMade.getRow(), turnMade.getCol(), currentParticipant.getParticipantSymbol());
    }

    @Override
    public void checkForWinner(int row, int col, int currParticipantSymbol){
        checkForWinningAcrossLeft(row, col, currParticipantSymbol);
        if(!winnerFound) {
            checkForWinningAcrossRight(row, col, currParticipantSymbol);
        }
        if(!winnerFound){
            checkForWinningSpanningHorizontally(row, col, currParticipantSymbol);
        }
        if(!winnerFound){
            checkForWinningSpanningVertically(row, col, currParticipantSymbol);
        }
    }

    private void checkForWinningSpanningVertically(int row, int col, int currParticipantSymbol) {
        // upwards
        int currRow = row;
        int numOfRows = gameBoard.getRows();
        int currStreak = 1;
        boolean keepLooking = true;

        while(currRow > 0 && keepLooking){
            currRow--;
            if(gameBoard.getTileSymbol(currRow,col) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        if(currRow == 0){ // check circularly
            currRow = numOfRows;

            while(currRow > row+1 && keepLooking){
                currRow--;
                if(gameBoard.getTileSymbol(currRow,col) == currParticipantSymbol){
                    currStreak++;
                } else {
                    keepLooking = false;
                }

                if(currStreak == N){
                    winnerFound = true;
                    keepLooking = false;
                }

            }
        }

        // downward
        currRow = row;
        currStreak = 1;
        while(currRow < numOfRows-2 && keepLooking){
            currRow++;
            if(gameBoard.getTileSymbol(currRow,col) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        if(currRow == numOfRows-1) { // check circularly
            currRow = -1;
            while(currRow < row-1 && keepLooking){
                currRow++;
                if(gameBoard.getTileSymbol(currRow,col) == currParticipantSymbol){
                    currStreak++;
                } else {
                    keepLooking = false;
                }

                if(currStreak == N){
                    winnerFound = true;
                    keepLooking = false;
                }

            }
        }

    }

    private void checkForWinningSpanningHorizontally(int row, int col, int currParticipantSymbol) {
        // to the left
        int currCol = row;
        int numOfCols = gameBoard.getCols();
        int currStreak = 1;
        boolean keepLooking = true;
        while(currCol > 0 && keepLooking){
            currCol--;
            if(gameBoard.getTileSymbol(row,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        if(currCol == 0){ // need to look the other way
            currCol = numOfCols;
            while(currCol > col && keepLooking){
                currCol--;
                if(gameBoard.getTileSymbol(row,currCol) == currParticipantSymbol){
                    currStreak++;
                } else {
                    keepLooking = false;
                }

                if(currStreak == N){
                    winnerFound = true;
                }
            }
        }

        // to the right
        currCol = col;
        currStreak = 1;
        while(currCol < numOfCols-2 && keepLooking){
            currCol++;
            if(gameBoard.getTileSymbol(row,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        if(currCol == numOfCols-1){
            currCol = -1;
            while(currCol < col && keepLooking){
                currCol++;
                if(gameBoard.getTileSymbol(row,currCol) == currParticipantSymbol){
                    currStreak++;
                } else {
                    keepLooking = false;
                }

                if(currStreak == N){
                    winnerFound = true;
                    keepLooking = false;
                }
            }
        }
    }
}
