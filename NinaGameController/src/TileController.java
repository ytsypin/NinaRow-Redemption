import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class TileController {

    @FXML private Pane tilePane;

    public void addElement(){
        AnchorPane anchor = new AnchorPane();

        tilePane.getChildren().add(anchor);

        Circle circle = new Circle(-20, -20, 18);
        circle.getStyleClass().add("circle");
        anchor.getChildren().add(circle);
    }

}
