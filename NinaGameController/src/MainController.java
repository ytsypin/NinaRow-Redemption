import gameBoard.Turn;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import regularGame.RegularGame;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML AnchorPane boardAnchorPane;
    @FXML private RightSideController rightSideController;
    @FXML private ReplayController replayAreaController;
    @FXML private GameBoardController gameBoardController;
    @FXML private BorderPane borderPane;
    @FXML private ScrollPane entireWindow;

    @FXML private Label player1;
    @FXML private Label player2;
    @FXML private Label player3;
    @FXML private Label player4;
    @FXML private Label player5;
    @FXML private Label player6;

    private Label[] playerLabels;

    private BusinessLogic businessLogic;
    private Stage primaryStage;
    private TileController[][] tileControllers;

    private static final int TILE_SIZE = 40;


    @FXML public void initialize(){
        businessLogic = new BusinessLogic();
        rightSideController.init(this, businessLogic);
        replayAreaController.init(this, businessLogic);
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
            rightSideController.setPlayerInfoTable(businessLogic.getPlayerData());
            createBoard();
        }

        public void createBoard() {
            boardAnchorPane.getChildren().clear();
            GridPane gridShape = makeGrid();
            gridShape.setAlignment(Pos.CENTER);
            boardAnchorPane.getChildren().add(gridShape);
        }

        private GridPane makeGrid() {
            int rows = businessLogic.getRows();
            int cols = businessLogic.getCols();

            GridPane grid = new GridPane();

            createButtonRow(grid, cols, 0, Turn.addDisk);

            createGameBoard(grid, rows, cols);

            TileController tileController = tileControllers[1][2];

            tileController.addElement();

            double x = (boardAnchorPane.getWidth() - ((cols+2)*TILE_SIZE)*((rows+2)*TILE_SIZE))/2;
            double y = (boardAnchorPane.getHeight() - ((cols+2)*TILE_SIZE)*((rows+2)*TILE_SIZE))/2;

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

            grid.getStylesheets().add("Resources/Tile.css");

            for(int i = 0; i < rows; i++){
                for(int j = 0; j < cols; j++){
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getResource("/fxmlResources/Tile.fxml");
                    fxmlLoader.setLocation(url);

                    Node singleTile = fxmlLoader.load();
                    TileController tileController = fxmlLoader.getController();
                    singleTile.getStyleClass().add("tile");

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
            Image buttonImage = new Image(getClass().getResourceAsStream("Resources/download.png"));
            ImageView buttonImageView = new ImageView(buttonImage);
            buttonImageView.setFitHeight(TILE_SIZE/2);
            buttonImageView.setFitWidth(TILE_SIZE/2);

            Button button = new Button("",buttonImageView);
            int buttonCol = i;
            if(turnType == Turn.addDisk) {
                button.setOnAction(e -> businessLogic.regularMove(buttonCol));
            } else {
                button.setOnAction(e->businessLogic.popoutMove(buttonCol));
            }
            grid.add(button, i, row);
        }
    }

    public void populateLabels(int size) {
        rightSideController.populateLabels(size);
    }
}