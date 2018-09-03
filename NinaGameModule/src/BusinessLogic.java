import Exceptions.CantPopoutException;
import Exceptions.ColumnFullException;
import gameBoard.Participant;
import gameBoard.Turn;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import regularGame.RegularGame;
import xmlExtractor.XMLExtractionTask;


public class BusinessLogic {
    private Task<RegularGame> currentRunningTask;
    private MainController controller;
    private RegularGame gameEngine;

    public void parseXMLFile(String fileName){
        currentRunningTask = new XMLExtractionTask(fileName);

        controller.bindTaskToUI(currentRunningTask);

        currentRunningTask.setOnSucceeded(e -> {
            gameEngine = currentRunningTask.getValue();
            if(gameEngine != null) {
                controller.populateTable();
                controller.populateLabels(gameEngine.getParticipants().size());
                controller.setGameTypeAndGoal(gameEngine.getGameType(), gameEngine.getN());
                controller.changeCurrPlayer(gameEngine.getCurrentPlayerName());
            }
        });

        new Thread(currentRunningTask).start();
    }

    public void setController(MainController controller){
        this.controller = controller;
    }

    public ObservableList<Participant> getPlayerData() {
        return gameEngine.getParticipants();
    }

    public int getCols(){
        return gameEngine.getCols();
    }

    public int getRows(){
        return gameEngine.getRows();
    }

    public void regularMove(int col){
        try {
            Turn turn = gameEngine.getParticipantTurn(col, Turn.addDisk);

            if (turn != null) {
                controller.drawTurn(turn.getCol(), turn.getRow(), turn.getParticipantSymbol());

                if (gameEngine.isWinnerFound()) {
                    controller.declareWinnerFound();
                    gameEngine.deactivateGame();
                } else{
                    if (gameEngine.drawReached()){
                        controller.declareDraw();
                        gameEngine.deactivateGame();
                    } else {
                        gameEngine.changeCurrentParticipant();
                        controller.changeCurrPlayer(gameEngine.getCurrentPlayerName());
                    }
                }
            }


        } catch (ColumnFullException e){
            controller.displayMesage("The selected column is full!", "Column Full");
        } catch (CantPopoutException e){}

        while(gameEngine.isCurrentParticipantBot() && gameEngine.getIsActive()){
            makeBotMove();
        }
    }

    public void popoutMove(int col) {
        Turn turn = null;
        try {
            turn = gameEngine.getParticipantTurn(col, Turn.removeDisk);
        } catch (ColumnFullException e) { }
        catch (CantPopoutException e) {
            controller.displayMesage("Can't popout this column, your piece is not on the bottom.", "Cant Popout");
        }

        if(turn != null){
            drawPopoutTurn(turn);

            if (gameEngine.isWinnerFound()) {
                if(gameEngine.getParticipants().size() == 1) {
                    controller.declareWinnerFound();
                } else {
                    controller.declareWinners(gameEngine.getParticipants());
                }
                gameEngine.deactivateGame();
            } else{

                if (gameEngine.drawReached()){
                    controller.declareDraw();
                    gameEngine.deactivateGame();
                } else {
                    gameEngine.changeCurrentParticipant();
                    controller.changeCurrPlayer(gameEngine.getCurrentPlayerName());
                }
            }

        }

        while(gameEngine.isCurrentParticipantBot() && gameEngine.getIsActive()){
            makeBotMove();
        }

    }

    private void drawPopoutTurn(Turn turn) {
        controller.popOutTile(turn.getRow(), turn.getCol());
        controller.cascadeTiles(turn.getRow(), turn.getCol());
    }

    public boolean isPopoutGame() {
        return gameEngine.getGameType() == RegularGame.popoutGame;
    }

    public String getCurrentPlayerName() {
        return gameEngine.getCurrentPlayerName();
    }

    public void clearBoard() {
        gameEngine.clearGame();
    }

    public boolean currentPlayerIsBot() {
        return gameEngine.isCurrentParticipantBot();
    }

    public void makeBotMove() {
        while(currentPlayerIsBot() && gameEngine.getIsActive()) {
            Turn turn = gameEngine.getBotTurn();

            if (turn != null) {
                if(turn.getTurnType() == Turn.removeDisk){
                    drawPopoutTurn(turn);
                } else {
                    controller.drawTurn(turn.getCol(), turn.getRow(), turn.getParticipantSymbol());
                }
            }

            if (gameEngine.isWinnerFound()) {
                controller.declareWinnerFound();
                gameEngine.deactivateGame();
            } else if (gameEngine.drawReached() && !isPopoutGame()) {
                // Do draw thing
                controller.declareDraw();
                gameEngine.deactivateGame();
            } else {
                controller.changeCurrPlayer(gameEngine.getCurrentPlayerName());
            }
        }
    }

    public void setGameIsActive() {
        gameEngine.setActive();
    }

    public void resetTurns() {
        gameEngine.resetTurns();
    }

    public void leaveGame() {
        int rows = gameEngine.getRows();
        int cols = gameEngine.getCols();

        for(int i = 0; i < rows; i++){
            for(int j = 0 ; j < cols; j++){
                if(gameEngine.getTileSymbol(i,j) == gameEngine.getCurrentPlayerSymbol()){
                    gameEngine.removeTile(i,j);
                    gameEngine.cascadeTiles(i, j);
                    controller.popOutTile(i,j);
                    controller.cascadeTiles(i, j);
                }
            }
        }
        gameEngine.removeCurrentPlayer();
        controller.changeCurrPlayer(gameEngine.getCurrentPlayerName());

        if(gameEngine.getParticipants().size() == 1){
            // TODO: end the game, notify
        }

        if(gameEngine.isWinnerFound()) {
            // TODO: notify winners
        }
    }
}
