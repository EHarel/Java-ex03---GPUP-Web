package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportNonFailed extends Report {
    public ReportNonFailed(Execution executionDataSim, PathMenu.ReportType reportType) {
        super(executionDataSim, reportType);
    }

    @Override
    public void DoAction() {
        reportNonFailed();
    }

    private void reportNonFailed() {
        TaskGeneral.printReports("Non-failed Processed Targets", execution.getProcessedData().getProcessedTargetsNoFailure(), reportType);
        System.out.println(System.lineSeparator());
    }
}