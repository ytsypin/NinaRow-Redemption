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
            }

            if (gameEngine.isWinnerFound()) {
                controller.declareWinnerFound();
                gameEngine.deactivateGame();
            } else{
                if (gameEngine.drawReached()){
                    // Do draw thing
                    controller.declareDraw();
                    gameEngine.deactivateGame();
                } else {
                    controller.changeCurrPlayer();
                    gameEngine.changeCurrentParticipant();
                }
            }
        } catch(ColumnFullException e){
            controller.displayMesage("The selected column is full!", "Column Full");
        }

        while(gameEngine.isCurrentParticipantBot()){
            makeBotMove();
        }
    }

    public void popoutMove(int col) {
        Turn turn = null;
        try {
            turn = gameEngine.getParticipantTurn(col, Turn.removeDisk);
        } catch (ColumnFullException e) { }

        if(turn != null){
            controller.drawPopOut(turn.getRow()-1, turn.getCol());
        }


    }

    public boolean isPopoutGame() {
        return gameEngine.getGameType() == gameEngine.popoutGame;
    }

    public void createParticipantList(ObservableList<Participant> playerData) {

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
                controller.drawTurn(turn.getCol(), turn.getRow(), turn.getParticipantSymbol());
            }

            if (gameEngine.isWinnerFound()) {
                controller.declareWinnerFound();
                gameEngine.deactivateGame();
            } else if (gameEngine.drawReached()) {
                // Do draw thing
                controller.declareDraw();
                gameEngine.deactivateGame();
            } else {
                controller.changeCurrPlayer();
            }
        }
    }

    public void setGameIsActive() {
        gameEngine.setActive();
    }
}
