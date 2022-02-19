package console.task.consumer;

import console.task.TaskGeneral;
import task.Execution;

import java.io.Serializable;
import java.util.function.Consumer;

public class ConsumerPrintExecution implements Consumer<Execution>, Serializable {
    private static final long serialVersionUID = 1; // 08-Dec-2021, creation

    @Override
    public void accept(Execution execution) {
        System.out.println("\n ---------- Execution completed (consumer print) ---------- \n");
        TaskGeneral.printExecutionReport(execution);

        // Old code before JavaFX
//        TaskGeneral.TaskState taskState = TaskGeneral.getTaskState();
//        synchronized (taskState) {
//            taskState.setFinished(true);
//            taskState.notifyAll();
//        }
    }
}
