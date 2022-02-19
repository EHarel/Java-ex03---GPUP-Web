package console.task.analysis.report;

import console.menu.loader.PathMenu;
import console.task.TaskGeneral;
import task.Execution;

public class ReportNonFailedAllRuns extends Report {
    public ReportNonFailedAllRuns(Execution execution, PathMenu.ReportType reportType) {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportNonFailedAllRuns();
    }

    private void reportNonFailedAllRuns() {
        TaskGeneral.printReports("Non-failed Processed Targets", execution.getProcessedData().getSuccessTargetsAllRunWithWarnings(), reportType);
        System.out.println(System.lineSeparator());
    }
}
