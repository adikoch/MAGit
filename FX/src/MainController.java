import Classess.*;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import generated.MagitRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    Label CommitText;

    /*
     *
     * */

    public SimpleStringProperty CommitTextP;
    public SimpleStringProperty RepositoryNameP;
    public SimpleStringProperty RepositoryPAthP;
    public SimpleStringProperty dynamicStatusContentP;

    public Stage primaryStage;
    public GitManager manager;

    public String InputTextBox = null;

    public MainController() {
        RepositoryNameP = new SimpleStringProperty();
        RepositoryPAthP = new SimpleStringProperty();
        dynamicStatusContentP = new SimpleStringProperty();
        CommitTextP = new SimpleStringProperty();
    }


    public void initialize() {
        RepName.textProperty().bind(RepositoryNameP);
        RepPath.textProperty().bind(RepositoryPAthP);
        dynamicStatusContent.textProperty().bind(dynamicStatusContentP);
        CommitText.textProperty().bind(CommitTextP);
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
                    sb.append ("Repository's Name:" + manager.getGITRepository().getRepositoryName()+"\n"
                    +"Repository's Path:" + manager.getGITRepository().getRepositoryPath().toString() + "\n" +
                            "Repository's User:" + manager.getUserName()+"\n");
                    try {
                        manager.ExecuteCommit("", false);
                        sb.append(System.lineSeparator());
                        sb.append("Deleted Files's Paths:" + manager.getDeletedFiles()+'\n');
                        sb.append("Added Files's Paths:" + manager.getCreatedFiles()+'\n');
                        sb.append("Updated Files's Paths:" + manager.getUpdatedFiles()+'\n');
                        manager.getCreatedFiles().clear();
                        manager.getDeletedFiles().clear();
                        manager.getUpdatedFiles().clear();

                        root.getChildren().addAll( new Label(sb.toString()),closeButton);

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
        //לבקש שא1 של קומיט
        //ליצור ממנו פולדר
        //להכנס ברקורסיה על הפולדרים ולכל אחד אם בלוב ליצור בלי חץ ולהחזיר אם הוא פולדר ליצור כזה עם חץ ולהוסיף אליו את כל מה שחוזר מהרקורסיה
        popUpTextBox("Please enter the sha1 of the wanted commit");
        String commitSha1= InputTextBox;
        InputTextBox= null;

        try{
            Commit commit= manager.getCommitFromSha1UsingFiles(manager.getGITRepository().getRepositoryPath().toString(),commitSha1);
            Folder folderOfCommit= manager.generateFolderFromCommitObject(commit.getRootFolderSHA1());
            //Folder rappingFolder= new Folder();
            //ArrayList<Folder.Component> arrayList= new ArrayList<Folder.Component>();
            //arrayList.add()
            //rappingFolder.setComponents()//(new ArrayList<Folder.Component>(folderOfCommit));//folderOfCommit);
            //TreeItem<String> toShow=showTreeLookRec(folderOfCommit);//להכניס אותו לסצנה ולהראות
            TreeItem<String> toShow= new TreeItem<>();
            toShow.setValue("commit's info:");

            toShow.getChildren().add(showTreeLookRec(folderOfCommit));
            //showing
            TreeView<String> tree = new TreeView<> (toShow);
            StackPane root = new StackPane();
            root.getChildren().add(tree);
            primaryStage.setScene(new Scene(root, 300, 250));
            primaryStage.show();


        }
        catch(Exception e){popUpTextBox("Unable to generate commit using the files");}

        try {
            String FilesOfCommit = manager.showFilesOfCommit();
            out.println(FilesOfCommit);


        } catch (Exception e) {
            popUpMessage("Opening zip file failed");
        }
    }


    public TreeItem<String> showTreeLookRec(Folder folder){
        TreeItem<String> folderItem = new TreeItem<String>();// (c.getComponentName());

        for(Folder.Component c: folder.getComponents())
        {
            if(c.getComponentType().equals(FolderType.Folder))
            {
                folderItem.setValue(c.getComponentName());
                folderItem.setExpanded(true);
                Folder recFolder= (Folder) c.getDirectObject();
                folderItem.getChildren().add(showTreeLookRec(recFolder));
            }
            if(c.getComponentType().equals(FolderType.Blob))
            {
                folderItem.setExpanded(false);
                folderItem.setValue(c.getComponentName());
            }

        }
        return folderItem;

    }

    //Repository

    @FXML
    public void DefineUserNameOnAction() {
        if (manager.getGITRepository() != null) {

            popUpTextBox("Please enter the new username:");
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


        return selectedFile.getAbsolutePath();


    }

    @FXML
    public void ImportRepFromXmlOnAction() {

        String pathString = null;
        String SorO = null;
        boolean isValid = false;

//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select XMl file");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
//        File selectedFile = fileChooser.showOpenDialog(primaryStage);
//        if (selectedFile == null) {
//            return;
//        }
//        = selectedFile.getAbsolutePath();
        String absolutePath = getFileWithChooser("xml");


        //i have a valid path from the user, can call ImportRepositoryFromXML with xmlPath
        try {
            manager.ImportRepositoryFromXML(true, absolutePath);
            RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
            RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
            dynamicStatusContentP.set("Import of Repository finished Successfully");
        } catch (IOException e) {//קיים כבר רפוזטורי עם אותו שם באותה התיקייה שקיבלנו מהאקסמל

            popUpTextBox("There is already a repository with the same name at the wanted location\nPlease enter O to over write the existing, S to switch to the existing one");
            SorO = InputTextBox;
            InputTextBox = null;
            while (!isValid) {
                if (SorO.toLowerCase().equals("s")) // switch to the existing repo
                {//להוסיף את החלק שבודק האם מגיט מהסוויצ רפוזטורי
                    isValid = true;
                    manager.setGITRepository(null);
                    switchRepHelper(true, Paths.get(e.getMessage()));
                } else if (SorO.toLowerCase().equals("o")) { // overwrite the existing
                    isValid = true;
                    manager.deleteFilesInFolder(new File(e.getMessage()));
                    manager.deleteFilesInFolder(new File(e.getMessage() + "\\.magit"));
                    try {
                        Files.delete((Paths.get(e.getMessage())));

                    }// delete the existing, prepering for loading , need the path of the folder to erase(c:\repo1)
                    catch (IOException e3) {
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
                    SorO = InputTextBox;
                    InputTextBox = null;

                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


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
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);

        root.getChildren().addAll(label, newText, okButton);

        Scene scene = new Scene(root, 500, 120, Color.WHITE);
        popUpWindow.setTitle("Submit text");
        popUpWindow.setScene(scene);
        popUpWindow.showAndWait();
    }

    //שלוש פעולות בכל פעם שרוצה לקבל משהו מהמשתמש: קריאה לטקסטבוקס, לקיחת מה שחזר משם אל המשתנה הרצוי, איפוס המשתנה אינפוטטקסטבוקס
    @FXML
    public void CreateEmptyRepositoryOnAction() {
        String repName;

        String pathString = getFolderWithChooser("New repository's location");

        popUpTextBox("Choose a name for the new repository:");
        repName = InputTextBox;
        InputTextBox = null;
        if (Files.exists(Paths.get(pathString + "\\" + repName))) {
            popUpMessage("The wanted name already exist, please try again");
            CreateEmptyRepositoryOnAction();
            RepositoryNameP.set(manager.getGITRepository().getRepositoryName());
            RepositoryPAthP.setValue(manager.getGITRepository().getRepositoryPath().toString());
            dynamicStatusContentP.set("Creating of Repository finished Successfully");

            return;
        }

        try {
            manager.createEmptyRepositoryFolders(pathString, repName);
            dynamicStatusContentP.set("Creating of Repository finished Successfully");

        } catch (Exception e) {
            popUpMessage("File creation failed, nothing changed");
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
        } else {
            try {
                manager.switchRepository(pathFromXml);
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

        popUpMessage(manager.getAllBranches());
    }

    @FXML
    public void CreateNewBranchOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, cannot creat new branch");
            return;
        }
        String newBranchName;

        popUpTextBox("Please enter the name of the new branch");

        newBranchName = InputTextBox;//getting the name

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
    public void DeleteBranchOnAction() {
        if (manager.getGITRepository() == null) {
            out.println("There is no repository defined, no branches to delete");
            return;
        }
        popUpTextBox("Please enter the name of the branch to delete");
        String branchName = InputTextBox;
        InputTextBox = null;
        if (manager.getGITRepository().getBranchByName(branchName) != null) {
            try {
                manager.DeleteBranch(branchName);
                dynamicStatusContentP.set("Branch was deleted Successfully");

            } catch (Exception e) {
                popUpMessage("Erasing the head branch is not a valid action, no changes occurred");
            }
            popUpMessage("No such Branch exist!");

        }


    }

    @FXML
    public void CheckOutOnAction() {
        if (manager.getGITRepository() == null) {
            popUpMessage("There is no repository defined, cannot check out");
            return;
        }
        popUpTextBox("Please enter the name of the branch to move over to");
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

    }

    public void popUpMessage(String toShow) {
        Stage popUpWindow = new Stage();

        popUpWindow.initModality(Modality.APPLICATION_MODAL);
        FlowPane root = new FlowPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        Label noRep = new Label(toShow);
        root.getChildren().addAll(noRep);
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

        return selectedFile.getAbsolutePath();
    }
}

//משימות
//לעשות שאנטר ילחץ על הכפתור של אוקיי
//לסדר את התצוגה של הקבצים בתיקייה
//
