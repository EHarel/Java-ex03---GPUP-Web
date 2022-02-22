package components.task.settings;

import algorithm.DFS;
import components.app.AppMainController;
import components.app.AppUtils;
import components.graph.general.GraphGeneralDetailsController;
import components.graph.targettable.TargetTableController;
import components.graph.targettable.selection.TableSelectionController;
import components.task.configuration.CompilationConfigurationController;
import components.task.configuration.SimulationConfigurationController;
import events.FileLoadedListener;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.Engine;
import task.Execution;
import task.OldCode.TaskProcess;
import task.enums.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationDTO;

import java.util.Collection;
import java.util.LinkedList;

public class TaskSettingsController implements FileLoadedListener {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML private Label graphNameLabel;
    @FXML private Label targetCountLabel;
    @FXML private Label numberOfRootsLabel;
    @FXML private Label numberOfMiddlesLabel;
    @FXML private Label numberOfLeavesLabel;
    @FXML private Label numberOfIndependentsLabel;
    @FXML private Label labelActiveConfiguration;
    @FXML private Pane paneStartPoint;
    @FXML private Label labelAddConfigMsg;
    @FXML private Button buttonCancelEdit;
    @FXML private Button buttonDeleteConfiguration;
    @FXML private Button buttonAddConfiguration;
    @FXML private Button buttonCreateNewConfig;
    @FXML private Button buttonSetActiveConfig;
    @FXML private Label labelSetActiveConfigUpdate;
    @FXML private ChoiceBox<Integer> threadNumberChoiceBox;
    @FXML private ListView<TaskType> taskTypeListView;
    @FXML private ListView<String> existingConfigurationsListView;
    @FXML private ToggleGroup TaskStartPoint;
    @FXML private SplitPane taskConfigurationSplitPane;
    @FXML private RadioButton startPointFromScratchRadioButton;
    @FXML private RadioButton startPointIncrementalRadioButton;
    @FXML private Button updateChosenTargetsButton;
    @FXML private Button clearAllChosenTargetsButton;


    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    // External controllers
    @FXML private ScrollPane graphGeneralDetailsComponent;
    @FXML private GraphGeneralDetailsController graphGeneralDetailsComponentController;

    @FXML private ScrollPane targetTableAvailableTargets;
    @FXML private TargetTableController targetTableAvailableTargetsController;

    @FXML private ScrollPane targetTableChosenTargets;
    @FXML private TargetTableController targetTableChosenTargetsController;

    @FXML private ScrollPane targetSelectionMenu;
    @FXML private TableSelectionController targetSelectionMenuController;

    @FXML private Parent graphGeneralDetailsComponent_ChosenTargetsGraph;
    @FXML private GraphGeneralDetailsController graphGeneralDetailsComponent_ChosenTargetsGraphController;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    AppMainController mainController;

    private Parent compilationParent;
    private Parent simulationParent;

    private CompilationConfigurationController compilationConfigurationController;
    private SimulationConfigurationController simulationConfigurationController;
    private SimpleBooleanProperty isEditingNewConfig;
    private DependenciesGraph chosenGraph;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    public TaskSettingsController() {
        isEditingNewConfig = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        taskTypeListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        existingConfigurationsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TaskType[] taskTypes = TaskType.values();

        for (int i = 0; i < taskTypes.length; i++) {
            taskTypeListView.getItems().add(taskTypes[i]);
        }


        taskTypeListView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            TaskType taskType = taskTypeListView.getSelectionModel().getSelectedItem();
            loadTaskType(taskType);
            updateActiveConfig(taskType);
            if(taskType != null) {
                paneStartPoint.setDisable(false);
                boolean isDisableIncremental = true;
                try {
                    Execution lastExecution = Engine.getInstance().getExecutionLast(taskType);
                    isDisableIncremental = (lastExecution == null);
                } catch (Exception e) {
                    isDisableIncremental = true;
                }

                startPointIncrementalRadioButton.setDisable(isDisableIncremental);
            }
        });

        targetTableAvailableTargetsController.ShowSelectColumn();

        setLoadConfiguration();
        setSelectAllAction();
        setClearAllAction();
        setRemoveAllChosenAction();
        setUpdateChosenAction();
        setStartPointPaneBorder();

        bindControlsToEditProperty();
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- BIND METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void bindControlsToEditProperty() {
        taskTypeListView.disableProperty().bind(isEditingNewConfig);
        buttonAddConfiguration.disableProperty().bind(isEditingNewConfig.not());
        buttonCreateNewConfig.disableProperty().bind(isEditingNewConfig);
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public TaskType getChosenTaskType() {
        return taskTypeListView.getSelectionModel().getSelectedItem();
    }

    public Integer getThreadNum() {
        return threadNumberChoiceBox.getSelectionModel().getSelectedItem();
    }

    public Button getButtonUpdateChosenTargets() { return updateChosenTargetsButton; }

    public Button getButtonClearAllChosenTargets() { return clearAllChosenTargetsButton; }

    public DependenciesGraph getChosenGraph() {
        return chosenGraph;
    }

    public void SetMainController(AppMainController mainController) {
        this.mainController = mainController;
        mainController.addEventListener_FileLoaded(this);
    }

    public void SetCompilationConfigurationController(Parent parent, CompilationConfigurationController controller) {
        this.compilationParent = parent;
        this.compilationConfigurationController = controller;

        controller.setTaskSettingsController(this);

        compilationConfigurationController.lockControls();
    }

    public void SetSimulationConfigurationController(Parent parent, SimulationConfigurationController controller) {
        this.simulationParent = parent;
        this.simulationConfigurationController = controller;

        controller.setTaskSettingsController(this);

        simulationConfigurationController.lockControls();
    }

    /**
     * Returns the working graph. Original if starting from scratch, or last execution graph if incremental.
     */
    public DependenciesGraph getWorkingGraph() {
        if (!isTaskTypeSelected()) {
            noTaskTypeSelectedErrorMessage();
            return null;
        }

        DependenciesGraph graph = null;

        if (isRunFromScratch()) {
            graph = (DependenciesGraph) Engine.getInstance().getGraph();
        } else {
            Execution lastExecution = Engine.getInstance().getExecutionLast(getChosenTaskType());
            if (lastExecution != null) {
                graph = getLastGraphReset(lastExecution);
            }
        }

        return graph;
    }

    public TaskProcess.StartPoint GetTaskStartPoint() {
        TaskProcess.StartPoint startPoint = null;

        if (startPointFromScratchRadioButton.isSelected()) {
            startPoint = TaskProcess.StartPoint.FROM_SCRATCH;
        } else if (startPointIncrementalRadioButton.isSelected()) {
            startPoint = TaskProcess.StartPoint.INCREMENTAL;
        }

        return startPoint;
    }

    public SimpleBooleanProperty getIsEditingNewConfigProperty() {
        return isEditingNewConfig;
    }

    public static void SetNameListener(TextField nameTF, Label labelNameMsg) {
        nameTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                labelNameMsg.setText("Valid input :)");
                labelNameMsg.setTextFill(Color.GREEN);
            } else {
                labelNameMsg.setText("Invalid input! Cannot have an empty name");
                labelNameMsg.setTextFill(Color.RED);
            }
        });
    }

    public Collection<String> getParticipatingTargets() {
        Collection<TargetDTO> participatingTargetsDTO = targetTableChosenTargetsController.getAllTargets();
        Collection<String> participatingTargetsNames = new LinkedList<>();

        participatingTargetsDTO.forEach(targetDTO -> {
            participatingTargetsNames.add(targetDTO.getName());
        });

        return participatingTargetsNames;
    }

    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------- GETTERS AND SETTERS (PRIVATE) ----------------------------------- */
    private DependenciesGraph getLastGraphReset(Execution lastExecution) {
        DependenciesGraph graph = null;

        if (lastExecution != null) {
            graph = lastExecution.getEndGraph();
        }

        return graph;
    }

    private void setStartPointPaneBorder() {
        paneStartPoint.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
    }

    private void setLoadConfiguration() {
        existingConfigurationsListView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            String configName = existingConfigurationsListView.getSelectionModel().getSelectedItem();
            TaskType taskType = getChosenTaskType();

            if (configName != null && taskType != null) {
                switch (taskType) {
                    case COMPILATION:
                        compilationConfigurationController.loadConfig(configName);
                        break;
                    case SIMULATION:
                        simulationConfigurationController.loadConfig(configName);
                        break;
                }
            }
        });
    }

    private void setSelectAllAction() {
        targetSelectionMenuController.getSelectAllButton().setOnAction(event -> {
            targetTableAvailableTargetsController.selectAll();
        });
    }

    private void setClearAllAction() {
        targetSelectionMenuController.getUnselectAlLButton().setOnAction(event -> {
            targetTableAvailableTargetsController.unselectAll();
        });
    }


    private void setRemoveAllChosenAction() {
        clearAllChosenTargetsButton.addEventHandler(ActionEvent.ACTION,
                event -> {
                    targetTableChosenTargetsController.clear();
                    graphGeneralDetailsComponent_ChosenTargetsGraphController.clear();
                });
    }

    private void setUpdateChosenAction() {
        updateChosenTargetsButton.setOnAction(event -> {
            Collection<TargetDTO> selectedTargets = targetTableAvailableTargetsController.getSelectedTargets();

            // TODO: I can improve efficiency of the following functions using a map
            // Without it I keep manually checking if a target already appears at O(n)
            if (targetSelectionMenuController.getDependentOnRB().isSelected()) {
                addTargetDependencies(selectedTargets, DFS.EdgeDirection.DEPENDENT_ON);
            }

            if (targetSelectionMenuController.getRequiredForRB().isSelected()) {
                addTargetDependencies(selectedTargets, DFS.EdgeDirection.REQUIRED_FOR);
            }

            updateSelectedTargets(selectedTargets);
            mainController.TaskSettingsEvent_updateChosenButtonAction();
        });
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void reset() {
        clear();
    }

    /**
     * This method adds ALL the dependencies in a given direction.
     */
    private void addTargetDependencies(Collection<TargetDTO> selectedTargets, DFS.EdgeDirection direction) {
        DependenciesGraph graph = getWorkingGraph();
        if (graph == null) {
            return;
        }

        Collection<Collection<TargetDTO>> allDependencies = new LinkedList<>();

        for (TargetDTO selectedTarget : selectedTargets) {
            Collection<TargetDTO> targetDependencies = graph.getAllDependencies_Transitive(selectedTarget.getName(), direction);
            allDependencies.add(targetDependencies);
        }

        addAllDependenciesToSelected(selectedTargets, allDependencies);
    }

    private void addAllDependenciesToSelected(Collection<TargetDTO> selectedTargets, Collection<Collection<TargetDTO>> allDependencies) {
        if (selectedTargets == null || allDependencies == null) {
            return;
        }

        allDependencies.forEach(targetDTOCollection -> {
            addDependenciesToSelected(selectedTargets, targetDTOCollection);
        });
    }

    private void addDependenciesToSelected(Collection<TargetDTO> selectedTargets, Collection<TargetDTO> targetDependencies) {
        if (selectedTargets == null || targetDependencies == null) {
            return;
        }

        targetDependencies.forEach(targetDTO -> {
            if (!targetAppearsInSelected(selectedTargets, targetDTO)) {
                selectedTargets.add(targetDTO);
            }
        });
    }

    private boolean targetAppearsInSelected(Collection<TargetDTO> selectedTargets, TargetDTO targetDTO) {
        if (selectedTargets == null || targetDTO == null) {
            return false;
        }

        for (TargetDTO selectedTargetDTO : selectedTargets) {
            if (selectedTargetDTO.getName().equals(targetDTO.getName())) {
                return true;
            }
        }

        return false;
    }

    private boolean isTaskTypeSelected() {
        TaskType taskType = getChosenTaskType();

        return (taskType != null);
    }

    private void noTaskTypeSelectedErrorMessage() {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Settings nto defined");
        errorAlert.setContentText("You must specify which type of task you want");
        errorAlert.showAndWait();
    }

    private boolean isRunFromScratch() {
        return startPointFromScratchRadioButton.isSelected();
    }

    private void updateSelectedTargets(Collection<TargetDTO> selectedTargets) {
        Collection<String> names = AppUtils.getNamesFromCollection(selectedTargets);
        chosenGraph = getWorkingGraph().DuplicateChosenOnly(names);
        chosenGraph.resetState();
        targetTableChosenTargetsController.populateData_SpecificTargets_SubGraph(chosenGraph, selectedTargets);
        graphGeneralDetailsComponent_ChosenTargetsGraphController.populateData(chosenGraph);
    }

    private void loadTaskType(TaskType taskType) {
        if (taskType == null) {
            return;
        }

        switch (taskType) {
            case COMPILATION: {
                taskConfigurationSplitPane.getItems().set(1, compilationParent);
                break;
            }
            case SIMULATION: {
                taskConfigurationSplitPane.getItems().set(1, simulationParent);
                break;
            }
        }

        updateConfigurationList(taskType);
    }

    private void updateConfigurationList(TaskType taskType) {
        if (taskType == null) {
            return;
        }

        Collection<ConfigurationDTO> configData = Engine.getInstance().getConfigAll(taskType);
        existingConfigurationsListView.getItems().clear();

        configData.forEach(configurationData -> {
            existingConfigurationsListView.getItems().add(configurationData.getName());
        });
    }

    private void loadInitialGraph() {
        DependenciesGraph graph = (DependenciesGraph) Engine.getInstance().getGraph();
        if (graph == null) {
            return;
        }

        populateAvailableGraphData(graph);
    }

    private void populateAvailableGraphData(DependenciesGraph graph) {
        if (graph != null) {
            targetTableAvailableTargetsController.PopulateData_AllTargets(graph);
            graphGeneralDetailsComponentController.populateData(graph);
        }
    }

    public Collection<TargetDTO> getChosenTargets() {
        return targetTableChosenTargetsController.getAllTargets();
    }

    public Configuration getTaskConfigurationWithParticipatingTargets() {
        Configuration configuration = getTaskConfigurationWithoutParticipatingTargets();
        if (configuration == null) {
            return null;
        }
        // TODO: insert into task settings DTO
        Collection<String> participatingTargets = getParticipatingTargets();

        configuration.setParticipatingTargets(participatingTargets);

        return configuration;
    }

    private Configuration getTaskConfigurationWithoutParticipatingTargets() {
        Configuration configuration = null;
        TaskType taskType = getChosenTaskType();
        Integer threadNum = getThreadNum();

        if (taskType != null && threadNum != null) {
            switch (taskType) {
                case SIMULATION: {
                    configuration = simulationConfigurationController.getConfig(threadNum);
                    break;
                }
                case COMPILATION: {
                    configuration = compilationConfigurationController.getConfig(threadNum);
                    break;
                }
            }
        }

        return configuration;
    }

    private void displayMsgOnLabelWithFade(Label label, String msg, Color color) {
        label.setText(msg);
        label.setTextFill(color);
        AppMainController.AnimationFadeSingle(null, label, msg, color);
    }

    private String getChosenConfiguration() {
        return existingConfigurationsListView.getSelectionModel().getSelectedItem();
    }

    private void clearControl(TaskType taskType) {
        if (taskType != null) {
            switch (taskType) {
                case COMPILATION:
                    compilationConfigurationController.clear();
                    break;
                case SIMULATION:
                    simulationConfigurationController.clear();
                    break;
            }
        }
    }

    private boolean isValidTaskAndConfigChosen() {
        boolean valid = false;

        String configName = getChosenConfiguration();
        if (configName == null) {
            displayMsgOnLabelWithFade(labelSetActiveConfigUpdate, "No configuration highlighted!", Color.RED);
        } else if (getChosenTaskType() == null) {
            displayMsgOnLabelWithFade(labelSetActiveConfigUpdate, "No task type chosen", Color.RED);
        } else {
            valid = true;
        }

        return valid;
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void updateActiveConfig(TaskType taskType) {
        try {
            ConfigurationDTO configData = Engine.getInstance().getConfigActive(taskType);
            if (configData != null) {
                labelActiveConfiguration.setText(configData.getName());
            } else {
                labelActiveConfiguration.setText("[None]");
            }
        } catch (Exception ignore) {}
    }

    @FXML
    void startPointFromScratchRadioButtonListener(ActionEvent event) {
        startPointRBSwitched();
        loadInitialGraph();
    }

    private void startPointRBSwitched() {
        graphGeneralDetailsComponent_ChosenTargetsGraphController.clear();
        targetTableChosenTargetsController.clear();
        targetTableAvailableTargetsController.clear();
        graphGeneralDetailsComponentController.clear();
    }

    @FXML
    void startPointIncrementalRadioButtonActionListener(ActionEvent event) {
        TaskType taskType = getChosenTaskType();
        if (taskType == null) {
            return;
        }

        startPointRBSwitched();
        Execution lastExecution = Engine.getInstance().getExecutionLast(taskType);
        if (lastExecution == null) {
            loadInitialGraph();
        } else {
            DependenciesGraph lastGraph = lastExecution.getEndGraph();
            populateAvailableGraphData(lastGraph);
        }
    }

    @Override
    public void fileLoaded() {
        AppMainController.setThreadNumberChoiceBox(threadNumberChoiceBox);
        clear();
    }

    private void clear() {
        graphGeneralDetailsComponentController.clear();
        graphGeneralDetailsComponent_ChosenTargetsGraphController.clear();
        targetTableAvailableTargetsController.clear();
        targetTableChosenTargetsController.clear();
        chosenGraph = null;
        Toggle selectedToggle = TaskStartPoint.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        labelActiveConfiguration.setText("");
    }

    @FXML
    void buttonCreateNewConfigActionListener(ActionEvent event) {
        TaskType taskType = getChosenTaskType();

        if (taskType == null) {
            AppMainController.AlertErrorMessage(null, "Must specify which task type");
            return;
        }

        isEditingNewConfig.set(true);
        switch (taskType) {
            case SIMULATION:
                simulationConfigurationController.openForNewConfig();
                break;
            case COMPILATION:
                compilationConfigurationController.openForNewConfig();
                break;
        }
    }

    @FXML
    void buttonSetActiveConfigActionListener(ActionEvent event) {
        if (isValidTaskAndConfigChosen()) {
            String configName = getChosenConfiguration();
            TaskType taskType = getChosenTaskType();
            boolean newActiveSet = Engine.getInstance().setActiveConfig(taskType, configName);
            if (newActiveSet) {
                displayMsgOnLabelWithFade(labelSetActiveConfigUpdate, configName + " set to new active configuration", Color.GREEN);
                updateActiveConfig(taskType);
            }
        }
    }

    @FXML
    void buttonDeleteConfigurationActionListener(ActionEvent event) {
        if (isValidTaskAndConfigChosen()) {
            String contentText = "Are you sure you want to remove this configuration?";
            if (AppMainController.ConfirmationAlert(contentText)) {
                String configName = getChosenConfiguration();
                TaskType taskType = getChosenTaskType();
                boolean removedConfig = Engine.getInstance().removeConfiguration(taskType, configName);
                if (removedConfig) {
                    displayMsgOnLabelWithFade(labelSetActiveConfigUpdate, configName + " removed", Color.GREEN);
                    updateConfigurationList(taskType);
                    clearControl(taskType);
                    updateActiveConfig(taskType);
                }
            }
        }
    }

    @FXML
    private void buttonCancelEditActionListener(ActionEvent event) {
        isEditingNewConfig.set(false);
    }

    @FXML
    private void buttonAddConfigurationActionListener(ActionEvent event) {
        // Gather data
        // Set error message if wrong
        // Try to make new
        // Send message if worked

        TaskType taskType = getChosenTaskType();
        if (taskType == null) {
            String msg = "No task type chosen";
            Color color = Color.RED;
            AppMainController.AnimationFadeSingle(null, labelAddConfigMsg, msg, color);
            return;
        }

        Configuration configuration = getTaskConfigurationWithoutParticipatingTargets();
        if (configuration == null) {
            String msg = "Invalid parameters";
            Color color = Color.RED;
            AppMainController.AnimationFadeSingle(null, labelAddConfigMsg, msg, color);
            return;
        }

        boolean added = Engine.getInstance().addConfiguration(taskType, configuration);
        if (added) {

            String msg = "Configuration added";
            Color color = Color.GREEN;
            AppMainController.AnimationFadeSingle(null, labelAddConfigMsg, msg, color);
            updateConfigurationList(taskType);
            isEditingNewConfig.set(false);
        }
    }
}

