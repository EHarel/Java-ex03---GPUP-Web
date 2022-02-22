package components.execution.panelworker;

import components.app.AppMainController;
import components.execution.receivedtargets.ReceivedTargetsController;
import components.execution.receivedtargets.TargetDTOWorkerDetails;
import components.login.LoginPerformedListenerWorker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class ExecutionAndTaskPanelController implements LoginPerformedListenerWorker {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML
    private Label label_TotalThreadCount;

    @FXML
    private Label label_ActiveThreadCount;

    @FXML
    private Label label_PointsEarnedCount;

    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private AppMainController mainController;

    @FXML private ScrollPane component_ReceivedTargets;
    @FXML private ReceivedTargetsController component_ReceivedTargetsController;



    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */




    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        this.label_ActiveThreadCount.textProperty().bind(mainController.getActiveThreadsProperty().asString());
        this.label_PointsEarnedCount.textProperty().bind(mainController.getActiveThreadsProperty().asString());
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void targetHistoryListUpdated(List<TargetDTOWorkerDetails> targets) {
        this.component_ReceivedTargetsController.clearAndSet(targets);
    }

    public void specificTargetUpdated(TargetDTOWorkerDetails updatedTarget) {
        this.component_ReceivedTargetsController.specificTargetUpdated(updatedTarget);
    }

    @Override
    public void loginPerformed(String username, int threadCount) {
        this.label_TotalThreadCount.setText(String.valueOf(threadCount));
    }
}
