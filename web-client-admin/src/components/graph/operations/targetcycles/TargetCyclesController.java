package components.graph.operations.targetcycles;

import components.app.AppMainController;
import components.graph.targettable.TargetDTOTable;
import components.graph.targettable.TargetTableController;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TargetCyclesController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- FXML DATA MEMBERS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    private Parent mainScene;

    @FXML
    private Button buttonCyclesWithTarget;
    @FXML
    private TextArea textAreaCyclesWithTarget;
    @FXML
    private Button buttonCyclesInGraph;
    @FXML
    private Label labelCyclesInGraphMsg;
    @FXML
    private ToggleGroup CycleCountToggle;
    @FXML
    private RadioButton radioButtonShowOneCycle;
    @FXML
    private RadioButton radioButtonShowAllCycles;

    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    @FXML
    private Parent targetTableComponent;
    @FXML
    private TargetTableController targetTableComponentController;

    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private Collection<List<TargetDTO>> currCycles;
    private RadioButton currSelectedCycleCountRB;


    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    @FXML
    public void initialize() {
        currSelectedCycleCountRB = (RadioButton) CycleCountToggle.getSelectedToggle();
        if (currSelectedCycleCountRB == null) {
            radioButtonShowAllCycles.setSelected(true);
            currSelectedCycleCountRB = radioButtonShowAllCycles;
        }

        radioButtonShowOneCycle.setOnAction(event -> {
            if (currSelectedCycleCountRB != radioButtonShowOneCycle) {
                currSelectedCycleCountRB = radioButtonShowOneCycle;
                showOneCycle();
            }
        });

        radioButtonShowAllCycles.setOnAction(event -> {
            if (currSelectedCycleCountRB != radioButtonShowAllCycles) {
                currSelectedCycleCountRB = radioButtonShowAllCycles;
                showAllCycles();
            }
        });
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Parent getMainScene() {
        return mainScene;
    }

    public Button getButtonCyclesWithTarget() {
        return buttonCyclesWithTarget;
    }

    public Button getButtonCyclesInGraph() {
        return buttonCyclesInGraph;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void showGraphHasCycle() {
        String msg = "Graph has a cycle! Uh-oh!";
        Color color = Color.RED;
        AppMainController.AnimationFadeSingle(null, labelCyclesInGraphMsg, msg, color);
    }

    public void showGraphDoesNotHaveCycle() {
        String msg = "Graph doesn't have a cycle! Rejoice!";
        Color color = Color.GREEN;
        AppMainController.AnimationFadeSingle(null, labelCyclesInGraphMsg, msg, color);
    }

    public void showNoGraphChosenForGeneralCycleCheck() {
        String msg = "No graph loaded or chosen to check to check";
        Color color = null;
        AppMainController.AnimationFadeSingle(null, labelCyclesInGraphMsg, msg, color);
    }

    public void populateData(DependenciesGraph graph) {
        if (graph != null) {
            targetTableComponentController.PopulateData_AllTargets(graph);
        }
    }

    public TargetDTOTable getChosenTarget() {
        return targetTableComponentController.getSelectedTarget();
    }

    public void showCycles(Collection<List<TargetDTO>> cycles) {
        if (cycles != null) {
            currCycles = cycles;
            textAreaCyclesWithTarget.clear();
            if (radioButtonShowOneCycle.isSelected()) {
                showOneCycle();
            } else {
                showAllCycles();
            }
        }
    }

    private void showOneCycle() {
        if (currCycles != null && !currCycles.isEmpty()) {
            textAreaCyclesWithTarget.clear();
            Collection<String> nameList = getNamesFromList(currCycles.iterator().next());
            textAreaCyclesWithTarget.appendText(nameList + "\n\n");
        } else {
            textAreaCyclesWithTarget.setText("No cycles to print");
        }
    }

    private void showAllCycles() {
        if (currCycles != null && !currCycles.isEmpty()) {
            textAreaCyclesWithTarget.clear();
            int num = 1;
            for (List<TargetDTO> targetList : currCycles) {
                Collection<String> nameList = getNamesFromList(targetList);
                textAreaCyclesWithTarget.appendText(num + ") " + nameList + "\n\n");
                num++;
            }
        } else {
            textAreaCyclesWithTarget.setText("No cycles to print");
        }
    }

    private Collection<String> getNamesFromList(List<TargetDTO> targetList) {
        Collection<String> namesList = new LinkedList<>();

        if (targetList != null) {
            targetList.forEach(targetDTO -> {
                namesList.add(targetDTO.getName());
            });
        }

        return namesList;
    }
}
