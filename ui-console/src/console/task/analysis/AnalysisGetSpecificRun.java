package console.task.analysis;

import console.Utils;
import console.menu.loader.ExecutionAnalysisMenu;
import console.menu.system.DoesAction;
import logic.Engine;
import task.Execution;
import task.TaskType;

public class AnalysisGetSpecificRun implements DoesAction {
    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        getSpecificRun();
    }

    private void getSpecificRun() {
        Execution execution;
        int numOfReports = engine.getExecutionCount(TaskType.SIMULATION);

        if (numOfReports == 0) {
            System.out.println("There haven't been any executions yet.");
            return;
        }

        if (numOfReports == 1) {
            System.out.println("There has been only 1 execution so far.");
            execution = engine.getExecutionLast(TaskType.SIMULATION);
        } else {
            System.out.println("There have been " + numOfReports + " executions thus far.");
            System.out.println("Which report do you want to view? Enter a number between 1 and " + numOfReports);
            int choice = Utils.readIntInRange(1, numOfReports);
            choice--; // Decrement for array indices starting from 0
            execution = engine.getExecutionReportIndex(TaskType.SIMULATION, choice);
        }

        new ExecutionAnalysisMenu(execution).run();
    }
}