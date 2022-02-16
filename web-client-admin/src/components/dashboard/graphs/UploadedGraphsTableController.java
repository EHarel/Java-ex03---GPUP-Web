package components.dashboard.graphs;

import graph.GraphGeneralData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import users.UserDTO;
import utilsharedclient.Constants;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UploadedGraphsTableController {

    @FXML
    private ScrollPane MainPane;

    @FXML
    private TableView<GraphGeneralData> tableView_GraphDetails;

    @FXML
    private TableColumn<GraphGeneralData, String> tableColumn_GraphName;
    @FXML
    private TableColumn<GraphGeneralData, String> tableColumn_UploadingUser;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_CountTotal;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_CountLeaves;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_CountMiddles;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_CountRoots;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_CountIndependents;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_PricingSimulation;
    @FXML
    private TableColumn<GraphGeneralData, Integer> tableColumn_PricingCompilation;



    private GraphGeneralData currentlySelectedGraph;

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    public BooleanProperty getAutoUpdateProperty() {
        return autoUpdate;
    }

    public UploadedGraphsTableController() {
        autoUpdate = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        tableView_GraphDetails.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tableColumn_GraphName.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, String>("graphName"));
        tableColumn_UploadingUser.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, String>("uploadingUserName"));
        tableColumn_CountTotal.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("countAllTargets"));
        tableColumn_CountLeaves.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("countLeaves"));
        tableColumn_CountMiddles.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("countMiddles"));
        tableColumn_CountRoots.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("countRoots"));
        tableColumn_CountIndependents.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("countIndependents"));
        tableColumn_PricingSimulation.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("priceSimulation"));
        tableColumn_PricingCompilation.setCellValueFactory(new PropertyValueFactory<GraphGeneralData, Integer>("priceCompilation"));

        setActive();
    }

    public TableView<GraphGeneralData> getTableView_GraphDetails() {
        return tableView_GraphDetails;
    }

    public void setActive() {
        getAutoUpdateProperty().set(true);
        startRefresher();
    }

    public void startRefresher() {
        listRefresher = new GraphListRefresher(
                autoUpdate,
                null, // Aviad code sent something else here
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE, Constants.REFRESH_RATE);
    }

    private void updateUsersList(List<GraphGeneralData> usersNames) {
        Platform.runLater(() -> {
            ObservableList<GraphGeneralData> items = tableView_GraphDetails.getItems();
            items.clear();
            items.addAll(usersNames);
        });
    }

    public void clear() {
        tableView_GraphDetails.getItems().clear();
    }

    /**
     * This method completely replaces the current data in the table by those given in the parameter.
     *
     * @param graphs new data to replace the old.
     */
    public void fillUsers(Collection<GraphGeneralData> graphs) {
        clear();

        graphs.forEach(graphDTO -> {
            tableView_GraphDetails.getItems().add(graphDTO);
        });
    }
}


