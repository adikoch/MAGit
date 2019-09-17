package MainFolder;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConflictController implements Initializable {
    @FXML
    TextArea FatherText;
    @FXML
    Label father;
    @FXML
    TextArea theirsText;
    @FXML
    Label theirs;
    @FXML
    TextArea Ourstext;
    @FXML
    Label Ours;
    @FXML
    Button submit;
    @FXML
    TextArea insertText;
    @FXML
    AnchorPane mainPane;


    public Stage primaryStage;
public MainController MC;
    public SimpleStringProperty InsertTextP;
    public SimpleStringProperty OurTextP;
    public SimpleStringProperty FatherTextP;
    public SimpleStringProperty TheirTextP;


    public ConflictController() {

//        FXMLLoader loader = new FXMLLoader();
//
//        // load main fxml
//        URL mainFXML = getClass().getResource("/Newmain.fxml");
//        loader.setLocation(mainFXML);
//        AnchorPane root = loader.load();
//
//        // wire up controller
//
//        MainController mainController = loader.getController();
//        GitManager manager = new GitManager();
//        mainController.setPrimaryStage(primaryStage);
//        mainController.setLogic(manager);
//
//
//        // set stage
//        primaryStage.setTitle("MAGit");
//        Scene scene = new Scene(root, 1050, 600);
////
////        Graph tree = new Graph();
////
////        ScrollPane scrollPane = (ScrollPane) scene.lookup("#pane");
////        PannableCanvas canvas = tree.getCanvas();
////        //canvas.setPrefWidth(100);
////        //canvas.setPrefHeight(100);
////        scrollPane.setContent(canvas);
//
//
//        primaryStage.setScene(scene);
//        primaryStage.show();

        //InsertTextP = new SimpleStringProperty();
        OurTextP= new SimpleStringProperty();
        FatherTextP= new SimpleStringProperty();
        TheirTextP= new SimpleStringProperty();
        //mainC = mainController;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Ourstext.textProperty().bind(OurTextP);
        theirsText.textProperty().bind(TheirTextP);
        FatherText.textProperty().bind(FatherTextP);
       // insertText.textProperty().bind(InsertTextP);
        insertText.setEditable(true);

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
@FXML
    public String submitOnAction()
    {
        MC.InputTextBox = insertText.getText();
        return InsertTextP.toString();
    }
//okButton.setOnAction(event -> {
//        InputTextBox = newText.getText();
//        popUpWindow.close();
//    });

}