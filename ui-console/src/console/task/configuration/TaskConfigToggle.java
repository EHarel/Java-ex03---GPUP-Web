package console.task.configuration;

import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import logic.Engine;
import task.TaskType;

public class TaskConfigToggle implements DoesAction {
    @Override
    public void DoAction() {
        toggleConfigRemembering();
    }

    private void toggleConfigRemembering() {
        Engine engine = Engine.getInstance();
        engine.toggleRememberLastConfiguration(TaskSubMenu.getTaskType());
        if (engine.isPersistentConfiguration(TaskSubMenu.getTaskType())) {
            System.out.println("\nWe'll remember your last active configuration for the next execution :)");
        } else {
            System.out.println("\nWe'll forget your last active configuration. You'll need to set an active configuration manually before each execution.");
        }
    }
}
