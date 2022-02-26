package components.task.execution.main;

import componentcode.executiontable.ExecutionDTOTable;
import components.app.AppMainController;
import components.graph.targettable.TargetDTOTable;
import components.task.execution.dynamicpanel.TaskExecutionDynamicPanelController;
import components.task.execution.executiondetail.ExecutionDetailsController;
import components.task.execution.statustables.ExecutionStatusTablesController;
import components.task.settings.TaskSettingsController;
import events.ExecutionChosenListener;
import events.ExecutionEndListener;
import events.ExecutionStartListener;
import events.FileLoadedListener;
import graph.DependenciesGraph;
import graph.TargetDTO;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import task.Execution;
import task.configuration.Configuration;
import task.enums.ExecutionStatus;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.util.List;

public class TaskExecutionMainController implements FileLoadedListener, ExecutionStartListener, ExecutionEndListener, ExecutionChosenListener {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML
    private SplitPane mainParent;
    @FXML
    private ProgressBar progressBar;


    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    @FXML
    private ScrollPane statusTablesComponent;
    @FXML
    private ExecutionStatusTablesController statusTablesComponentController;

    @FXML
    private Parent dynamicPanelComponent;
    @FXML
    private TaskExecutionDynamicPanelController dynamicPanelComponentController;

    @FXML    private ScrollPane component_ExecutionDetails;
    @FXML    private ExecutionDetailsController component_ExecutionDetailsController;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private AppMainController mainController;
    private boolean isFirstFileLoaded = false;
    private DependenciesGraph workingGraph;
    private DependenciesGraph taskProcessWorkingGraph;
    private String chosenExecutionName;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        dynamicPanelComponentController.setMainExecutionController(this);

        addTargetClickEvent(statusTablesComponentController.getTableAllTargetsComponentController().getTableView());
        addTargetClickEvent(statusTablesComponentController.getTableUnprocessedComponentController().getTableView());
        addTargetClickEvent(statusTablesComponentController.getTableProcessedComponentController().getTableView());
        addTargetClickEvent(statusTablesComponentController.getTableFailedComponentController().getTableView());
//        addTargetClickEvent(statusTablesComponentController.getTableSkippedComponentController().getTableView());
        addTargetClickEvent(statusTablesComponentController.getTableSucceededComponentController().getTableView());

        statusTablesComponentController.setProgressBar(progressBar);
    }

    private void addTargetClickEvent(TableView<TargetDTOTable> tableView) {
        tableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                dynamicPanelComponentController.showTargetDetails(newValue, taskProcessWorkingGraph);
            }
        }));
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public TextArea getExecutionTextArea() {
        return dynamicPanelComponentController.getExecutionTextArea();
    }

    public SplitPane getMainParent() {
        return mainParent;
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        mainController.addEventListener_FileLoaded(this);
        mainController.addEventListener_ExecutionStarted(this);
        mainController.addEventListener_ExecutionEnded(this);
        mainController.addEventListener_ExecutionChosen(this);

        dynamicPanelComponentController.setMainAppController(mainController);

        mainController.getExecutionListRefresher().addConsumer(this::updateExecutionList);
    }

    public void updateExecutionList(List<ExecutionDTOTable> list) {
        Platform.runLater(() -> {
            for (ExecutionDTOTable executionDTO : list) {
                if (executionDTO.getExecutionName().equals(chosenExecutionName)) {
                    chosenExecutionName = chosenExecutionName;
                    executionChosen(executionDTO);
                    this.dynamicPanelComponentController.executionChosen(executionDTO);
                    break;
                }
            }
        });
    }

    public ExecutionStatusTablesController getStatusTablesComponentController() {
        return statusTablesComponentController;
    }

    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------- GETTERS AND SETTERS (PRIVATE) ----------------------------------- */


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public void fileLoaded() {
        if (!isFirstFileLoaded) {
            isFirstFileLoaded = true;
            setEvent_ClearChosenTargetsFromTaskSettings();
        }

        statusTablesComponentController.newFileLoaded();
//        dynamicPanelComponentController.newFileLoaded();
    }

    public void TaskSettingsEvent_UpdateChosenTargetsAction() {
        TaskSettingsController settingsController = mainController.getTaskSettingsController();

        workingGraph = settingsController.getGraphOfChosenTargets();

        setWorkingGraph(workingGraph);
        statusTablesComponentController.updateUnprocessed(workingGraph);
    }

    private void setWorkingGraph(DependenciesGraph workingGraph) {
        this.workingGraph = workingGraph;
        statusTablesComponentController.setWorkingGraph(workingGraph);
//        dynamicPanelComponentController.setWorkingGraph(workingGraph);
    }

    private void setEvent_ClearChosenTargetsFromTaskSettings() {
        TaskSettingsController settingsController = mainController.getTaskSettingsController();

        settingsController.getButtonClearAllChosenTargets().addEventHandler(ActionEvent.ACTION,
                event -> {
                    clear();
                });
    }

    public void event_ButtonPressed_StartExecution() {
        mainController.event_ButtonPressed_StartExecution();
    }

    public void eventButtonPressed_PauseExecution() {
        mainController.event_ButtonPressed_PauseExecution();
    }

    public void event_ButtonPressed_ContinueExecution() {
        mainController.event_ButtonPressed_ContinueExecution();
    }

    public void finishedProcessingTarget(TargetDTO targetDTO) {
        statusTablesComponentController.finishedProcessingTarget(targetDTO);
        dynamicPanelComponentController.finishedProcessingTarget(targetDTO);
    }

    public void pauseButtonPressed() {
        mainController.pauseButtonPressed();
    }

    public void continueButtonPressed() {
        mainController.continueButtonPressed();
    }

    @Override
    public void executionStarted(Configuration startingConfig) {
        dynamicPanelComponentController.startExecution();
    }

    public void threadChangedDuringExecution(int oldValue, int newValue) {
        if (oldValue != newValue) {
            mainController.threadChangedDuringExecution(newValue);
        }
    }

    @Override
    public void executedEnded(Execution execution) {
        dynamicPanelComponentController.endExecution(execution);
    }

    /**
     * This method handles the event of a new execution being selected.
     * It clears the current execution displayed and shows the new one.
     */
    @Override
    public void executionChosen(ExecutionDTOTable executionDTOTable) {
        reset();
        if (! executionDTOTable.getCreatingUser().equals(mainController.getUsername())) {
            return;
        }

        setWorkingGraph(new DependenciesGraph(executionDTOTable.getStartGraphDTO()));

        this.chosenExecutionName = executionDTOTable != null ? executionDTOTable.getExecutionName() : null;

        component_ExecutionDetailsController.executionChosen(executionDTOTable);
        statusTablesComponentController.executionChosen(executionDTOTable);
        dynamicPanelComponentController.executionChosen(executionDTOTable);

        progressBar.setProgress(executionDTOTable.getExecutionProgress());
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void clear() {
        workingGraph = null;
        taskProcessWorkingGraph = null;
        statusTablesComponentController.clear();
        dynamicPanelComponentController.clear();
        component_ExecutionDetailsController.clear();
        progressBar.setProgress(0);
    }

    public void targetStateChanged(TargetDTO targetDTO) {
        statusTablesComponentController.updateTargetStateChanged(targetDTO);
    }

    public void setTaskProcessWorkingGraph(DependenciesGraph graph) {
        this.taskProcessWorkingGraph = graph;
    }

    public void reset() {
        clear();
    }

    public void sendServerRequest_ExecutionStart() {
        String executionName = mainController.getChosenExecutionName();
        sendServerRequest_ExecutionStatusUpdate(executionName, ExecutionStatus.EXECUTING);
    }

    public void sendServerRequest_ExecutionPause() {
        String executionName = mainController.getChosenExecutionName();
        sendServerRequest_ExecutionStatusUpdate(executionName, ExecutionStatus.PAUSED);
    }

    public void sendServerRequest_ExecutionStop() {
        String executionName = mainController.getChosenExecutionName();
        sendServerRequest_ExecutionStatusUpdate(executionName, ExecutionStatus.STOPPED);
    }

    public void sendServerRequest_ExecutionContinue() {
        sendServerRequest_ExecutionStart();
    }

    private void sendServerRequest_ExecutionStatusUpdate(String executionName, ExecutionStatus executionStatus) {
        // Request parameters:
        // Execution name
        // Operation status

        if (executionName == null || executionStatus == null) {
            return;
        }


        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(ConstantsAll.EXECUTION_STATUS_UPDATE)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_EXECUTION_NAME, executionName)
                .addQueryParameter(ConstantsAll.QP_EXECUTION_STATUS, executionStatus.name())
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        getExecutionTextArea().appendText("Something went wrong with updating the status!" + ConstantsAll.LINE_SEPARATOR + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            getExecutionTextArea().appendText("Something went wrong with updating the status!" + ConstantsAll.LINE_SEPARATOR + responseBody));
                } else {
                    Platform.runLater(() -> {

                        getExecutionTextArea().appendText("Status changed to " + executionStatus + "!" + ConstantsAll.LINE_SEPARATOR);
//                        chatAppMainController.updateUserName(userName);   // Aviad code
//                        chatAppMainController.switchToChatRoom();         // Aviad code

                        invokeBasedOnStatus(executionStatus);
                    });
                }
            }
        });
    }

    private void invokeBasedOnStatus(ExecutionStatus executionStatus) {
        switch (executionStatus) {
            case NEW:
                break;
            case EXECUTING:
                dynamicPanelComponentController.startExecution();
                break;
            case ENDED:
                break;
            case PAUSED:
                dynamicPanelComponentController.pauseExecution();
                break;
            case STOPPED:
                dynamicPanelComponentController.stopExecution();
                break;
        }
    }
}


/* ---------------------------------------------------------------------------------------------------- */
/* ------------------------------------------- BIND METHODS ------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */



/* ---------------------------------------------------------------------------------------------------- */
/* ------------------------------------------- OLD METHODS -------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */