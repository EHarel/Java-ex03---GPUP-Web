package console.task.analysis;

import console.menu.loader.ExecutionAnalysisMenu;
import console.menu.system.DoesAction;
import logic.Engine;
import task.Execution;
import task.enums.TaskType;

public class AnalysisGetLastRun implements DoesAction {
    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        getLastRun();
    }

    private void getLastRun() {
        int numOfReports = engine.getExecutionCount(TaskType.SIMULATION);

        if (numOfReports == 0) {
            System.out.println("There haven't been any executions yet.");
            return;
        }

        Execution execution = engine.getExecutionLast(TaskType.SIMULATION);

        new ExecutionAnalysisMenu(execution).run();
    }
}
