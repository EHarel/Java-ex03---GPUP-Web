package console.menu.loader;

import console.menu.system.MenuAction;
import console.menu.system.SubMenu;
import console.task.configuration.*;
import logic.Engine;
import task.enums.TaskType;
import task.configuration.ConfigurationDTO;

public class ConfigSubMenu extends SubMenu {
    private static String subMenuName = "Configuration - ";
    private static String subMenuSimAddon = "SIMULATION";
    private static String subMenuCompAddon = "COMPILATION";

    private static final String seeActiveConfigText = "See details of active configuration";
    private static final String rememberingString = "currently remembering";
    private static final String notRememberingString = "currently not remembering";
    private String configToggleMessage = notRememberingString;
    private boolean rememberLastConfig = false;

    private static final String getAllLoadedStr = "Get all loaded task configurations";

    Engine engine = Engine.getInstance();
    MenuAction getActiveConfig;
    MenuAction toggleConfig;
    MenuAction getAllLoaded;
//    String currentName;

    public ConfigSubMenu(SubMenu parentItem) {
        super(subMenuName, parentItem);
        super.name = getCurrentName();

        new MenuAction("Load system default task parameters", this, new TaskSetConfigurationDefault());
        new MenuAction("Create new configuration", this, new TaskSetConfiguration());
        new MenuAction("Change active configuration", this, new TaskSetActiveConfiguration());
        getActiveConfig = new MenuAction(seeActiveConfigText, this, new TaskGetConfiguration(TaskGetConfiguration.RequestedConfiguration.ACTIVE));
        getAllLoaded = new MenuAction(getAllLoadedStr, this, new TaskGetConfiguration(TaskGetConfiguration.RequestedConfiguration.ALL));
        SubMenu removeConfig = new SubMenu("Remove configuration", this);
        new MenuAction("Remove specific configuration", removeConfig, new TaskRemoveConfiguration(TaskRemoveConfiguration.RemoveCount.ONE));
        new MenuAction("Remove all configurations", removeConfig, new TaskRemoveConfiguration(TaskRemoveConfiguration.RemoveCount.ALL));
        toggleConfig = new MenuAction("[Toggle] Remember last configuration for next execution (active config you manually set override last one used)",
                this, new TaskConfigToggle());
    }

    private String getCurrentName() {
        updateCurrentName();

        return name;
    }

    @Override
    public void updateMenuItemText() {
        updateActiveConfigText();
        updateRememberLastConfigText();
        updateGetAllLoadedConfigText();
        updateCurrentName();
    }

    private void updateCurrentName() {
        if (TaskSubMenu.getTaskType() == TaskType.COMPILATION) {
            name = subMenuName + subMenuCompAddon;
        } else {
            name = subMenuName + subMenuSimAddon;
        }
    }

    private void updateActiveConfigText() {
        ConfigurationDTO configData = Engine.getInstance().getConfigActive(TaskSubMenu.getTaskType());
        String textToAdd;

        if(configData != null) {
            textToAdd = configData.getName();
        } else {
            textToAdd = "none active";
        }

        this.getActiveConfig.setName(seeActiveConfigText + " (" + textToAdd + ")");
    }

    private void updateRememberLastConfigText() {
        rememberLastConfig = engine.isPersistentConfiguration(TaskSubMenu.getTaskType());

        if (rememberLastConfig) {
            toggleConfig.setComments(rememberingString);
        } else {
            toggleConfig.setComments(notRememberingString);
        }
    }

    private void updateGetAllLoadedConfigText() {
        int numOfLoaded = engine.getConfigAll(TaskSubMenu.getTaskType()).size();

        getAllLoaded.setName(getAllLoadedStr + " (" + numOfLoaded + ")");
    }
}
