package components.task.consumer;

import components.task.execution.main.TaskExecutionMainController;
import graph.TargetDTO;
import javafx.application.Platform;
import logic.Engine;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConsumerFinishedTargetJavaFX implements Consumer<TargetDTO>, Serializable {
    private static final long serialVersionUID = 0; // 13-Jan-2021, creation
    TaskExecutionMainController taskExecutionMainController;

    public ConsumerFinishedTargetJavaFX(TaskExecutionMainController taskExecutionMainController) {
        this.taskExecutionMainController = taskExecutionMainController;
    }

    @Override
    public void accept(TargetDTO targetDTO) {
        String str = Engine.getInstance().getFormalizedTargetDataString(targetDTO) + System.lineSeparator();

        Platform.runLater(
                () -> taskExecutionMainController.finishedProcessingTarget(targetDTO)
        );
    }
}
