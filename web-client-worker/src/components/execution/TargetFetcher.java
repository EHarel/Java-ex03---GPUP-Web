package components.execution;

import components.app.AppMainController;
import graph.GraphDTO;
import graph.Target;
import graph.TargetDTO;
import httpclient.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class TargetFetcher extends TimerTask {
    private final BooleanProperty shouldUpdate;
    private final ExecutionManager executionManager;
    private final AppMainController mainController;
    private int requestNumber;


    public TargetFetcher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<List<GraphDTO>> usersListConsumer, ExecutionManager executionManager, AppMainController mainController) {
        this.shouldUpdate = shouldUpdate;
//        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
//        this.GraphListConsumer = usersListConsumer;
        requestNumber = 0;
        this.executionManager = executionManager;
        this.mainController = mainController;
    }


    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        shouldUpdate.set(false); // TODO: delete once done debugging


        final int finalRequestNumber = ++requestNumber;
//        httpRequestLoggerConsumer.accept("About to invoke: " + utilsharedclient.Constants.USERS_LIST + " | Users Request # " + finalRequestNumber);

        int availableThreads = executionManager.getAvailableThreads(); // TODO uncomment and implement
//        int availableThreads = 3;

        if (availableThreads == 0) {
            return;
        }

        String finalUrl = HttpUrl
                .parse(ConstantsAll.EXECUTION_TARGET_FETCH)
                .newBuilder()
                .addQueryParameter(ConstantsAll.QP_TARGET_COUNT, String.valueOf(availableThreads))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
                shouldUpdate.set(true); // TODO: delete once done debugging
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[TargetFetcher] onResponse - start");

                String jsonArrayOfTargets = response.body().string();
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);

                if (jsonArrayOfTargets != null && !jsonArrayOfTargets.isEmpty()) {

                    TargetDTO[] targetDTOs = ConstantsAll.GSON_INSTANCE.fromJson(jsonArrayOfTargets, TargetDTO[].class);

                    List<TargetDTO> targetDTOList = Arrays.asList(targetDTOs);

                    if (targetDTOList.size() > 0) {
                        mainController.newTargetsReceived(Arrays.asList(targetDTOs));
                    }
                }
                shouldUpdate.set(true); // TODO: delete once done debugging
            }
        });
    }
}
