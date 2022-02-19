package components.task.settings;

import algorithm.DFS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.app.AppMainController;
import components.app.AppUtils;
import components.graph.general.GraphGeneralDetailsController;
import components.graph.targettable.TargetTableController;
import components.graph.targettable.selection.TableSelectionController;
import components.task.configuration.CompilationConfigurationController;
import components.task.configuration.SimulationConfigurationController;
import events.FileLoadedListener;
import events.GraphChosenListener;
import graph.DependenciesGraph;
import graph.TargetDTO;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.Engine;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import task.Execution;
import task.TaskProcess;
import task.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationDTO;
import task.configuration.ConfigurationDTOCompilation;
import task.configuration.ConfigurationDTOSimulation;
import task.execution.ExecutionDTO;
import task.execution.ExecutionStatus;
import utilsharedall.Constants;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

public class TaskSettingsController implements FileLoadedListener, GraphChosenListener {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML
    private Label graphNameLabel;
    @FXML
    private Label targetCountLabel;
    @FXML
    private Label numberOfRootsLabel;
    @FXML
    private Label numberOfMiddlesLabel;
    @FXML
    private Label numberOfLeavesLabel;
    @FXML
    private Label numberOfIndependentsLabel;
    @FXML
    private Label labelActiveConfiguration;
    @FXML
    private Pane paneStartPoint;
    @FXML
    private Label labelAddConfigMsg;
    @FXML
    private Button buttonCancelEdit;
    @FXML
    private Button buttonDeleteConfiguration;
    @FXML
    private Button buttonAddConfiguration;
    @FXML
    private Button buttonCreateNewConfig;
    @FXML
    private Button buttonSetActiveConfig;
    @FXML
    private Label labelSetActiveConfigUpdate;
    @FXML
    private ListView<TaskType> taskTypeListView;
    @FXML
    private ListView<String> existingConfigurationsListView;
    @FXML
    private ToggleGroup TaskStartPoint;
    @FXML
    private SplitPane taskConfigurationSplitPane;
    @FXML
    private RadioButton startPointFromScratchRadioButton;
    @FXML
    private RadioButton startPointIncrementalRadioButton;
    @FXML
    private Button updateChosenTargetsButton;
    @FXML
    private Button clearAllChosenTargetsButton;
    @FXML
    private Label label_SubmissionResult;
    @FXML
    private Button button_SubmitExecution;
    @FXML
    private TextField textField_ExecutionName;


    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    // External controllers
    @FXML
    private ScrollPane graphGeneralDetailsComponent;
    @FXML
    private GraphGeneralDetailsController graphGeneralDetailsComponentController;

    @FXML
    private ScrollPane targetTableAvailableTargets;
    @FXML
    private TargetTableController targetTableAvailableTargetsController;

    @FXML
    private ScrollPane targetTableChosenTargets;
    @FXML
    private TargetTableController targetTableChosenTargetsController;

    @FXML
    private ScrollPane targetSelectionMenu;
    @FXML
    private TableSelectionController targetSelectionMenuController;

    @FXML
    private Parent graphGeneralDetailsComponent_ChosenTargetsGraph;
    @FXML
    private GraphGeneralDetailsController graphGeneralDetailsComponent_ChosenTargetsGraphController;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    AppMainController mainController;

    private Parent compilationParent;
    private Parent simulationParent;

    private CompilationConfigurationController compilationConfigurationController;
    private SimulationConfigurationController simulationConfigurationController;
    private SimpleBooleanProperty isEditingNewConfig;
    private DependenciesGraph chosenGraph;
    private DependenciesGraph graphOfChosenTargets;


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
            if (taskType != null) {
                paneStartPoint.setDisable(false);
                boolean isDisableIncremental = true;
                // TODO: old code from ex02
//                try {
//                    ExecutionData lastExecution = Engine.getInstance().getExecutionLast(taskType);
//                    isDisableIncremental = (lastExecution == null);
//                } catch (Exception e) {
//                    isDisableIncremental = true;
//                }

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

    public Button getButtonUpdateChosenTargets() {
        return updateChosenTargetsButton;
    }

    public Button getButtonClearAllChosenTargets() {
        return clearAllChosenTargetsButton;
    }

    public DependenciesGraph getGraphOfChosenTargets() {
        return graphOfChosenTargets;
    }

    public void SetMainController(AppMainController mainController) {
        this.mainController = mainController;
//        mainController.addEventListener_FileLoaded(this);
        mainController.addEventListener_GraphChosen(this);
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
//            graph = (DependenciesGraph) Engine.getInstance().getGraph();
//            graph = mainController.getChosenGraph();
            graph = chosenGraph;
        } else {
            graph = chosenGraph;

//            TODO: fix later, commented out from ex02
//            ExecutionData lastExecution = Engine.getInstance().getExecutionLast(getChosenTaskType());
//            if (lastExecution != null) {
//                graph = getLastGraphReset(lastExecution);
//            }
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
                        compilationConfigurationController.loadConfig(configName, mainController);
                        break;
                    case SIMULATION:
                        simulationConfigurationController.loadConfig(configName, mainController);
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
                    graphOfChosenTargets = null;
                });
    }

    private void setUpdateChosenAction() {
        updateChosenTargetsButton.setOnAction(event -> {
            Collection<TargetDTO> selectedTargets = targetTableAvailableTargetsController.getSelectedTargets();

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
        graphOfChosenTargets = getWorkingGraph().DuplicateChosenOnly(names);
        graphOfChosenTargets.resetState();
        targetTableChosenTargetsController.populateData_SpecificTargets_SubGraph(chosenGraph, selectedTargets);
        graphGeneralDetailsComponent_ChosenTargetsGraphController.populateData(graphOfChosenTargets);
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

//        updateConfigurationList(taskType);
    }

    // TODO: fix this for the rest of ex03. I commented this out because the method relies on old ex02 code
    // of addressing the engine for old configurations
    private void updateConfigurationList(TaskType taskType) {
        if (taskType == null) {
            return;
        }

//        Collection<ConfigurationData> configData = Engine.getInstance().getConfigAll(taskType);
        Collection<ConfigurationDTO> configData = mainController.getConfigDataAll(taskType);


        existingConfigurationsListView.getItems().clear();

        configData.forEach(configurationData -> {
            existingConfigurationsListView.getItems().add(configurationData.getName());
        });
    }

//    private void loadInitialGraph() {
////        DependenciesGraph graph = (DependenciesGraph) Engine.getInstance().getGraph();
//        DependenciesGraph graph = mainController.getChosenGraph();
//
//        if (graph == null) {
//            return;
//        }
//
//        populateAvailableGraphData(graph);
//    }

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

        if (taskType != null) {
            switch (taskType) {
                case SIMULATION: {
                    configuration = simulationConfigurationController.getConfig();
                    break;
                }
                case COMPILATION: {
                    configuration = compilationConfigurationController.getConfig();
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
//            ConfigurationData configData = Engine.getInstance().getConfigActive(taskType);
            ConfigurationDTO configData = mainController.getActiveConfigData(taskType);

            if (configData != null) {
                labelActiveConfiguration.setText(configData.getName());
            } else {
                labelActiveConfiguration.setText("[None]");
            }
        } catch (Exception ignore) {
        }
    }

    @FXML
    void startPointFromScratchRadioButtonListener(ActionEvent event) {
        startPointRBSwitched();
        populateAvailableGraphData(chosenGraph);
//        loadInitialGraph(); // TODO: leftover from ex02
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

        populateAvailableGraphData(chosenGraph); // TODO: quick-and-dirty, before I deal with incremental

// TODO: ex02 code
//        ExecutionData lastExecution = Engine.getInstance().getExecutionLast(taskType);
//        if (lastExecution == null) {
//            loadInitialGraph();
//        } else {
//            DependenciesGraph lastGraph = lastExecution.getEndGraph();
//            populateAvailableGraphData(lastGraph);
//        }
    }

    @Override
    public void graphChosen() {
        clear();
        chosenGraph = mainController.getChosenGraph();
    }

    @Override
    public void fileLoaded() {
////        AppMainController.setThreadNumberChoiceBox(threadNumberChoiceBox);
//        clear();
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
            mainController.setActiveConfig(taskType, configName);
            displayMsgOnLabelWithFade(labelSetActiveConfigUpdate, configName + " set to new active configuration", Color.GREEN);
            updateActiveConfig(taskType);

//            boolean newActiveSet = Engine.getInstance().setActiveConfig(taskType, configName);
//            if (newActiveSet) {
//                displayMsgOnLabelWithFade(labelSetActiveConfigUpdate, configName + " set to new active configuration", Color.GREEN);
//                updateActiveConfig(taskType);
//            }
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

//        boolean added = Engine.getInstance().addConfiguration(taskType, configuration); // Old code

        boolean added = mainController.addConfiguration(configuration);

        if (added) {

            String msg = "Configuration added";
            Color color = Color.GREEN;
            AppMainController.AnimationFadeSingle(null, labelAddConfigMsg, msg, color);
            updateConfigurationList(taskType);
            isEditingNewConfig.set(false);
        }
    }



    @FXML
    void button_SubmitExecutionActionListener(ActionEvent event) {
        System.out.println("[TaskSettingsController - button_SubmitExecutionActionListener] Preparing execution data.");

        String executionName = textField_ExecutionName.getText();

        if (executionName == null || executionName == "") {
            AppMainController.AnimationFadeSingle(null, label_SubmissionResult, "Cannot send empty name", Color.RED);
            return;
        }

        Configuration config = getTaskConfigurationWithoutParticipatingTargets();
        if (config == null) {
            AppMainController.AnimationFadeSingle(null, label_SubmissionResult, "Configuration not specified", Color.RED);
            return;
        }

        DependenciesGraph chosenGraph = getGraphOfChosenTargets();
        if (chosenGraph == null) {
            AppMainController.AnimationFadeSingle(null, label_SubmissionResult, "Configuration not specified", Color.RED);
            return;
        }

        TaskType taskType = config.getTaskType();

        ConfigurationDTOCompilation configDTOComp = null;
        ConfigurationDTOSimulation configDTOSim = null;

        switch (config.getTaskType()) {
            case COMPILATION:
                configDTOComp = (ConfigurationDTOCompilation) config.toDTO();
                break;
            case SIMULATION:
                configDTOSim = (ConfigurationDTOSimulation) config.toDTO();
                break;
        }

        ExecutionDTO executionDTO = new ExecutionDTO(
                executionName,
                mainController.getUsername(),
                // config.toDTO(),
                chosenGraph.toDTO(),
                taskType,
                chosenGraph.getPrice(taskType),
                0,
                ExecutionStatus.NEW,
                configDTOComp,
                configDTOSim,
                null);

//        Gson gson = new Gson();
        Gson gson = new GsonBuilder().serializeNulls().create();

        String executionDTOJson = gson.toJson(executionDTO) + Constants.LINE_SEPARATOR;

//        RequestBody body =
//                new MultipartBody.Builder()
//                        .addFormDataPart("executionDTOJson", executionDTOJson)
//                        .build();

        String bodyStr =
                Constants.BP_EXECUTION_DTO + "=" + gson.toJson(executionDTO) + Constants.LINE_SEPARATOR;



        RequestBody body = RequestBody.create(
                Constants.JSON, executionDTOJson);

//
//        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.EXECUTION_UPLOAD)
                .newBuilder()
                .build()
                .toString();


//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code


//        HttpClientUtil.runAsync(finalUrl, body, new Callback() {
        HttpClientUtil.runAsync(finalUrl, bodyStr, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                showFileLoadingErrorMessage("Something went wrong with the file upload!");

//                        Platform.runLater(() ->
//                                resultMessageProperty.set("Something went wrong: " + e.getMessage()));
                Platform.runLater(() ->
                        executionSubmissionFailure());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            executionSubmissionFailure());
                } else {
                    Platform.runLater(() -> {
                        executionSubmissionSuccess();
                    });
                }
            }
        });

        /*
        HttpClientUtil.runAsync(finalUrl, body, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                showFileLoadingErrorMessage("Something went wrong with the file upload!");
                        Platform.runLater(() ->
                                label_SubmissionResult.setText("Something went wrong: " + e.getMessage()));
//                                resultMessageProperty.set("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
//                                    resultMessageProperty.set("Something went wrong: " + responseBody));
                            showFileLoadingErrorMessage("Something went wrong with the file upload!"));
                } else {
                    Platform.runLater(() -> {
                        fileLoadedListeners.forEach(FileLoadedListener::fileLoaded);

//                            showFileLoadingErrorMessage("Yay, file loaded! :)");

//                                appMainController.loginSuccessful(userName);
//                        chatAppMainController.updateUserName(userName);   // Aviad code
//                        chatAppMainController.switchToChatRoom();         // Aviad code
                    });
                }
            }
        });


         */
    }

    private void executionSubmissionSuccess() {
        String msg = "Execution submitted successfully! You should see it updated soon in the table";
        mainController.AnimationFadeSingle(15000, label_SubmissionResult, msg, Color.GREEN);
    }

    private void executionSubmissionFailure() {
        String msg = "Execution denied! Are you sure the name is unique, and that all the parameters are correct?";

        mainController.AnimationFadeSingle(15000, label_SubmissionResult, msg, Color.RED);
    }
}

