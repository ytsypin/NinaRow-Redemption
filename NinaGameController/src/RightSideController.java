import gameBoard.Participant;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
    @FXML private ComboBox<String> skinSelector;
    @FXML private VBox playerLabelArea;

    private ObservableList<String> styles = FXCollections.observableArrayList("Default", "Dark", "Sundown", "Ocean");

    @FXML private Label player1;
    @FXML private Label player2;
    @FXML private Label player3;
    @FXML private Label player4;
    @FXML private Label player5;
    @FXML private Label player6;

    private Label[] playerLabels = new Label[6];

    @FXML private Label p1TurnIndicator;
    @FXML private Label p2TurnIndicator;
    @FXML private Label p3TurnIndicator;
    @FXML private Label p4TurnIndicator;
    @FXML private Label p5TurnIndicator;
    @FXML private Label p6TurnIndicator;

    private Label[] turnIndicators = new Label[6];


    private MainController mainController;
    private Stage primaryStage;
    private BusinessLogic businessLogic;
    private XMLLoaderController xmlLoaderController;

    private SimpleBooleanProperty isFileLoaded;
    public SimpleBooleanProperty isGameActive;
    private int currentPlayerTurn;

    public RightSideController() {
        isFileLoaded = new SimpleBooleanProperty(false);
        isGameActive = new SimpleBooleanProperty(false);
    }

    public void init(MainController mainController, BusinessLogic businessLogic) {
        this.mainController = mainController;
        this.businessLogic = businessLogic;

        mainController.disableReplayAreaBind(isFileLoaded);

    }

    @FXML
    void onExitPressed(ActionEvent event) {
        mainController.endProgram();
    }

    @FXML
    void onStartPressed(ActionEvent event) {
        isGameActive.setValue(true);
        businessLogic.clearBoard();
        mainController.clearBoard();

        isGameActive.setValue(true);
        businessLogic.setGameIsActive();
        businessLogic.resetTurns();
        resetTurns();

        if(businessLogic.currentPlayerIsBot()){
            businessLogic.makeBotMove();
        }
    }

    private void resetTurns() {
        turnIndicators[currentPlayerTurn].setText("");
        currentPlayerTurn = 0;
        turnIndicators[currentPlayerTurn].setText("#");
    }

    public void initialize(){
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("idNum"));
        isBotCol.setCellValueFactory(new PropertyValueFactory<>("isBot"));
        numOfTurnsCol.setCellValueFactory(new PropertyValueFactory<>("turnsTaken"));

        playerLabels[0] = player1;
        playerLabels[1] = player2;
        playerLabels[2] = player3;
        playerLabels[3] = player4;
        playerLabels[4] = player5;
        playerLabels[5] = player6;

        turnIndicators[0] = p1TurnIndicator;
        turnIndicators[1] = p2TurnIndicator;
        turnIndicators[2] = p3TurnIndicator;
        turnIndicators[3] = p4TurnIndicator;
        turnIndicators[4] = p5TurnIndicator;
        turnIndicators[5] = p6TurnIndicator;

        startGameButton.disableProperty().bind(
                Bindings.or(isFileLoaded.not(),isGameActive));
        skinSelector.disableProperty().bind(isFileLoaded.not());
        loadXMLButton.disableProperty().bind(isGameActive);

        skinSelector.setItems(styles);

        skinSelector.setOnAction((e) ->{
            if(skinSelector.getSelectionModel().getSelectedItem().equals("Default")){
                mainController.setSkin("/fxmlResources/cssResources/DefaultStyle.css");
            } else if(skinSelector.getSelectionModel().getSelectedItem().equals("Dark")){
                mainController.setSkin("/fxmlResources/cssResources/DarkStyle.css");
            } else if(skinSelector.getSelectionModel().getSelectedItem().equals("Sundown")){
                mainController.setSkin("/fxmlResources/cssResources/SundownStyle.css");
            } else {
                mainController.setSkin("/fxmlResources/cssResources/OceanStyle.css");
            }
        });
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

        if(selectedFile != null) {
            String fileName = selectedFile.getAbsolutePath();

            if (selectedFile == null) {
                return;
            }


            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getResource("/fxmlResources/XMLLoader.fxml");
                fxmlLoader.setLocation(url);
                Parent root = fxmlLoader.load();
                xmlLoaderController = fxmlLoader.getController();

                businessLogic.parseXMLFile(fileName);
            } catch (IOException e) {
            }

        }
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
            newWindow.initModality(Modality.APPLICATION_MODAL);
            newWindow.show();


            xmlLoaderController = fxmlLoader.getController();
            xmlLoaderController.bindUIToController(currentRunningTask);
        } catch (IOException e) { }
    }

    public void setBusinessLogic(BusinessLogic businessLogic){
        this.businessLogic = businessLogic;
    }

    public void populateLabels(int size) {
        for(int i = 0; i < 6; i++){
            playerLabels[i].setText("");
            playerLabels[i].getStyleClass().clear();
        }

        for(int i = size; i < 6; i++){
            playerLabels[i].setText("");
        }

        for(int i = 0; i < size; i++){
            playerLabels[i].getStyleClass().add("player"+ (i+1));
        }

        playerLabels[0].getStyleClass().add("style1");

        turnIndicators[0].setText("#");
        currentPlayerTurn = 0;

        isFileLoaded.setValue(true);
    }

    public void changeCurrentPlayer() {
        turnIndicators[currentPlayerTurn].setText("");
        if(currentPlayerTurn == businessLogic.getPlayerData().size()-1){
            currentPlayerTurn = 0;
        } else {
            currentPlayerTurn++;
        }

        turnIndicators[currentPlayerTurn].setText("#");
    }

    public void clearPlayerInfoTable() {
        playerTable.getItems().clear();
    }
}
