package console.menu.loader;

import console.task.*;
import console.menu.system.MenuAction;
import console.menu.system.SubMenu;
import console.task.analysis.AnalysisGetLastRun;
import console.task.analysis.AnalysisGetReportCount;
import console.task.analysis.AnalysisGetSpecificRun;
import task.OldCode.TaskProcess;
import task.enums.TaskType;

public class TaskSubMenu extends SubMenu {
    private static final String subMenuName = "Task - ";
    private static final String compAddonName = "COMPILATION";
    private static final String simAddonName = "SIMULATION";

    private static TaskType taskType = TaskType.SIMULATION;
//    private static TaskType taskType = TaskType.COMPILATION;

    private SubMenu configSubMenu;

    public TaskSubMenu(SubMenu parentItem) {
        super(subMenuName, parentItem);
        updateName();

        configSubMenu = new ConfigSubMenu(this);
        new MenuAction("New run (start from scratch)", this, new console.task.TaskGeneral(TaskProcess.StartPoint.FROM_SCRATCH));
        MenuAction taskIncremental = new MenuAction("Resume run (incremental execution) ", this, new console.task.TaskGeneral(TaskProcess.StartPoint.INCREMENTAL));
        taskIncremental.setComments("If there was no previous processing, this is equivalent to 'from scratch'");
        new MenuAction("Number of executions so far", this, new AnalysisGetReportCount());
        new MenuAction("Analyze last execution", this, new AnalysisGetLastRun());
        new MenuAction("Analyze specific execution", this, new AnalysisGetSpecificRun());
        new MenuAction("Change task type", this, new ChangeTaskType(this));
    }

    @Override
    public void updateMenuItemText() {
        updateName();
        configSubMenu.updateMenuItemText();
    }

    private void updateName() {
        if (taskType == TaskType.COMPILATION) {
            name = subMenuName + compAddonName;
        } else {
            name = subMenuName + simAddonName;
        }
    }

    public static TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        TaskSubMenu.taskType = taskType;
        updateName();
        configSubMenu.updateMenuItemText();
    }
}