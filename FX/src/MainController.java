import Classess.*;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;

import Classess.GitManager;

import com.fxgraph.graph.PannableCanvas;
import com.fxgraph.layout.Layout;
import generated.Item;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import generated.MagitRepository;
import javafx.beans.property.SimpleStringProperty;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static java.lang.System.out;

//import java.awt.*;

public class MainController {

    //    @FXML BorderPane border;
//    @FXML VBox vbox;
//    @FXML MenuBar menu;
    @FXML
    MenuItem DefineUsername;
    @FXML
    MenuItem ImportRepositoryFromXML;
    @FXML
    MenuItem CreateEmptyRepository;
    @FXML
    MenuItem SwitchRepository;
    @FXML
    MenuItem ShowCommitsInfo;
    @FXML
    MenuItem ShowStatus;
    @FXML
    MenuItem Commit;
    @FXML
    MenuItem ShowAllBranches;
    @FXML
    MenuItem CreateNewBranch;
    @FXML
    MenuItem DeleteBranch;
    @FXML
    MenuItem CheckOut;
    @FXML
    MenuItem ResetBranch;
    @FXML
    GridPane root;
    @FXML
    Label CommitInformation;
    @FXML
    ScrollPane CommitTree;
    @FXML
    Label RepName;
    @FXML
    Label RepPath;
    @FXML
    Label dynamicStatusContent;
    @FXML
    TreeView<String> CommitText;
    @FXML
    TreeView<String> tree;
    @FXML
    Button merge;

    /*
     *
     * */

    public SimpleObjectProperty CommitTextP;
    public SimpleStringProperty RepositoryNameP;
    public SimpleStringProperty RepositoryPAthP;
    public SimpleObjectProperty BraanchesP;
    public SimpleStringProperty dynamicStatusContentP;

    public Stage primaryStage;
    public GitManager manager;

    public String InputTextBox = null;

    public MainController() {
        RepositoryNameP = new SimpleStringProperty();
        RepositoryPAthP = new SimpleStringProperty();
        dynamicStatusContentP = new SimpleStringProperty();
        CommitTextP = new SimpleObjectProperty();
        viewCommitsGraph();
    }


    public void initialize() {
        RepName.textProperty().bind(RepositoryNameP);
        RepPath.textProperty().bind(RepositoryPAthP);
        dynamicStatusContent.textProperty().bind(dynamicStatusContentP);
        //CommitText.().bind(CommitTextP);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setLogic(GitManager manager) {
        this.manager = manager;
    }


    //Commit

    @FXML
    public void CommitOnAction() {
        Label label = new Label("Please enter the description of your commit");
        TextField newText = new TextField();
        Button okButton = new Button("OK");
        //okButton.setDisable(false);
        //okButton.setVisible(true);

        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        okButton.setOnAction(event -> {
            try {
                manager.ExecuteCommit(newText.getText(), true);
                dynamicStatusContentP.set("Commit finished Successfully");
                CommitTextP.set(manager.getGITRepository().getHeadBranch().getPointedCommit().getSHAContent());
            } catch (Exception e) {
                popUpMessage("could not execute commit");//***
            }
            out.println(newText.getText());
            popUpWindow.close();

        });
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);

        root.getChildren().addAll(label, newText, okButton);

        Scene scene = new Scene(root, 500, 120, Color.WHITE);
        popUpWindow.setTitle("Submit description");
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();
//check where delete

        popUpMessage("Deleted Files's Paths:" + manager.getDeletedFiles().toString()+
                            "Added Files's Paths:" + manager.getCreatedFiles().toString()+
                            "Updated Files's Paths:" + manager.getUpdatedFiles().toString());
        manager.getCreatedFiles().clear();
        manager.getDeletedFiles().clear();
        manager.getUpdatedFiles().clear();
    }



    @FXML
    public void ShowStatusOnAction() {
        StringBuilder sb= new StringBuilder();

        sb.append("The current status of WC is:\n");
        sb.append(System.lineSeparator());
        Stage popUpWindow= new Stage();
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> popUpWindow.close());


        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);


        if (manager.getGITRepository() != null) {
            sb.append("Repository's Name:" + manager.getGITRepository().getRepositoryName() + "\n"
                    + "Repository's Path:" + manager.getGITRepository().getRepositoryPath().toString() + "\n" +
                    "Repository's User:" + manager.getUserName() + "\n");
            try {
                manager.ExecuteCommit("", false);
                sb.append(System.lineSeparator());
                sb.append("Deleted Files's Paths:" + manager.getDeletedFiles() + '\n');
                sb.append("Added Files's Paths:" + manager.getCreatedFiles() + '\n');
                sb.append("Updated Files's Paths:" + manager.getUpdatedFiles() + '\n');
                manager.getCreatedFiles().clear();
                manager.getDeletedFiles().clear();
                manager.getUpdatedFiles().clear();

                root.getChildren().addAll(new Label(sb.toString()));

                Scene scene = new Scene(root);
                popUpWindow.setTitle("WC status");
                popUpWindow.setScene(scene);
                popUpWindow.showAndWait();

            } catch (Exception e) {
                popUpMessage("Show Status Failed! Unable to create files");
            }
        } else {
            popUpMessage("There is no repository defined, no status to show");
        }
        popUpWindow.close();

    }



//    @FXML
//    public void ShowStatusOnAction() {
//        Label starting = new Label("The current status of WC is:\n");
//        Button closeButton = new Button("Close");
//        Stage popUpWindow = new Stage();
//        //*****************************************************************************************************
//        closeButton.setOnAction(event -> popUpWindow.close());
//
//        popUpWindow.initModality(Modality.APPLICATION_MODAL);
//        FlowPane root = new FlowPane();
//        root.setPadding(new Insets(10));
//        root.setHgap(10);
//
//
//        if (manager.getGITRepository() != null) {
//            Label repInfo = new Label("Repository's Name:" + manager.getGITRepository().getRepositoryName() + "\n"
//                    + "Repository's Path:" + manager.getGITRepository().getRepositoryPath().toString() + "\n" +
//                    "Repository's User:" + manager.getUserName());
//            try {
//                manager.ExecuteCommit("", false);
//                Label deleted = new Label("Deleted Files's Paths:" + manager.getDeletedFiles() + '\n');
//                Label added = new Label("Added Files's Paths:" + manager.getCreatedFiles() + '\n');
//                Label updated = new Label("Updated Files's Paths:" + manager.getUpdatedFiles() + '\n');
//                manager.getCreatedFiles().clear();
//                manager.getDeletedFiles().clear();
//                manager.getUpdatedFiles().clear();
//
//
//                root.getChildren().addAll(starting, repInfo, deleted, added, updated, closeButton);
//
//                Scene scene = new Scene(root, 500, 120, Color.WHITE);
//                popUpWindow.setTitle("Submit description");
//                popUpWindow.setScene(scene);
//                popUpWindow.showAndWait();
//
//            } catch (Exception e) {
//                popUpMessage("Show Status Failed! Unable to create files");
//            }
//        } else {
//            popUpMessage("There is no repository defined, no status to show");
//        }
//        popUpWindow.close();
//
//    }


    @FXML
    public void ShowHistoryOfActiveBranchOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, therefor no active branch defined");
            return;
        }
        try {
            String historyOfActiveBranch = manager.ShowHistoryActiveBranch();
            popUpMessage(historyOfActiveBranch);
        } catch (Exception e) {
            popUpMessage("Opening zip file failed");
        }
    }

    public void ShowCommitFilesOnAction() {
        //כרגע מבקש שא1 של קומיט, כשיהיה גרף כאשר המשתמש לחץ קודם על אחד מהקומיטים נעביר לפונק הזו את הsha של הקומיט הלחוץ
        popUpTextBox("Please enter the sha1 of the wanted commit");
        if (InputTextBox==null) return;
        String commitSha1= InputTextBox;
        InputTextBox= null;

        try{
            Commit commit= manager.getCommitFromSha1UsingFiles(manager.getGITRepository().getRepositoryPath().toString(),commitSha1);
            Folder folderOfCommit= manager.generateFolderFromCommitObject(commit.getRootFolderSHA1());
            TreeItem<String> toShow;//= new TreeItem<>(manager.getGITRepository().getRepositoryName());

            toShow=showTreeLookRec(folderOfCommit,manager.getGITRepository().getRepositoryPath().toString());
            toShow.setExpanded(true);

            //showing
            //TreeView<String> comtree = new TreeView<> (toShow);
            //StackPane root = new StackPane();
            //root.getChildren().add(comtree);
           // primaryStage.setScene(new Scene(root, 300, 250));
            //primaryStage.show();
            CommitText.setRoot(toShow);
        }

        catch(Exception e){popUpMessage("Unable to generate commit using the files");}

        try {
            String FilesOfCommit = manager.showFilesOfCommit();
            out.println(FilesOfCommit);


        } catch (Exception e) {
            popUpMessage("Opening zip file failed");
        }
    }


    public TreeItem<String> showTreeLookRec(Folder folder, String mainName){
        TreeItem<String> folderItem = new TreeItem<String>(mainName);// (c.getComponentName());

        for(Folder.Component c: folder.getComponents())
        {
            TreeItem<String> subFolderItem;// = new TreeItem<String>(c.getComponentName());// ();

            if(c.getComponentType().equals(FolderType.Folder))
            {
                Folder recFolder= (Folder) c.getDirectObject();
                subFolderItem=showTreeLookRec(recFolder ,c.getComponentName());
                //subFolderItem.setValue(c.getComponentName());
                subFolderItem.setExpanded(false);
                //subFolderItem.getChildren().add();


            }
            else//if(c.getComponentType().equals(FolderType.Blob))
            {
                subFolderItem = new TreeItem<>(c.getComponentName());
                //subFolderItem.setExpanded(false);
                //subFolderItem.setValue(c.getComponentName());
            }
            folderItem.getChildren().add(subFolderItem);

        }
        return folderItem;

    }

    //Repository

    @FXML
    public void DefineUserNameOnAction() {
        if (manager.getGITRepository() != null) {

            popUpTextBox("Please enter the new username:");
            if(InputTextBox==null) return;
            manager.updateNewUserNameInLogic(InputTextBox);
            InputTextBox=null;
        } else {
            popUpMessage("There is no repository defined! no changes occurred");
        }
    }

    @FXML
    public String getFileWithChooser(String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select XMl file");
        if (extension != null)
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*." + extension));


        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null)
            return selectedFile.getAbsolutePath();
        else
            return null;
    }



    @FXML
    public void ImportRepFromXmlOnAction() {

        String pathString = null;
        String SorO = null;
        boolean isValid = false;

        String absolutePath = getFileWithChooser("xml");
        if (absolutePath != null) {

            try {
                manager.ImportRepositoryFromXML(true, absolutePath);
                RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
                RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
                dynamicStatusContentP.set("Import of Repository finished Successfully");
            } catch (IOException e) {//קיים כבר רפוזטורי עם אותו שם באותה התיקייה שקיבלנו מהאקסמל
                //popUpTextBox("There is already a repository with the same name at the wanted location\nPlease enter O to over write the existing, S to switch to the existing one");
                popUpConfirmationBox("There is already a repository with the same name at the wanted location\nDo you want to override it or switch to it?", "Override", "Switch");
                if(InputTextBox==null) return;
                SorO = InputTextBox;
                InputTextBox = null;
                while (!isValid) {
                    if (SorO.toLowerCase().equals("2")) // switch to the existing repo
                    {//להוסיף את החלק שבודק האם מגיט מהסוויצ רפוזטורי
                        isValid = true;
                        manager.setGITRepository(null);
                        switchRepHelper(true, Paths.get(e.getMessage()));
                    } else if (SorO.toLowerCase().equals("1")) { // overwrite the existing
                        isValid = true;
                        manager.deleteFilesInFolder(new File(e.getMessage()));
                        manager.deleteFilesInFolder(new File(e.getMessage() + "\\.magit"));

                        try {
                            Files.delete((Paths.get(e.getMessage())));

                        }// delete the existing, prepering for loading , need the path of the folder to erase(c:\repo1)
                        catch (IOException e3) {
                            popUpMessage("Could not delete the old reposetory, check if it is open some where else");
                        }//ואיך שהוא לחזור לתפריט הראשי
                        /////
                        try {
                            manager.ImportRepositoryFromXML(false, pathString);
                            RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
                            RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
                            dynamicStatusContentP.set("Creating of Repository finished Successfully");
                        }// delete the existing, prepering for loading , need the path of the folder to erase(c:\repo1)
                        catch (Exception e3) {
                            popUpMessage("Could not delete the old repository, check if it is open some where else");
                        }//ואיך שהוא לחזור לתפריט הראשי
                        /////
                        try {
                            manager.ImportRepositoryFromXML(false, pathString);
                            RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
                            RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
                            dynamicStatusContentP.set("Creating of Repository finished Successfully");

                        } catch (Exception e4) {
                            popUpMessage("Could not import from xml");
                        }

                    } else//not o not s
                    {
                        isValid = false;
                        popUpTextBox("please enter a valid input: O/S");
                        if(InputTextBox==null) return;
                        SorO = InputTextBox;
                        InputTextBox = null;

                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }


    public void popUpChooseBox(String textToUser) {
        Label label = new Label(textToUser);
        //TextField newText = new TextField();
        Button okButton = new Button("OK");

        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);

        ChoiceBox<String> menu = new ChoiceBox();
        for (Branch b :manager.getGITRepository().getBranches())
        {
            String s = b.getBranchName();
            //t.addEventHandler();
            menu.getItems().add(s);
        }
        okButton.setOnAction(event -> {
            InputTextBox = menu.getValue();
            popUpWindow.close();
        });

        okButton.setDefaultButton(true);
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);

        root.getChildren().addAll(label,menu, okButton);

        Scene scene = new Scene(root, 500, 120, Color.WHITE);
        popUpWindow.setTitle("Submit text");
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();

        InputTextBox=null;
    }
    //פונקציה שמייצרת חלון חדש עם תיבת טקסט בפנים עם הטקסט שהיא מקלבת וכשלוחצים לה ok מחזירה לי את הטקסט שכתבו שם בתוך המשתנה מחלקה יוזראינפוט
    public void popUpTextBox(String textToUser) {
        Label label = new Label(textToUser);
        TextField newText = new TextField();
        Button okButton = new Button("OK");

        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);

        okButton.setOnAction(event -> {
            InputTextBox = newText.getText();
            popUpWindow.close();
        });

        okButton.setDefaultButton(true);
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);

        root.getChildren().addAll(label, newText, okButton);

        Scene scene = new Scene(root, 500, 120, Color.WHITE);
        popUpWindow.setTitle("Submit text");
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();


    }
    public void popUpConfirmationBox(String textToUser,String option1, String option2) {
        Label label = new Label(textToUser);
       // TextField newText = new TextField();
        Button firstButton = new Button(option1);
        Button secondButton = new Button(option2);

        Stage popUpWindow = new Stage();
        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        firstButton.setOnAction(event -> {
            InputTextBox = "1";
            popUpWindow.close();
        });
        secondButton.setOnAction(event -> {
            InputTextBox = "2";
            popUpWindow.close();
        });

        firstButton.setDefaultButton(true);
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);

        root.getChildren().addAll(label, firstButton,secondButton);

        Scene scene = new Scene(root, 500, 120, Color.WHITE);
        popUpWindow.setTitle("Submit text");
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();

    }

    //שלוש פעולות בכל פעם שרוצה לקבל משהו מהמשתמש: קריאה לטקסטבוקס, לקיחת מה שחזר משם אל המשתנה הרצוי, איפוס המשתנה אינפוטטקסטבוקס
    @FXML
    public void CreateEmptyRepositoryOnAction() {
        String repName;
String repFolder;
        String pathString = getFolderWithChooser("New repository's location");
        if (pathString != null) {
            popUpTextBox("Choose a name for the folder of the repository:");
            if(InputTextBox==null) return;
            repFolder = InputTextBox;
            InputTextBox = null;

            popUpTextBox("Choose a name for the new repository:");
            if(InputTextBox==null) return;
            repName = InputTextBox;
            InputTextBox = null;

            if (Files.exists(Paths.get(pathString + "\\" + repFolder))) {
                popUpMessage("The wanted repository path already exist as a repository, please try again");
                CreateEmptyRepositoryOnAction();
                return;
            }

            try {
                manager.createEmptyRepositoryFolders(pathString + "\\" + repFolder, repName);
                RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
                RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
                dynamicStatusContentP.set("Creating of Repository finished Successfully");

            } catch (Exception e) {
                popUpMessage("File creation failed, nothing changed");
            }
        }
}

    @FXML
    public void SwitchRepositoryOnAction() {
        switchRepHelper(false, null);
    }


    public void switchRepHelper(boolean isFromXml, Path pathFromXml) {
        if (!isFromXml)//לא מאקסמל, צריך לבקש את הפאט
        {
            String pathString = getFolderWithChooser("Select a directory");
            if (pathString != null) {

                if (!Files.exists(Paths.get(pathString + "\\" + ".magit"))) {//the path exist but not magit
                    popUpMessage("The wanted path is not a part of the magit system, please try again");
                    switchRepHelper(isFromXml, pathFromXml);
                }

                //here the path i have exist, and is a part of the magit system
                //
                try {
                    manager.switchRepository(Paths.get(pathString));
                    RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
                    RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
                    dynamicStatusContentP.set("Import of Repository finished Successfully");

                } catch (IOException e) {
                    popUpMessage("opening zip file failed");
                    return;
                } catch (IllegalArgumentException e) {
                    popUpMessage("was unable to generate folder from commit object");
                    return;
                }
            }
        } else {
            try {
                manager.switchRepository(pathFromXml);
                manager.getGITRepository().getRepositoryName();
                RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
                RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
                dynamicStatusContentP.set("Import of Repository finished Successfully");

            } catch (IOException e) {
                popUpMessage("opening zip file failed");
                return;
            } catch (IllegalArgumentException e) {
                popUpMessage("was unable to generate folder from commit object");
                return;
            }
        }

    }

    //Branches

    @FXML
    public void ShoeAllBranchesOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, no branches to show");
            return;
        }
        //tree = new TreeView();
        // Get the Products
//        TreeItem<String> rootItem = new TreeItem("Branches");
        // Add children to the root
        TreeItem<String> rootItem = new TreeItem("Branches");

        ArrayList<TreeItem<String>> products = new ArrayList<TreeItem<String>>();

        for (Branch b :manager.getGITRepository().getBranches())
        {
            TreeItem t = new TreeItem(b.getBranchName());
            //t.addEventHandler();
            products.add(t);
        }
        rootItem.getChildren().addAll(products);
        // Set the Root Node
        tree.setRoot(rootItem);
        //popUpMessage(manager.getAllBranches());
    }

    @FXML
    public void CreateNewBranchOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, cannot creat new branch");
            return;
        }
        String newBranchName;

        popUpTextBox("Please enter the name of the new branch");
        if(InputTextBox==null) return;
        newBranchName = InputTextBox;//getting the name
        InputTextBox=null;
        if (manager.getGITRepository().getBranchByName(newBranchName) != null) //checking if exist already
        {
            popUpMessage("Branch name already exist, please enter a different name");
            CreateNewBranchOnAction();
            return;

        }

        try {
            manager.CreatBranch(newBranchName);
            dynamicStatusContentP.set("Creation of Branch finished Successfully");
        } catch (IOException e) {
            popUpMessage("Reading text file failed");
        }

        //valid

    }
    @FXML
    public void mergeTwoBranchesOnAction() throws Exception {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, cannot creat new branch");
            return;
        }
        String theirBranchName;

        popUpTextBox("Please enter the name of the second branch");

        theirBranchName = InputTextBox;//getting the name
try {
    if (manager.getGITRepository().getBranchByName(theirBranchName) != null) //checking if exist already
    {
        manager.merge(theirBranchName);
        return;

    } else {
        popUpMessage("Branch does not exist, please enter a name");

    }
}
        catch (InvocationTargetException e)
        {
            out.println(e.getCause());
        }
    }

    @FXML
    public void DeleteBranchOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, no branches to delete");
            return;
        }
        // popUpTextBox("Please enter the name of the branch to delete");
        popUpChooseBox("Choose Branch For Checkout");
        if(InputTextBox==null) return;
        String branchName = InputTextBox;
        InputTextBox = null;
        if (manager.getGITRepository().getBranchByName(branchName) != null) {
            try {
                manager.DeleteBranch(branchName);
                dynamicStatusContentP.set("Branch was deleted Successfully");

            } catch (Exception e) {
                popUpMessage("Erasing the head branch is not a valid action, no changes occurred");
            }
        } else
            popUpMessage("No such Branch exist!");
    }





    @FXML
    public void CheckOutOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, cannot check out");
            return;
        }
       // popUpTextBox("Please enter the name of the branch to move over to");
        popUpChooseBox("Choose Branch For Checkout");
        if(InputTextBox==null) return;
        String branchName = InputTextBox;
        InputTextBox = null;

        if (manager.getGITRepository().getBranchByName(branchName) != null) {
            try {
                manager.ExecuteCommit("", false);
                dynamicStatusContentP.set("Checkout was executed");
                if (manager.getDeletedFiles().size() != 0 ||
                        manager.getUpdatedFiles().size() != 0 ||
                        manager.getCreatedFiles().size() != 0) {
                    popUpTextBox("There are unsaved changes in the WC. would you like to save it before checkout? (yes/no");
                    if (InputTextBox==null) return;
                    String toCommit = InputTextBox;
                    InputTextBox = null;
                    if (toCommit.toLowerCase().equals("yes".toLowerCase())) {
                        try {
                            manager.ExecuteCommit("commit before checkout to " + branchName + "Branch", true);
                            dynamicStatusContentP.set("Checkout was executed");
                        } catch (Exception e) {
                            popUpMessage("Unable to create zip file");
                            return;
                        }
                    }
                }
                manager.executeCheckout(branchName);
                dynamicStatusContentP.set("Checkout was executed");
                manager.getCreatedFiles().clear();
                manager.getDeletedFiles().clear();
                manager.getUpdatedFiles().clear();
            } catch (Exception e) {
                popUpMessage("Unable to create zip file");
            }
        } else popUpMessage("Branch does not exist!");
    }

    @FXML
    public void ResetBranchOnAction() {
        //לעבור על כל הברנצים הקיימים כבר המערכת, אם אחד מהם מצביע על הקומיט שנתנו לי אז גם לוגית לשנות את הבראנצ הנוכחי להיות הבראנצ הזה שקיים וגם בקבצים לשנות את השם שכתוב בתוך הקובץ של הד להיות השם של מי שמצאתי שקיים
        //אם אף אחד מהבראנצים הקיימים לא מצביע על קומיט שכזה ליצור קובץ חדש של בראנצ, תוכנו יהיה השא1 שקיבלתי מהמשתמש, וגם לוגית להוסיף בראנצ חדש לרשימת הראנצים
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, no branches defined yet");
            return;
        }
        Commit newCommit;
        popUpTextBox("Please enter the SHA of commit for head Branch");
        if(InputTextBox==null) return;
        String sha = InputTextBox;
        InputTextBox=null;

        try {
            //לפני שיוצרת יכולה לבדוק אם כבר קיים במערכת לוגית
            newCommit= manager.getCommitFromSha1UsingFiles(manager.getGITRepository().getRepositoryPath().toString(), sha);
            newCommit.setSHA1(sha);}
        catch (Exception e) {
            out.println(e.getMessage());
            return;}
        try {newCommit.setRootFolder(manager.generateFolderFromCommitObject(newCommit.getRootFolderSHA1()));}
        catch(Exception e) {out.println(e.getMessage());}

        //checking if there are open changes in the WC
        try {
            manager.ExecuteCommit("", false);
            if (manager.getDeletedFiles().size() != 0 ||
                    manager.getUpdatedFiles().size() != 0 ||
                    manager.getCreatedFiles().size() != 0) {
                popUpConfirmationBox("There are unsaved changes in the WC. would you like to save it before checkout?","Yes","No");
                out.println("There are unsaved changes in the WC. would you like to save it before checkout? (yes/no");
                if(InputTextBox==null) return;
                String toCommit = InputTextBox;
                InputTextBox=null;
                if (toCommit.toLowerCase().equals("1")) {
                    try{
                        manager.ExecuteCommit("commit before checkout to " +manager.getGITRepository().getHeadBranch() + "Branch", true);}
                    catch(Exception er) {
                        out.println("Unable to create zip file");
                    }
                }
            }

            //text file update:
            manager.updateFile(sha);
            manager.getGITRepository().getHeadBranch().setPointedCommit(newCommit);//////////////////
//השורה הזו משום מה לא באמת משנה את הלוגיקה להצביע מתוך דיפ אל אל הקומיט החדש

            manager.executeCheckout(manager.getGITRepository().getHeadBranch().getBranchName());
            manager.getCreatedFiles().clear();
            manager.getDeletedFiles().clear();
            manager.getUpdatedFiles().clear();
        } catch (Exception er) {
            out.println("Unable to create zip file");
        }

//        try {manager.switchPointingOfHeadBranch(userInput);}//with the wanted sha1, after checking if there are open changes and deciding what to do
//        catch (Exception err) {
//            out.println(err.getMessage());
//            return;}
      //  ShowFilesOfCurrCommit();

    }



    public void popUpMessage(String toShow) {
        Stage popUpWindow = new Stage();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        FlowPane root = new FlowPane();
        Button gotItButton = new Button("Got It");
        gotItButton.setDefaultButton(true);
        gotItButton.setOnAction(event -> {
            popUpWindow.close();
        });
        root.setPadding(new Insets(10));
        root.setHgap(10);
        Label noRep = new Label(toShow);
        root.getChildren().addAll(noRep, gotItButton);
        Scene scene = new Scene(root);
        //Scene scene = new Scene(root, 500, 400, Color.WHITE);
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();
    }

    @FXML
    public void ShowCommitsInfoOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, no files to show");
            return;
        }
        try {
            String s = manager.showFilesOfCommit();
            popUpMessage(s);
        } catch (Exception e) {
            popUpMessage("Unable to generate folder from commit object");
        }
    }


    @FXML
    public String getFolderWithChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);

        File selectedFile = directoryChooser.showDialog(primaryStage);
        if (selectedFile != null)
            return selectedFile.getAbsolutePath();
        else
            return null;
    }

    public void viewCommitsGraph()//הגדולה שתציג את הקומיטים- כנראה תהפוך לחלק מהיו איי הגדול
            //לקרוא לה מתוך
    {
        Graph tree = new Graph();
        createCommits(tree);
        GridPane root = new GridPane();

        final Scene scene = new Scene(root, 700, 400);

        ScrollPane scrollPane = (ScrollPane) scene.lookup("#scrollpaneContainer");
        PannableCanvas canvas = tree.getCanvas();
        //canvas.setPrefWidth(100);
        //canvas.setPrefHeight(100);
        scrollPane.setContent(canvas);

//        Button button = (Button) scene.lookup("#pannableButton");
//        button.setOnAction(e -> {
//            addMoreCommits(tree);
//        });

        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> {
            tree.getUseViewportGestures().set(false);
            tree.getUseNodeGestures().set(false);
        });


    }

    public void createCommits(Graph graph)//יצירת הקומיטים בגרף מתוך הלוגיקה והחזרה שלו
            //        ICell c1 = new CommitNode("20.07.2019 | 22:36:57", "Menash", "initial commit");   יצירת נוד בנראות
            //
    {

        final Model model = graph.getModel();

        graph.beginUpdate();

        ICell c1 = new CommitNode("20.07.2019 | 22:36:57", "Menash", "initial commit");
        ICell c2 = new CommitNode("21.07.2019 | 22:36:57", "Moyshe Ufnik", "developing some feature");
        ICell c3 = new CommitNode("20.08.2019 | 22:36:57", "Old Majesty, The FU*!@N Queen of england", "A very long commit that aims to see if and where the line will be cut and how it will look a like... very Interesting");
        ICell c4 = new CommitNode("20.09.2019 | 13:33:57", "el professore", "yet another commit");
        ICell c5 = new CommitNode("30.10.2019 | 11:36:54", "bella chao", "merge commit of 'yet another commit' and other commit");

        model.addCell(c1);
        model.addCell(c2);
        model.addCell(c3);
        model.addCell(c4);
        model.addCell(c5);

        final Edge edgeC12 = new Edge(c1, c2);
        model.addEdge(edgeC12);

        final Edge edgeC23 = new Edge(c2, c4);
        model.addEdge(edgeC23);

        final Edge edgeC45 = new Edge(c4, c5);
        model.addEdge(edgeC45);

        final Edge edgeC13 = new Edge(c1, c3);
        model.addEdge(edgeC13);

        final Edge edgeC35 = new Edge(c3, c5);
        model.addEdge(edgeC35);

        graph.endUpdate();

        graph.layout(new CommitTreeLayout());


    }


}

//משימות
//לעשות שאנטר ילחץ על הכפתור של אוקיי
//לסדר את התצוגה של הקבצים בתיקייה
//
