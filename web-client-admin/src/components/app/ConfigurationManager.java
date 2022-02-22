package components.app;

import task.enums.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConfigurationManager {
    Set<Configuration> configurations;
    Configuration activeSimConfig;
    Configuration activeCompConfig;

    public ConfigurationManager() {
        configurations = new HashSet<>();
    }


    public synchronized void setActiveConfig(TaskType taskType, String configName) {
        if (taskType != null && configName != null) {
            Configuration chosenConfig = null;

            for (Configuration configuration :
                    configurations) {
                if (configuration.getTaskType() == taskType && configuration.getName().equals(configName)) {
                    chosenConfig = configuration;
                    break;
                }
            }

            if (chosenConfig != null) {
                switch (taskType) {
                    case COMPILATION:
                        this.activeCompConfig = chosenConfig;
                        break;
                    case SIMULATION:
                        this.activeSimConfig = chosenConfig;
                        break;
                }
            }
        }
    }

    public synchronized boolean addConfiguration(Configuration config) {
        boolean added = false;
        boolean isExistingName = false;

        for (Configuration existingConfig : configurations) {
            if (existingConfig.getName().equals(config.getName()) && existingConfig.getTaskType() == config.getTaskType()) {
                isExistingName = true;
                break;
            }
        }

        if (!isExistingName) {
            configurations.add(config);
            added = true;
        }

        return added;
    }

    public synchronized Collection<ConfigurationDTO> getConfigDataAll(TaskType taskType) {
        Collection<ConfigurationDTO> configData = new ArrayList<>();

        if (taskType != null) {
            for (Configuration config : configurations) {
                if (config.getTaskType() == taskType) {
                    configData.add(config.toDTO());
                }
            }
        }

        return configData;
    }

    public synchronized ConfigurationDTO getActiveConfigData(TaskType taskType) {
        ConfigurationDTO configData = null;

        switch (taskType) {
            case SIMULATION:
                if (activeSimConfig != null) {
                    configData = activeSimConfig.toDTO();
                }
                break;
            case COMPILATION:
                if (activeCompConfig != null) {
                    configData = activeCompConfig.toDTO();
                }
                break;
        }

        return configData;
    }

    public synchronized ConfigurationDTO getConfigDataSpecific(TaskType taskType, String configName) {
        ConfigurationDTO configData = null;
        Configuration chosenConfig = null;

        for (Configuration config :
                configurations) {
            if (config.getTaskType() == taskType && config.getName().equals(configName)) {
                chosenConfig = config;
                break;
            }
        }

        if (chosenConfig != null) {
            configData = chosenConfig.toDTO();
        }

        return configData;
    }
}
