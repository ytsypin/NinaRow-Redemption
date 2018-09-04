import javafx.concurrent.Task;
import regularGame.RegularGame;

public class BotMoveTask extends Task<RegularGame> {
    private BusinessLogic businessLogic;

    @Override
    protected RegularGame call() throws Exception {
        businessLogic.makeBotMove();
        return null;
    }

    public BotMoveTask(BusinessLogic logic){
        businessLogic = logic;
    }
}
