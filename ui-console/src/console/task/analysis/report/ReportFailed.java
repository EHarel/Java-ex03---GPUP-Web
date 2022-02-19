package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportFailed extends Report {
    public ReportFailed(Execution execution, PathMenu.ReportType reportType) {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportFailed();
    }

    private void reportFailed() {
        TaskGeneral.printReports("Failed Targets", execution.getProcessedData().getFailedTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}