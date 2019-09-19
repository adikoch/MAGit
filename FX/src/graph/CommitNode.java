package graph;


import Classess.Commit;
import MainFolder.MainController;
import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;

public class CommitNode extends AbstractCell {

    private String timestamp;
    private String committer;
    private String message;
    public String branches="";
    private CommitNodeController commitNodeController= new CommitNodeController();
    public Commit commitRelated;
    public MainController mainController;

    public static Button commitButton;

    //
    public int YCoordinate;
    public int XCoordinate;

    public CommitNode(Commit commit, MainController mainController)
    {
        this.commitRelated=commit;
        this.committer=commit.getChanger();
        this.message=commit.getDescription();
        this.timestamp=commit.getCreationDate();
        //this.commitNodeController.Initialize();
        this.mainController=mainController;
    }

    public CommitNode(String timestamp, String committer, String message) {
        this.timestamp = timestamp;
        this.committer = committer;
        this.message = message;
        //this.commitNodeController?
        this.commitButton= new Button();
        //this.commitNodeController.Initialize();

    }



    @Override
    public Region getGraphic(Graph graph) {

        try {

            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/graph/commitNode.fxml");
            fxmlLoader.setLocation(url);
            GridPane root = fxmlLoader.load(url.openStream());

            commitNodeController = fxmlLoader.getController();
            commitNodeController.setCommitMessage(message);
            commitNodeController.setCommitter(committer);
            commitNodeController.setCommitTimeStamp(timestamp);
            commitNodeController.setCommitNode(this);
            commitNodeController.setMainController(this.mainController);

            return root;
        } catch (IOException e) {
            return new Label("Error when tried to create graphic node !");
        }
    }

    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(commitNodeController.getCircleRadius());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommitNode that = (CommitNode) o;

        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        return timestamp != null ? timestamp.hashCode() : 0;
    }

    public void setBranches(String branch){
        branches= branches + branch;

    }
}
