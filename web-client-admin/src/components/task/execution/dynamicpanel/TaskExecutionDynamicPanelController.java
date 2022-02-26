package components.task.execution.dynamicpanel;

import componentcode.executiontable.ExecutionDTOTable;
import components.app.AppMainController;
import components.graph.specifictarget.SpecificTargetController;
import components.graph.targettable.TargetDTOTable;
import components.task.execution.main.TaskExecutionMainController;
import events.ExecutionChosenListener;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import logic.Engine;
import task.Execution;

import java.util.List;

public class TaskExecutionDynamicPanelController implements ExecutionChosenListener {
    @FXML private Parent mainScene;
    @FXML private SplitPane splitPane;
    @FXML private BorderPane borderPane;


    @FXML    private Button startButton;
    @FXML    private Button pauseButton;
    @FXML    private Button continueButton;
    @FXML    private Button buttonStop;
    @FXML    private TextArea taskExecutionUpdatesTA;


    @FXML private Parent specificTargetDetailsComponent;
    @FXML private SpecificTargetController specificTargetDetailsComponentController;

    private TaskExecutionMainController taskExecutionMainController;

    private SimpleBooleanProperty isExecuting; // Determines if there is an execution underway, whether paused or not
    private SimpleBooleanProperty isPaused;
    private SimpleBooleanProperty isEnded;

    private int threadNumValueDuringPause;
    private int threadNumNewValueDuringExecution;
    private AppMainController mainAppController;

    public TaskExecutionDynamicPanelController() {
        isPaused = new SimpleBooleanProperty(true);
        isExecuting = new SimpleBooleanProperty(false);
        isEnded = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {

//        startButton.disableProperty().bind(isExecuting);
//        continueButton.disableProperty().bind(isPaused.not());
//        pauseButton.disableProperty().bind(isPaused);
//        threadNumCB.disableProperty().bind(isPaused.not());
//        buttonResetForNewExecution.disableProperty().bind(isExecuting);

//        setThreadChoiceBoxHandler();

        reset();
    }


    public void setMainAppController(AppMainController mainController) {
        this.mainAppController = mainController;
        this.mainAppController.addEventListener_ExecutionChosen(this);
    }


    private void updateTextAreaWithThreadChange(int oldValue, int newValue) {
        String strIncOrDec = newValue > oldValue ? "Increasing" : "Decreasing";
        String fullMsg = String.format("%s threads from %d to %d.\n\n", strIncOrDec, oldValue, newValue);
        taskExecutionUpdatesTA.appendText(fullMsg);
    }

    public TextArea getExecutionTextArea() {
        return taskExecutionUpdatesTA;
    }

    public void setMainExecutionController(TaskExecutionMainController taskExecutionMainController) {
        this.taskExecutionMainController = taskExecutionMainController;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getPauseButton() {
        return pauseButton;
    }

    public Button getContinueButton() {
        return continueButton;
    }

    @FXML
    void startButtonActionListener(ActionEvent event) {
        taskExecutionMainController.event_ButtonPressed_StartExecution();
    }

    @FXML void buttonStopActionListener(ActionEvent event) {
        mainAppController.event_ButtonPressed_StopExecution();
    }

    @FXML
    void pauseButtonActionListener(ActionEvent event) {
        taskExecutionMainController.eventButtonPressed_PauseExecution();
        if (isExecuting.get()) {
            taskExecutionUpdatesTA.appendText("Pausing execution, this might take a moment...\n\n");
            isPaused.set(true);
            taskExecutionMainController.pauseButtonPressed();
        }
    }


    @FXML
    void continueButtonActionListener(ActionEvent event) {
        taskExecutionMainController.event_ButtonPressed_ContinueExecution();

        if (isExecuting.get()) {
            taskExecutionUpdatesTA.appendText("Continuing execution...\n\n");
            isPaused.set(false);
            taskExecutionMainController.continueButtonPressed();
            if (threadNumValueDuringPause != threadNumNewValueDuringExecution) {
                updateTextAreaWithThreadChange(threadNumValueDuringPause, threadNumNewValueDuringExecution);
                taskExecutionMainController.threadChangedDuringExecution(threadNumValueDuringPause, threadNumNewValueDuringExecution);
                threadNumValueDuringPause = threadNumNewValueDuringExecution;
            }
        }
    }

    public void startExecution() {
        isPaused.set(false);
        isExecuting.set(true);
        taskExecutionUpdatesTA.appendText("Starting execution...\n\n");
//        startButton.disableProperty().set(true);
//        updateStartingThreadCount(startingConfig);
    }

    public void finishedProcessingTarget(TargetDTO targetDTO) {
        if (targetDTO == null) {
            return;
        }

        String str = Engine.getInstance().getFormalizedTargetDataString(targetDTO) + "\n\n";

        taskExecutionUpdatesTA.appendText(str);
    }

    private void reset() {
        isPaused.set(true);
        taskExecutionUpdatesTA.clear();
        specificTargetDetailsComponentController.clear();
        isExecuting.set(false);
    }

    public void pauseExecution() {

    }

    public void stopExecution() {
    }

    public void endExecution(Execution execution) {
        isExecuting.set(false);
        isPaused.set(true);

        String executionStr = Engine.getInstance().getFormalizedExecutionReportString(execution);

        taskExecutionUpdatesTA.appendText("\n\n -------------- Execution Ended -------------- \n\n");
        taskExecutionUpdatesTA.appendText(executionStr);
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ EVENTS METHODS ------------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public void executionChosen(ExecutionDTOTable executionDTOTable) {
        switch (executionDTOTable.getExecutionStatus()) {
            case NEW:
                enableButtons(true, false, false, false);
                break;
            case PAUSED:
                enableButtons(false, false, true, true);
                break;
            case EXECUTING:
                enableButtons(false, true, false, true);
                break;
            case ENDED:
            case STOPPED:
            case COMPLETED:
                enableButtons(false, false, false, false);
                break;
        }

        String executionLog = executionDTOTable.getExecutionLog();
        String currTextArea = taskExecutionUpdatesTA.getText();

        if (! currTextArea.equals(executionLog)) {
            taskExecutionUpdatesTA.setText(executionLog);
        }

        specificTargetDetailsComponentController.executionChosen(executionDTOTable);
    }

    private void enableButtons(boolean startEnable, boolean pauseEnable, boolean continueEnable, boolean stopEnabled) {
        startButton.setDisable(!startEnable);
        pauseButton.setDisable(!pauseEnable);
        continueButton.setDisable(!continueEnable);
        buttonStop.setDisable(!stopEnabled);
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void clear() {
        reset();
    }

    public void showTargetDetails(TargetDTOTable newValue, DependenciesGraph taskProcessWorkingGraph) {
        specificTargetDetailsComponentController.populateData(newValue, taskProcessWorkingGraph);
    }
}



/* ---------------------------------------------------------------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
/* --------------------------------------------- OLD CODE --------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */

/*


    private void updateStartingThreadCount(Configuration startingConfig) {
        int startThreadCount = startingConfig.getNumberOfThreads();
        threadNumCBSetNewSelectedNumber(startThreadCount);
        threadNumValueDuringPause = startThreadCount;
        threadNumNewValueDuringExecution = startThreadCount;
    }

    /**
     * Clears the old selection (if exists) and highlights the new item by the given number.
     */
//private void threadNumCBSetNewSelectedNumber(int numberToSelect) {
//    // Check if there is already a selected number
//    Integer selectedInteger = threadNumCB.getSelectionModel().getSelectedItem();
//    if (selectedInteger != null) {
//        if (selectedInteger.intValue() == numberToSelect) {
//            return;
//        } else {
//            threadNumCB.getSelectionModel().clearSelection();
//        }
//    }
//
//    // Update
//    int index = 0;
//    for (Integer integer : threadNumCB.getItems()) {
//        if (integer == numberToSelect) {
//            threadNumCB.getSelectionModel().select(index);
//            return;
//        }
//
//        index++;
//    }
//}

/*
    private void setThreadChoiceBoxHandler() {
        threadNumCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (isExecuting.get()) {
                    if (! oldValue.equals(newValue)) {
                        threadNumNewValueDuringExecution = threadNumCB.getItems().get(newValue.intValue());
                    }
                }
            }
        });
    }

 */



