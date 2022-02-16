package components.dashboard;

import components.app.AppMainController;
import components.dashboard.graphs.UploadedGraphsTableController;
import graph.GraphGeneralData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML
    private ScrollPane MainPane;

    @FXML
    private BorderPane borderPane;

    private AppMainController mainController;

    @FXML
    private Label label_SelectedGraphName;


    @FXML private ScrollPane component_GraphTable;
    @FXML private UploadedGraphsTableController graphTableController;


    private GraphGeneralData currentlyChosenGraph;


    @FXML
    public void initialize() {
        graphTableController.getTableView_GraphDetails().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            currentlyChosenGraph = newValue;
            if (currentlyChosenGraph != null) {
                label_SelectedGraphName.setText(currentlyChosenGraph.getGraphName());
            } else {
                label_SelectedGraphName.setText("None");
            }
        });
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }

    public UploadedGraphsTableController getGraphTableController() {
        return graphTableController;
    }
}


