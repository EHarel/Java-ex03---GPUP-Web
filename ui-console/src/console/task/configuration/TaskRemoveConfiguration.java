package console.task.configuration;

import console.Utils;
import console.menu.system.DoesAction;
import logic.Engine;
import task.enums.TaskType;

public class TaskRemoveConfiguration implements DoesAction {
    public enum RemoveCount {
        ONE, ALL
    }

    private Engine engine = Engine.getInstance();
    private RemoveCount removeCount;

    public TaskRemoveConfiguration(RemoveCount removeCount) {
        this.removeCount = removeCount;
    }

    @Override
    public void DoAction() {
        removeConfig();
    }

    private void removeConfig() {
        switch (removeCount) {
            case ONE:
                removeOne();
                break;
            case ALL:
                removeAll();
                break;
        }
    }

    private void removeAll() {
        if (engine.getConfigCount(TaskType.SIMULATION) == 0) {
            System.out.println("No configurations have been loaded. Nothing to remove.");
        } else {
            engine.removeConfigurationAll(TaskType.SIMULATION);
            System.out.println("Removed all configurations. Don't forget to load a new one!");
            System.out.println();
        }
    }

    private void removeOne() {
        if (engine.getConfigCount(TaskType.SIMULATION) == 0) {
            System.out.println("No configurations have been loaded. Nothing to remove.");
        } else {
            String configName = Utils.readName(null);

            if (engine.removeConfiguration(TaskType.SIMULATION, configName)) {
                System.out.println("Configuration \"" + configName + "\" removed successfully.");
            } else {
                System.out.println("Could not find configuration by that name to remove.");
            }
        }
    }
}
