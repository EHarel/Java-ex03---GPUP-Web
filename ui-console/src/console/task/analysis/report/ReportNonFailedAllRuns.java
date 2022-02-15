package console.task.analysis.report;

import console.menu.loader.PathMenu;
import console.task.TaskGeneral;
import task.ExecutionData;

public class ReportNonFailedAllRuns extends Report {
    public ReportNonFailedAllRuns(ExecutionData executionData, PathMenu.ReportType reportType) {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportNonFailedAllRuns();
    }

    private void reportNonFailedAllRuns() {
        TaskGeneral.printReports("Non-failed Processed Targets", executionData.getProcessedData().getSuccessTargetsAllRunWithWarnings(), reportType);
        System.out.println(System.lineSeparator());
    }
}
