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
        Turn turn = gameEngine.getParticipantTurn(col, Turn.addDisk);

        if(turn != null) {
            controller.drawTurn(turn.getCol(), turn.getRow(), turn.getParticipantSymbol());
        }
    }

    public void popoutMove(int col){
        gameEngine.takeParticipantTurn(col, Turn.removeDisk);
    }

    public boolean isPopoutGame() {
        return gameEngine.getGameType() == gameEngine.popoutGame;
    }

    public void createParticipantList(ObservableList<Participant> playerData) {

    }
}
