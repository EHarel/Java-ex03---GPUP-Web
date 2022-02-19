package components.graph.alldata;

import components.app.AppMainController;
import components.graph.alldata.menu.GraphChooserMenuController;
import components.graph.operations.menu.GraphOperationsMenuController;
import components.graph.operations.overview.GraphOverviewController;
import components.graph.operations.paths.TargetPathsController;
import components.graph.operations.serialset.SerialSetsController;
import components.graph.operations.targetcycles.TargetCyclesController;
import components.graph.operations.whatif.WhatIfController;
import components.graph.targettable.TargetDTOTable;
import datastructure.Queue;
import events.ExecutionEndListener;
import events.FileLoadedListener;
import exception.NonexistentTargetException;
import exception.UninitializedNullException;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import logic.Engine;
import task.Execution;

import java.util.Collection;
import java.util.List;

public class GraphAllDataController implements FileLoadedListener, ExecutionEndListener {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- FXML DATA MEMBERS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML private BorderPane borderPaneMain;

    @FXML private ScrollPane graphOperationsMenuComponent;
    @FXML private GraphOperationsMenuController graphOperationsMenuComponentController;

    @FXML private ScrollPane graphChooserMenuComponent;
    @FXML private GraphChooserMenuController graphChooserMenuComponentController;


    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- CUSTOM DATA MEMBERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private AppMainController mainController;
    private GraphOverviewController graphOverviewController;
    private TargetPathsController targetPathsController;
    private SerialSetsController serialSetsController;
    private TargetCyclesController targetCyclesController;
    private WhatIfController whatIfController;

    private DependenciesGraph workingGraph = null;
    boolean performedFirstLoad = false;


    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    @FXML
    public void initialize() {
        setGraphChooserOriginalGraphButton();
        initializeOperationsMenuEvents();
    }

    private void setGraphChooserOriginalGraphButton() {
        graphChooserMenuComponentController.getButtonOriginalGraph().setOnAction(event -> {
            if (performedFirstLoad) {
                loadMainGraph();
            }
        });
    }

    private void initializeOperationsMenuEvents() {
        graphOperationsMenuComponentController.getButtonGraphOverview().setOnAction(event -> {
            setMainScene(graphOverviewController.getMainScene());
        });

        graphOperationsMenuComponentController.getButtonPathsBetweenTargets().setOnAction(event -> {
            setMainScene(targetPathsController.getMainScene());
        });

        graphOperationsMenuComponentController.getButtonCyclesWithTarget().setOnAction(event -> {
            setMainScene(targetCyclesController.getMainScene());
        });

        graphOperationsMenuComponentController.getButtonWhatIf().setOnAction(event -> {
            setMainScene(whatIfController.getMainScene());
        });

        graphOperationsMenuComponentController.getButtonSerialSets().setOnAction(event -> {
            setMainScene(serialSetsController.getMainScene());
        });
    }




    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void setMainController(AppMainController mainController) {
        this.mainController = mainController;
        mainController.addEventListener_FileLoaded(this);
        mainController.addEventListener_ExecutionEnded(this);
    }

    public void setGraphOverviewController(GraphOverviewController graphOverviewController) {
        this.graphOverviewController = graphOverviewController;
        setMainScene(graphOverviewController.getMainScene());
    }

    public void setSerialSetsController(SerialSetsController serialSetsController) {
        this.serialSetsController = serialSetsController;
    }

    public void setTargetCyclesController(TargetCyclesController controller) {
        this.targetCyclesController = controller;
        setEventCycleAllGraph();
        setEventCycleSpecificTarget();
    }

    public void setTargetPathsController(TargetPathsController controller) {
        this.targetPathsController = controller;
    }

    public void setWhatIfController(WhatIfController controller) {
        this.whatIfController = controller;
    }

    private void setEventCycleAllGraph() {
        targetCyclesController.getButtonCyclesInGraph().setOnAction(event ->  {
            if (workingGraph != null) {
                Queue<TargetDTO> topologicalSort = workingGraph.getTopologicalSort(true);

                if (topologicalSort == null) {
                    targetCyclesController.showGraphHasCycle();
                } else {
                    targetCyclesController.showGraphDoesNotHaveCycle();
                }
            } else {
                targetCyclesController.showNoGraphChosenForGeneralCycleCheck();
            }
        });
    }

    private void setEventCycleSpecificTarget() {
        targetCyclesController.getButtonCyclesWithTarget().setOnAction(event ->  {
            if (workingGraph != null) {
                TargetDTOTable chosenTargetDTO = targetCyclesController.getChosenTarget();
                if (chosenTargetDTO != null) {
                    Collection<List<TargetDTO>> cycles = null;
                    try {
                        cycles = workingGraph.getCycleWithTarget(chosenTargetDTO.getName());
                        targetCyclesController.showCycles(cycles);
                    } catch (NonexistentTargetException e) {
                        e.printStackTrace();
                    } catch (UninitializedNullException e) {
                        e.printStackTrace();
                    } catch (Exception ignore) {}
                }
            }
        });
    }

    private void setMainScene(Parent mainScene) {
        borderPaneMain.setCenter(mainScene);
    }

    @Override
    public void fileLoaded() {
        loadMainGraph();

        if (!performedFirstLoad) {
            performedFirstLoad = true;
        }
    }

    private void loadMainGraph() {
        workingGraph = (DependenciesGraph) Engine.getInstance().getGraph();

        populateData(workingGraph);
    }

    public void populateData(DependenciesGraph graph) {
        graphOverviewController.populateData(graph);
        serialSetsController.populateData(graph);
        targetCyclesController.populateData(graph);
        targetPathsController.populateData(graph);
        whatIfController.populateData(graph);
    }

    @Override
    public void executedEnded(Execution execution) {
        Button newButtonStartGraph = graphChooserMenuComponentController.addNewExecutionStartGraph(execution);
        if (newButtonStartGraph != null) {
            newButtonStartGraph.setOnAction(event -> {
                Execution exeData = Engine.getInstance().getExecutionByExeNumber(execution.getTaskType(), execution.getExecutionNumber());
                if (exeData != null) {
                    workingGraph = exeData.getStartingGraph();
                    populateData(workingGraph);
                }
            });
        }

        Button newButtonEndGraph = graphChooserMenuComponentController.addNewExecutionEndGraph(execution);
        if (newButtonEndGraph != null) {
            newButtonEndGraph.setOnAction(event -> {
                Execution exeData = Engine.getInstance().getExecutionByExeNumber(execution.getTaskType(), execution.getExecutionNumber());
                if (exeData != null) {
                    workingGraph = exeData.getEndGraph();
                    populateData(workingGraph);
                }
            });
        }
    }
}
