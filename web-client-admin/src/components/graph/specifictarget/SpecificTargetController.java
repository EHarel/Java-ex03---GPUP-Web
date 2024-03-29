package components.graph.specifictarget;

import algorithm.DFS;
import componentcode.executiontable.ExecutionDTOTable;
import components.app.AppUtils;
import components.graph.alldata.GraphAllDataController;
import graph.DependenciesGraph;
import graph.GraphDTO;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import task.enums.TaskResult;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;

public class SpecificTargetController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */

    @FXML private Label targetNameLabel;
    @FXML private Label labelLevel;
    @FXML private Label labelSerialSets;
    @FXML private Label labelStatus;
    @FXML private Label labelStatusData;

    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    private GraphAllDataController graphController;

    private String lastExecutionChosen;
    private String lastTargetChosen;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */


    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */




    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */


    public void setGraphController(GraphAllDataController graphAllDataController) {
        this.graphController = graphAllDataController;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */






    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    public void populateData(TargetDTO targetDTO, DependenciesGraph taskProcessWorkingGraph) {
        if (targetDTO != null) {
            targetNameLabel.setText(targetDTO.getName());
            labelLevel.setText(targetDTO.getDependency().toString());
            labelSerialSets.setText(AppUtils.getNamesFromSerialSets(targetDTO.getSerialSetsDTOS()).toString());

            if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.WAITING) {
                labelStatus.setText("Waiting");
                long milliseconds = AppUtils.getMillisecondsBetweenTwoInstants(targetDTO.getTimeOfEntryIntoTaskQueue(), Instant.now());
                labelStatusData.setText("Time waiting in queue (milliseconds): " + milliseconds);
            } else if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.IN_PROCESS) {
                labelStatus.setText("In process");
                long milliseconds = AppUtils.getMillisecondsBetweenTwoInstants(targetDTO.getTimeOfExitOutOfTaskQueue(), Instant.now());
                labelStatusData.setText("Time being processed (milliseconds): " + milliseconds);
            } else if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.FROZEN) {
                labelStatus.setText("Frozen");
                if (taskProcessWorkingGraph == null) {
                    labelStatusData.setText("");
                } else {
                    try {
                        // get all neighbors from target
                        Collection<TargetDTO> allDependencies = taskProcessWorkingGraph.getAllDependencies_Transitive(targetDTO.getName(), DFS.EdgeDirection.DEPENDENT_ON);
                        Collection<String> unfinishedDependenciesNames = new LinkedList<>();

                        // Check which of them aren't yet processed
                        allDependencies.forEach(targetDTO1 -> {
                            if (targetDTO1.getTaskStatusDTO().getState() != TargetDTO.TargetState.FINISHED) {
                                unfinishedDependenciesNames.add(targetDTO1.getName());
                            }
                        });

                        labelStatusData.setText("Waiting on following targets to finish: " + unfinishedDependenciesNames.toString());
                    } catch (Exception ignore) {}
                }
            } else if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.SKIPPED) {
                labelStatus.setText("Skipped");
                if (taskProcessWorkingGraph == null) {
                    labelStatusData.setText("");
                } else {
                    try {
                        Collection<String> failedDependencies = new LinkedList<>();

                        // get all dependencies from target
                        Collection<TargetDTO> allDependencies = taskProcessWorkingGraph.getAllDependencies_Transitive(targetDTO.getName(), DFS.EdgeDirection.DEPENDENT_ON);

                        // Check which of them have failed
                        allDependencies.forEach(targetDTO1 -> {
                            if (targetDTO1.getTaskStatusDTO().getTaskResult() == TaskResult.FAILURE) {
                                failedDependencies.add(targetDTO1.getName());
                            }
                        });

                        labelStatusData.setText("Skipped due to following failed dependencies: " + failedDependencies.toString());
                    } catch (Exception ignore) {}
                }
            } else if (targetDTO.getTaskStatusDTO().getState() == TargetDTO.TargetState.FINISHED) {
                labelStatus.setText("Finished");
                labelStatusData.setText("Result: " + targetDTO.getTaskStatusDTO().getTaskResult());
            }
        }
    }

    public void clear() {
        String noneSelectedText = "[None selected]";

        String currText = targetNameLabel.getText();
        if (!currText.equals(noneSelectedText)) {
            lastTargetChosen = currText;
        }

        targetNameLabel.setText(noneSelectedText);
    }

    public void executionChosen(ExecutionDTOTable executionDTOTable) {
        String chosenTargetName = targetNameLabel.getText();
        if (chosenTargetName != null && !chosenTargetName.isEmpty()) {

            // Find the target in the graph
            GraphDTO endGraphDTO = executionDTOTable.getEndGraphDTO();
            if (endGraphDTO != null) {
                for (TargetDTO targetDTO : endGraphDTO.getTargetDTOs()) {
                    if (targetDTO.getName().equals(lastTargetChosen)) {
                        populateData(targetDTO, new DependenciesGraph(endGraphDTO));
                        break;
                    }
                }
            }
        }
    }
}