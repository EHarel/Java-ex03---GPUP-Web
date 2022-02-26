package components.app;

import componentcode.executiontable.ExecutionDTOTable;
import componentcode.executiontable.ExecutionListRefresher;
import components.chat.chatroomgeneral.ChatRoomGeneralController;
import components.css.Themes;
import components.dashboard.DashboardController;
import components.graph.alldata.GraphAllDataController;
import components.login.LoginAdminController;
import components.menu.MenuController;
import components.graph.targettable.TargetTableController;
import components.task.execution.main.TaskExecutionMainController;
import components.task.settings.TaskSettingsController;
//import console.task.TaskGeneral;
import events.*;
import exception.*;
import graph.DependenciesGraph;
import graph.GraphDTO;
import httpclient.HttpClientUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Engine;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import task.Execution;
import task.enums.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationCompilation;
import task.configuration.ConfigurationDTO;
import task.configuration.ConfigurationSimulation;
import utilsharedall.ConstantsAll;
import utilsharedclient.ConstantsClient;

import javax.naming.NameNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppMainController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    private boolean hasPerformedInitialGraphLoad = false;

    private BorderPane root;

    @FXML
    private Parent headerComponent;
    @FXML
    private MenuController headerComponentController;
    @FXML
    private MenuController menuController;
    @FXML
    private ScrollPane targetTable;
    @FXML
    private TargetTableController targetTableController;
    private Parent menu;

    private TaskSettingsController taskSettingsController;
    private Parent taskSettings;

    private Parent graphAllDataComponent;
    private GraphAllDataController graphAllDataComponentController;

    private TaskExecutionMainController taskExecutionController;

    private Scene mainScene;

    private SimpleBooleanProperty allowAnimations = new SimpleBooleanProperty(false);


    private Parent login;
    private LoginAdminController loginController;

    private Parent dashboard;
    private DashboardController dashboardController;
    private Stage primaryStage;

    private Parent chat;
    private ChatRoomGeneralController chatController;


    public boolean getAllowAnimations() {
        return allowAnimations.get();
    }

    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private String username;
    private String chosenGraphName;
    private DependenciesGraph chosenGraph;
    private GraphManager graphManager;
    private ConfigurationManager configManager;
    private ExecutionManager executionManager;

    private final BooleanProperty shouldUpdateExecution;
    private ExecutionListRefresher executionListRefresher;
    private Timer executionListTimer;



    // Events
    private List<FileLoadedListener> fileLoadedListeners;
    private List<ExecutionStartListener> executionStartListeners;
    private List<ExecutionEndListener> executionEndListeners;
    private List<GraphChosenListener> graphChosenListeners;
    private List<ExecutionChosenListener> executionChosenListeners;
    private List<LoginPerformedListener> loginPerformedListeners;



    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    public AppMainController() {
        fileLoadedListeners = new LinkedList<>();
        executionStartListeners = new LinkedList<>();
        executionEndListeners = new LinkedList<>();
        graphChosenListeners = new LinkedList<>();
        executionChosenListeners = new LinkedList<>();
        loginPerformedListeners = new LinkedList<>();
        graphManager = new GraphManager();
        configManager = new ConfigurationManager();
        executionManager = new ExecutionManager();

        this.shouldUpdateExecution = new SimpleBooleanProperty(false);
        startExecutionListRefresher();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMainScene(Scene scene) {
        this.mainScene = scene;
    }

    public static void AlertErrorMessage(String headMsg, String contentMsg) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);

        String header = headMsg != null ? headMsg : "Error";
        errorAlert.setHeaderText(header);

        errorAlert.setContentText(contentMsg);
        errorAlert.showAndWait();
    }

    /**
     * @param fadeTimeMilli sent null if you want system default time.
     * @param label
     * @param msg
     * @param color
     */
    public static void AnimationFadeSingle(Integer fadeTimeMilli, Label label, String msg, Color color) {
        if (label == null || msg == null) {
            return;
        }
        label.setText(msg);
        if (color != null) {
            label.setTextFill(color);
            label.setTextFill(color);
        }
        int millis = fadeTimeMilli != null ? fadeTimeMilli.intValue() : 10000;
        FadeTransition ft = new FadeTransition(Duration.millis(millis), label);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);

        ft.play();
    }

    public static boolean ConfirmationAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setContentText(contentText);

        Optional<ButtonType> option = alert.showAndWait();

        boolean confirm = false;

        if (option.get() == null || option.get() == ButtonType.CANCEL) {
            confirm = false;
        } else if (option.get() == ButtonType.OK) {
            confirm = true;
        }

        return confirm;
    }

    public void addEventListener_FileLoaded(FileLoadedListener newListener) {
        fileLoadedListeners.add(newListener);
    }

    public void addEventListener_ExecutionStarted(ExecutionStartListener newListener) {
        executionStartListeners.add(newListener);
    }

    public void addEventListener_LoginPerformed(LoginPerformedListener newListener) {
        loginPerformedListeners.add(newListener);
    }

    public void addEventListener_ExecutionEnded(ExecutionEndListener newListener) {
        executionEndListeners.add(newListener);
    }

    public void addEventListener_GraphChosen(GraphChosenListener newListener) {
        graphChosenListeners.add(newListener);
    }

    public void addEventListener_ExecutionChosen(ExecutionChosenListener newListener) { executionChosenListeners.add(newListener); }

    @FXML
    public void initialize() {
        if (headerComponentController != null) {
            headerComponentController.setMainController(this);
//            headerComponentController.disableTaskSettingsButton();
        }

        if (targetTableController != null) {
            targetTableController.setMainController(this);
        }
    }

    public TaskSettingsController getTaskSettingsController() {
        return taskSettingsController;
    }

    public void loadFile() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(GPUPAdmin.getStage());
        if (selectedFile != null) {

            System.out.println("Loading file.");

            RequestBody body =
                    new MultipartBody.Builder()
                            .addFormDataPart("file1", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("text/plain")))
                            .build();

            //noinspection ConstantConditions
            String finalUrl = HttpUrl
                    .parse(ConstantsAll.FILE_UPLOAD)
                    .newBuilder()
                    .build()
                    .toString();


//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code
            HttpClientUtil.runAsync(finalUrl, body, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    showFileLoadingErrorMessage("Something went wrong with the file upload!");

//                        Platform.runLater(() ->
//                                resultMessageProperty.set("Something went wrong: " + e.getMessage()));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() != 200) {
                        Platform.runLater(() ->
//                                    resultMessageProperty.set("Something went wrong: " + responseBody));
                                showFileLoadingErrorMessage(responseBody));
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
//
//            try {
//
//                if (!hasPerformedInitialGraphLoad) {
//                    hasPerformedInitialGraphLoad = true;
//                    loadDefaultConfigurationsToEngine();
//                    menuController.enableTaskSettingsButton();
//                }
//                loaded = true;
//                fileLoadedListeners.forEach(FileLoadedListener::fileLoaded);
//            } catch (FileNotFoundException e) {
//                showFileLoadingErrorMessage(e.getMessage());
//                e.printStackTrace();
//            } catch (JAXBException e) {
//                e.printStackTrace();
//            } catch (ExistingItemException e) {
//                showFileLoadingErrorMessage(e.getMessage());
//                e.printStackTrace();
//            } catch (DependencyOnNonexistentTargetException e) {
//                showFileLoadingErrorMessage(e.getMessage());
//                e.printStackTrace();
//            } catch (ImmediateCircularDependencyException e) {
//                showFileLoadingErrorMessage(e.getMessage());
//            } catch (NullOrEmptyStringException e) {
//                e.printStackTrace();
//            } catch (InvalidInputRangeException e) {
//                showFileLoadingErrorMessage(e.getMessage());
//                e.printStackTrace();
//            } catch (NonexistentTargetException e) {
//                showFileLoadingErrorMessage(e.getMessage());
//            } catch (SerialSetNameRepetitionException e) {
//                showFileLoadingErrorMessage("Repeating serial set name.\n" + e.getMessage());
//            } finally {
//                if (!loaded) {
//                    menuController.failedToLoad();
//                }
//            }
        }
    }

    private void showFileLoadingErrorMessage(String msg) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("File Load Error");
        errorAlert.setContentText(msg);
        errorAlert.showAndWait();
    }

    private void switchToMainGraphView() {
        menuController.highlightTable();

        root.setCenter(graphAllDataComponent);
    }

    public void setLogin(Parent parent, LoginAdminController controller) {
        this.login = parent;
        this.loginController = controller;

        loginController.setMainController(this);

        loginController.getButton_Login().addEventHandler(ActionEvent.ACTION,
                event -> {
                    // Try login to server

                    // If failed, present message
                    // If successful, switch views
                });
    }

    public void setChat(Parent parent, ChatRoomGeneralController controller) {
        this.chat = parent;
        this.chatController = controller;
        this.chatController.setMainController(this);
    }

    public void setDashboardComponent(Parent parent, DashboardController dashboardController) {
        this.dashboard = parent;
        this.dashboardController = dashboardController;
        dashboardController.setMainController(this);
    }

    public void setMenu(Parent parent, MenuController controller) {
        this.menu = parent;
        this.menuController = controller;

        menuController.setMainController(this);

//        menuController.getButtonDisableAnimations().selectedProperty().addListener((observable, oldValue, newValue) -> {
//            allowAnimations.set(!menuController.getButtonDisableAnimations().isSelected());
//        });

        root.setTop(menu);

        setThemesListener();
    }

    private void setThemesListener() {
        menuController.getThemesComboBox().getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            Themes theme = menuController.getThemesComboBox().getSelectionModel().getSelectedItem();
            mainScene.getStylesheets().removeAll();

            switch (theme) {
                case Regular:
                    mainScene.getStylesheets().clear();
                    break;
                case Dark:
                    mainScene.getStylesheets().add(getClass().getResource("/components/css/darktheme.css").toExternalForm());
                    break;
                case Blue:
                    mainScene.getStylesheets().add(getClass().getResource("/components/css/bluetheme.css").toExternalForm());
                    break;
            }
        });
    }

    public void setAllDataComponent(Parent parent, GraphAllDataController controller) {
        this.graphAllDataComponent = parent;
        this.graphAllDataComponentController = controller;

        controller.setMainController(this);

        switchToMainGraphView();
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public void setTaskSettings(Parent parent, TaskSettingsController controller) {
        this.taskSettings = parent;
        this.taskSettingsController = controller;

        controller.SetMainController(this);

        root.setTop(menu);
    }

    public void taskButtonPressed() {
        displayTaskSettings();
    }

    private void displayTaskSettings() {
        dashboardController.setActive(false);
        root.setCenter(taskSettings);
    }

    public void graphDetailsButtonGeneralPressed() {
        switchToMainGraphView();
    }


    public void taskExecutionButtonPressed() {
        root.setCenter(taskExecutionController.getMainParent());
    }

    public void setTaskExecutionController(TaskExecutionMainController controller) {
        this.taskExecutionController = controller;

        controller.setMainController(this);
    }

    public void pauseButtonPressed() {
        Engine.getInstance().pauseExecution(true);
        System.out.println("[pauseButtonPressed - AppMainController] Sent pause to engine.");
    }

    public void continueButtonPressed() {
        System.out.println("[continueButtonPressed - AppMainController] About to continue execution..");
        Engine.getInstance().pauseExecution(false);
    }

    public void threadChangedDuringExecution(int newValue) {
        Engine.getInstance().setThreadCount_activeThreads(newValue);
    }

    public void finishedExecutionProcess(Execution execution) {
        executionEndListeners.forEach(executionEndListener -> executionEndListener.executedEnded(execution));
    }

    private void loadDefaultConfigurationsToEngine() {
        loadDefaultSimulationConfiguration();
        loadDefaultCompilationConfiguration();
    }

    private void loadDefaultCompilationConfiguration() {
        try {
            ConfigurationCompilation compConfig = new ConfigurationCompilation(
                    "Default Comp Config",
                    1,
                    "C:/Temp/",
                    "C:/temp/"
            );

            Engine.getInstance().addConfigAndSetActive(TaskType.COMPILATION, compConfig);
        } catch (Exception ignore) {
        }
    }

    private void loadDefaultSimulationConfiguration() {
        try {
            ConfigurationSimulation simConfig = new ConfigurationSimulation(
                    "Default Sim Config",
                    1,
                    0.5,
                    5000,
                    true,
                    0.5);

            Engine.getInstance().addConfigAndSetActive(TaskType.SIMULATION, simConfig);
        } catch (InvalidInputRangeException | NameNotFoundException ignore) {
        }
    }

    public void TaskSettingsEvent_updateChosenButtonAction() {
        taskExecutionController.TaskSettingsEvent_UpdateChosenTargetsAction();
    }

    public void resetForNewExecutionButton_FromDynamicPanel() {
        clearAll();
    }

    private void clearAll() {
        taskSettingsController.reset();
        taskExecutionController.reset();
    }

    public void displayLogin() {
        this.root.setTop(null);
        this.root.setBottom(null);
        this.root.setLeft(null);
        this.root.setRight(null);
        this.root.setCenter(login);
    }

    public void loginSuccessful(String userName) {
        this.username = userName;
        this.primaryStage.setTitle(this.primaryStage.getTitle() + " - Logged in as: " + userName);

        this.executionListRefresher.setUsername(username);
        this.shouldUpdateExecution.set(true);

        displayMainApp();

        loginPerformedListeners.forEach(loginPerformedListener -> {loginPerformedListener.loginPerformed(userName);});
    }

    public void startExecutionListRefresher() {
        this.executionListRefresher = new ExecutionListRefresher(shouldUpdateExecution, null, null);

        this.executionListTimer = new Timer();
        this.executionListTimer.schedule(executionListRefresher, ConstantsClient.REFRESH_RATE_EXECUTIONS, ConstantsClient.REFRESH_RATE_EXECUTIONS);
    }


    private void displayMainApp() {
        this.root.setTop(menu);
        this.root.setBottom(null);
        this.root.setLeft(null);
        this.root.setRight(null);
        displayDashboard();
    }

    private void displayDashboard() {
        this.root.setCenter(dashboard);
        dashboardController.setActive(true);
    }

    private void displayChat() {
        this.root.setCenter(chat);
    }

    public void dashboardButtonPressed() {
        displayDashboard();
    }

    /**
     * This event is responsible for handling all the implications of a new graph being chosen by the admin.
     * This graph must be read from the dashboard, and then be made relevant to all the other components.
     */
    public void event_NewGraphSelected() {
        String chosenGraphName = dashboardController.getChosenGraphName();

        chosenGraph = null;

        if (chosenGraphName != null) {
            chosenGraph = graphManager.getGraph(chosenGraphName);
        }

        graphChosenListeners.forEach(GraphChosenListener::graphChosen);
    }

    public void event_NewExecutionSelected(ExecutionDTOTable executionDTOTable) {
        executionChosenListeners.forEach(executionChosenListener -> { executionChosenListener.executionChosen(executionDTOTable);} );
    }

    public DependenciesGraph getChosenGraph() {
        return chosenGraph;
    }

    public void addGraphs(GraphDTO[] graphDTOs) {
        graphManager.addGraphs(graphDTOs);
    }

    public boolean addConfiguration(Configuration configuration) {
        return configManager.addConfiguration(configuration);
    }

    public boolean removeConfiguration(TaskType taskType, String configName) {
        return configManager.removeConfiguration(taskType, configName);
    }

    public Collection<ConfigurationDTO> getConfigDataAll(TaskType taskType) {
        return configManager.getConfigDataAll(taskType);
    }

    public ConfigurationDTO getActiveConfigData(TaskType taskType) {
        return configManager.getActiveConfigData(taskType);
    }

    public void setActiveConfig(TaskType taskType, String configName) {
        configManager.setActiveConfig(taskType, configName);
    }

    public ConfigurationDTO getConfigDataSpecific(TaskType taskType, String configName) {
        return configManager.getConfigDataSpecific(taskType, configName);
    }

    public void setMainStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void event_ButtonPressed_StartExecution() {
        // In the old-code section there is the old version of this method. Might be worth it to consider.

        // Send
        taskExecutionController.sendServerRequest_ExecutionStart();
    }

    public void event_ButtonPressed_ContinueExecution() {
        taskExecutionController.sendServerRequest_ExecutionContinue();
    }

    public String getChosenExecutionName() {
        return dashboardController.getChosenExecutionName();
    }

    public void event_ButtonPressed_PauseExecution() {

        taskExecutionController.sendServerRequest_ExecutionPause();
    }

    public void event_ButtonPressed_StopExecution() {
        taskExecutionController.sendServerRequest_ExecutionStop();
    }

    public void addConfigurationIfMissing(ExecutionDTOTable executionDTOTable) {
        configManager.addConfigurationIfMissing(executionDTOTable);
    }

    /**
     * Set active config based on the configuration of the given execution.
     */
    public void setActiveConfig(ExecutionDTOTable executionDTOTable) {
        configManager.setActiveConfig(executionDTOTable);
    }

    public Integer getExecutionCount(String executionName) {
        return executionManager.getExecutionCount(executionName);
    }

    /**
     * Adds a new execution to the list of executions user has created.
     */
    public void addExecution(String executionName) {
        executionManager.addExecution(executionName);
    }

    public ExecutionListRefresher getExecutionListRefresher() {
        return executionListRefresher;
    }

    public void increaseExecutionCount(String executionOriginalName) {
        executionManager.increaseExecutionCount(executionOriginalName);
    }

    public void chatButtonPressed() {
        displayChat();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
}


/* ---------------------------------------------------------------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
/* --------------------------------------------- OLD CODE --------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
//    public static void setThreadNumberChoiceBox(ChoiceBox<Integer> threadNumCB) {
//        int maxParallelism = Engine.getInstance().getThreadCount_maxParallelism();
//        threadNumCB.getItems().clear();
//        for (int i = 0; i < maxParallelism; i++) {
//            int value = i + 1;
//            threadNumCB.getItems().add(value);
//        }
//
//        threadNumCB.getSelectionModel().select(0);
//    }


//
//    public void startExecutionButtonPressed() {
////        Configuration configuration = taskSettingsController.getTaskConfigurationWithParticipatingTargets();
////        if (configuration == null) {
////            return;
////        }
//        String errorTitle = "Execution start error";
//
//        TaskProcess.StartPoint taskStartPoint = taskSettingsController.GetTaskStartPoint();
//        if (taskStartPoint == null) {
//            AlertErrorMessage(errorTitle, "No start point defined");
//            return;
//        }
//
//        TaskType taskType = taskSettingsController.getChosenTaskType();
//        if (taskType == null) {
//            AlertErrorMessage(errorTitle, "No task type defined");
//            return;
//        }
//
//        ConfigurationData configData = Engine.getInstance().getConfigActive(taskType);
//        if (configData == null) {
//            AlertErrorMessage(errorTitle, "No active configuration set. Go to the settings and define an active config.");
//            return;
//        }
//
//        ConsumerManager consumerManager = getTaskConsumers();
//
//        Integer threadNum = taskSettingsController.getThreadNum();
//        if (threadNum == null) {
//            AlertErrorMessage(errorTitle, "No thread number specified. Go to the settings and define how many threads to use.");
//            return;
//        }
//
//        Collection<String> participatingTargetNames = taskSettingsController.getParticipatingTargets();
//        if (participatingTargetNames == null || participatingTargetNames.isEmpty()) {
//            AlertErrorMessage(errorTitle, "No participating targets defined. Go to target selection and choose targets.");
//            return;
//        }
//
//        try {
//            Engine.getInstance().activeConfig_UpdateThreadCount(taskType, threadNum.intValue());
//            Engine.getInstance().activeConfig_UpdateParticipatingTargets(taskType, participatingTargetNames);
//
//            Configuration activeConfig = Engine.getInstance().getActiveConfigNotData(taskType);
//
//
//            Engine.getInstance().executeTask(taskType, taskStartPoint, consumerManager, participatingTargetNames);
//
//
//            executionStartListeners.forEach(executionStartListener -> executionStartListener.executionStarted(activeConfig));
//        } catch (UninitializedTaskException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NoConfigurationException e) {
//            e.printStackTrace();
//        } catch (InvalidInputRangeException invalidInputRangeException) {
//            AlertErrorMessage(errorTitle, "Invalid number of threads chosen");
//        }
//    }

//    private ConsumerManager getTaskConsumers() {
//        ConsumerManager consumerManager = TaskGeneral.getTaskConsumers(); // TODO: change to something GUI related
//        consumerManager.getEndTargetConsumers().add(new ConsumerFinishedTargetJavaFX(taskExecutionController));
//        consumerManager.getEndProcessConsumers().add(new ConsumerFinishedTaskProcessJavaFX(this));
//        consumerManager.getTargetStateChangedConsumers().add(new ConsumerTargetStateChangedJavaFX(taskExecutionController));
//        consumerManager.getStartTargetProcessingConsumers().add(new ConsumerStartProcessingTargetsJavaFX(taskExecutionController));
//
//        return consumerManager;
//    }