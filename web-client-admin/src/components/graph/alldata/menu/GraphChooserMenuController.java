package components.graph.alldata.menu;

import components.graph.alldata.GraphAllDataController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import task.Execution;
import task.enums.TaskType;

import java.util.concurrent.atomic.AtomicBoolean;

public class GraphChooserMenuController {

    @FXML
    private Button buttonOriginalGraph;

    @FXML
    private VBox vboxSimulation;
    @FXML
    private VBox vboxCompilation;

    private GraphAllDataController graphAllDataController;

    public void setGraphController(GraphAllDataController graphAllDataController) {
        this.graphAllDataController = graphAllDataController;
    }

    public Button getButtonOriginalGraph() { return buttonOriginalGraph; }

    public Button addNewExecutionStartGraph(Execution execution) {
        Button newButton = null;

        if (execution != null) {
            VBox relevantVbox = getVBox(execution.getTaskType());
            String nameToSet = execution.getStartingGraph().getName();

            if (! doesButtonAlreadyExist(relevantVbox, nameToSet)) {
                newButton = new Button();
                newButton.textProperty().set(nameToSet);
                relevantVbox.getChildren().add(newButton);
                newButton.setTooltip(new Tooltip("Show the graph '" + newButton.textProperty().get() + "'"));
            }
        }

        return newButton;
    }

    public Button addNewExecutionEndGraph(Execution execution) {
        Button newButton = null;

        if (execution != null) {
            VBox relevantVbox = getVBox(execution.getTaskType());
            String nameToSet = execution.getEndGraph().getName();

            if (! doesButtonAlreadyExist(relevantVbox, nameToSet)) {
                newButton = new Button();
                newButton.textProperty().set(nameToSet);
                relevantVbox.getChildren().add(newButton);
                newButton.setTooltip(new Tooltip("Show the graph '" + newButton.textProperty().get() + "'"));
            }
        }

        return newButton;
    }

    private boolean doesButtonAlreadyExist(VBox relevantVbox, String nameToSet) {
        AtomicBoolean doesExist = new AtomicBoolean(false);

        relevantVbox.getChildren().forEach(node -> {
            Button nodeAsButton = (Button) node;
            if (nodeAsButton.getText().equals(nameToSet)) {
                doesExist.set(true);
            }
        });

        return doesExist.get();
    }

    private VBox getVBox(TaskType taskType) {
        VBox relevantVbox = null;

        if (taskType != null) {
            switch (taskType) {
                case SIMULATION:
                    relevantVbox = vboxSimulation;
                    break;
                case COMPILATION:
                    relevantVbox = vboxCompilation;
                    break;
            }
        }

        return relevantVbox;
    }
}
