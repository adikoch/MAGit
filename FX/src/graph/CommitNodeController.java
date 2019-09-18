package graph;

import MainFolder.MainController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;

public class CommitNodeController {

    @FXML private Label commitTimeStampLabel;
    @FXML private Label messageLabel;
    @FXML private Label committerLabel;
    @FXML private Circle CommitCircle;
    public CommitNode commitNode;

    private MainController mainController;


    @FXML
    public void initialize()
    {
        //branchParentSha1
        setContextMenu();
    }

    public void setContextMenu()
    {
        ContextMenu contextMenu= new ContextMenu();

        MenuItem createNewPointingBranch= new MenuItem("Create new pointing branch");
        createNewPointingBranch.setOnAction(e-> {
            mainController.CreateNewBranchToCommit(commitNode.commitRelated);
        });

        MenuItem resetHeadBranchToThisCommit= new MenuItem("Reset head Branch");
        resetHeadBranchToThisCommit.setOnAction(e->{
//            mainController.manager.getGITRepository().getHeadBranch().setPointedCommit(mainController.manager.getGITRepository().getCommitMap().get(commitNode.));
            mainController.ResetBranchToCommit(commitNode.commitRelated);
        });

        MenuItem mergePointingCommitWithHeadBranch= new MenuItem("Merge with head branch");
        mergePointingCommitWithHeadBranch.setOnAction(e->{
            try {
                mainController.mergeWithHeadBranch(commitNode.commitRelated);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        MenuItem deletePointingBranches= new MenuItem("Delete pointing branch");
        deletePointingBranches.setOnAction((e->{
            mainController.deletePointingBranches(commitNode.commitRelated);
        }));



        contextMenu.getItems().addAll(createNewPointingBranch, resetHeadBranchToThisCommit, mergePointingCommitWithHeadBranch, deletePointingBranches);

        CommitCircle.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton()==  MouseButton.PRIMARY)
                {
                    mainController.ShowCommitHierarchy(commitNode.commitRelated);
                    mainController.showCommitsInfo(commitNode.commitRelated);
                }

                else
                {
                    contextMenu.show(CommitCircle, event.getScreenX(), event.getScreenY());
                }
            }
        });


    }

    public void setCommitTimeStamp(String timeStamp) {
        commitTimeStampLabel.setText(timeStamp);
        commitTimeStampLabel.setTooltip(new Tooltip(timeStamp));
    }

    public void setCommitter(String committerName) {
        committerLabel.setText(committerName);
        committerLabel.setTooltip(new Tooltip(committerName));
    }

    public void setCommitMessage(String commitMessage) {
        messageLabel.setText(commitMessage);
        messageLabel.setTooltip(new Tooltip(commitMessage));
    }

    public int getCircleRadius() {
        return (int)CommitCircle.getRadius();
    }

    public void setCommitNode(CommitNode commitNode) {
        this.commitNode = commitNode;
    }

    public void setMainController(MainController mainController)
    {
        this.mainController=mainController;
    }
}
