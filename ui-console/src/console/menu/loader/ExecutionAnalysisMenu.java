package console.menu.loader;

import console.task.TaskGraphAnalysis;
import console.task.analysis.report.*;
import console.menu.system.MenuAction;
import console.menu.system.MenuManager;
import console.menu.system.SubMenu;
import task.ExecutionData;

public class ExecutionAnalysisMenu {
    private MenuManager analysisReportManager;
    private SubMenu mainMenu = new SubMenu("Execution Analysis", null);
    private ExecutionData executionData;

    public ExecutionAnalysisMenu(ExecutionData executionData) throws NullPointerException {
        if (executionData == null) {
            throw new NullPointerException();
        }

        this.executionData = executionData;
        initMenu();
    }

    public void run() {
        analysisReportManager.RunMenu();
    }

    private void initMenu() {
        new ProcessedTargetSubMenu(mainMenu, PathMenu.ReportType.GENERAL, executionData);
        new ProcessedTargetSubMenu(mainMenu, PathMenu.ReportType.DETAIL, executionData);
        new MenuAction("See specific target results", mainMenu, new ReportSpecificTarget(executionData));
        new MenuAction("Execution configuration data", mainMenu, new ReportConfigurationData(executionData));
        new MenuAction("Analyze graph at beginning of task", mainMenu, new TaskGraphAnalysis(executionData.getStartingGraph(), "Graph At Start"));
        new MenuAction("Analyze graph at end of task", mainMenu, new TaskGraphAnalysis(executionData.getEndGraph(), "Graph At End"));

        analysisReportManager = new MenuManager(mainMenu, true);
    }
}