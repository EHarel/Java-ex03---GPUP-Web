package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportProcessed extends Report {
    public ReportProcessed(Execution execution, PathMenu.ReportType reportType) throws NullPointerException {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportProcessed();
    }

    private void reportProcessed() {
        TaskGeneral.printReports("Processed Targets (successful and failed)", execution.getProcessedData().getAllProcessedTargetsOfAllResults(), reportType);
        System.out.println(System.lineSeparator());
    }
}