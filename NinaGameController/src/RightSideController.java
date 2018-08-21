import gameBoard.Participant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import regularGame.RegularGame;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RightSideController {

    @FXML private Label playerInfoLabel;
    @FXML private TableView<Participant> playerTable;
    @FXML private TableColumn<Participant, String> nameCol;
    @FXML private TableColumn<Participant, Integer> idCol;
    @FXML private TableColumn<Participant, Boolean> isBotCol;
    @FXML private TableColumn<Participant, Integer> numOfTurnsCol;
    @FXML private Button loadXMLButton;
    @FXML private Button startGameButton;
    @FXML private Button exitButton;
    @FXML private ComboBox<?> skinSelector;

    private MainController mainController;
    private Stage primaryStage;
    private BusinessLogic businessLogic;
    private XMLLoaderController xmlLoaderController;

    public void init(MainController mainController, BusinessLogic businessLogic) {
        this.mainController = mainController;
        this.businessLogic = businessLogic;
    }

    @FXML
    void onExitPressed(ActionEvent event) {

    }

    @FXML
    void onStartPressed(ActionEvent event) {

    }

    public RightSideController() {

    }

    public void initialize(){
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("idNum"));
        isBotCol.setCellValueFactory(new PropertyValueFactory<>("isBot"));
        numOfTurnsCol.setCellValueFactory(new PropertyValueFactory<>("turnsTaken"));

        ObservableList<Participant> data = FXCollections.observableArrayList(
                new Participant("Adam", true, 123),
                new Participant("Ben", false, 1234)
        );

        setPlayerInfoTable(data);

    }

    public void setPlayerInfoTable(ObservableList<Participant> playerData){
        playerTable.setItems(playerData);
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @FXML
    public void onLoadPressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        String fileName = selectedFile.getAbsolutePath();

        if(selectedFile == null){
            return;
        }


        try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/fxmlResources/XMLLoader.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load();
        xmlLoaderController = fxmlLoader.getController();

        businessLogic.parseXMLFile(fileName);
        } catch (IOException e) { }

    }

    public void bindUIToXMLController(Task<RegularGame> currentRunningTask) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/fxmlResources/XMLLoader.fxml");
            fxmlLoader.setLocation(url);
            Pane root = fxmlLoader.load(url.openStream());

            Scene scene = new Scene(root, 500, 500);
            Stage newWindow = new Stage();
            newWindow.setTitle("Loading XML File");
            newWindow.setScene(scene);
            newWindow.show();


            xmlLoaderController = fxmlLoader.getController();
            xmlLoaderController.bindUIToController(currentRunningTask);
        } catch (IOException e) { }
    }

    public void setBusinessLogic(BusinessLogic businessLogic){
        this.businessLogic = businessLogic;
    }

}
