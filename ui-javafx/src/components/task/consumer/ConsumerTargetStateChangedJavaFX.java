package components.task.consumer;

import components.task.execution.main.TaskExecutionMainController;
import graph.TargetDTO;
import javafx.application.Platform;
import logic.Engine;

import java.util.function.Consumer;

public class ConsumerTargetStateChangedJavaFX implements Consumer<TargetDTO> {
    private final TaskExecutionMainController taskExecutionMainController;

    public ConsumerTargetStateChangedJavaFX(TaskExecutionMainController taskExecutionMainController) {
        this.taskExecutionMainController = taskExecutionMainController;
    }

    @Override
    public void accept(TargetDTO targetDTO) {

        String str = Engine.getInstance().getFormalizedTargetDataString(targetDTO) + System.lineSeparator();

        Platform.runLater(
                () -> taskExecutionMainController.targetStateChanged(targetDTO)
        );
    }
}
