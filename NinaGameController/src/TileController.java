import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
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

    public Bounds getBounds() {
        return tilePane.localToScreen(tilePane.getBoundsInLocal());
    }

    public void draw(Node node) {
        tilePane.getChildren().add(node);
    }

    public void clearChildren() {
        tilePane.getChildren().clear();
    }
}
