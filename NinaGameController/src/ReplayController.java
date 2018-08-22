import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReplayController {

    @FXML private Button replayToggleButton;

    @FXML private Button toBeginningButton;

    @FXML private Button undoTurnButton;

    @FXML private Button nextTurnButton;

    @FXML private Button toEndButton;

    private MainController mainController;
    private BusinessLogic businessLogic;

    public void init(MainController mainController, BusinessLogic businessLogic) {
        this.mainController = mainController;
        this.businessLogic = businessLogic;
    }

    @FXML
    void activateReplay(ActionEvent event) {

    }

    @FXML
    void goToStart(ActionEvent event) {

    }

    @FXML
    void nextTurn(ActionEvent event) {

    }

    @FXML
    void toEnd(ActionEvent event) {

    }

    @FXML
    void undoTurn(ActionEvent event) {

    }


    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public void disableButtonsBind(SimpleBooleanProperty isFileLoaded) {
        replayToggleButton.disableProperty().bind(isFileLoaded.not());
        toBeginningButton.disableProperty().bind(isFileLoaded.not());
        undoTurnButton.disableProperty().bind(isFileLoaded.not());
        nextTurnButton.disableProperty().bind(isFileLoaded.not());
        toEndButton.disableProperty().bind(isFileLoaded.not());
    }
}
