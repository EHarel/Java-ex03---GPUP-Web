package console.task.analysis.report;

import console.menu.loader.PathMenu;
import console.menu.system.DoesAction;
import task.ExecutionData;

public abstract class Report implements DoesAction {
    protected final ExecutionData executionData;
    protected final PathMenu.ReportType reportType;

    public Report(ExecutionData executionData, PathMenu.ReportType reportType) throws NullPointerException {
        if (executionData == null || reportType == null) {
            throw new NullPointerException();
        }

        this.executionData = executionData;
        this.reportType = reportType;
    }
}