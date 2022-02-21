package components.dashboard.execution;

import componentcode.executiontable.ExecutionDTOTable;
import componentcode.executiontable.ExecutionTableControllerShared;
import components.app.AppMainController;
import components.dashboard.DashboardWorkerController;
import events.LoginPerformedListener;
import graph.GraphDTO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import task.execution.ExecutionStatus;

public class ExecutionTableWorkerController implements LoginPerformedListener {

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

    @FXML
    private TableColumn<ExecutionDTOTable, Boolean> tableColumn_Subscribed;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private ExecutionTableControllerShared sharedController;
    private AppMainController mainController;
    private ExecutionDTOTable currentlySelectedRow;
    private DashboardWorkerController dashboardController;


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
        tableColumn_Subscribed.setCellValueFactory(new PropertyValueFactory<ExecutionDTOTable, Boolean>("userIsSubscribed"));


        setDoubleClickEvent();

        sharedController = new ExecutionTableControllerShared(tableView_Executions);
        setActive(false);
        sharedController.startListRefresher();
    }

    private void setDoubleClickEvent() {
        tableView_Executions.setRowFactory( tv -> {
            TableRow<ExecutionDTOTable> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ExecutionDTOTable rowData = row.getItem();
                    System.out.println("[Execution TableView] chosen row data: " + rowData);

                    currentlySelectedRow = row.getItem();
                    dashboardController.rowChangedEvent(currentlySelectedRow);
                }
            });

            return row ;
        });
    }

    public void setActive(boolean isActive) {
        sharedController.getAutoUpdateProperty().set(isActive);
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        this.mainController.addEventListener_LoginPerformed(this);

    }

    public void setDashboardController(DashboardWorkerController dashboardWorkerController) {
        this.dashboardController = dashboardWorkerController;
    }

    public ExecutionDTOTable getCurrentlySelectedRow() {
        return currentlySelectedRow;
    }

    @Override
    public void loginPerformed(String username) {
        this.sharedController.setUsername(username);
    }
}