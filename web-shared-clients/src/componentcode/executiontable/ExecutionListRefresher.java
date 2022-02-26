package componentcode.executiontable;

import httpclient.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import task.execution.ExecutionDTO;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utilsharedall.ConstantsAll.GSON_INSTANCE;

public class ExecutionListRefresher extends TimerTask {

    //    private final Consumer<String> httpRequestLoggerConsumer;
    private Collection<Consumer<List<ExecutionDTOTable>>> listConsumer;
    private String username;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;


    public ExecutionListRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, String username) {
        this.shouldUpdate = shouldUpdate;
//        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.listConsumer = new LinkedList<>();
        this.username = username;
        requestNumber = 0;
    }

    public void addConsumer(Consumer<List<ExecutionDTOTable>> newConsumer) {
        if (newConsumer == null) {
            return;
        }

        if (listConsumer == null) {
            listConsumer = new LinkedList<>();
        }

        listConsumer.add(newConsumer);
    }


    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
//        httpRequestLoggerConsumer.accept("About to invoke: " + utilsharedclient.Constants.USERS_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(ConstantsAll.EXECUTION_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[ExecutionListRefresher] onResponse - start");
                String jsonArrayOfExecutions = response.body().string();
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);

                ExecutionDTO[] executionDTOS = GSON_INSTANCE.fromJson(jsonArrayOfExecutions, ExecutionDTO[].class);

                List<ExecutionDTOTable> tableDTOList = ExecutionTableControllerShared.toTableList(executionDTOS, username);

                listConsumer.forEach(listConsumer1 -> {listConsumer1.accept(tableDTOList);});
//                listConsumer.accept(tableDTOList);
            }
        });
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
