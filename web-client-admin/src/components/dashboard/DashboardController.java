package components.dashboard;

import components.app.AppMainController;
import components.dashboard.execution.ExecutionTableAdminController;
import components.dashboard.graphs.UploadedGraphsTableController;
import components.dashboard.users.UserTableController;
import events.LoginPerformedListener;
import graph.GraphDTO;
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

    @FXML private ScrollPane component_UserTable;
    @FXML private UserTableController component_UserTableController;

    @FXML private ScrollPane component_GraphTable;
    @FXML private UploadedGraphsTableController component_GraphTableController;

    @FXML private ScrollPane component_ExecutionTable;
    @FXML private ExecutionTableAdminController component_ExecutionTableController;


    private GraphDTO currentlyChosenGraph;


    @FXML
    public void initialize() {
//        graphTableController.getTableView_GraphDetails().getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            currentlyChosenGraph = newValue;
//            if (currentlyChosenGraph != null) {
//                label_SelectedGraphName.setText(currentlyChosenGraph.getGraphName());
//            } else {
//                label_SelectedGraphName.setText("None");
//            }
//        });

        component_GraphTableController.setDashboardController(this);
        component_ExecutionTableController.setMainController(mainController);
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }

    public UploadedGraphsTableController getGraphTableController() {
        return component_GraphTableController;
    }

    public void setActive(boolean isActive) {
        component_GraphTableController.setActive(isActive);
        component_UserTableController.setActive(isActive);
        component_ExecutionTableController.setActive(isActive);
    }

    public void rowChangedEvent() {
        GraphDTO currentlySelectedGraphData = component_GraphTableController.getCurrentlySelectedGraph();

        String name = "None";
        if (currentlySelectedGraphData != null) {
            name = currentlySelectedGraphData.getGraphName();
        }

        label_SelectedGraphName.setText(name);

        mainController.newGraphChosen();
    }

    public String getChosenGraphName() {
        String selectedGraphName = null;

        GraphDTO selectedGraph = component_GraphTableController.getCurrentlySelectedGraph();

        if (selectedGraph != null) {
            selectedGraphName = selectedGraph.getGraphName();
        }

        return selectedGraphName;
    }

    public void addGraphs(GraphDTO[] graphDTOs) {
        mainController.addGraphs(graphDTOs);
    }
}


