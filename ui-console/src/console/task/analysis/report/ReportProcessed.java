package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportProcessed extends Report {
    public ReportProcessed(ExecutionData executionData, PathMenu.ReportType reportType) throws NullPointerException {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportProcessed();
    }

    private void reportProcessed() {
        TaskGeneral.printReports("Processed Targets (successful and failed)", executionData.getProcessedData().getAllProcessedTargetsOfAllResults(), reportType);
        System.out.println(System.lineSeparator());
    }
}