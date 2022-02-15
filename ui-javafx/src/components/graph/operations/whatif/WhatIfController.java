package components.graph.operations.whatif;

import algorithm.DFS;
import components.app.AppUtils;
import components.graph.specifictarget.SpecificTargetController;
import components.graph.targettable.TargetTableController;
import exception.NonexistentTargetException;
import exception.UninitializedNullException;
import graph.DependenciesGraph;
import graph.TargetDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.util.Collection;
import java.util.List;


public class WhatIfController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- DATA MEMBERS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /* ----------------------------------------------- FXML ----------------------------------------------- */
    @FXML private Parent mainScene;

    @FXML private RadioButton radioButtonRequiredFor;
    @FXML private ToggleGroup toggleGroupDependencyDirection;
    @FXML private RadioButton radioButtonDependentOn;
    @FXML private Label labelRequiredForText;
    @FXML private Label labelDependentOnText;
    @FXML private TextArea textAreaPaths;
    @FXML private TextField textFieldAffectedTargetsCount;
    @FXML private TextField textFieldAffectedTargetsNames;
    @FXML private Button buttonFindDependencies;


    /* --------------------------------------- EXTERNAL COMPONENTS ---------------------------------------- */
    @FXML private Parent targetTableComponent;
    @FXML private TargetTableController targetTableComponentController;

    @FXML private Parent specificTargetDetailsComponent;
    @FXML private SpecificTargetController specificTargetDetailsComponentController;


    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private DependenciesGraph workingGraph;
    private TargetDTO workingTargetDTO;
    private Collection<String> cacheDependentOnTargetNames;
    private Collection<String> cacheRequiredForTargetNames;
    private Collection<List<TargetDTO>> cacheDependentOnPaths;
    private Collection<List<TargetDTO>> cacheRequiredForPaths;
    private String REQUIRED_FOR_DEFAULT_STR = "Show all the targets chosen target is required for.";
    private String DEPENDENT_ON_DEFAULT_STR = "Show all the targets chosen target is dependent on.";


    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    @FXML
    public void initialize() {
        targetTableComponentController.getTableView().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setTableListener();
    }

    private void setTableListener() {
        targetTableComponentController.getTableView().getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue) {
                clearCache();
                workingTargetDTO = newValue;
                specificTargetDetailsComponentController.populateData(workingTargetDTO, null);
            }

            updateDependencyLabelsText();
        }));
    }

    private void updateDependencyLabelsText() {
        if (workingTargetDTO == null) {
            labelRequiredForText.setText(REQUIRED_FOR_DEFAULT_STR);
            labelDependentOnText.setText(DEPENDENT_ON_DEFAULT_STR);
        } else {
            labelRequiredForText.setText("Show all the targets " + workingTargetDTO.getName() + " is required for.");
            labelDependentOnText.setText("Show all the targets " + workingTargetDTO.getName() + " is dependent on.");
        }
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Parent getMainScene() {
        return mainScene;
    }

    public DFS.EdgeDirection getEdgeDirection() {
        DFS.EdgeDirection edgeDirection = null;

        if (radioButtonDependentOn.isSelected()) {
            edgeDirection = DFS.EdgeDirection.DEPENDENT_ON;
        } else if (radioButtonRequiredFor.isSelected()) {
            edgeDirection = DFS.EdgeDirection.REQUIRED_FOR;
        }

        return edgeDirection;
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------- EVENTS ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML
    void buttonFindDependenciesActionListener(ActionEvent event) {
        if (workingTargetDTO == null || workingGraph == null)
            return;

        try {
            cacheDependentOnPaths = workingGraph.getTargetPaths(workingTargetDTO.getName(), DFS.EdgeDirection.DEPENDENT_ON);
            cacheDependentOnTargetNames = AppUtils.getNamesFromPaths_ExcludeChosen(cacheDependentOnPaths, workingTargetDTO.getName());

            cacheRequiredForPaths = workingGraph.getTargetPaths(workingTargetDTO.getName(), DFS.EdgeDirection.REQUIRED_FOR);
            // cacheRequiredForTargetNames = AppUtils.getNamesFromPaths(cacheRequiredForPaths);
            cacheRequiredForTargetNames = AppUtils.getNamesFromPaths_ExcludeChosen(cacheRequiredForPaths, workingTargetDTO.getName());


            showData();
        } catch (NonexistentTargetException e) {
            e.printStackTrace();
        } catch (UninitializedNullException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void radioButtonDependentOnActionListener(ActionEvent event) {
        showData();
    }

    @FXML
    void radioButtonRequiredForActionListener(ActionEvent event) {
        showData();
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ MISC. METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void showData() {
        if (radioButtonRequiredFor.isSelected()) {
            showAllAffectedTargets(cacheRequiredForTargetNames);
            showAllPaths(cacheRequiredForPaths);
        }

        if (radioButtonDependentOn.isSelected()) {
            showAllAffectedTargets(cacheDependentOnTargetNames);
            showAllPaths(cacheDependentOnPaths);
        }
    }

    public void populateData(DependenciesGraph graph) {
        if (graph != null) {
            clear();
            workingGraph = graph;
            targetTableComponentController.PopulateData_AllTargets(graph);
        }
    }

    public void clear() {
        workingGraph = null;
        workingTargetDTO = null;
        targetTableComponentController.clear();
        specificTargetDetailsComponentController.clear();
        textAreaPaths.clear();
        textFieldAffectedTargetsNames.clear();
        textFieldAffectedTargetsCount.clear();
        clearCache();
        updateDependencyLabelsText();
    }

    private void clearCache() {
        cacheRequiredForTargetNames = null;
        cacheDependentOnTargetNames = null;
        cacheDependentOnPaths = null;
        cacheRequiredForPaths = null;
    }

    private void showAllPaths(Collection<List<TargetDTO>> allPaths) {
        if (allPaths != null) {
            textAreaPaths.clear();
            AppUtils.showPathsInTextArea(allPaths, textAreaPaths);
        }
    }

    private void showAllAffectedTargets(Collection<String> allDependencies) {
        if (allDependencies != null) {
            textFieldAffectedTargetsNames.clear();
            textFieldAffectedTargetsNames.setText(allDependencies.toString());
            textFieldAffectedTargetsCount.setText(String.valueOf(allDependencies.size()));
        }
    }
}