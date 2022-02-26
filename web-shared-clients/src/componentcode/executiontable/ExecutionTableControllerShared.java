package componentcode.executiontable;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import task.execution.ExecutionDTO;
import utilsharedclient.ConstantsClient;

import java.util.*;

public class ExecutionTableControllerShared {
    private final TableView<ExecutionDTOTable> usersTable;
    private final String username;

    private Timer timer;
    private ExecutionListRefresher listRefresher;
    private final BooleanProperty autoUpdate;


    public ExecutionTableControllerShared(TableView<ExecutionDTOTable> usersTable) {
        this.autoUpdate = new SimpleBooleanProperty();
        this.usersTable = usersTable;
        this.username = null;

//        startListRefresher();
    }

    public BooleanProperty getAutoUpdateProperty() {
        return autoUpdate;
    }

    public void startListRefresher() {
        listRefresher = new ExecutionListRefresher(
                autoUpdate,
                null, // Aviad code sent something else here
                this.username);
        timer = new Timer();
        timer.schedule(listRefresher, ConstantsClient.REFRESH_RATE_EXECUTIONS, ConstantsClient.REFRESH_RATE_EXECUTIONS);

        listRefresher.addConsumer(this::updateExecutionList);
    }

    public void updateExecutionList(List<ExecutionDTOTable> list) {
        Platform.runLater(() -> {
            ObservableList<ExecutionDTOTable> items = usersTable.getItems();
            items.clear();
            items.addAll(list);
        });
    }

    public static List<ExecutionDTOTable> toTableList(ExecutionDTO[] usersDTOs, String username) {
        List<ExecutionDTOTable> executionDTOTableList = new LinkedList<>();
        List<ExecutionDTO> executionDTOList = Arrays.asList(usersDTOs);

        executionDTOList.forEach(executionDTO -> {
            executionDTOTableList.add(toTableItem(executionDTO, username));
        });

        return executionDTOTableList;
    }

    public static ExecutionDTOTable toTableItem(ExecutionDTO executionDTO, String username) {
        ExecutionDTOTable executionDTOTable = new ExecutionDTOTable(executionDTO, username);

        return executionDTOTable;
    }

    public void setUsername(String username) {
        this.listRefresher.setUsername(username);
    }
}
