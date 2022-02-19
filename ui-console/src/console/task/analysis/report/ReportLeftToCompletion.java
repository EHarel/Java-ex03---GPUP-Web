package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportLeftToCompletion extends Report {
    public ReportLeftToCompletion(Execution execution, PathMenu.ReportType reportType) throws NullPointerException {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportLeftToCompletion();
    }

    private void reportLeftToCompletion() {
        TaskGeneral.printReports("Targets left to completion (failed and unprocessed)", execution.getProcessedData().getTargetsLeftToCompletion(), reportType);
        System.out.println(System.lineSeparator());
    }
}