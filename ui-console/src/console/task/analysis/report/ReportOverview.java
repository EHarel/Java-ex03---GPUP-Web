package console.task.analysis.report;

import console.task.TaskGeneral;
import console.menu.loader.PathMenu;
import task.Execution;

public class ReportOverview extends Report {
    public ReportOverview(Execution execution, PathMenu.ReportType reportType) throws NullPointerException {
        super(execution, reportType);
    }

    @Override
    public void DoAction() {
        reportOverview();
    }

    private void reportOverview() {
        switch (reportType) {
            case GENERAL:
                TaskGeneral.printExecutionReport(execution);
                break;
            case DETAIL:
                TaskGeneral.printReports("All reports", execution.getProcessedData().getAllTargetData(), PathMenu.ReportType.DETAIL);
                break;
        }
    }
}