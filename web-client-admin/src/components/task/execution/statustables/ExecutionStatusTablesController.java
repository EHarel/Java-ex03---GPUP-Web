package components.task.execution.statustables;

import componentcode.executiontable.ExecutionDTOTable;
import components.graph.targettable.TargetTableController;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ExecutionStatusTablesController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML private TabPane tabPane;
    @FXML private Tab tabUnprocessed;


    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    @FXML private Parent tableAllTargetsComponent;
    @FXML private TargetTableController tableAllTargetsComponentController;

    @FXML private ScrollPane tableUnprocessedComponent;
    @FXML private TargetTableController tableUnprocessedComponentController;

    @FXML private ScrollPane tableProcessedComponent;
    @FXML private TargetTableController tableProcessedComponentController;

    @FXML private ScrollPane tableFailedComponent;
    @FXML private TargetTableController tableFailedComponentController;

//    @FXML private ScrollPane tableSkippedComponent;
//    @FXML private TargetTableController tableSkippedComponentController;

    @FXML private ScrollPane tableSucceededComponent;
    @FXML private TargetTableController tableSucceededComponentController;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private DependenciesGraph workingGraph;
    private ProgressBar progressBar;
    private int workingGraphStartingTargetCount;
    private int workingGraphFinishedTargets;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    public void initialize() {
        tabPane.getTabs().remove(tabUnprocessed);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public TargetTableController getTableAllTargetsComponentController() {
        return tableAllTargetsComponentController;
    }

    public TargetTableController getTableUnprocessedComponentController() {
        return tableUnprocessedComponentController;
    }

    public TargetTableController getTableProcessedComponentController() {
        return tableProcessedComponentController;
    }

    public TargetTableController getTableFailedComponentController() {
        return tableFailedComponentController;
    }

//    public TargetTableController getTableSkippedComponentController() {
//        return tableSkippedComponentController;
//    }

    public TargetTableController getTableSucceededComponentController() {
        return tableSucceededComponentController;
    }




    public void setWorkingGraph(DependenciesGraph workingGraph) {
        clear();
        this.workingGraph = workingGraph;
        if (workingGraph != null) {
            populateData();
            this.workingGraphStartingTargetCount = workingGraph.targets().size();
            workingGraphFinishedTargets = 0;
        }
    }

    private void populateData() {
        if (workingGraph != null) {
            workingGraph.targets().forEach(target -> {
                tableAllTargetsComponentController.AppendTarget(target.toDTO(), workingGraph);
                switch (target.getTaskStatus().getTaskResult()) {
                    case UNPROCESSED: {
                        if (target.getTaskStatus().getTargetState() == TargetDTO.TargetState.SKIPPED) {
//                            tableSkippedComponentController.AppendTarget(target.toData(), workingGraph);
                        } else {
                            tableUnprocessedComponentController.AppendTarget(target.toDTO(), workingGraph);
                        }
                        break;
                    }
                    case FAILURE:
                        tableFailedComponentController.AppendTarget(target.toDTO(), workingGraph);
                        break;
                    case SUCCESS:
                    case SUCCESS_WITH_WARNINGS:
                        tableSucceededComponentController.AppendTarget(target.toDTO(), workingGraph);
                        break;
                }
            });
        }
    }

    public void updateUnprocessed(DependenciesGraph graph) {
        workingGraph = graph;
        tableUnprocessedComponentController.PopulateData_AllTargets(graph);
        tableAllTargetsComponentController.PopulateData_AllTargets(graph);
    }

    public void finishedProcessingTarget(TargetDTO targetDTO) {
        if (targetDTO == null) {
            return;
        }

        updateInAllTable(targetDTO);
        workingGraphFinishedTargets++;

        double progress = (double) workingGraphFinishedTargets / (double) workingGraphStartingTargetCount;
        progressBar.setProgress(progress);

        if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.FINISHED) {
            tableUnprocessedComponentController.removeTarget(targetDTO.getName());

            tableProcessedComponentController.AppendTarget(targetDTO, workingGraph);

            if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.SKIPPED) {
//                tableSkippedComponentController.AppendTarget(targetDTO, workingGraph);
            }

            switch (targetDTO.getTaskStatusDTO().getTaskResult()) {
                case SUCCESS_WITH_WARNINGS:
                case SUCCESS: {
                    tableSucceededComponentController.AppendTarget(targetDTO, workingGraph);
                    break;
                }
                case FAILURE: {
                    tableFailedComponentController.AppendTarget(targetDTO, workingGraph);
                }
            }
        }
    }

    private void updateInAllTable(TargetDTO targetDTO) {
        tableAllTargetsComponentController.updateRowValue(targetDTO);
    }

    public void newFileLoaded() {
        reset();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void clear() {
        reset();
    }

    private void reset() {
        tableUnprocessedComponentController.removeAll();
        tableProcessedComponentController.removeAll();
        tableFailedComponentController.removeAll();
//        tableSkippedComponentController.RemoveAll();
        tableSucceededComponentController.removeAll();
        tableAllTargetsComponentController.removeAll();

        workingGraph = null;
        workingGraphFinishedTargets = 0;
        workingGraphStartingTargetCount = 0;
    }




    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void updateTargetStateChanged(TargetDTO targetDTO) {
        updateInAllTable(targetDTO);
    }

    public void executionChosen(ExecutionDTOTable executionDTOTable) {
        if (executionDTOTable != null) {
            if (executionDTOTable.getEndGraphDTO() != null) {
                setWorkingGraph(new DependenciesGraph(executionDTOTable.getEndGraphDTO()));
            } else if (executionDTOTable.getStartGraphDTO() != null) {
                setWorkingGraph(new DependenciesGraph(executionDTOTable.getStartGraphDTO()));
            }
        }
    }
}
