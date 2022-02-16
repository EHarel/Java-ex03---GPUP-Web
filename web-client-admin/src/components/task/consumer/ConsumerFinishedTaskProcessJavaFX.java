package components.task.consumer;

import components.app.AppMainController;
import console.task.TaskGeneral;
import javafx.application.Platform;
import task.ExecutionData;
import java.util.function.Consumer;

public class ConsumerFinishedTaskProcessJavaFX implements Consumer<ExecutionData>{
    AppMainController appMainController;

    public ConsumerFinishedTaskProcessJavaFX(AppMainController appMainController) {
        this.appMainController = appMainController;
    }

    @Override
    public void accept(ExecutionData executionData) {
// Old code from console version
        // TaskGeneral.printExecutionReport(executionData);
//        TaskGeneral.TaskState taskState = TaskGeneral.getTaskState();
//        synchronized (taskState) {
//            taskState.setFinished(true);
//            taskState.notifyAll();
//        }

        Platform.runLater(
                () -> appMainController.finishedExecutionProcess(executionData)
        );
    }
}