import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import regularGame.RegularGame;

public class XMLLoaderController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressMessage;

    public void bindUIToController(Task<RegularGame> currentRunningTask) {
        progressBar.progressProperty().bind(currentRunningTask.progressProperty());
        progressMessage.textProperty().bind(currentRunningTask.messageProperty());
    }
}
