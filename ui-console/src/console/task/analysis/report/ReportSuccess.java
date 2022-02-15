package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportSuccess extends Report {
    public ReportSuccess(ExecutionData executionData, PathMenu.ReportType reportType)  {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportSuccess();
    }

    private void reportSuccess() {
        TaskGeneral.printReports("Successful targets", executionData.getProcessedData().getSuccessfulTargets(), reportType);
        System.out.println(System.lineSeparator());
    }
}