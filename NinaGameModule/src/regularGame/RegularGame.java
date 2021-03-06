package regularGame;

import Exceptions.CantPopoutException;
import Exceptions.ColumnFullException;
import gameBoard.NinaBoard;
import gameBoard.Participant;
import gameBoard.Turn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RegularGame{
    public static final int popoutGame = 0;
    public static final int regularGame = 1;
    public static final int circularGame = 2;

    protected int N;
    protected NinaBoard gameBoard;
    protected boolean winnerFound = false;
    protected boolean isActive = false;
    protected List<Participant> allParticipants;
    protected List<Participant> originalParticipants;
    protected Participant currentParticipant = null;
    private int currentParticipantNumber = 0;
    protected static int noMove = -1;
    protected List<Participant> winners;


    public boolean isCurrentParticipantBot() {
        return currentParticipant.getIsBot()/*.getValue()*/;
    }

    public boolean isWinnerFound() {
        return winners.size() != 0;
    }

    public int getN() {
        return N;
    }

    public boolean moveIsValid(int col) {
        return !((getFirstOpenRow(col) < 0) || (gameBoard.getRows() < getFirstOpenRow(col)));
    }

    protected List<Integer> getPossibleMoves() {
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

        if (possibleMoves.isEmpty()) {
            return noMove;
        } else {
            Random random = new Random();
            return possibleMoves.get(random.nextInt(possibleMoves.size()));
        }
    }

    protected int getFirstOpenRow(int column) {
        return gameBoard.getFirstOpenRow(column);
    }

    protected Turn implementTurn(int col) throws ColumnFullException {
        Turn currTurn;
        if(!gameBoard.isColFull(col)){
            int row = getFirstOpenRow(col);
            currTurn = new Turn(row, col, currentParticipant.getParticipantSymbol(), Turn.addDisk);

            if(currTurn != null) {
                int currParticipantSymbol = currentParticipant.getParticipantSymbol();
                gameBoard.applyTurn(currTurn, currParticipantSymbol);
                currentParticipant.addTurnPlayed();
            }
        } else {
            throw new ColumnFullException();
        }
        return currTurn;
    }

    public void changeCurrentParticipant() {
        if(currentParticipantNumber == allParticipants.size()-1){
            currentParticipantNumber = 0;
        } else {
            currentParticipantNumber++;
        }

        currentParticipant = allParticipants.get(currentParticipantNumber);
    }

    protected void addWinner(int currentTile) {
        for(Participant participant : allParticipants){
            if(currentTile == participant.getParticipantSymbol()){
                if(!winners.contains(participant)) {
                    winners.add(participant);
                }
            }
        }
    }

    protected void checkForWinner(int row, int col, int currParticipantSymbol){
        checkForWinningRow(row, col, currParticipantSymbol);
        checkForWinningCol(row, col, currParticipantSymbol);
        checkForWinningAcrossLeft(row,col,currParticipantSymbol);
        checkForWinningAcrossRight(row,col,currParticipantSymbol);

        if(winnerFound){
            addWinner(currParticipantSymbol);
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

    public int getRows() {
        return gameBoard.getRows();
    }

    public int getCols() {
        return gameBoard.getCols();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularGame that = (RegularGame) o;
        return  N == that.N &&
                winnerFound == that.winnerFound &&
                isActive == that.isActive &&
                Objects.equals(gameBoard, that.gameBoard) &&
                Objects.equals(allParticipants, that.allParticipants) &&
                Objects.equals(currentParticipant, that.currentParticipant);
    }

    public int hashCode() {
        return Objects.hash(N, gameBoard, winnerFound, isActive, allParticipants, currentParticipant);
    }

    public int getGameType() {
        return regularGame;
    }

    public RegularGame(int n, ObservableList<Participant> allParticipants, int rows, int cols) {
        N = n;
        this.allParticipants = FXCollections.observableArrayList(allParticipants);
        this.gameBoard = new NinaBoard(rows, cols);
        currentParticipant = allParticipants.get(0);
        winners = new LinkedList<>();
        originalParticipants = FXCollections.observableArrayList(allParticipants);
    }

    public ObservableList<Participant> getParticipants() {
        return (ObservableList<Participant>) allParticipants;
    }

    public Turn getParticipantTurn(int col, int turnType) throws ColumnFullException, CantPopoutException {
        Turn turnMade = implementTurn(col);

        checkForWinner(turnMade.getRow(), col, currentParticipant.getParticipantSymbol());

        return turnMade;
    }

    public String getCurrentPlayerName() {
        return currentParticipant.getName();
    }

    public void clearGame() {
        winnerFound = false;
        gameBoard.clear();
        if(winners != null) {
            winners.clear();
        }
        for(Participant participant : allParticipants){
            participant.clearTurns();
        }
    }

    public boolean drawReached() {
        return getPossibleColumn() == noMove;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public Turn getBotTurn() {
        Turn turnMade = null;

        int col = getPossibleColumn();

        try {
            turnMade = implementTurn(col);
        } catch (ColumnFullException e) { }

        checkForWinner(turnMade.getRow(), col, currentParticipant.getParticipantSymbol());

        if (!winnerFound) {
            changeCurrentParticipant();
        }

        return turnMade;
    }

    public void setActive() {
        isActive = true;
    }

    public void deactivateGame() {
        isActive = false;
    }

    public void resetTurns() {
        allParticipants = FXCollections.observableArrayList(originalParticipants);
        currentParticipantNumber = 0;
        currentParticipant = allParticipants.get(currentParticipantNumber);
    }

    public int getCurrentPlayerSymbol() {
        return currentParticipant.getParticipantSymbol();
    }

    public int getTileSymbol(int row, int col) {
        return gameBoard.getTileSymbol(row,col);
    }

    public void removeTile(int row, int col) {
        gameBoard.popOutTile(row, col);
    }

    public void cascadeTiles(int row, int col) {
        boolean winnersFound = false;

        while((row > 0) && gameBoard.getTileSymbol(row-1, col) != NinaBoard.getEmptyTile()){
            int currentTile = gameBoard.getTileSymbol(row-1, col);
            gameBoard.dropTile(row-1, col);

            checkForWinner(row, col, currentTile);

            if(winnerFound){
                for(Participant participant : allParticipants){
                    if(currentTile == participant.getParticipantSymbol()){
                        if(!winners.contains(participant)) {
                            winners.add(participant);
                        }
                    }
                }
                winnersFound = true;
                winnerFound = false;
            }

            row--;
        }

        if(winnersFound){
            winnerFound = true;
        }
    }

    public void removeCurrentPlayer() {
        allParticipants.remove(currentParticipant);

        if(!allParticipants.isEmpty()) {
            currentParticipant = allParticipants.get(currentParticipantNumber);
        }
    }

    public ObservableList<Participant> getWinners(){
        return FXCollections.observableArrayList(winners);
    }
}
