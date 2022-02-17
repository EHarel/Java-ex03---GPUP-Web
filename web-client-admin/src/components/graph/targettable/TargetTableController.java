package components.graph.targettable;

import algorithm.DFS;
import components.app.AppMainController;
import components.app.AppUtils;
import components.graph.alldata.GraphAllDataController;
import graph.DependenciesGraph;
import graph.Graph;
import graph.TargetDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TargetTableController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML
    private ScrollPane mainScene;

    @FXML
    private TableView<TargetDTOTable> tableViewTargets;
    @FXML
    private TableColumn<TargetDTOTable, String> tableColumnSelected;
    @FXML
    private TableColumn<TargetDTOTable, String> tableColumnTargetName;
    @FXML
    private TableColumn<TargetDTOTable, TargetDTOTable.Dependency> tableColumnLevel;
    @FXML
    private TableColumn<TargetDTOTable, String> tableColumnRequiredForHead;
    @FXML
    private TableColumn<TargetDTOTable, Integer> tableColumnRequiredForDirect;
    @FXML
    private TableColumn<TargetDTOTable, Integer> tableColumnRequiredForTotal;
    @FXML
    private TableColumn<TargetDTOTable, String> tableColumnDependsOnHead;
    @FXML
    private TableColumn<TargetDTOTable, Integer> tableColumnDependsOnDirect;
    @FXML
    private TableColumn<TargetDTOTable, Integer> tableColumnDependsOnTotal;
    @FXML
    private TableColumn<TargetDTOTable, Integer> tableColumnSerialSetsCount;
    @FXML
    private TableColumn<TargetDTOTable, String> tableColumnUserData;
    @FXML
    private TableColumn<TargetDTOTable, TargetDTO.TaskStatusDTO.TaskResult> tableColumnTaskResult;
    @FXML
    private TableColumn<TargetDTOTable, TargetDTO.TargetState> tableColumnTargetState;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private DependenciesGraph workingGraph;
    private AppMainController mainController;
    private GraphAllDataController graphController;
    private Parent scene;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        tableColumnSelected.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, String>("checkbox"));
        tableColumnTargetName.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, String>("name"));
        tableColumnLevel.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, TargetDTO.Dependency>("dependency"));
        tableColumnRequiredForDirect.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("noTargetsThisIsDirectlyRequiredFor"));
        tableColumnRequiredForTotal.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("numberTargetsThisTotalRequiredFor"));
        tableColumnDependsOnDirect.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("noTargetsThisDirectlyDependsOn"));
        tableColumnDependsOnTotal.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("numberTargetsThisTotalDependsOn"));
        tableColumnSerialSetsCount.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, Integer>("serialSetCount"));
        tableColumnTargetState.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, TargetDTO.TargetState>("targetState"));
        tableColumnTaskResult.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, TargetDTO.TaskStatusDTO.TaskResult>("taskResult"));
        tableColumnUserData.setCellValueFactory(new PropertyValueFactory<TargetDTOTable, String>("userData"));

        HideSelectColumn();
        setColors();
    }

    private void setColors() {
        tableViewTargets.setRowFactory(tv -> new TableRow<TargetDTOTable>() {
            @Override
            protected void updateItem(TargetDTOTable item, boolean empty) {
                super.updateItem(item, empty);
//                if (item == null || item.getValue() == null)
                if (item == null)
                    setStyle("");
                else {
                    switch (item.getTaskResult()) {
                        case UNPROCESSED:
                            switch (item.getTargetState()) {
                                case FROZEN:
                                    setStyle("");
                                    break;
                                case WAITING:
                                    setStyle("-fx-background-color:#ffffff;");
                                    break;
                                case SKIPPED:
                                    setStyle("-fx-background-color:#5b5b5b;");
                                    break;
                                case IN_PROCESS:
                                    setStyle("-fx-background-color:#6e6ef1;");
                                    break;
                            }
                            break;
                        case SUCCESS:
                            setStyle("-fx-background-color:lightgreen;");
                            break;
                        case SUCCESS_WITH_WARNINGS:
                            setStyle("-fx-background-color:orange;");
                            break;
                        case FAILURE:
                            setStyle("-fx-background-color:#ef5252;");
                            break;
                    }
                }
            }
        });
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
    }

    public TableView<TargetDTOTable> getTableView() {
        return this.tableViewTargets;
    }

    public Parent getScene() {
        return scene;
    }

    public void setScene(Parent parent) {
        this.scene = parent;
    }

    public Collection<TargetDTO> getAllTargets() {
        ObservableList<TargetDTOTable> allItems = tableViewTargets.getItems();
        Collection<TargetDTO> allTargets = new LinkedList<>();

        if (allItems != null) {
            allItems.forEach(targetDTOTable -> {
                allTargets.add(targetDTOTable);
            });
        }

        return allTargets;
    }

    public Collection<TargetDTO> getSelectedTargets() {
        ObservableList<TargetDTOTable> allItems = tableViewTargets.getItems();
        Collection<TargetDTO> selectedTargets = new LinkedList<>();

        if (allItems != null) {
            allItems.forEach(targetDTOTable -> {
                if (targetDTOTable.getCheckbox().isSelected()) {
                    selectedTargets.add(targetDTOTable);
                }
            });
        }

        return selectedTargets;
    }

    public TargetDTOTable getSelectedTarget() {
        return tableViewTargets.getSelectionModel().getSelectedItem();
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------- POPULATE DATA METHODS --------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * Clears the table and adds all targets from given graph to the table.
     */
    public void PopulateData_AllTargets(Graph graph) {
        workingGraph = (DependenciesGraph) graph;
        Collection<TargetDTO> targetDTOCollection = graph.getAllTargetData();

        populateData(workingGraph, targetDTOCollection);
    }

    /**
     * This method creates a new graph based on the selected targets, and displays the new target data accordingly.
     */
    public void populateData_SpecificTargets_SubGraph(DependenciesGraph workingGraph, Collection<TargetDTO> selectedTargets) {
        if (workingGraph != null && selectedTargets != null) {
            Collection<String> targetNames = AppUtils.getNamesFromCollection(selectedTargets);
            DependenciesGraph duplicateGraph = workingGraph.DuplicateChosenOnly(targetNames);
            PopulateData_AllTargets(duplicateGraph);
        }
    }

    /**
     * This method adds only the given targets in the collection, based on the working graph (it doesn't create a duplicate sub-graph).
     */
    public void populateData_SpecificTargets_OriginalGraph(DependenciesGraph workingGraph, Collection<TargetDTO> dependentOnTargets) {
        populateData(workingGraph, dependentOnTargets);
    }

    /**
     * A private method meant to add all given targets to the graph.
     */
    private void populateData(DependenciesGraph workingGraph, Collection<TargetDTO> allTargetsToAdd) {
        if (allTargetsToAdd != null && workingGraph != null) {
            tableViewTargets.getItems().clear();

            allTargetsToAdd.forEach(targetDTO -> {
                try {
                    TargetDTOTable newTargetDTO = createNewTargetDTOTable(targetDTO);
                    AppendTarget(newTargetDTO, workingGraph);
                } catch (Exception ignore) {
                }
            });
        }
    }

    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------- MISC. TABLE OPERATIONS -------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void HideSelectColumn() {
        SetSelectColumnVisibility(false);
    }

    public void ShowSelectColumn() {
        SetSelectColumnVisibility(true);
    }

    public void SetSelectColumnVisibility(boolean isVisible) {
        tableColumnSelected.visibleProperty().set(isVisible);
    }

    public void selectAll() {
        updateSelectedStateAll(true);
    }

    public void unselectAll() {
        updateSelectedStateAll(false);
    }

    private void updateSelectedStateAll(boolean isSelected) {
        ObservableList<TargetDTOTable> allItems = tableViewTargets.getItems();
        if (allItems == null) {
            return;
        }

        allItems.forEach(targetDTOTable -> {
            targetDTOTable.getCheckbox().setSelected(isSelected);
        });
    }

    public void removeAll() {
        tableViewTargets.getItems().clear();
    }

    /**
     * Removes a target by the given name if appears in table.
     */
    public void removeTarget(String name) {
        TargetDTOTable targetToRemove = null;

        for (TargetDTOTable targetDTOTable : tableViewTargets.getItems()) {
            if (targetDTOTable.getName().equals(name)) {
                targetToRemove = targetDTOTable;
                break;
            }
        }

        if (targetToRemove != null) {
            tableViewTargets.getItems().remove(targetToRemove);
        }
    }

    /**
     * Appends a target to the table. Does not clear the table of its existing data.
     */
    public void AppendTarget(TargetDTO targetDTO, DependenciesGraph workingGraph) {
        this.workingGraph = workingGraph;
        TargetDTOTable newTargetDTOTable = createNewTargetDTOTable(targetDTO);
        tableViewTargets.getItems().add(newTargetDTOTable);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private TargetDTOTable createNewTargetDTOTable(TargetDTO targetDTO) {
        // int totalDependsOn = workingGraph.getTotalNumberDependencies(targetDTO.getName(), DFS.EdgeDirection.DEPENDENT_ON); // TODO: finish this method in graph for better design

        Integer totalRequiredFor = getTotalDependenciesPlaceholder(targetDTO.getName(), DFS.EdgeDirection.REQUIRED_FOR);
        Integer totalDependsOn = getTotalDependenciesPlaceholder(targetDTO.getName(), DFS.EdgeDirection.DEPENDENT_ON);

        TargetDTOTable newTargetDTOTable = new TargetDTOTable(targetDTO, totalRequiredFor, totalDependsOn);

        return newTargetDTOTable;
    }

    /**
     * TODO: when I have time, remove this method and then implement the proper one in the graph, with Nullable<Integer>.
     */
    private Integer getTotalDependenciesPlaceholder(String name, DFS.EdgeDirection dependencyDirection) {
        Integer num = null;

        if (workingGraph != null && name != null && dependencyDirection != null) {
            try {
                Collection<List<TargetDTO>> allPathsInDirection = workingGraph.getTargetPaths(name, dependencyDirection);
                if (allPathsInDirection != null) {
                    num = AppUtils.getNamesFromPaths_ExcludeChosen(allPathsInDirection, name).size();
                }
            } catch (Exception ignore) {
            }
        }

        return num;
    }

    public void clear() {
        removeAll();
        workingGraph = null;
    }

    public void updateRowValue(TargetDTO targetDTO) {
        if (targetDTO == null)
            return;

        for (int idx = 0; idx < tableViewTargets.getItems().size(); idx++) {
            TargetDTOTable data = tableViewTargets.getItems().get(idx);

            if (data.getName().equalsIgnoreCase(targetDTO.getName())) {
                Integer totalRequiredFor = data.getNumberTargetsThisTotalRequiredFor();
                Integer totalDependentOn = data.getNumberTargetsThisTotalDependsOn();
                tableViewTargets.getItems().set(idx, new TargetDTOTable(targetDTO, totalRequiredFor, totalDependentOn));

                return;
            }
        }
    }
}