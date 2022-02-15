package components.graph.operations.overview;

import components.graph.general.GraphGeneralDetailsController;
import components.graph.specifictarget.SpecificTargetController;
import components.graph.specifictarget.SpecificTargetDependenciesController;
import components.graph.targettable.TargetTableController;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;

public class GraphOverviewController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- FXML DATA MEMBERS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML private ScrollPane mainScene;

    @FXML private ScrollPane graphGeneralDetailsComponent;
    @FXML private GraphGeneralDetailsController graphGeneralDetailsComponentController;

    @FXML private ScrollPane targetTableComponent;
    @FXML private TargetTableController targetTableComponentController;

    @FXML private ScrollPane specificTargetComponent;
    @FXML private SpecificTargetController specificTargetComponentController;

    @FXML private ScrollPane specificTargetDependenciesComponent;
    @FXML private SpecificTargetDependenciesController specificTargetDependenciesComponentController;
    private DependenciesGraph workingGraph;


    @FXML
    public void initialize() {
        // Old - pre-split to overview
        if (targetTableComponentController != null) {
            targetTableComponentController.getTableView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                TargetDTO selectedItem = targetTableComponentController.getTableView().getSelectionModel().getSelectedItem();

                if (selectedItem != null) {
                    if (specificTargetComponentController != null) {
                        specificTargetComponentController.populateData(selectedItem, null);
                    }

                    if (specificTargetDependenciesComponentController != null) {
                        specificTargetDependenciesComponentController.PopulateData(selectedItem, workingGraph);
                    }
                }
            });
        }
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public ScrollPane getMainScene() {
        return mainScene;
    }




    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void populateData(DependenciesGraph graph) {
        this.workingGraph = graph;
        graphGeneralDetailsComponentController.populateData(graph);
        targetTableComponentController.PopulateData_AllTargets(graph);
    }
}


