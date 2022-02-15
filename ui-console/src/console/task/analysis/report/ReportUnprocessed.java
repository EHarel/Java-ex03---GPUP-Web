package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportUnprocessed extends Report {
    public ReportUnprocessed(ExecutionData executionData, PathMenu.ReportType reportType) {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportUnprocessed();
    }

    private void reportUnprocessed() {
        TaskGeneral.printReports("Unprocessed Targets", executionData.getProcessedData().getUnprocessedTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}