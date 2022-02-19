package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportSuccessWarnings extends Report {
    public ReportSuccessWarnings(Execution execution, PathMenu.ReportType reportType) {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportWarnings();
    }

    private void reportWarnings() {
        TaskGeneral.printReports("Successful targets (with warnings)" +
                "", execution.getProcessedData().getWarningTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}