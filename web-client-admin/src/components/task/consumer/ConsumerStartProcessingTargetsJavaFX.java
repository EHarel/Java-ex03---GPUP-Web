package components.task.consumer;

import components.task.execution.main.TaskExecutionMainController;
import graph.DependenciesGraph;
import javafx.application.Platform;

import java.util.function.Consumer;

public class ConsumerStartProcessingTargetsJavaFX implements Consumer<DependenciesGraph> {
    private final TaskExecutionMainController taskExecutionMainController;

    public ConsumerStartProcessingTargetsJavaFX(TaskExecutionMainController taskExecutionMainController) {
        this.taskExecutionMainController = taskExecutionMainController;
    }

    @Override
    public void accept(DependenciesGraph graph) {
        Platform.runLater(
                () -> taskExecutionMainController.setTaskProcessWorkingGraph(graph)
        );
    }
}
