package components.graph.specifictarget;

import components.graph.targettable.TargetTableController;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import java.util.Collection;

public class SpecificTargetDependenciesController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML private ScrollPane dependentOnTable;
    @FXML private TargetTableController dependentOnTableController;

    @FXML private ScrollPane requiredForTableComponent;
    @FXML private TargetTableController requiredForTableComponentController;

    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private DependenciesGraph workingGraph;



    public void PopulateData(TargetDTO targetDTO, DependenciesGraph workingGraph) {
        if (targetDTO != null && workingGraph != null) {
            this.workingGraph = workingGraph;
            Collection<TargetDTO> dependentOnTargets = workingGraph.getAllDependentOnTargetsFromTarget(targetDTO.getName());
            Collection<TargetDTO> requiredForTargets = workingGraph.getAllRequiredForTargetsFromTarget(targetDTO.getName());

            dependentOnTableController.populateData_SpecificTargets_OriginalGraph(workingGraph, dependentOnTargets);
            requiredForTableComponentController.populateData_SpecificTargets_OriginalGraph(workingGraph, requiredForTargets);
        }
    }
}
