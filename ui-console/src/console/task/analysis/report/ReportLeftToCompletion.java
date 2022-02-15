package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportLeftToCompletion extends Report {
    public ReportLeftToCompletion(ExecutionData executionData, PathMenu.ReportType reportType) throws NullPointerException {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportLeftToCompletion();
    }

    private void reportLeftToCompletion() {
        TaskGeneral.printReports("Targets left to completion (failed and unprocessed)", executionData.getProcessedData().getTargetsLeftToCompletion(), reportType);
        System.out.println(System.lineSeparator());
    }
}