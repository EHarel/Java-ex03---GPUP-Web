package components.task.consumer;

import components.app.AppMainController;
import javafx.application.Platform;
import task.Execution;
import java.util.function.Consumer;

public class ConsumerFinishedTaskProcessJavaFX implements Consumer<Execution>{
    AppMainController appMainController;

    public ConsumerFinishedTaskProcessJavaFX(AppMainController appMainController) {
        this.appMainController = appMainController;
    }

    @Override
    public void accept(Execution execution) {
// Old code from console version
        // TaskGeneral.printExecutionReport(executionData);
//        TaskGeneral.TaskState taskState = TaskGeneral.getTaskState();
//        synchronized (taskState) {
//            taskState.setFinished(true);
//            taskState.notifyAll();
//        }

        Platform.runLater(
                () -> appMainController.finishedExecutionProcess(execution)
        );
    }
}