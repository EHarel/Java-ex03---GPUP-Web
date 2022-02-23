package componentcode.usertable;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import users.UserDTO;
import utilsharedclient.Constants;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UserTableControllerShared {

    private final TableView<UserDTO> usersTable;

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalUsers;

    public UserTableControllerShared(TableView<UserDTO> usersTable) {
        autoUpdate = new SimpleBooleanProperty();
        totalUsers = new SimpleIntegerProperty();
        this.usersTable = usersTable;

        startListRefresher();
    }

    public BooleanProperty getAutoUpdateProperty() {
        return autoUpdate;
    }

    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate,
                null, // Aviad code sent something else here
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE_USERS, Constants.REFRESH_RATE_USERS);
    }

    private void updateUsersList(List<UserDTO> usersNames) {
        Platform.runLater(() -> {
            ObservableList<UserDTO> items = usersTable.getItems();
            items.clear();
            items.addAll(usersNames);
            totalUsers.set(usersNames.size());
        });
    }
}
