package componentcode.usertable;

import httpclient.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UserDTO;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;


import static utilsharedall.ConstantsAll.GSON_INSTANCE;

public class UserListRefresher extends TimerTask {

//    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<UserDTO>> usersListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;


    public UserListRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<List<UserDTO>> usersListConsumer) {
        this.shouldUpdate = shouldUpdate;
//        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.usersListConsumer = usersListConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
//        httpRequestLoggerConsumer.accept("About to invoke: " + utilsharedclient.Constants.USERS_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(ConstantsAll.USERS_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("[UserListRefresher] onResponse - start");
                String jsonArrayOfUsersNames = response.body().string();
//                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);

                UserDTO[] usersDTOs = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, UserDTO[].class);

                // TODO: need to convert to list of UserDTO
                usersListConsumer.accept(Arrays.asList(usersDTOs));
            }
        });
    }
}

