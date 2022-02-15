package console.task.configuration;

import console.Utils;
import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import logic.Engine;
import task.TaskType;

public class TaskSetActiveConfiguration implements DoesAction {
    Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        setActive();
    }

    private void setActive() {
        String name = Utils.readName("Note that name must exist already. This doesn't create a new one.");

        if (engine.setActiveConfig(TaskSubMenu.getTaskType(), name)) {
            System.out.println("Active configuration updated.");
        } else {
            System.out.println("Failed to update active configuration. Are you sure configuration by said name exists?");
        }
    }
}