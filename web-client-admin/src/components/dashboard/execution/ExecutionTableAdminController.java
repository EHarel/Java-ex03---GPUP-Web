package components.dashboard.execution;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ExecutionTableAdminController {

    @FXML
    private ScrollPane mainPane;

    @FXML
    private TableView<?> tableView_Executions;

    @FXML
    private TableColumn<?, ?> tableColumn_ExecutionName;

    @FXML
    private TableColumn<?, ?> tableColumn_UploadingUser;

    @FXML
    private TableColumn<?, ?> tableColumn_GraphName;

    @FXML
    private TableColumn<?, ?> tableColumn_Count_Total;

    @FXML
    private TableColumn<?, ?> tableColumn_Count_Independent;

    @FXML
    private TableColumn<?, ?> tableColumn_Count_Leaf;

    @FXML
    private TableColumn<?, ?> tableColumn_Count_Middle;

    @FXML
    private TableColumn<?, ?> tableColumn_Count_Root;

    @FXML
    private TableColumn<?, ?> tableColumn_TotalPrice;

    @FXML
    private TableColumn<?, ?> tableColumn_Workers;

    @FXML
    private TableColumn<?, ?> tableColumn_Status;

}