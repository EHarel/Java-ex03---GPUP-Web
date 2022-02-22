package components.dashboard;

import componentcode.executiontable.ExecutionDTOTable;
import components.app.AppMainController;
import components.dashboard.execution.ExecutionTableWorkerController;
import components.login.LoginPerformedListenerWorker;
import events.LoginPerformedListener;
import httpclient.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilsharedall.ConstantsAll;

import java.io.IOException;

public class DashboardWorkerController implements LoginPerformedListenerWorker {

    @FXML
    private ScrollPane MainPane;

    @FXML
    private BorderPane BorderPane;

    @FXML
    private Label label_SelectedExecutionName;

    @FXML
    private Button button_Subscribe;

    @FXML
    private Label label_SubscribeResult;

    @FXML private ScrollPane component_ExecutionTableWorker;
    @FXML private ExecutionTableWorkerController component_ExecutionTableWorkerController;

    private AppMainController mainController;

    @FXML
    public void initialize() {
        component_ExecutionTableWorkerController.setDashboardController(this);
    }

    @FXML
    void button_SubscribeActionListener(ActionEvent event) {
        ExecutionDTOTable executionDTOTable = component_ExecutionTableWorkerController.getCurrentlySelectedRow();
        if (executionDTOTable == null) {
            label_SubscribeResult.setText("No execution chosen!");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(ConstantsAll.EXECUTION_SUBSCRIBE)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_EXECUTION_NAME, executionDTOTable.getExecutionName())
                .build()
                .toString();

//        updateHttpStatusLine("New request is launched for: " + finalUrl); // Aviad Code

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        label_SubscribeResult.setText("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() ->
                            label_SubscribeResult.setText("Something went wrong: " + responseBody));
                } else {
                    Platform.runLater(() -> {
                        label_SubscribeResult.setText("Successfully subscribed to the execution!");
                        label_SelectedExecutionName.setText("");
                        mainController.event_SubscribedToExecution(executionDTOTable);
                    });
                }
            }
        });
    }

    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        this.mainController.addEventListener_LoginPerformed(this);
        this.component_ExecutionTableWorkerController.setMainController(mainController);
    }

    @Override
    public void loginPerformed(String username, int threadCount) {
        this.component_ExecutionTableWorkerController.setActive(true);
    }

    public void rowChangedEvent(ExecutionDTOTable currentlySelectedRow) {
        this.label_SelectedExecutionName.setText(currentlySelectedRow.getExecutionName());
    }
}
