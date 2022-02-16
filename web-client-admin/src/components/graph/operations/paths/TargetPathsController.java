package components.graph.operations.paths;

import components.app.AppUtils;
import components.graph.targettable.TargetTableController;
import exception.NonexistentTargetException;
import exception.UninitializedNullException;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TargetPathsController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- FXML DATA MEMBERS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML private Parent mainScene;

    @FXML private RadioButton radioButtonPaths1to2;
    @FXML private ToggleGroup toggleGroupTargetDirection;
    @FXML private RadioButton radioButtonPaths2to1;
    @FXML private TextArea textAreaPaths;
    @FXML private Label labelTarget1Name;
    @FXML private Label labelTarget2name;
    @FXML private Button buttonFindPaths;


    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    @FXML private Parent targetTableComponent1;
    @FXML private TargetTableController targetTableComponent1Controller;
    @FXML private Parent targetTableComponent2;
    @FXML private TargetTableController targetTableComponent2Controller;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private DependenciesGraph workingGraph;
    private ArrayList<Collection<List<TargetDTO>>> workingPaths;
    private int INDEX_1TO2 = 0;
    private int INDEX_2TO1 = 1;
    @FXML private String DEFAULT_STR_1TO2 = "Paths from target 1 to target 2 (2 depends on 1)";
    @FXML private String DEFAULT_STR_2TO1 = "Paths from target 2 to target 1 (1 depends on 2)";
    private RadioButton currSelectedRB;


    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    @FXML
    public void initialize() {
        targetTableComponent1Controller.getTableView().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        targetTableComponent2Controller.getTableView().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setTableListener(targetTableComponent1Controller, labelTarget1Name);
        setTableListener(targetTableComponent2Controller, labelTarget2name);
        initializeCurrSelected();
        setRadioListener(radioButtonPaths1to2);
        setRadioListener(radioButtonPaths2to1);
    }

    private void setTableListener(TargetTableController targetTableComponentController, Label labelTargetName) {
        targetTableComponentController.getTableView().getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null) {
                labelTargetName.setText(newValue.getName());
            } else {
                labelTargetName.setText("[unselected]");
            }

            updateRadioButtonsText();
        }));
    }

    private void updateRadioButtonsText() {
        String name1 = labelTarget1Name.getText();
        String name2 = labelTarget2name.getText();

        if (name1 == null) {
            name1 = "[unselected]";
        }

        if (name2 == null) {
            name2 = "[unselected]";
        }

        radioButtonPaths1to2.setText("Paths from target " + name1 + " to target " + name2 + " (" + name1 + " depends on " + name2 + ")");
        radioButtonPaths2to1.setText("Paths from target " + name2 + " to target " + name1 + " (" + name2 + " depends on " + name1 + ")");
    }

    private void initializeCurrSelected() {
        currSelectedRB = (RadioButton) toggleGroupTargetDirection.getSelectedToggle();
        if (currSelectedRB == null) {
            radioButtonPaths1to2.setSelected(true);
            currSelectedRB = radioButtonPaths1to2;
        }
    }

    private void setRadioListener(RadioButton radioButtonPaths) {
        radioButtonPaths.setOnAction(event -> {
            if (radioButtonPaths.isSelected() && radioButtonPaths != currSelectedRB) {
                currSelectedRB = radioButtonPaths;
                showWorkingPaths();
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

    public void populateData(DependenciesGraph graph) {
        if (graph != null) {
            this.workingGraph = graph;
            targetTableComponent1Controller.PopulateData_AllTargets(graph);
            targetTableComponent2Controller.PopulateData_AllTargets(graph);
        }
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    private void buttonFindPathsActionListener(ActionEvent event) {
        String targetName1 = labelTarget1Name.getText();
        String targetName2 = labelTarget2name.getText();

        if (!faultyTargetName(targetName1) && !faultyTargetName(targetName2)) {
            if (workingGraph != null) {
                try {
                    ArrayList<Collection<List<TargetDTO>>> targetPaths = workingGraph.getPathsBetweenTargets(targetName1, targetName2);

                    if (targetPaths != null) {
                        workingPaths = targetPaths;
                        showWorkingPaths();
                    }
                } catch (NonexistentTargetException e) {
                    e.printStackTrace();
                } catch (UninitializedNullException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean faultyTargetName(String targetName) {
        return (targetName == null || targetName.isEmpty());
    }

    private void showWorkingPaths() {
        if (workingPaths != null) {
            if (radioButtonPaths1to2.isSelected()) {
                showPath(workingPaths.get(INDEX_1TO2));
            } else {
                showPath(workingPaths.get(INDEX_2TO1));
            }
        }
    }

    private void showPath(Collection<List<TargetDTO>> pathsList) {
        if (pathsList != null) {
            textAreaPaths.clear();
            AppUtils.showPathsInTextArea(pathsList, textAreaPaths);
        }
    }
}
