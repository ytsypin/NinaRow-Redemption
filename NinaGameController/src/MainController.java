import gameBoard.Turn;
import javafx.animation.PathTransition;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
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
            Image buttonImage = new Image(getClass().getResourceAsStream("Resources/download.png"));
            ImageView buttonImageView = new ImageView(buttonImage);
            buttonImageView.setFitHeight(TILE_SIZE/2);
            buttonImageView.setFitWidth(TILE_SIZE/2);

            Button button = new Button("",buttonImageView);
            button.disableProperty().bind(rightSideController.isGameStarted.not());
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

    public void disableReplayAreaBind(SimpleBooleanProperty isFileLoaded) {
        replayAreaController.disableButtonsBind(isFileLoaded);
    }

    public void endProgram() {
        primaryStage.close();
    }

    public void drawTurn(int col, int row, int participantSymbol) {
        Bounds startPos = tileControllers[0][col].getBounds();
        Bounds endPos = tileControllers[row][col].getBounds();

        Circle circle = new Circle(startPos.getHeight()-20, startPos.getWidth()-20, 17);
        Group root  = new Group();
        root.getChildren().add(circle);
        Path path = new Path();
        path.getElements().addAll(new MoveTo(endPos.getHeight()-20,endPos.getWidth()-20), new VLineTo(startPos.getHeight() -20 - endPos.getHeight()+20));
        root.getChildren().add(path);

        PathTransition pt = new PathTransition(Duration.millis(400), path, circle);
        pt.setCycleCount(1);
        pt.play();
        circle.getStyleClass().add("player" + participantSymbol);

        tileControllers[row][col].draw(circle);
    }
}