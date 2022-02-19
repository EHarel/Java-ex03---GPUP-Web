package console.task.configuration;

import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import logic.Engine;
import task.configuration.ConfigurationDTO;

import java.util.Collection;

public class TaskGetConfiguration implements DoesAction {
    public enum RequestedConfiguration {
        ACTIVE, ALL
    }

    private Engine engine = Engine.getInstance();
    private final RequestedConfiguration requestedConfiguration;

    public TaskGetConfiguration(RequestedConfiguration requestedConfiguration) {
        this.requestedConfiguration = requestedConfiguration;
    }

    @Override
    public void DoAction() {
        showTaskParameters();
    }

    public void showTaskParameters() {
        switch (requestedConfiguration) {
            case ACTIVE:
                getActiveOnly();
                break;
            case ALL:
                getAllConfig();
                break;
        }
    }

    private void getActiveOnly() {
        ConfigurationDTO configData = engine.getConfigActive(TaskSubMenu.getTaskType());

        if (configData != null) {
            ConfigUtils.printConfig(configData);
        } else {
            System.out.println("Whoops! No configuration has been loaded yet.");
        }
    }

    private void getAllConfig() {
        Collection<ConfigurationDTO> configDataColl = engine.getConfigAll(TaskSubMenu.getTaskType());

        if (configDataColl == null || configDataColl.isEmpty()) {
            System.out.println("No configuration has been loaded yet. Not a single one. Pfft.");
        } else {
            int i = 1;
            for (ConfigurationDTO configData : configDataColl) {
                System.out.print(i + ") ");
                i++;
                ConfigUtils.printConfig(configData);
                System.out.println();
            }
        }
    }
}