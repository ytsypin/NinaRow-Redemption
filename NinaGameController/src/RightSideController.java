import gameBoard.Participant;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import regularGame.RegularGame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @FXML private Button leaveGameButton;
    @FXML private Label currentPlayerLabel;

    private ObservableList<String> styles = FXCollections.observableArrayList("Default", "Dark", "Sundown", "Ocean");

    @FXML private Label player1;
    @FXML private Label player2;
    @FXML private Label player3;
    @FXML private Label player4;
    @FXML private Label player5;
    @FXML private Label player6;

    private Map<Integer, Label> playerLabels = new HashMap();

    private MainController mainController;
    private Stage primaryStage;
    private BusinessLogic businessLogic;
    private XMLLoaderController xmlLoaderController;

    private SimpleBooleanProperty isFileLoaded;
    public SimpleBooleanProperty isGameActive;

    public RightSideController() {
        isFileLoaded = new SimpleBooleanProperty(false);
        isGameActive = new SimpleBooleanProperty(false);
    }

    public void init(MainController mainController, BusinessLogic businessLogic) {
        this.mainController = mainController;
        this.businessLogic = businessLogic;
    }

    @FXML
    void onExitPressed(ActionEvent event) {
        mainController.endProgram();
    }

    @FXML
    void onStartPressed(ActionEvent event) {
        businessLogic.clearBoard();
        mainController.clearBoard();

        isGameActive.setValue(true);
        businessLogic.setGameIsActive();
        businessLogic.resetTurns();
        setPlayerInfoTable(businessLogic.getPlayerData());
        populateLabels(businessLogic.getPlayerData().size());

        currentPlayerLabel.setText(businessLogic.getCurrentPlayerName());

        if(businessLogic.currentPlayerIsBot()){
            businessLogic.makeBotMove();
        }
    }

    @FXML
    void onLeavePressed(ActionEvent event) {
        businessLogic.leaveGame();
    }

    public void initialize(){
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("idNum"));
        isBotCol.setCellValueFactory(new PropertyValueFactory<>("isBot"));
        numOfTurnsCol.setCellValueFactory(new PropertyValueFactory<>("turnsTaken"));

        playerLabels.put(0, player1);
        playerLabels.put(1, player2);
        playerLabels.put(2, player3);
        playerLabels.put(3, player4);
        playerLabels.put(4, player5);
        playerLabels.put(5, player6);

        startGameButton.disableProperty().bind(
                Bindings.or(isFileLoaded.not(),isGameActive));
        loadXMLButton.disableProperty().bind(isGameActive);
        leaveGameButton.disableProperty().bind(isGameActive.not());

        skinSelector.setItems(styles);

        skinSelector.getStyleClass().add("button");

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

            primaryStage.sizeToScene();
        });

        skinSelector.getSelectionModel().select("Default");

        currentPlayerLabel.getStyleClass().add("currentPlayer");
    }

    public void setPlayerInfoTable(ObservableList<Participant> playerData){
        playerTable.setItems(playerData);
        populateLabels(playerData.size());
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
        playerLabels.put(0, player1);
        playerLabels.put(1, player2);
        playerLabels.put(2, player3);
        playerLabels.put(3, player4);
        playerLabels.put(4, player5);
        playerLabels.put(5, player6);

        for(int i = 0; i < 6; i++){
            playerLabels.get(i).setText("");
            playerLabels.get(i).getStyleClass().clear();
        }

        for(int i = size; i < 6; i++){
            playerLabels.get(i).setText("");
        }

        for(int i = 0; i < size; i++){
            playerLabels.get(i).getStyleClass().add("player"+ (i+1));
        }

        isFileLoaded.setValue(true);
    }

    public void changeCurrentPlayer(String playerName) {
        currentPlayerLabel.setText(playerName);
    }

    public void clearPlayerInfoTable() {
        playerTable.getItems().clear();
    }

    public void removeCurrPlayerColorLabel(int playerSymbol) {
        int numOfPlayers = businessLogic.getPlayerData().size();

        for(int i = playerSymbol; i < numOfPlayers; i++ ){
            String oldStyleClass = "player"+(i+1);
            String newStyleClass = "player"+(i+2);
            playerLabels.get(i).getStyleClass().remove(oldStyleClass);
            playerLabels.get(i).getStyleClass().add(newStyleClass);
        }

        playerLabels.get(numOfPlayers-1).getStyleClass().clear();
    }
}
