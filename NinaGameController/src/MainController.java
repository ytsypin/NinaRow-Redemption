import Exceptions.ColumnFullException;
import gameBoard.Participant;
import gameBoard.Turn;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import regularGame.RegularGame;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML AnchorPane boardAnchorPane;
    @FXML private RightSideController rightSideController;
    @FXML private ReplayController replayAreaController;
    @FXML private BorderPane borderPane;
    @FXML private ScrollPane entireWindow;
    @FXML private Label gameTypeHeader;
    @FXML private Label gameTypeLabel;
    @FXML private Label goalHeader;
    @FXML private Label goalLabel;
    @FXML private Label informationLabel;
    @FXML private HBox topHBox;

    private BusinessLogic businessLogic;
    private Stage primaryStage;
    private TileController[][] tileControllers;

    private static final int TILE_SIZE = 40;


    @FXML public void initialize(){
        businessLogic = new BusinessLogic();
        rightSideController.init(this, businessLogic);
        replayAreaController.init(this, businessLogic);
        borderPane.getStyleClass().add("gameBorder");
        topHBox.getStyleClass().add("topHBox");
        entireWindow.getStylesheets().add("/fxmlResources/cssResources/Style1.css");
    }

    public void bindTaskToUI(Task<RegularGame> currentRunningTask) {
        rightSideController.bindUIToXMLController(currentRunningTask);
    }


    public void setBusinessLogic(BusinessLogic businessLogic) {
            this.businessLogic = businessLogic;
            businessLogic.setController(this);
            replayAreaController.setBusinessLogic(businessLogic);
            rightSideController.setBusinessLogic(businessLogic);
        }

        public void setPrimaryStage(Stage primaryStage){
            this.primaryStage = primaryStage;
        }

        public void populateTable() {
            rightSideController.clearPlayerInfoTable();
            rightSideController.setPlayerInfoTable(businessLogic.getPlayerData());
            businessLogic.createParticipantList(businessLogic.getPlayerData());
            createBoard();
        }

        public void createBoard() {
            boardAnchorPane.getChildren().clear();
            GridPane gridShape = makeGrid();
            gridShape.setAlignment(Pos.CENTER);
            boardAnchorPane.getChildren().add(gridShape);
            primaryStage.sizeToScene();
        }

        private GridPane makeGrid() {
            int rows = businessLogic.getRows();
            int cols = businessLogic.getCols();

            GridPane grid = new GridPane();

            createButtonRow(grid, cols, 0, Turn.addDisk);

            createGameBoard(grid, rows, cols);

            AnchorPane.setRightAnchor(grid, .0);
            AnchorPane.setTopAnchor(grid, .0);
            AnchorPane.setLeftAnchor(grid, .0);
            AnchorPane.setBottomAnchor(grid, .0);

            if(businessLogic.isPopoutGame()){
                createButtonRow(grid, cols, rows+2, Turn.removeDisk);
            }

            return grid;
    }

    private void createGameBoard(GridPane grid, int rows, int cols) {
        try {

            tileControllers = new TileController[rows][cols];

            grid.getStylesheets().add("fxmlResources/cssResources/PlayerLabels.css");

            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getResource("/fxmlResources/Tile.fxml");
                    fxmlLoader.setLocation(url);

                    Pane singleTile = fxmlLoader.load();
                    TileController tileController = fxmlLoader.getController();
                    singleTile.getStyleClass().add("tile");

                    singleTile.getStylesheets().add("fxmlResources/cssResources/PlayerLabels.css");

                    tileControllers[i][j] = tileController;

                    grid.add(singleTile,j, i+1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createButtonRow(GridPane grid, int cols, int row, int turnType) {
        for(int i = 0; i < cols; i++){
            Image buttonImage = new Image(getClass().getResourceAsStream("Resources/arrow-down.png"));
            ImageView buttonImageView = new ImageView(buttonImage);
            buttonImageView.setFitHeight(TILE_SIZE/2);
            buttonImageView.setFitWidth(TILE_SIZE/2);

            Button button = new Button("",buttonImageView);
            button.disableProperty().bind(rightSideController.isGameActive.not());
            int buttonCol = i;
            if(turnType == Turn.addDisk) {
                button.setOnAction(e -> businessLogic.regularMove(buttonCol));
            } else {
                button.setOnAction(e-> {
                businessLogic.popoutMove(buttonCol);
                });
            }
            grid.add(button, i, row);
        }
    }

    public void populateLabels(int size) {
        rightSideController.populateLabels(size);
    }

    public void disableReplayAreaBind(SimpleBooleanProperty isFileLoaded) {
        replayAreaController.disableButtonsBind(isFileLoaded);
    }

    public void endProgram() {
        primaryStage.close();
    }

    public void drawTurn(int col, int row, int participantSymbol) {
        Bounds startPos = tileControllers[0][col].getBounds();
        Bounds endPos = tileControllers[row][col].getBounds();

        Circle circle = new Circle(startPos.getWidth()-20, startPos.getHeight()-20, 17);
        circle.getStyleClass().add("player" + participantSymbol);

        tileControllers[row][col].draw(circle);
    }

    public void declareWinnerFound(){
        rightSideController.isGameActive.setValue(false);

        displayMesage(businessLogic.getCurrentPlayerName() + " Won!", "Winner Found!");
    }

    public void setGameTypeAndGoal(int gameType, Integer n) {
        String gameTypeString = new String();

        if(gameType == RegularGame.regularGame){
            gameTypeString = "Regular";
        } else if(gameType == RegularGame.popoutGame){
            gameTypeString = "Popout";
        } else {
            gameTypeString = "Circular";
        }

        gameTypeLabel.setText(gameTypeString);
        goalLabel.setText(n.toString());
    }

    public void changeCurrPlayer() {
        rightSideController.changeCurrentPlayer();
    }

    public void clearBoard() {
        int rowNum = businessLogic.getRows();
        int colNum = businessLogic.getCols();

        for(int i =0; i < rowNum; i++){
            for(int j = 0; j < colNum; j++){
                tileControllers[i][j].clearChildren();
            }
        }
    }

    public void displayMesage(String message, String header){
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(header);
        dialog.setHeaderText(message);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.showAndWait();
    }

    public void declareDraw() {
        displayMesage("There Are No Moves Left - We All Lose, Just Like In Real Life!", "Draw");
    }

    public void popOutTile(int row, int col) {
        tileControllers[row][col].removePiece();
    }

    public void cascadeTiles(int col) {
        int currRow = businessLogic.getRows()-1;

        while(currRow > 0 && tileControllers[currRow-1][col].isOccupied()){
            Node droppingNode = tileControllers[currRow-1][col].getChild();
            tileControllers[currRow-1][col].clearChildren();
            tileControllers[currRow][col].attachNode(droppingNode);
            currRow--;
        }
    }

    public void declareWinners(ObservableList<Participant> participants) {
        StringBuilder winners = new StringBuilder();

        for(Participant participant: participants){
            winners.append(participant);
            if(participant != participants.get(participants.size()-1)){
                winners.append(", ");
            }
        }

        winners.append("!");

        displayMesage("Several winners found! " + winners.toString(), "Winners Found");
    }
}