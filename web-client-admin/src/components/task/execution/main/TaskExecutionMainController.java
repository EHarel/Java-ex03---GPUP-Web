package components.task.execution.main;

import components.app.AppMainController;
import components.graph.targettable.TargetDTOTable;
import components.task.execution.dynamicpanel.TaskExecutionDynamicPanelController;
import components.task.execution.statustables.ExecutionStatusTablesController;
import components.task.settings.TaskSettingsController;
import events.ExecutionEndListener;
import events.ExecutionStartListener;
import events.FileLoadedListener;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import task.ExecutionData;
import task.configuration.Configuration;

public class TaskExecutionMainController implements FileLoadedListener, ExecutionStartListener, ExecutionEndListener {
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


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private AppMainController mainController;
    private boolean isFirstFileLoaded = false;
    private DependenciesGraph workingGraph;
    private DependenciesGraph taskProcessWorkingGraph;


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

        dynamicPanelComponentController.setMainAppController(mainController);
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
        dynamicPanelComponentController.newFileLoaded();
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

    public void startButtonPressed() {
//        mainController.startExecutionButtonPressed();
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
        dynamicPanelComponentController.startExecution(startingConfig);
    }

    public void threadChangedDuringExecution(int oldValue, int newValue) {
        if (oldValue != newValue) {
            mainController.threadChangedDuringExecution(newValue);
        }
    }

    @Override
    public void executedEnded(ExecutionData executionData) {
        dynamicPanelComponentController.endExecution(executionData);
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
}


/* ---------------------------------------------------------------------------------------------------- */
/* ------------------------------------------- BIND METHODS ------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
