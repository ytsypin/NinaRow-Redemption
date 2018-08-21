package ReplayMechanism;

import gameBoard.NinaBoard;
import gameBoard.Turn;

import java.util.Iterator;
import java.util.List;

public class Replay {
    private List<Turn> turnList;
    private NinaBoard gameBoard;
    private int currentTurnNumber;
    private int numOfTurns;
    private int[][] replayBoard;

    public Replay(List<Turn> turnList, NinaBoard gameBoard){
        this.turnList = turnList;
        this.gameBoard = gameBoard;
        replayBoard = gameBoard.getBoardTiles();
        currentTurnNumber = turnList.size()-1;
        numOfTurns = turnList.size();
    }

    public void undoTurn(){
        currentTurnNumber--;
        Turn lastTurn = turnList.get(currentTurnNumber);

        if(lastTurn.getTurnType() == Turn.addDisk){
            // the last turn was adding a disk, remove it from the top
            replayBoard[lastTurn.getRow()][lastTurn.getCol()] = 0;
        } else {
            // the last turn was removing a disk, move all other disks up, add it at the bottom
            int currentColumn = lastTurn.getCol();
            int numOfRows = gameBoard.getRows();
            for(int i = 1; i < numOfRows; i++){
                replayBoard[i][currentColumn] = replayBoard[i+1][currentColumn];
                replayBoard[i+1][currentColumn] = 0;
            }
        }
    }

    public void applyTurn(){
        currentTurnNumber++;
        Turn nextTurn = turnList.get(currentTurnNumber);

        if(nextTurn.getTurnType() == Turn.addDisk){
            // add disk on top
            replayBoard[nextTurn.getRow()][nextTurn.getCol()] = nextTurn.getParticipantSymbol();
        } else {
            // remove disk from bottom, move pieces down
            replayBoard[0][nextTurn.getCol()] = gameBoard.getEmptyTile();

            int numOfRows = gameBoard.getRows();

            for(int i = 0 ; i < numOfRows; i++){
                replayBoard[i][nextTurn.getCol()] = replayBoard[i+1][nextTurn.getCol()];
                replayBoard[i+1][nextTurn.getCol()] = gameBoard.getEmptyTile();
            }
        }
    }

    public void initialPosition(){
        currentTurnNumber = 0;
        for(int i = 0; i < gameBoard.getRows(); i++){
            for(int j = 0; j < gameBoard.getCols(); j++){
                replayBoard[i][j] = gameBoard.getEmptyTile();
            }
        }
    }

    public void lastPosition(){
        currentTurnNumber = numOfTurns;

        replayBoard = gameBoard.getBoardTiles();
    }
}
