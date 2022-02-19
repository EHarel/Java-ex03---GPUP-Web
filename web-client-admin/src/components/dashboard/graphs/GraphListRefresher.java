package components.dashboard.graphs;

import components.dashboard.DashboardController;
import graph.GraphDTO;
import httpclient.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utilsharedall.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utilsharedall.Constants.GSON_INSTANCE;

public class GraphListRefresher extends TimerTask {

    //    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<GraphDTO>> GraphListConsumer;
    private DashboardController dashboardController;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;


    public GraphListRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<List<GraphDTO>> usersListConsumer, DashboardController dashboardController) {
        this.shouldUpdate = shouldUpdate;
//        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.GraphListConsumer = usersListConsumer;
        requestNumber = 0;
        this.dashboardController = dashboardController;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
//        httpRequestLoggerConsumer.accept("About to invoke: " + utilsharedclient.Constants.USERS_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(Constants.GRAPH_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[GraphListRefresher] onResponse - start");
                String jsonArrayOfGraphs = response.body().string();
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);

                GraphDTO[] graphDTOs = GSON_INSTANCE.fromJson(jsonArrayOfGraphs, GraphDTO[].class);

                // TODO: need to convert to list of UserDTO
                GraphListConsumer.accept(Arrays.asList(graphDTOs));

                if (dashboardController != null) {
                    dashboardController.addGraphs(graphDTOs);
                }
            }
        });
    }
}

