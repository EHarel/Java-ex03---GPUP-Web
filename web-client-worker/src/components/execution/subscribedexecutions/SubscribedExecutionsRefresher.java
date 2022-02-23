package components.execution.subscribedexecutions;

import graph.GraphDTO;
import httpclient.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import task.execution.WorkerExecutionReportDTO;
import utilsharedall.ConstantsAll;
import utilsharedall.UserType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import static utilsharedall.ConstantsAll.GSON_INSTANCE;

public class SubscribedExecutionsRefresher extends TimerTask {
    private final BooleanProperty shouldUpdate;
    private final Consumer<List<WorkerExecutionReportDTO>> GraphListConsumer;
    private int requestNumber;
    BooleanProperty filter_ExecutionMustBeRunning;
    BooleanProperty filter_UserMustBeActive;

    public SubscribedExecutionsRefresher(BooleanProperty shouldUpdate,
                                         Consumer<String> httpRequestLoggerConsumer,
                                         Consumer<List<WorkerExecutionReportDTO>> listConsumer,
                                         BooleanProperty filter_ExecutionMustBeRunning,
                                         BooleanProperty filter_UserMustBeActive) {
        this.shouldUpdate = shouldUpdate;
//        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.GraphListConsumer = listConsumer;
        this.requestNumber = 0;

        this.filter_ExecutionMustBeRunning = filter_ExecutionMustBeRunning;
        this.filter_UserMustBeActive = filter_UserMustBeActive;
    }

    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
//        httpRequestLoggerConsumer.accept("About to invoke: " + utilsharedclient.Constants.USERS_LIST + " | Users Request # " + finalRequestNumber);

        String filterExecutionStr = filter_ExecutionMustBeRunning.getValue().toString();
        String filterUserActiveStr = filter_UserMustBeActive.getValue().toString();

        String finalUrl = HttpUrl
                .parse(ConstantsAll.USERS_PARTICIPATING_EXECUTIONS)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_FILTER_EXECUTION_MUST_BE_RUNNING, filterExecutionStr)
                .addQueryParameter(ConstantsAll.QP_FILTER_USER_MUST_BE_ACTIVE, filterUserActiveStr)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[SubscribedExecutionsRefresher] onResponse - start");
                String jsonArrayOfReports = response.body().string();
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);

                WorkerExecutionReportDTO[] graphDTOs = GSON_INSTANCE.fromJson(jsonArrayOfReports, WorkerExecutionReportDTO[].class);

                if (graphDTOs != null) {
                    GraphListConsumer.accept(Arrays.asList(graphDTOs));
                }
            }
        });
    }
}
