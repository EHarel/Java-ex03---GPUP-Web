package console.menu.loader;

import console.menu.system.MenuAction;
import console.menu.system.SubMenu;
import console.task.analysis.report.*;
import task.Execution;

public class ProcessedTargetSubMenu extends SubMenu {
    private static final String subMenuName = "Target Processing";

    private String strAllProcessed = "See all processed targets (of all results)";
    private String strNonFailedThisRun = "See all non-failed targets -- success with or without warnings (this run)";
    private String strNonFailedAllRuns = "See all non-failed targets -- success with or without warnings (ALL runs)";
    private String strSuccessful = "See all successful targets (this run)";
    private String strSuccessWithWarnings = "See all success with warnings targets (this run)";
    private String strFailed = "See all failed targets";
    private String strUnprocessed = "See all unprocessed targets";
    private String strLeftToCompletion = "See all targets left to completion (failed and unprocessed)";

    private String reportTypeStr;

    MenuAction menuAllProcessed;
    MenuAction menuNonFailedThisRun;
    MenuAction menuNonFailedAllRuns;
    MenuAction menuSuccessfulThisRun;
    MenuAction menuSuccessWarningsThisRun;
    MenuAction menuFailed;
    MenuAction menuUnprocessed;
    MenuAction menuLeftToCompletion;

    Execution execution;

    public ProcessedTargetSubMenu(SubMenu parentItem , PathMenu.ReportType reportType, Execution execution) {
        super(subMenuName, parentItem);
        setReportTypeStr(reportType);
        super.setName(reportTypeStr + subMenuName);
        this.execution = execution;
        updateMenuNamesWithReportType();

        new MenuAction(reportTypeStr + "Report review", this, new ReportOverview(execution, reportType));
        menuAllProcessed = new MenuAction(strAllProcessed, this, new ReportProcessed(execution, reportType));
        menuNonFailedThisRun = new MenuAction(strNonFailedThisRun, this, new ReportNonFailed(execution, reportType));
        menuNonFailedAllRuns = new MenuAction(strNonFailedAllRuns, this, new ReportNonFailedAllRuns(execution, reportType));
        menuSuccessfulThisRun = new MenuAction(strSuccessful, this, new ReportSuccess(execution, reportType));
        menuSuccessWarningsThisRun = new MenuAction(strSuccessWithWarnings, this, new ReportSuccessWarnings(execution, reportType));
        menuFailed = new MenuAction(strFailed, this, new ReportFailed(execution, reportType));
        menuUnprocessed = new MenuAction(strUnprocessed, this, new ReportUnprocessed(execution, reportType));
        menuLeftToCompletion = new MenuAction(strLeftToCompletion, this, new ReportLeftToCompletion(execution, reportType));
    }

    private String setReportTypeStr(PathMenu.ReportType reportType) {
        String res;

        if (reportType == PathMenu.ReportType.GENERAL) {
            res = "[General] ";
        } else {
            res = "[Detailed] ";
        }

        reportTypeStr = res;

        return res;
    }

    private void updateMenuNamesWithReportType() {
        strAllProcessed = reportTypeStr + strAllProcessed;
        strNonFailedThisRun = reportTypeStr + strNonFailedThisRun;
        strNonFailedAllRuns = reportTypeStr + strNonFailedAllRuns;
        strSuccessful = reportTypeStr + strSuccessful;
        strSuccessWithWarnings = reportTypeStr + strSuccessWithWarnings;
        strFailed = reportTypeStr + strFailed;
        strUnprocessed = reportTypeStr + strUnprocessed;
        strLeftToCompletion = reportTypeStr + strLeftToCompletion;
    }

    @Override
    public void updateMenuItemText() {
        updateAllProcessedStr();
        updateNonFailedThisRunStr();
        updateNonFailedAllRunsStr();
        updateSuccessfulStr();
        updateSuccessWithWarningsStr();
        updateFailedTargetsStr();
        updateUnprocessedStr();
        updateLeftToCompletionStr();
    }

    private void updateAllProcessedStr() {
        int countAllProcessed = execution.getProcessedData().getAllProcessedTargetsOfAllResults().size();

        menuAllProcessed.setName(strAllProcessed + surroundNumWithParentheses(countAllProcessed));
    }

    private void updateNonFailedThisRunStr() {
        int num = execution.getProcessedData().getProcessedTargetsNoFailure().size();

        menuNonFailedThisRun.setName(strNonFailedThisRun + surroundNumWithParentheses(num));
    }

    private void updateNonFailedAllRunsStr() {
        int num = execution.getProcessedData().getSuccessTargetsAllRunWithWarnings().size();

        menuNonFailedAllRuns.setName(strNonFailedAllRuns + surroundNumWithParentheses(num));
    }

    private void updateSuccessfulStr() {
        int num = execution.getProcessedData().getSuccessfulTargets().size();

        menuSuccessfulThisRun.setName(strSuccessful + surroundNumWithParentheses(num));
    }

    private void updateSuccessWithWarningsStr() {
        int num = execution.getProcessedData().getWarningTargets().size();

        menuSuccessWarningsThisRun.setName(strSuccessWithWarnings + surroundNumWithParentheses(num));
    }

    private void updateFailedTargetsStr() {
        int num = execution.getProcessedData().getFailedTargets().size();

        menuFailed.setName(strFailed + surroundNumWithParentheses(num));
    }

    private void updateUnprocessedStr() {
        int num = execution.getProcessedData().getUnprocessedTargets().size();

        menuUnprocessed.setName(strUnprocessed + surroundNumWithParentheses(num));
    }

    private void updateLeftToCompletionStr() {
        int num = execution.getProcessedData().getTargetsLeftToCompletion().size();

        menuLeftToCompletion.setName(strLeftToCompletion + surroundNumWithParentheses(num));
    }

    private String surroundNumWithParentheses(int num) {
        return " (" + num + ")";
    }
}
