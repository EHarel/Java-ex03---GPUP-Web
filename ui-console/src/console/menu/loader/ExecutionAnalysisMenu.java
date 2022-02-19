package console.menu.loader;

import console.task.TaskGraphAnalysis;
import console.task.analysis.report.*;
import console.menu.system.MenuAction;
import console.menu.system.MenuManager;
import console.menu.system.SubMenu;
import task.Execution;

public class ExecutionAnalysisMenu {
    private MenuManager analysisReportManager;
    private SubMenu mainMenu = new SubMenu("Execution Analysis", null);
    private Execution execution;

    public ExecutionAnalysisMenu(Execution execution) throws NullPointerException {
        if (execution == null) {
            throw new NullPointerException();
        }

        this.execution = execution;
        initMenu();
    }

    public void run() {
        analysisReportManager.RunMenu();
    }

    private void initMenu() {
        new ProcessedTargetSubMenu(mainMenu, PathMenu.ReportType.GENERAL, execution);
        new ProcessedTargetSubMenu(mainMenu, PathMenu.ReportType.DETAIL, execution);
        new MenuAction("See specific target results", mainMenu, new ReportSpecificTarget(execution));
        new MenuAction("Execution configuration data", mainMenu, new ReportConfigurationData(execution));
        new MenuAction("Analyze graph at beginning of task", mainMenu, new TaskGraphAnalysis(execution.getStartingGraph(), "Graph At Start"));
        new MenuAction("Analyze graph at end of task", mainMenu, new TaskGraphAnalysis(execution.getEndGraph(), "Graph At End"));

        analysisReportManager = new MenuManager(mainMenu, true);
    }
}