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
            } else {
                controller.changeCurrPlayer();
            }
        } catch(ColumnFullException e){
            controller.displayMesage("The selected column is full!");
        }
    }

    public void popoutMove(int col) throws ColumnFullException{
        gameEngine.takeParticipantTurn(col, Turn.removeDisk);
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
}
