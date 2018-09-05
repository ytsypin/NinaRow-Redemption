package popoutGame;

import Exceptions.CantPopoutException;
import Exceptions.ColumnFullException;
import gameBoard.NinaBoard;
import gameBoard.Participant;
import gameBoard.Turn;
import javafx.collections.ObservableList;
import regularGame.RegularGame;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class PopoutGame extends RegularGame {
    public PopoutGame(int n, ObservableList<Participant> readyParticipants, int rows, int cols) {
        super(n, readyParticipants, rows, cols);
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
            checkPopoutWinners(col, turnType, turnMade);
        }

        return turnMade;
    }

    private void checkPopoutWinners(int col, int turnType, Turn turnMade) {
        if(turnType == Turn.removeDisk) {
            boolean winnersFound = false;

            int currRow = turnMade.getRow();

            while (currRow > 0 && gameBoard.getTileSymbol(currRow-1, col) != NinaBoard.getEmptyTile()) {
                int currentTile = gameBoard.getTileSymbol(currRow-1,col);
                gameBoard.dropTile(currRow-1, col);

                checkForWinner(currRow, col, currentTile);

                if (winnerFound) {
                    addWinner(currentTile);
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
        }
    }

    private Turn getPopoutTurn(int col) {
        Turn turnMade = implementPopout(col);

        return turnMade;
    }

    private Turn implementPopout(int col) {
        Turn turnMade;
        if(gameBoard.getTileSymbol(gameBoard.getRows()-1,col) == currentParticipant.getParticipantSymbol()){
            gameBoard.popOutTile(gameBoard.getRows()-1, col);

            turnMade = new Turn(gameBoard.getRows()-1, col, currentParticipant.getParticipantSymbol(), Turn.removeDisk);
        } else {
            turnMade = null;
        }

        if(turnMade != null) {
            currentParticipant.addTurnPlayed();
        }

        return turnMade;
    }

    @Override
    public Turn getBotTurn() {
        Turn turnMade = null;

        List<Integer> regularMoves = getPossibleMoves();
        List<Integer> popoutMoves = getPossiblePopoutMoves();

        Random random = new Random();
        int randomDigit = random.nextInt(regularMoves.size() + popoutMoves.size());

        int turnType;
        int col;

        if(randomDigit >= regularMoves.size()){
            turnType = Turn.removeDisk;
            randomDigit = randomDigit-regularMoves.size();
            col = popoutMoves.get(randomDigit);
        } else {
            turnType = Turn.addDisk;
            col = regularMoves.get(randomDigit);
        }

        try {
            if(turnType == Turn.addDisk){
                try {
                    turnMade = super.getParticipantTurn(col, turnType);
                } catch (CantPopoutException e) { }
            } else {
                turnMade = getPopoutTurn(col);
            }
        } catch (ColumnFullException e) { }


        checkPopoutWinners(col, turnType, turnMade);

        if (!winnerFound) {
            changeCurrentParticipant();
        }

        return turnMade;
    }

    private List<Integer> getPossiblePopoutMoves() {
        LinkedList<Integer> possiblePopoutTurns = new LinkedList<>();

        int numOfCols = gameBoard.getCols();

        for(int i = 0; i < numOfCols; i++){
            if(popoutMoveIsValid(i)){
                possiblePopoutTurns.add(i);
            }
        }

        return possiblePopoutTurns;
    }

    private boolean popoutMoveIsValid(int col) {
        if(gameBoard.getTileSymbol(gameBoard.getRows()-1, col) == currentParticipant.getParticipantSymbol()){
            return true;
        }

        return false;
    }

    @Override
    public int getGameType() {
        return popoutGame;
    }

    @Override
    public boolean drawReached() {
        return noPopoutMovesLeft() && super.drawReached();
    }

    private boolean noPopoutMovesLeft() {
        int cols = gameBoard.getCols();
        int lastRow = gameBoard.getRows()-1;
        boolean popoutNotPossible = true;

        for(int i = 0; i < cols && popoutNotPossible; i++){
            if(gameBoard.getTileSymbol(lastRow, i) == currentParticipant.getParticipantSymbol()){
                popoutNotPossible = false;
            }
        }

        return popoutNotPossible;
    }
}
