package components.task.execution.executiondetail;

import componentcode.executiontable.ExecutionDTOTable;
import components.graph.general.GraphGeneralDetailsController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class ExecutionDetailsController {
    @FXML private Label label_ExecutionName;
    @FXML private Label label_ExecutionType;
    @FXML private Label label_ExecutionState;
    @FXML private Label label_WorkerCount;

    @FXML private Label label_TargetCount_WAITING;
    @FXML private Label label_TargetCount_IN_PROCESS;
    @FXML private Label label_ExecutionProgress;

    @FXML private ScrollPane component_GeneralDetailsStartGraph;
    @FXML private GraphGeneralDetailsController component_GeneralDetailsStartGraphController;

    @FXML private ScrollPane component_GeneralDetailsEndGraph;
    @FXML private GraphGeneralDetailsController component_GeneralDetailsEndGraphController;



    public void executionChosen(ExecutionDTOTable executionDTOTable) {
        if (executionDTOTable == null) {
            return;
        }

        label_ExecutionName.setText(executionDTOTable.getExecutionName());
        label_ExecutionType.setText(executionDTOTable.getTaskType().name());
        label_ExecutionState.setText(executionDTOTable.getExecutionStatus().name());
        label_WorkerCount.setText(String.valueOf(executionDTOTable.getTotalWorkers()));

        label_TargetCount_WAITING.setText(String.valueOf(executionDTOTable.getTargetStateCount_WAITING()));
        label_TargetCount_IN_PROCESS.setText(String.valueOf(executionDTOTable.getTargetStateCount_IN_PROCESS()));
        label_ExecutionProgress.setText(String.valueOf(executionDTOTable.getExecutionProgress()));

        component_GeneralDetailsStartGraphController.populateData(executionDTOTable.getStartGraphDTO());
        component_GeneralDetailsEndGraphController.populateData(executionDTOTable.getEndGraphDTO());
    }

    public void clear() {
        label_ExecutionName.setText("");
        label_ExecutionType.setText("");
        label_ExecutionState.setText("");
        label_WorkerCount.setText("");

        label_TargetCount_WAITING.setText("");
        label_TargetCount_IN_PROCESS.setText("");
        label_ExecutionProgress.setText("");

        component_GeneralDetailsEndGraphController.clear();
        component_GeneralDetailsStartGraphController.clear();
    }
}
