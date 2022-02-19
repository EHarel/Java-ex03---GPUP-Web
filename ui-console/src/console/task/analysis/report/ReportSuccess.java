package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportSuccess extends Report {
    public ReportSuccess(Execution execution, PathMenu.ReportType reportType)  {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportSuccess();
    }

    private void reportSuccess() {
        TaskGeneral.printReports("Successful targets", execution.getProcessedData().getSuccessfulTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}