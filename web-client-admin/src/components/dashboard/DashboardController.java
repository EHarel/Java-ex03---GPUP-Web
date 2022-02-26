package components.dashboard;

import componentcode.executiontable.ExecutionDTOTable;
import componentcode.executiontable.ExecutionListRefresher;
import components.app.AppMainController;
import components.dashboard.execution.ExecutionTableAdminController;
import components.dashboard.graphs.UploadedGraphsTableController;
import components.dashboard.users.UserTableController;
import graph.GraphDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class DashboardController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML private ScrollPane MainPane;
    @FXML private BorderPane borderPane;

    @FXML private Label label_SelectedGraphName;
    @FXML private Label label_SelectedExecution;
    @FXML private Label label_ExecutionSelectionMessage;

    @FXML private ScrollPane component_UserTable;
    @FXML private UserTableController component_UserTableController;

    @FXML private ScrollPane component_GraphTable;
    @FXML private UploadedGraphsTableController component_GraphTableController;

    @FXML private ScrollPane component_ExecutionTable;
    @FXML private ExecutionTableAdminController component_ExecutionTableController;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private AppMainController mainController;
    private GraphDTO currentlyChosenGraph;
    private String currentlyChosenExecutionName;



    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
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
        component_ExecutionTableController.setDashboardController(this);
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        this.component_ExecutionTableController.setMainController(mainController);
    }

    public UploadedGraphsTableController getGraphTableController() {
        return component_GraphTableController;
    }

    public String getChosenGraphName() {
        String selectedGraphName = null;

        GraphDTO selectedGraph = component_GraphTableController.getCurrentlySelectedGraph();

        if (selectedGraph != null) {
            selectedGraphName = selectedGraph.getGraphName();
        }

        return selectedGraphName;
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void addGraphs(GraphDTO[] graphDTOs) {
        mainController.addGraphs(graphDTOs);
    }

    public void setActive(boolean isActive) {
        component_GraphTableController.setActive(isActive);
        component_UserTableController.setActive(isActive);
        component_ExecutionTableController.setActive(isActive);
    }

    public void event_GraphRowChanged() {
        GraphDTO currentlySelectedGraphData = component_GraphTableController.getCurrentlySelectedGraph();

        String name = "None";
        if (currentlySelectedGraphData != null) {
            name = currentlySelectedGraphData.getGraphName();
        }

        label_SelectedGraphName.setText(name);

        mainController.event_NewGraphSelected();
    }

    public void event_ExecutionRowSelected(ExecutionDTOTable currentlySelectedRow) {
        String msg = "";
        Color color = Color.BLACK;
        if (validNewExecution(currentlySelectedRow)) {
            msg = "Execution selected! You may view its details in the panel.";
            color = Color.GREEN;
        } else {
            msg = "You are not the creator. Limited options available (only graph analysis)";
            color = Color.RED;
        }

        AppMainController.AnimationFadeSingle(null, label_ExecutionSelectionMessage, msg, color);
        currentlyChosenExecutionName = currentlySelectedRow.getExecutionName();
        label_SelectedExecution.setText(currentlyChosenExecutionName);
        mainController.event_NewExecutionSelected(currentlySelectedRow);
    }

    private boolean validNewExecution(ExecutionDTOTable currentlySelectedRow) {
        boolean validNewExecution = true;

        // TODO: change after debugging!
        // Check if execution uploader is current user
        if (! currentlySelectedRow.getCreatingUser().equals(mainController.getUsername())) {
            validNewExecution = false;
        }

        // Check if already selected
  // TODO: is this bad?
//        if ( currentlySelectedRow.getExecutionName().equals(label_SelectedExecution.getText())) {
//            validNewExecution = false;
//        }

        return validNewExecution;
    }

    public String getChosenExecutionName() {
        return currentlyChosenExecutionName;
    }
}


