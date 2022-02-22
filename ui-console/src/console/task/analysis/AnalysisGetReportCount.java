package console.task.analysis;

import console.menu.system.DoesAction;
import logic.Engine;
import task.enums.TaskType;

public class AnalysisGetReportCount implements DoesAction {
    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        getReportCount();
    }

    private void getReportCount() {
        int count = engine.getExecutionCount(TaskType.SIMULATION);

        System.out.println("There have been " + count + " executions thus far.");
    }
}