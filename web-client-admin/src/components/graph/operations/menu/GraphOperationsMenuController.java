package components.graph.operations.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GraphOperationsMenuController {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- FXML DATA MEMBERS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @FXML private Button buttonGraphOverview;
    @FXML private Button buttonSerialSets;
    @FXML private Button buttonCyclesWithTarget;
    @FXML private Button buttonWhatIf;
    @FXML private Button buttonPathsBetweenTargets;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Button getButtonGraphOverview() {
        return buttonGraphOverview;
    }

    public Button getButtonPathsBetweenTargets() {
        return buttonPathsBetweenTargets;
    }

    public Button getButtonSerialSets() {
        return buttonSerialSets;
    }

    public Button getButtonWhatIf() { return buttonWhatIf; }

    public Button getButtonCyclesWithTarget() {
        return buttonCyclesWithTarget;
    }
}
