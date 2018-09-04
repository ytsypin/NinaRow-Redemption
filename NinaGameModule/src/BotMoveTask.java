import gameBoard.Turn;
import javafx.application.Platform;
import javafx.concurrent.Task;
import regularGame.RegularGame;

public class BotMoveTask extends Task<RegularGame> {
    private BusinessLogic businessLogic;
    private final int SLEEP_TIME = 400;

    @Override
    protected RegularGame call() throws Exception {

        while(businessLogic.currentPlayerIsBot() && businessLogic.getIsActive()) {
            updateMessage("Calculating Bot's Turn...");
            Turn turn = businessLogic.getBotTurn();

            Thread.sleep(SLEEP_TIME);
            updateMessage("Showing The Turn...");
            if (turn != null) {
                if(turn.getTurnType() == Turn.removeDisk){
                    businessLogic.drawPopoutTurn(turn);
                } else {
                    Platform.runLater(() -> {businessLogic.drawTurn(turn.getCol(), turn.getRow(), turn.getParticipantSymbol());});
                }
            }

            Thread.sleep(SLEEP_TIME);
            updateMessage("Checking For Winners Or A Draw...");
            if (businessLogic.isWinnerFound()) {
                Platform.runLater(()->businessLogic.declareWinnerFound());
                businessLogic.deactivateGame();
                Platform.runLater(()->businessLogic.deactivate());
            } else if (businessLogic.drawReached()) {
                Platform.runLater(()->businessLogic.declareDraw());
                businessLogic.deactivateGame();
                Platform.runLater(()->businessLogic.deactivate());
            } else {
                Platform.runLater(()->businessLogic.changeCurrPlayer());
            }
        }

        Thread.sleep(SLEEP_TIME);
        updateMessage("Done!");

        Thread.sleep(SLEEP_TIME);

        updateMessage("");

        Thread.sleep(SLEEP_TIME);


        return null;
    }

    public BotMoveTask(BusinessLogic logic){
        businessLogic = logic;
    }
}
