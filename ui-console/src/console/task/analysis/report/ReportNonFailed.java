package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportNonFailed extends Report {
    public ReportNonFailed(ExecutionData executionDataDataSim, PathMenu.ReportType reportType) {
        super(executionDataDataSim, reportType);
    }

    @Override
    public void DoAction() {
        reportNonFailed();
    }

    private void reportNonFailed() {
        TaskGeneral.printReports("Non-failed Processed Targets", executionData.getProcessedData().getProcessedTargetsNoFailure(), reportType);
        System.out.println(System.lineSeparator());
    }
}