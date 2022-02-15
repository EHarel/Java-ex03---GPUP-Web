package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportSuccessWarnings extends Report {
    public ReportSuccessWarnings(ExecutionData executionData, PathMenu.ReportType reportType) {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportWarnings();
    }

    private void reportWarnings() {
        TaskGeneral.printReports("Successful targets (with warnings)" +
                "", executionData.getProcessedData().getWarningTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}