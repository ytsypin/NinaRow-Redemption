import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

public class GameLauncher extends Application {
    private BusinessLogic businessLogic;
    private MainController controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/fxmlResources/MainFXML.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        controller = fxmlLoader.getController();
        businessLogic = new BusinessLogic();
        controller.setBusinessLogic(businessLogic);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        controller.setPrimaryStage(primaryStage);
        primaryStage.show();

    }

    public static void main(String[] args){
        launch(args);
    }

}
