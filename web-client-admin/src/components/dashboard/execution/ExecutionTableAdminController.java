package components.dashboard.execution;

import componentcode.executiontable.ExecutionDTOTable;
import componentcode.executiontable.ExecutionTableControllerShared;
import componentcode.usertable.UserTableControllerShared;
import components.app.AppMainController;
import components.graph.targettable.TargetDTOTable;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import task.execution.ExecutionDTO;
import task.execution.ExecutionStatus;

public class ExecutionTableAdminController {

    @FXML
    private ScrollPane mainPane;

    @FXML
    private TableView<ExecutionDTOTable> tableView_Executions;

    @FXML
    private TableColumn<ExecutionDTOTable, String> tableColumn_ExecutionName;

    @FXML
    private TableColumn<ExecutionDTOTable, String> tableColumn_UploadingUser;

    @FXML
    private TableColumn<ExecutionDTOTable, String> tableColumn_GraphName;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_TargetCount_Total;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_TargetCount_Independent;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_TargetCount_Leaf;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_TargetCount_Middle;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_TargetCount_Root;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_TotalPrice;

    @FXML
    private TableColumn<ExecutionDTOTable, Integer> tableColumn_Workers;

    @FXML
    private TableColumn<ExecutionDTOTable, ExecutionStatus> tableColumn_Status;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private ExecutionTableControllerShared sharedController;
    private AppMainController mainController;


    @FXML
    public void initialize() {
        tableColumn_ExecutionName.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, String>("executionName"));
        tableColumn_UploadingUser.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, String>("creatingUser"));
        tableColumn_GraphName.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, String>("graphName"));
        tableColumn_TargetCount_Total.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("targetCount_Total"));
        tableColumn_TargetCount_Independent.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("targetCount_Independent"));
        tableColumn_TargetCount_Leaf.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("targetCount_Leaf"));
        tableColumn_TargetCount_Middle.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("targetCount_Middle"));
        tableColumn_TargetCount_Root.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("targetCount_Root"));
        tableColumn_TotalPrice.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("totalPrice"));
        tableColumn_Workers.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Integer>("totalWorkers"));
        tableColumn_Status.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, ExecutionStatus>("executionStatus"));


        sharedController = new ExecutionTableControllerShared(tableView_Executions);
        setActive(false);
        sharedController.startListRefresher();
    }

    public void setActive(boolean isActive) {
        sharedController.getAutoUpdateProperty().set(isActive);
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }
}
