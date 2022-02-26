package components.graph.general;

import components.graph.alldata.GraphAllDataController;
import graph.DependenciesGraph;
import graph.GraphDTO;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class GraphGeneralDetailsController {
    private GraphAllDataController setGraphController;

    public Label getGraphNameLabel() {
        return graphNameLabel;
    }

    public Label getTargetCountLabel() {
        return targetCountLabel;
    }

    public Label getNumberOfRootsLabel() {
        return numberOfRootsLabel;
    }

    public Label getNumberOfMiddlesLabel() {
        return numberOfMiddlesLabel;
    }

    public Label getNumberOfLeavesLabel() {
        return numberOfLeavesLabel;
    }

    public Label getNumberOfIndependentsLabel() {
        return numberOfIndependentsLabel;
    }

    @FXML private Label graphNameLabel;
    @FXML private Label targetCountLabel;
    @FXML private Label numberOfRootsLabel;
    @FXML private Label numberOfMiddlesLabel;
    @FXML private Label numberOfLeavesLabel;
    @FXML private Label numberOfIndependentsLabel;

    @FXML private Tooltip tooltip_GraphName;


    private Parent scene;


    public void populateData(DependenciesGraph graph) {
        if (graph != null) {
            GraphDTO graphDTO = graph.toDTO();

            populateData(graphDTO);
        }
    }

    public void populateData(GraphDTO graphDTO) {
        if (graphDTO != null) {
            String graphName = graphDTO.getGraphName();
            this.getGraphNameLabel().setText(graphName);
            this.tooltip_GraphName.setText(graphName);
            this.getNumberOfIndependentsLabel().setText(String.valueOf(graphDTO.getCountIndependents()));
            this.getNumberOfLeavesLabel().setText(String.valueOf(graphDTO.getCountLeaves()));
            this.getNumberOfMiddlesLabel().setText(String.valueOf(graphDTO.getCountMiddles()));
            this.getNumberOfRootsLabel().setText(String.valueOf(graphDTO.getCountRoots()));
            this.getTargetCountLabel().setText(String.valueOf(graphDTO.getCountAllTargets()));
        }
    }

    public void setScene(Parent parent) {
        this.scene = parent;
    }

    public Parent getScene() { return this.scene; }

    public void setGraphController(GraphAllDataController graphAllDataController) {
        this.setGraphController = graphAllDataController;
    }

    public void clear() {
        this.getGraphNameLabel().setText("No graph selected");
        this.getNumberOfIndependentsLabel().setText("");
        this.getNumberOfLeavesLabel().setText("");
        this.getNumberOfMiddlesLabel().setText("");
        this.getNumberOfRootsLabel().setText("");
        this.getTargetCountLabel().setText("");
    }
}

