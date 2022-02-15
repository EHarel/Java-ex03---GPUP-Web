package console.task;

import console.Utils;
import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import task.TaskType;

public class ChangeTaskType implements DoesAction {
    private final TaskSubMenu taskSubMenu;

    public ChangeTaskType(TaskSubMenu taskSubMenu) {
        this.taskSubMenu = taskSubMenu;
    }

    @Override
    public void DoAction() {
        changeTaskType();
    }

    private void changeTaskType() {
        TaskType taskType = Utils.getTaskType();

        taskSubMenu.setTaskType(taskType);
    }
}
