package gameBoard;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;

public class NinaBoard implements Serializable {
    private static int fullCol = 1000;
    private static int emptyTile = 0;

    private int[][] boardTiles;
    private SimpleIntegerProperty rows;
    private SimpleIntegerProperty cols;

    public NinaBoard(int rows, int cols){
        this.rows = new SimpleIntegerProperty(rows);
        this.cols = new SimpleIntegerProperty(cols);

        boardTiles = new int[rows][cols];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                boardTiles[i][j] = (emptyTile);
            }
        }
    }

    public static int getEmptyTile(){
        return emptyTile;
    }

    public int[][] getBoardTiles() {
        return boardTiles;
    }

    public int getFirstOpenRow(int col){
        boolean found = false;
        int resRow = fullCol;
        int numOfRowsInIndex = rows.getValue() - 1;

        if(boardTiles[0][col] != emptyTile){
            resRow = fullCol;
            found = true;
        } else if(boardTiles[numOfRowsInIndex][col] == emptyTile){
            resRow = numOfRowsInIndex;
            found = true;
        } else {
            for (int i = numOfRowsInIndex; (i >= 0) && (!found); i--) {
                if (boardTiles[i][col] == emptyTile) {
                    resRow = i;
                    found = true;
                }
            }
        }
        // if found == false, resRow will be set to fullCol as an impossible value to signify a full column.
        // otherwise, a row should have been found.
        if(!found){
            resRow = fullCol;
        }

        return resRow;
    }

    public int getRows() {
        return rows.getValue();
    }

    public int getCols() {
        return cols.getValue();
    }

    public void applyTurn(Turn currTurn, int currPlayerMark) {
        boardTiles[currTurn.getRow()][currTurn.getCol()] = currPlayerMark;
    }

    public int getTileSymbol(int row, int col){
        return boardTiles[row][col];
    }


    public void popOutTile(int row, int col){
        boardTiles[row][col] = emptyTile;
    }

    public void dropTile(int row, int col) {
        boardTiles[row+1][col] = boardTiles[row][col];
        boardTiles[row][col] = emptyTile;
    }

    public boolean isColFull(int col) {
        int firstOpenRow = getFirstOpenRow(col);

        if(firstOpenRow == fullCol){
            return true;
        } else {
            return false;
        }
    }

    public void clear() {
        int rowNum = rows.getValue();
        int colNum = cols.getValue();

        for(int i = 0; i < rowNum; i++){
            for(int j = 0; j < colNum; j++){
                boardTiles[i][j] = emptyTile;
            }
        }
    }
}
