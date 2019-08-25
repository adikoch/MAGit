import Classess.GitManager;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.PannableCanvas;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        // load main fxml
        URL mainFXML = getClass().getResource("/MainControl.fxml");
        loader.setLocation(mainFXML);
        AnchorPane root = loader.load();

        // wire up controller

        MainController mainController = loader.getController();
        GitManager manager = new GitManager();
        mainController.setPrimaryStage(primaryStage);
        mainController.setLogic(manager);


        // set stage
        primaryStage.setTitle("MAGit");
        Scene scene = new Scene(root, 1050, 600);

        Graph tree = new Graph();

        ScrollPane scrollPane = (ScrollPane) scene.lookup("#pane");
        PannableCanvas canvas = tree.getCanvas();
        //canvas.setPrefWidth(100);
        //canvas.setPrefHeight(100);
        scrollPane.setContent(canvas);


        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> {
            tree.getUseViewportGestures().set(false);
            tree.getUseNodeGestures().set(false);
        });
    }

    public static void main(String[] args) {

        launch(args);
    }
}
