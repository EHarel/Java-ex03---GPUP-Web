package components.graph.targettable.selection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;

public class TableSelectionController {
    @FXML private Button selectAllButton;
    @FXML private RadioButton requiredForRB;
    @FXML private RadioButton dependentOnRB;
    @FXML private Button unselectAllButton;

    public Button getSelectAllButton() {
        return selectAllButton;
    }

    public RadioButton getRequiredForRB() {
        return requiredForRB;
    }

    public RadioButton getDependentOnRB() {
        return dependentOnRB;
    }

    public Button getUnselectAlLButton() {
        return unselectAllButton;
    }
}