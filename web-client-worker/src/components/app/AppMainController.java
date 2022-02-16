package components.app;

import components.login.LoginWorkerController;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class AppMainController {
    private BorderPane root;
    private Scene mainScene;

    private Parent login;
    private LoginWorkerController loginController;


    public void setMainScene(Scene scene) {
        this.mainScene = scene;
    }

    public void setRoot(BorderPane root) {
        this.root = root;
    }

    public void displayLogin() {
        this.root.setTop(null);
        this.root.setBottom(null);
        this.root.setLeft(null);
        this.root.setRight(null);
        this.root.setCenter(login);
    }

    public void loginSuccessful(String userName) {
        displayMainApp();
    }

    private void displayMainApp() {
//        this.root.setTop(menu);
        this.root.setBottom(null);
        this.root.setLeft(null);
        this.root.setRight(null);
        this.root.setCenter(null);
    }


        public void setLogin(Parent parent, LoginWorkerController controller) {
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
}

//
//public class AppMainController {
//    private boolean hasPerformedInitialGraphLoad = false;
//
//
//    @FXML
//    private Parent headerComponent;
//    @FXML
//    private MenuController headerComponentController;
//    @FXML
//    private MenuController menuController;
//    @FXML
//    private ScrollPane targetTable;
//    @FXML
//    private TargetTableController targetTableController;
//    private Parent menu;
//
//    private TaskSettingsController taskSettingsController;
//    private Parent taskSettings;
//
//    private Parent graphAllDataComponent;
//    private GraphAllDataController graphAllDataComponentController;
//
//    private TaskExecutionMainController taskExecutionController;
//
//
//    private SimpleBooleanProperty allowAnimations = new SimpleBooleanProperty(true);
//
//

//
//    public boolean getAllowAnimations() { return allowAnimations.get(); }
//
//
//    // Events
//    private List<FileLoadedListener> fileLoadedListeners;
//    private List<ExecutionStartListener> executionStartListeners;
//    private List<ExecutionEndListener> executionEndListeners;
//
//
//    public AppMainController() {
//        fileLoadedListeners = new LinkedList<>();
//        executionStartListeners = new LinkedList<>();
//        executionEndListeners = new LinkedList<>();
//    }
//
//

//
//    public static void AlertErrorMessage(String headMsg, String contentMsg) {
//        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//
//        String header = headMsg != null ? headMsg : "Error";
//        errorAlert.setHeaderText(header);
//
//        errorAlert.setContentText(contentMsg);
//        errorAlert.showAndWait();
//    }
//
//    /**
//     * @param fadeTimeMilli sent null if you want system default time.
//     * @param label
//     * @param msg
//     * @param color
//     */
//    public static void AnimationFadeSingle(Integer fadeTimeMilli, Label label, String msg, Color color) {
//        if (label == null || msg == null) {
//            return;
//        }
//        label.setText(msg);
//        if (color != null) {
//            label.setTextFill(color);
//            label.setTextFill(color);
//        }
//        int millis = fadeTimeMilli != null ? fadeTimeMilli.intValue() : 10000;
//        FadeTransition ft = new FadeTransition(Duration.millis(millis), label);
//        ft.setFromValue(1.0);
//        ft.setToValue(0.0);
//        ft.setCycleCount(1);
//        ft.setAutoReverse(false);
//
//        ft.play();
//    }
//
//    public static boolean ConfirmationAlert(String contentText) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Confirm");
//        alert.setContentText(contentText);
//
//        Optional<ButtonType> option = alert.showAndWait();
//
//        boolean confirm = false;
//
//        if (option.get() == null || option.get() == ButtonType.CANCEL) {
//            confirm = false;
//        } else if (option.get() == ButtonType.OK) {
//            confirm = true;
//        }
//
//        return confirm;
//    }
//
//    public void addEventListener_FileLoaded(FileLoadedListener newListener) {
//        fileLoadedListeners.add(newListener);
//    }
//
//    public void addEventListener_ExecutionStarted(ExecutionStartListener newListener) {
//        executionStartListeners.add(newListener);
//    }
//
//    public void addEventListener_ExecutionEnded(ExecutionEndListener newListener) {
//        executionEndListeners.add(newListener);
//    }
//
//    @FXML
//    public void initialize() {
//        if (headerComponentController != null) {
//            headerComponentController.setMainController(this);
//            headerComponentController.disableTaskSettingsButton();
//        }
//
//        if (targetTableController != null) {
//            targetTableController.setMainController(this);
//        }
//    }
//
//    public boolean isHasPerformedInitialGraphLoad() {
//        return hasPerformedInitialGraphLoad;
//    }
//
//    public TaskSettingsController getTaskSettingsController() {
//        return taskSettingsController;
//    }
//
//
//    private void showFileLoadingErrorMessage(String msg) {
//        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
//        errorAlert.setHeaderText("File Load Error");
//        errorAlert.setContentText(msg);
//        errorAlert.showAndWait();
//    }
//
//    private void switchToMainGraphView() {
//        menuController.highlightTable();
//
//        root.setCenter(graphAllDataComponent);
//    }
//

//
//    public void setMenu(Parent parent, MenuController controller) {
//        this.menu = parent;
//        this.menuController = controller;
//
//        menuController.setMainController(this);
//        menuController.disableTaskSettingsButton();
//
//
//        menuController.getButtonDisableAnimations().selectedProperty().addListener((observable, oldValue, newValue) -> {
//            allowAnimations.set(! menuController.getButtonDisableAnimations().isSelected());
//        });
//
//
//        root.setTop(menu);
//
//        setThemesListener();
//    }
//
//    private void setThemesListener() {
//        menuController.getThemesComboBox().getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
//            Themes theme = menuController.getThemesComboBox().getSelectionModel().getSelectedItem();
//            mainScene.getStylesheets().removeAll();
//
//            switch (theme) {
//                case Regular:
//                    mainScene.getStylesheets().clear();
//                    break;
//                case Dark:
//                    mainScene.getStylesheets().add(getClass().getResource("/components/css/darktheme.css").toExternalForm());
//                    break;
//                case Blue:
//                    mainScene.getStylesheets().add(getClass().getResource("/components/css/bluetheme.css").toExternalForm());
//                    break;
//            }
//        });
//    }
//
//    public void setAllDataComponent(Parent parent, GraphAllDataController controller) {
//        this.graphAllDataComponent = parent;
//        this.graphAllDataComponentController = controller;
//
//        controller.setMainController(this);
//
//        switchToMainGraphView();
//    }
//

//
//    public void setTaskSettings(Parent parent, TaskSettingsController controller) {
//        this.taskSettings = parent;
//        this.taskSettingsController = controller;
//
//        controller.SetMainController(this);
//
//        root.setTop(menu);
//    }
//
//    public void taskButtonPressed() {
//        root.setCenter(taskSettings);
//    }
//
//    public void graphDetailsButtonGeneralPressed() {
//        switchToMainGraphView();
//    }
//
//
//    public void taskExecutionButtonPressed() {
//        root.setCenter(taskExecutionController.getMainParent());
//    }
//
//    public void setTaskExecutionController(TaskExecutionMainController controller) {
//        this.taskExecutionController = controller;
//
//        controller.setMainController(this);
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
//
//    private ConsumerManager getTaskConsumers() {
//        ConsumerManager consumerManager = TaskGeneral.getTaskConsumers(); // TODO: change to something GUI related
//        consumerManager.getEndTargetConsumers().add(new ConsumerFinishedTargetJavaFX(taskExecutionController));
//        consumerManager.getEndProcessConsumers().add(new ConsumerFinishedTaskProcessJavaFX(this));
//        consumerManager.getTargetStateChangedConsumers().add(new ConsumerTargetStateChangedJavaFX(taskExecutionController));
//        consumerManager.getStartTargetProcessingConsumers().add(new ConsumerStartProcessingTargetsJavaFX(taskExecutionController));
//
//        return consumerManager;
//    }
//
//    public void pauseButtonPressed() {
//        Engine.getInstance().pauseExecution(true);
//        System.out.println("[pauseButtonPressed - AppMainController] Sent pause to engine.");
//    }
//
//    public void continueButtonPressed() {
//        System.out.println("[continueButtonPressed - AppMainController] About to continue execution..");
//        Engine.getInstance().pauseExecution(false);
//    }
//
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
//    public void threadChangedDuringExecution(int newValue) {
//        Engine.getInstance().setThreadCount_activeThreads(newValue);
//    }
//
//    public void finishedExecutionProcess(ExecutionData executionData) {
//        executionEndListeners.forEach(executionEndListener -> executionEndListener.executedEnded(executionData));
//    }
//
//    private void loadDefaultConfigurationsToEngine() {
//        loadDefaultSimulationConfiguration();
//        loadDefaultCompilationConfiguration();
//    }
//
//    private void loadDefaultCompilationConfiguration() {
//        try {
//            ConfigurationCompilation compConfig = new ConfigurationCompilation(
//                    "Default Comp Config",
//                    1,
//                    "C:/Temp/",
//                    "C:/temp/"
//            );
//
//            Engine.getInstance().addConfigAndSetActive(TaskType.COMPILATION, compConfig);
//        } catch (Exception ignore) {
//        }
//    }
//
//    private void loadDefaultSimulationConfiguration() {
//        try {
//            ConfigurationSimulation simConfig = new ConfigurationSimulation(
//                    "Default Sim Config",
//                    1,
//                    0.5,
//                    5000,
//                    true,
//                    0.5);
//
//            Engine.getInstance().addConfigAndSetActive(TaskType.SIMULATION, simConfig);
//        } catch (InvalidInputRangeException | NameNotFoundException ignore) {
//        }
//    }
//
//    public void TaskSettingsEvent_updateChosenButtonAction() {
//        taskExecutionController.TaskSettingsEvent_UpdateChosenTargetsAction();
//    }
//
//    public void resetForNewExecutionButton_FromDynamicPanel() {
//        clearAll();
//    }
//
//    private void clearAll() {
//        taskSettingsController.reset();
//        taskExecutionController.reset();
//    }
//

//

//}