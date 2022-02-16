package components.app;

import components.dashboard.DashboardWorkerController;
import components.login.LoginWorkerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class GPUPWorker extends javafx.application.Application {
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        GPUPWorker.mainStage = primaryStage;

        primaryStage.setTitle("G.P.U.P. Worker");

//        Parent mainComponent = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
//        Scene scene = new Scene(mainComponent, 600, 600);

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("MainScene.fxml");
        fxmlLoader.setLocation(url);
        BorderPane root = fxmlLoader.load(url.openStream());
        AppMainController mainController = fxmlLoader.getController();
        mainController.setRoot(root);

        loadSubComponents(mainController);

        Scene scene = new Scene(root, 1000, 600);

        mainController.setMainScene(scene);

        mainController.displayLogin();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadSubComponents(AppMainController mainController) {
//        loadMainMenu(mainController);
        loadLogin(mainController);
        loadDashboard(mainController);
//        loadTaskSettings(mainController);
//        loadGraphAllDataComponentWithSubComponents(mainController);
//        loadTaskExecution(mainController);
    }

    private void loadLogin(AppMainController mainController) {
        try {
            System.out.println("[loadLogin] Start");
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/components/login/LoginWorker.fxml");
            fxmlLoader.setLocation(url);
            Parent parent = fxmlLoader.load(url.openStream());
            LoginWorkerController controller = fxmlLoader.getController();

            mainController.setLogin(parent, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboard(AppMainController mainController) {
        //        loadMainDashboard();
//        loadUserTable();
//        loadGraphTable();
//        loadGraphTasks();

        try {
            System.out.println("[loadDashboard] Start");
            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getResource("/components/dashboard/DashboardWorker.fxml");
            fxmlLoader.setLocation(url);
            Parent parent = fxmlLoader.load(url.openStream());
            DashboardWorkerController dashboardController = fxmlLoader.getController();

//            loadGraphOverviewComponent(allDataController);
//            loadTargetPathsComponent(allDataController);
//            loadTargetCycleComponent(allDataController);
//            loadWhatIfComponent(allDataController);
//            loadGraphSerialSetsComponent(allDataController);

            mainController.setDashboardComponent(parent, dashboardController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private void loadMainMenu(AppMainController mainController) {
//        try {
//            System.out.println("[loadMainMenu] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/menu/Menu.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            MenuController controller = fxmlLoader.getController();
//
//            mainController.setMenu(parent, controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void loadGraphAllDataComponentWithSubComponents(AppMainController mainController) {
//        try {
//            System.out.println("[loadGraphAllDataComponent] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/graph/alldata/GraphAllData.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            GraphAllDataController allDataController = fxmlLoader.getController();
//
//            loadGraphOverviewComponent(allDataController);
//            loadTargetPathsComponent(allDataController);
//            loadTargetCycleComponent(allDataController);
//            loadWhatIfComponent(allDataController);
//            loadGraphSerialSetsComponent(allDataController);
//
//            mainController.setAllDataComponent(parent, allDataController);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadGraphOverviewComponent(GraphAllDataController allDataController) {
//        try {
//            System.out.println("[loadGraphOverviewComponent] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/graph/operations/overview/GraphOverview.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            GraphOverviewController controller = fxmlLoader.getController();
//
//            allDataController.setGraphOverviewController(controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadTargetPathsComponent(GraphAllDataController allDataController) {
//        try {
//            System.out.println("[loadTargetPathsComponent] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/graph/operations/paths/TargetPaths.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            TargetPathsController controller = fxmlLoader.getController();
//
//            allDataController.setTargetPathsController(controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadWhatIfComponent(GraphAllDataController allDataController) {
//        try {
//            System.out.println("[loadWhatIfComponent] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/graph/operations/whatif/WhatIf.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            WhatIfController controller = fxmlLoader.getController();
//
//            allDataController.setWhatIfController(controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadGraphSerialSetsComponent(GraphAllDataController allDataController) {
//        try {
//            System.out.println("[loadGraphSerialSetsComponent] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/graph/operations/serialset/SerialSets.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            SerialSetsController controller = fxmlLoader.getController();
//
//            allDataController.setSerialSetsController(controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadTargetCycleComponent(GraphAllDataController allDataController) {
//        try {
//            System.out.println("[loadTargetCycleComponent] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/graph/operations/targetcycles/TargetCycles.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            TargetCyclesController controller = fxmlLoader.getController();
//
//            allDataController.setTargetCyclesController(controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void loadTaskSettings(AppMainController mainController) {
//        try {
//            System.out.println("[loadTaskSettings] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/task/settings/TaskSettings.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            TaskSettingsController controller = fxmlLoader.getController();
//
//            mainController.setTaskSettings(parent, controller);
//
//            loadSimulationConfiguration(mainController, controller);
//            loadCompilationConfiguration(mainController, controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadSimulationConfiguration(AppMainController mainController, TaskSettingsController taskSettingsController) {
//        try {
//            System.out.println("[loadSimulationConfiguration] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/task/configuration/SimulationConfiguration.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            SimulationConfigurationController controller = fxmlLoader.getController();
//
//            taskSettingsController.SetSimulationConfigurationController(parent, controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadCompilationConfiguration(AppMainController mainController, TaskSettingsController taskSettingsController) {
//        try {
//            System.out.println("[loadCompilationConfiguration] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/task/configuration/CompilationConfiguration.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            CompilationConfigurationController controller = fxmlLoader.getController();
//
//            taskSettingsController.SetCompilationConfigurationController(parent, controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    /**
//     * Must be done after
//     * @param mainController
//     */
//    private void loadTaskExecution(AppMainController mainController) {
//        try {
//            System.out.println("[loadTaskExecution] Start");
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            URL url = getClass().getResource("/components/task/execution/main/TaskExecutionMain.fxml");
//            fxmlLoader.setLocation(url);
//            Parent parent = fxmlLoader.load(url.openStream());
//            TaskExecutionMainController controller = fxmlLoader.getController();
//
//            mainController.setTaskExecutionController(controller);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static Stage getStage() {
        return GPUPWorker.mainStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
