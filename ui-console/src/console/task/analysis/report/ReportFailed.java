package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportFailed extends Report {
    public ReportFailed(ExecutionData executionData, PathMenu.ReportType reportType) {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportFailed();
    }

    private void reportFailed() {
        TaskGeneral.printReports("Failed Targets", executionData.getProcessedData().getFailedTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}