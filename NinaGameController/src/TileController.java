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

    public void removePiece() {
        tilePane.getChildren().clear();
    }

    public boolean isOccupied() {
        return tilePane.getChildren().size() != 0;
    }

    public Node getChild() {
        if(this.isOccupied()) {
            return tilePane.getChildren().get(0);
        } else {
            return null;
        }
    }

    public void attachNode(Node droppingNode) {
        tilePane.getChildren().add(droppingNode);
    }
}
