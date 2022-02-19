package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportUnprocessed extends Report {
    public ReportUnprocessed(Execution execution, PathMenu.ReportType reportType) {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportUnprocessed();
    }

    private void reportUnprocessed() {
        TaskGeneral.printReports("Unprocessed Targets", execution.getProcessedData().getUnprocessedTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}