package components.dashboard.graphs;

import components.dashboard.DashboardController;
import graph.GraphDTO;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import utilsharedclient.Constants;

import java.util.Collection;
import java.util.List;
import java.util.Timer;

public class UploadedGraphsTableController {

    @FXML
    private ScrollPane MainPane;

    @FXML
    private TableView<GraphDTO> tableView_GraphDetails;

    @FXML
    private TableColumn<GraphDTO, String> tableColumn_GraphName;
    @FXML
    private TableColumn<GraphDTO, String> tableColumn_UploadingUser;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_CountTotal;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_CountLeaves;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_CountMiddles;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_CountRoots;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_CountIndependents;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_PricingSimulation;
    @FXML
    private TableColumn<GraphDTO, Integer> tableColumn_PricingCompilation;


    private GraphDTO currentlySelectedGraph;


    private Timer timer;
    private GraphListRefresher listRefresher;
    private final BooleanProperty autoUpdate;
    private DashboardController dashboardController;

    public BooleanProperty getAutoUpdateProperty() {
        return autoUpdate;
    }

    public UploadedGraphsTableController() {
        autoUpdate = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        tableView_GraphDetails.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        tableColumn_GraphName.setCellValueFactory(new PropertyValueFactory<GraphDTO, String>("graphName"));
        tableColumn_UploadingUser.setCellValueFactory(new PropertyValueFactory<GraphDTO, String>("uploadingUserName"));
        tableColumn_CountTotal.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("countAllTargets"));
        tableColumn_CountLeaves.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("countLeaves"));
        tableColumn_CountMiddles.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("countMiddles"));
        tableColumn_CountRoots.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("countRoots"));
        tableColumn_CountIndependents.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("countIndependents"));
        tableColumn_PricingSimulation.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("priceSimulation"));
        tableColumn_PricingCompilation.setCellValueFactory(new PropertyValueFactory<GraphDTO, Integer>("priceCompilation"));

        setDoubleClickEvent();

        setActive(false);
        startRefresher();
    }

    private void setDoubleClickEvent() {
        tableView_GraphDetails.setRowFactory( tv -> {
            TableRow<GraphDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    GraphDTO rowData = row.getItem();
                    System.out.println("[Graph TableView] chosen row data: " + rowData);

                    currentlySelectedGraph = row.getItem();
                    dashboardController.event_GraphRowChanged();
                }
            });

            return row ;
        });
    }

    public TableView<GraphDTO> getTableView_GraphDetails() {
        return tableView_GraphDetails;
    }

    public void setActive(boolean isActive) {
        getAutoUpdateProperty().set(isActive);
    }

    public void startRefresher() {
        listRefresher = new GraphListRefresher(
                autoUpdate,
                null, // Aviad code sent something else here
                this::updateUsersList,
                dashboardController);
        timer = new Timer();
        timer.schedule(listRefresher, Constants.REFRESH_RATE_GRAPHS, Constants.REFRESH_RATE_GRAPHS);
    }

    private void updateUsersList(List<GraphDTO> usersNames) {
        Platform.runLater(() -> {
            ObservableList<GraphDTO> items = tableView_GraphDetails.getItems();
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
    public void fillUsers(Collection<GraphDTO> graphs) {
        clear();

        graphs.forEach(graphDTO -> {
            tableView_GraphDetails.getItems().add(graphDTO);
        });
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        this.listRefresher.setDashboardController(dashboardController);
    }

    public GraphDTO getCurrentlySelectedGraph() {
        return currentlySelectedGraph;
    }
}


