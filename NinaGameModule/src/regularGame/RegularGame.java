package regularGame;

import gameBoard.NinaBoard;
import gameBoard.Participant;
import gameBoard.Turn;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RegularGame{
    public final int popoutGame = 0;
    public final int regularGame = 1;

    protected int N;
    protected NinaBoard gameBoard;
    protected boolean winnerFound = false;
    protected boolean isActive = false;
    protected boolean gameOver = false;
    protected List<Turn> turnHistory;
    protected List<Participant> allParticipants;
    protected Participant currentParticipant = null;
    protected static int noMove = -1;
    protected List<Integer> winners;

    public void takeParticipantTurn(int col, int turnType) {
        // implement the turn
        // presume the column is valid, and there is a possible move.
        Turn turnMade = implementTurn(col);

        checkForWinner(turnMade.getRow(), col, currentParticipant.getParticipantSymbol());

        if(!winnerFound) {
            changeCurrentParticipant();
            gameOver = (getPossibleColumn() == noMove);
        }
    }

    public boolean isCurrentParticipantBot() {
        return currentParticipant.getIsBot()/*.getValue()*/;
    }

    public boolean isWinnerFound() {
        return winnerFound;
    }

    public int getN() {
        return 0;
    }

    public boolean isActive() {
        return false;
    }


    public boolean moveIsValid(int col) {
        return !((getFirstOpenRow(col) < 0) || (gameBoard.getRows() < getFirstOpenRow(col)));
    }

    private List<Integer> getPossibleMoves() {
        LinkedList<Integer> possibleMoves = new LinkedList<>();
        int numOfCols = gameBoard.getCols();

        for(int i = 0; i < numOfCols; i++){
            if(moveIsValid(i)){
                possibleMoves.addLast(i);
            }
        }

        return possibleMoves;
    }

    protected int getPossibleColumn() {
        List<Integer> possibleMoves = getPossibleMoves();

        if (possibleMoves.size() == 0) {
            return noMove;
        } else {
            Random random = new Random();
            return possibleMoves.get(random.nextInt(possibleMoves.size()));
        }
    }

    protected int getFirstOpenRow(int column) {
        return gameBoard.getFirstOpenRow(column);
    }

    protected Turn implementTurn(int col) {
        int row = getFirstOpenRow(col);
        Turn currTurn = new Turn(row, col, currentParticipant.getParticipantSymbol(), Turn.addDisk);
        int currParticipantSymbol = currentParticipant.getParticipantSymbol();
        gameBoard.applyTurn(currTurn, currParticipantSymbol);
        turnHistory.add(currTurn);

        currentParticipant.addTurnPlayed();

        return currTurn;
    }

    protected void changeCurrentParticipant() {
    }

    protected void checkForWinner(int row, int col, int currParticipantSymbol){
        checkForWinningRow(row, col, currParticipantSymbol);
        if(!winnerFound){
            checkForWinningCol(row, col, currParticipantSymbol);
        }
        if(!winnerFound){
            checkForWinningAcrossLeft(row,col,currParticipantSymbol);
        }
        if(!winnerFound){
            checkForWinningAcrossRight(row,col,currParticipantSymbol);
        }
    }

    // direction: /
    protected void checkForWinningAcrossRight(int row, int col, int currParticipantSymbol) {
        int currStreak = 1;
        boolean keepLooking = true;
        int lastRow = gameBoard.getRows()-1;
        int lastCol = gameBoard.getCols()-1;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currRow < lastRow) && (currCol > 0) && (!winnerFound) && keepLooking){
            currRow++;
            currCol--;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currRow > 0) && (currCol < lastCol) && !winnerFound && keepLooking){
            currRow--;
            currCol++;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
            }
        }
    }

    // direction: \
    protected void checkForWinningAcrossLeft(int row, int col, int currParticipantSymbol) {
        int currStreak = 1;
        boolean keepLooking = true;
        int lastRow = gameBoard.getRows()-1;
        int lastCol = gameBoard.getCols()-1;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currRow < lastRow) && (currCol < lastCol) && (!winnerFound) && keepLooking){
            currRow++;
            currCol++;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currRow > 0) && (currCol > 0) && !winnerFound && keepLooking){
            currRow--;
            currCol--;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
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

    private void checkForWinningCol(int row, int col, int currParticipantSymbol) {
        int currStreak = 1;
        boolean keepLooking = true;
        int lastCol = gameBoard.getCols()-1;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currCol < lastCol) && (!winnerFound) && keepLooking){
            currCol++;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currCol > 0) && !winnerFound && keepLooking){
            currCol--;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
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

    private void checkForWinningRow(int row, int col, int currParticipantSymbol) {
        int currStreak = 1;
        int lastRow = gameBoard.getRows()-1;
        boolean keepLooking = true;

        // Check the first way starting from the starting cell
        int currRow = row;
        int currCol = col;
        while((currRow < lastRow) && (!winnerFound) && keepLooking){
            currRow++;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
                currStreak++;
            } else {
                keepLooking = false;
            }

            if(currStreak == N){
                winnerFound = true;
                keepLooking = false;
            }
        }

        // Get back to the starting cell, check the other way
        keepLooking = true;
        currRow = row;
        currCol = col;
        while((currRow > 0) && !winnerFound && keepLooking){
            currRow--;
            if(gameBoard.getTileSymbol(currRow,currCol) == currParticipantSymbol){
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

    public int[][] getBoard() {
        return gameBoard.getBoardTiles();
    }

    public String getCurrentParticipantName() {
        return currentParticipant.getName();
    }

    public int getCurrentParticipantTurnsPlayed() {
        return currentParticipant.getTurnsTaken();
    }

    public int getRows() {
        return gameBoard.getRows();
    }

    public int getCols() {
        return gameBoard.getCols();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isTurnHistoryEmpty() {
        return turnHistory.isEmpty();
    }

    public List<Integer> getTurnHistory() {
        LinkedList<Integer> resultingTurnHistory = new LinkedList<>();

        for (Turn turn : turnHistory) {
            resultingTurnHistory.addLast(turn.getCol());
        }

        return resultingTurnHistory;
    }

    public void takeBotTurn() {
        int column = getPossibleColumn();

        if (column == noMove) {
            gameOver = true;
        } else {
            implementTurn(column);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularGame that = (RegularGame) o;
        return  N == that.N &&
                winnerFound == that.winnerFound &&
                isActive == that.isActive &&
                gameOver == that.gameOver &&
                Objects.equals(gameBoard, that.gameBoard) &&
                Objects.equals(turnHistory, that.turnHistory) &&
                Objects.equals(allParticipants, that.allParticipants) &&
                Objects.equals(currentParticipant, that.currentParticipant);
    }

    public int hashCode() {
        return Objects.hash(N, gameBoard, winnerFound, isActive, gameOver, turnHistory, allParticipants, currentParticipant);
    }

    protected int getNumOfParticipants(){
        return allParticipants.size();
    }

    public int getGameType() {
        return regularGame;
    }


    public RegularGame(int n, ObservableList<Participant> allParticipants, int rows, int cols) {
        N = n;
        this.allParticipants = allParticipants;
        this.gameBoard = new NinaBoard(rows, cols);

    }


    public ObservableList<Participant> getParticipants() {
        return (ObservableList<Participant>) allParticipants;
    }
}
