package console.task.analysis.report;

import console.menu.loader.PathMenu;
import console.menu.system.DoesAction;
import task.Execution;

public abstract class Report implements DoesAction {
    protected final Execution execution;
    protected final PathMenu.ReportType reportType;

    public Report(Execution execution, PathMenu.ReportType reportType) throws NullPointerException {
        if (execution == null || reportType == null) {
            throw new NullPointerException();
        }

        this.execution = execution;
        this.reportType = reportType;
    }
}