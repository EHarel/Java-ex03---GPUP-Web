package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.ExecutionData;

public class ReportOverview extends Report {
    public ReportOverview(ExecutionData executionData, PathMenu.ReportType reportType) throws NullPointerException {
        super(executionData, reportType);
    }

    @Override
    public void DoAction() {
        reportOverview();
    }

    private void reportOverview() {
        switch (reportType) {
            case GENERAL:
                TaskGeneral.printExecutionReport(executionData);
                break;
            case DETAIL:
                TaskGeneral.printReports("All reports", executionData.getProcessedData().getAllTargetData(), PathMenu.ReportType.DETAIL);
                break;
        }
    }
}