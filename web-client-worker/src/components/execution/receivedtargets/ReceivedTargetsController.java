package components.execution.receivedtargets;

import graph.GraphDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import task.enums.TaskResult;
import task.enums.TaskType;

import java.util.List;

public class ReceivedTargetsController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML    private TableView<TargetDTOWorkerDetails> tableView_Targets;

    @FXML    private TableColumn<TargetDTOWorkerDetails, String> tableColumn_ExecutionName;
    @FXML    private TableColumn<TargetDTOWorkerDetails, TaskType> tableColumn_TaskType;
    @FXML    private TableColumn<TargetDTOWorkerDetails, String> tableColumn_TargetName;
    @FXML    private TableColumn<TargetDTOWorkerDetails, TaskResult> tableColumn_TaskResult;
    @FXML    private TableColumn<TargetDTOWorkerDetails, Integer> tableColumn_Paycheck;

    @FXML    private TextArea textArea_Logs;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */

    

    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        tableColumn_ExecutionName.setCellValueFactory(new PropertyValueFactory<TargetDTOWorkerDetails, String>("executionName"));
        tableColumn_TaskType.setCellValueFactory(new PropertyValueFactory<TargetDTOWorkerDetails, TaskType>("taskType"));
        tableColumn_TargetName.setCellValueFactory(new PropertyValueFactory<TargetDTOWorkerDetails, String>("targetName"));
        tableColumn_TaskResult.setCellValueFactory(new PropertyValueFactory<TargetDTOWorkerDetails, TaskResult>("taskResult"));
        tableColumn_Paycheck.setCellValueFactory(new PropertyValueFactory<TargetDTOWorkerDetails, Integer>("paycheck"));
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------- POPULATE DATA METHODS --------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * This method updates an existing row if one exists, or adds the new data altogether.
     * @param targetDTOWorkerDetails
     */
//    public synchronized void addOrUpdateTarget(TargetDTOWorkerDetails targetDTOWorkerDetails) {
//        boolean doesExist = false;
//
//        for (TargetDTOWorkerDetails targetDTO : tableView_Targets.getItems()) {
//            if (matchingRow)
//        }
//
//        if (existsInTable(targetDTOWorkerDetails)) {
//            updateRow(targetDTOWorkerDetails);
//        } else {
//            tableView_Targets.getItems().add(targetDTOWorkerDetails);
//        }
//    }
//
//    private boolean existsInTable(TargetDTOWorkerDetails targetDTOWorkerDetails) {
//
//    }

    /**
     * This method removes all the current targets and adds the parameter.
     */
    public void clearAndSet(List<TargetDTOWorkerDetails> targets) {
        this.tableView_Targets.getItems().clear();
        this.tableView_Targets.getItems().addAll(targets);
    }

    public void specificTargetUpdated(TargetDTOWorkerDetails updatedTarget) {
        for (TargetDTOWorkerDetails targetDTO : tableView_Targets.getItems()) {
            if (sameTargetInRow(targetDTO, updatedTarget)) {
                updateTargetInRow(targetDTO, updatedTarget);
            }
        }
    }

    private void updateTargetInRow(TargetDTOWorkerDetails targetFromRow, TargetDTOWorkerDetails targetWithUpdatedDetails) {
        targetFromRow.setTaskResult(targetWithUpdatedDetails.getTaskResult());
    }

    private boolean sameTargetInRow(TargetDTOWorkerDetails targetFromRow, TargetDTOWorkerDetails targetToCheck) {
        boolean isSameTarget = true;

        if (! targetFromRow.getExecutionName().equals(targetToCheck.getExecutionName())) {
            isSameTarget = false;
        }

        if (! targetFromRow.getTargetName().equals(targetToCheck.getTargetName())) {
            isSameTarget = false;
        }

        return isSameTarget;
    }
}
