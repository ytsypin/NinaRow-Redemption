import gameBoard.Turn;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import regularGame.RegularGame;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MainController {
    @FXML Pane boardAnchorPane;
    @FXML private RightSideController rightSideController;
    @FXML private ReplayController replayAreaController;
    @FXML private GameBoardController gameBoardController;
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
            GridPane gridShape = makeGrid();
            boardAnchorPane.getChildren().add(gridShape);
        }

        private GridPane makeGrid() {
            int rows = businessLogic.getRows();
            int cols = businessLogic.getCols();

            GridPane grid = new GridPane();

            createButtonRow(grid, cols, 0, Turn.addDisk);

            createGameBoard(grid, rows, cols);

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

                    TileController tileController = fxmlLoader.getController();
                    Node singleTile = fxmlLoader.load();
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
}