package components.execution.subscribedexecutions;

import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import task.enums.ExecutionStatus;
import task.execution.WorkerExecutionReportDTO;
import utilsharedall.ConstantsAll;
import utilsharedclient.ConstantsClient;

import java.io.IOException;
import java.util.List;
import java.util.Timer;

public class SubscribedExecutionsWorkerController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */


    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML    private TableView<WorkerExecutionReportDTO> tableView_SubscribedExecutions;
    @FXML    private TableColumn<WorkerExecutionReportDTO, String> tableColumn_ExecutionName;
    @FXML    private TableColumn<WorkerExecutionReportDTO, Integer> tableColumn_TotalWorkers;
    @FXML    private TableColumn<WorkerExecutionReportDTO, Float> tableColumn_ExecutionProgress;
    @FXML    private TableColumn<WorkerExecutionReportDTO, Integer> tableColumn_TargetsUserProcessed;
    @FXML    private TableColumn<WorkerExecutionReportDTO, Integer> tableColumn_CreditUserEarned;
    @FXML    private TableColumn<WorkerExecutionReportDTO, Boolean> tableColumn_IsUserActive;
    @FXML    private TableColumn<WorkerExecutionReportDTO, ExecutionStatus> tableColumn_ExecutionStatus;

    @FXML    private Button button_PauseParticipation;
    @FXML    private Button button_ResumeParticipation;
    @FXML    private Button button_CancelParticipation;

    @FXML    private RadioButton radioButton_ShowOnlyRunningExecutions;
    @FXML    private RadioButton radioButton_ShowOnlyExecutionsUserIsActiveIn;

    @FXML    private Label label_CurrentlySelectedExecutionName;
    @FXML    private Label label_UpdateResult;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private Timer timer;
    private SubscribedExecutionsRefresher listRefresher;
    private final BooleanProperty autoUpdate;
    private final BooleanProperty filter_ExecutionMustBeRunning;
    private final BooleanProperty filter_UserMustBeActive;



    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    public SubscribedExecutionsWorkerController() {
        this.autoUpdate = new SimpleBooleanProperty();
        this.filter_ExecutionMustBeRunning = new SimpleBooleanProperty(true);
        this.filter_UserMustBeActive = new SimpleBooleanProperty(true);
    }

    @FXML
    public void initialize() {
        tableColumn_ExecutionName.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, String>("executionName"));
        tableColumn_TotalWorkers.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, Integer>("totalWorkerCount"));
        tableColumn_ExecutionProgress.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, Float>("executionProgress"));
        tableColumn_TargetsUserProcessed.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, Integer>("targetsUserProcessedWithAnyResult"));
        tableColumn_CreditUserEarned.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, Integer>("totalCreditUserEarned"));
        tableColumn_IsUserActive.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, Boolean>("isUserActive"));
        tableColumn_ExecutionStatus.setCellValueFactory(new PropertyValueFactory<WorkerExecutionReportDTO, ExecutionStatus>("executionStatus"));

        setDoubleClickEvent();

        setActive(false);
        startRefresher();

        filter_ExecutionMustBeRunning.bind(radioButton_ShowOnlyRunningExecutions.selectedProperty());
        filter_UserMustBeActive.bind(radioButton_ShowOnlyExecutionsUserIsActiveIn.selectedProperty());

        tableColumn_ExecutionStatus.visibleProperty().bind(radioButton_ShowOnlyRunningExecutions.selectedProperty().not());
        tableColumn_IsUserActive.visibleProperty().bind(radioButton_ShowOnlyExecutionsUserIsActiveIn.selectedProperty().not());

        radioButton_ShowOnlyRunningExecutions.setSelected(true);
        radioButton_ShowOnlyExecutionsUserIsActiveIn.setSelected(true);
    }

    private void setDoubleClickEvent() {
        tableView_SubscribedExecutions.setRowFactory( tv -> {
            TableRow<WorkerExecutionReportDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    WorkerExecutionReportDTO rowData = row.getItem();

                    label_CurrentlySelectedExecutionName.setText(rowData.getExecutionName());
                }
            });

            return row ;
        });
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void startRefresher() {
        listRefresher = new SubscribedExecutionsRefresher(
                autoUpdate,
                null, // Aviad code sent something else here
                this::updateTable,
                filter_ExecutionMustBeRunning,
                filter_UserMustBeActive);
        timer = new Timer();
        timer.schedule(listRefresher, ConstantsClient.REFRESH_RATE_PARTICIPATING_EXECUTIONS, ConstantsClient.REFRESH_RATE_PARTICIPATING_EXECUTIONS);
    }

    private void updateTable(List<WorkerExecutionReportDTO> newList) {
        Platform.runLater(() -> {
            ObservableList<WorkerExecutionReportDTO> items = tableView_SubscribedExecutions.getItems();
            items.clear();
            items.addAll(newList);
        });
    }

    

    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public BooleanProperty getAutoUpdateProperty() {
        return autoUpdate;
    }

    public void setActive(boolean isActive) {
        getAutoUpdateProperty().set(isActive);
    }

    

    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    void button_CancelParticipationActionListener(ActionEvent event) {
        String confirmMsg = "Are you sure you want to remove yourself from the execution?";
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmMsg);

        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        if (ButtonType.OK.equals(result)) {
            removeUserFromExecution();
        }

    }

    private void removeUserFromExecution() {
        String executionName = label_CurrentlySelectedExecutionName.getText();
        if (executionName == null || executionName.isEmpty()) {
            return;
        }

        String finalUrl = HttpUrl
                .parse(ConstantsAll.EXECUTION_CANCEL_SUBSCRIBE)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_EXECUTION_NAME, executionName)
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        label_UpdateResult.setText("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            label_UpdateResult.setText("Something went wrong: " + responseBody));
                } else {
                    Platform.runLater(() -> {
                        label_UpdateResult.setText("User removed from " + executionName);
                        label_CurrentlySelectedExecutionName.setText("");
                    });
                }
            }
        });
    }

    @FXML
    void button_PauseParticipationActionListener(ActionEvent event) {
        updateActivityInExecution(false);
    }

    @FXML
    void button_ResumeParticipationActionListener(ActionEvent event) {
        updateActivityInExecution(true);
    }

    void updateActivityInExecution(boolean isActive) {
        String executionName = label_CurrentlySelectedExecutionName.getText();
        if (executionName == null || executionName.isEmpty()) {
            return;
        }

        String finalUrl = HttpUrl
                .parse(ConstantsAll.UPDATE_USER_EXECUTION_ACTIVITY)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_EXECUTION_NAME, executionName)
                .addQueryParameter(ConstantsAll.QP_IS_ACTIVE_IN_EXECUTION, String.valueOf(isActive))
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code
        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        label_UpdateResult.setText("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            label_UpdateResult.setText("Something went wrong: " + responseBody));
                } else {
                    Platform.runLater(() -> {
                        String msgSuffix;

                        if (isActive) {
                            msgSuffix = "resumed";
                        } else {
                            msgSuffix = "paused";
                        }

                        label_UpdateResult.setText(executionName + " participation " + msgSuffix);
                    });
                }
            }
        });
    }
}
