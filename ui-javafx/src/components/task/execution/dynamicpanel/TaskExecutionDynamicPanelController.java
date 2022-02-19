package components.task.execution.dynamicpanel;

import components.app.AppMainController;
import components.graph.specifictarget.SpecificTargetController;
import components.graph.targettable.TargetDTOTable;
import components.task.execution.main.TaskExecutionMainController;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import logic.Engine;
import task.Execution;
import task.configuration.Configuration;

public class TaskExecutionDynamicPanelController {
    @FXML private Parent mainScene;
    @FXML private SplitPane splitPane;
    @FXML private BorderPane borderPane;

    @FXML private Button buttonResetForNewExecution;

    @FXML
    private Button startButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button continueButton;
    @FXML
    private TextArea taskExecutionUpdatesTA;
    @FXML
    private ChoiceBox<Integer> threadNumCB;

    @FXML private Parent specificTargetDetailsComponent;
    @FXML private SpecificTargetController specificTargetDetailsComponentController;

    private TaskExecutionMainController taskExecutionMainController;

    private SimpleBooleanProperty isExecuting; // Determines if there is an execution underway, whether paused or not
    private SimpleBooleanProperty isPaused;

    private int threadNumValueDuringPause;
    private int threadNumNewValueDuringExecution;
    private AppMainController mainAppController;

    public TaskExecutionDynamicPanelController() {
        isPaused = new SimpleBooleanProperty(true);
        isExecuting = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        startButton.disableProperty().bind(isExecuting);
        continueButton.disableProperty().bind(isPaused.not());
        pauseButton.disableProperty().bind(isPaused);
        threadNumCB.disableProperty().bind(isPaused.not());
        buttonResetForNewExecution.disableProperty().bind(isExecuting);

        setThreadChoiceBoxHandler();

        reset();
    }


    public void setMainAppController(AppMainController mainController) {
        this.mainAppController = mainController;
    }

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
        taskExecutionMainController.startButtonPressed();
    }

    @FXML
    void pauseButtonActionListener(ActionEvent event) {
        if (isExecuting.get()) {
            taskExecutionUpdatesTA.appendText("Pausing execution, this might take a moment...\n\n");
            isPaused.set(true);
            taskExecutionMainController.pauseButtonPressed();
        }
    }

    @FXML
    void buttonResetForNewExecutionActionListener(ActionEvent event) {
        mainAppController.resetForNewExecutionButton_FromDynamicPanel();
    }

    @FXML
    void continueButtonActionListener(ActionEvent event) {
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

    public void startExecution(Configuration startingConfig) {
        isPaused.set(false);
        isExecuting.set(true);
        taskExecutionUpdatesTA.appendText("Starting execution...\n\n");
//        startButton.disableProperty().set(true);
        updateStartingThreadCount(startingConfig);
    }

    private void updateStartingThreadCount(Configuration startingConfig) {
        int startThreadCount = startingConfig.getNumberOfThreads();
        threadNumCBSetNewSelectedNumber(startThreadCount);
        threadNumValueDuringPause = startThreadCount;
        threadNumNewValueDuringExecution = startThreadCount;
    }

    /**
     * Clears the old selection (if exists) and highlights the new item by the given number.
     */
    private void threadNumCBSetNewSelectedNumber(int numberToSelect) {
        // Check if there is already a selected number
        Integer selectedInteger = threadNumCB.getSelectionModel().getSelectedItem();
        if (selectedInteger != null) {
            if (selectedInteger.intValue() == numberToSelect) {
                return;
            } else {
                threadNumCB.getSelectionModel().clearSelection();
            }
        }

        // Update
        int index = 0;
        for (Integer integer : threadNumCB.getItems()) {
            if (integer == numberToSelect) {
                threadNumCB.getSelectionModel().select(index);
                return;
            }

            index++;
        }
    }

    public void finishedProcessingTarget(TargetDTO targetDTO) {
        if (targetDTO == null) {
            return;
        }

        String str = Engine.getInstance().getFormalizedTargetDataString(targetDTO) + "\n\n";

        taskExecutionUpdatesTA.appendText(str);
    }

    public void newFileLoaded() {
        AppMainController.setThreadNumberChoiceBox(threadNumCB);
        reset();
    }

    private void reset() {
        isPaused.set(true);
        taskExecutionUpdatesTA.clear();
        specificTargetDetailsComponentController.clear();
        isExecuting.set(false);
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
