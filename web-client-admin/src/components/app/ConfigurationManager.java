package components.app;

import componentcode.executiontable.ExecutionDTOTable;
import task.configuration.*;
import task.enums.TaskType;

import java.util.*;

public class ConfigurationManager {
    List<Configuration> configurations;
    Configuration activeSimConfig;
    Configuration activeCompConfig;

    public ConfigurationManager() {
        configurations = new LinkedList<>();
    }

    /**
     * Sets the active configuration based on the configuration in the given execution DTO.
     */
    public synchronized void setActiveConfig(ExecutionDTOTable executionDTOTable) {
        if (executionDTOTable != null && executionDTOTable.getConfigDTO() != null) {
            Configuration chosenConfig = null;

            // Find the matching configuration
            for (Configuration configuration :
                    configurations) {
                if (areEqualConfigurations(configuration.toDTO(), executionDTOTable.getConfigDTO()) ) {
                    chosenConfig = configuration;
                    break;
                }
            }

            // Set a new active, if found a matching
            if (chosenConfig != null) {
                switch (executionDTOTable.getTaskType()) {
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

    public synchronized boolean removeConfiguration(TaskType taskType, String configName) {
        Configuration configToRemove = null;
        int i;
        for (i=0 ; i < configurations.size() ; i++) {
            Configuration existingConfig = configurations.get(i);

            if (existingConfig.getName().equals(configName) && existingConfig.getTaskType() == taskType) {
                configToRemove = existingConfig;
                break;
            }
        }

        boolean isRemoved = (configToRemove != null);

        if (configToRemove != null) {
            configurations.remove(i);
        }

        return isRemoved;
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

    public synchronized void addConfigurationIfMissing(ExecutionDTOTable executionDTOTable) {
        if (executionDTOTable == null) {
            return;
        }
        
        if (executionDTOTable.getConfigDTO() == null) {
            return;
        }
        
        ConfigurationDTO exeConfigDTO = executionDTOTable.getConfigDTO();
        boolean foundIdenticalParameters = false;
        Integer indexOfIdenticalName = null;


        for (int i = 0 ; i < configurations.size() ; i++) {
            Configuration existingConfig = configurations.get(i);

            if (areEqualConfigurations(existingConfig.toDTO(), exeConfigDTO)) {
                foundIdenticalParameters = true;
            }

            if (existingConfig.getName().equals(exeConfigDTO.getName())) {
                indexOfIdenticalName = new Integer(i);
            }
        }

        if (!foundIdenticalParameters) {
            if (indexOfIdenticalName != null) {
                configurations.remove(indexOfIdenticalName);
            }

            Configuration newConfig = null;
            try {

                switch (exeConfigDTO.getTaskType()) {
                    case COMPILATION:
                        newConfig = new ConfigurationCompilation((ConfigurationDTOCompilation) exeConfigDTO);
                        break;
                    default:
                    case SIMULATION:
                        newConfig = new ConfigurationSimulation((ConfigurationDTOSimulation) exeConfigDTO);
                        break;
                }
            } catch (Exception ignore) {}
            configurations.add(newConfig);
        }
    }
    
    private boolean areEqualConfigurations(ConfigurationDTO config1, ConfigurationDTO config2) {
        if (config1 == null || config2 == null) {
            return false;
        }
        
        if (config1.getTaskType() != config2.getTaskType()) {
            return false;
        }
        
        boolean areEqual;
        switch (config1.getTaskType()) {
            case SIMULATION:
                ConfigurationDTOSimulation config1Sim = (ConfigurationDTOSimulation) config1;
                ConfigurationDTOSimulation config2Sim = (ConfigurationDTOSimulation) config2;
                
                areEqual = true;
                
                if (config1Sim.getWarningsProbability() != config2Sim.getWarningsProbability()) {
                    areEqual = false;
                }
                
                if (
                        (! config1Sim.isRandomProcessingTime() && config2Sim.isRandomProcessingTime())
                        ||
                        (config1Sim.isRandomProcessingTime() && ! config2Sim.isRandomProcessingTime())
                ) {
                    areEqual = false;
                }
                
                if (config1Sim.getSuccessProbability() != config2Sim.getSuccessProbability()) {
                    areEqual = false;
                }
                
                if (config1Sim.getProcessingTime() != config2Sim.getProcessingTime()) {
                    areEqual = false;
                }
                
                break;
            default:
            case COMPILATION:
                ConfigurationDTOCompilation config1Comp = (ConfigurationDTOCompilation) config1;
                ConfigurationDTOCompilation config2Comp = (ConfigurationDTOCompilation) config2;
                areEqual = true;

                if (! config1Comp.getSourceCodePath().equals(config2Comp.getSourceCodePath())) {
                    areEqual = false;
                }

                if (! config1Comp.getOutPath().equals(config2Comp.getOutPath())) {
                    areEqual = false;
                }

                break;
        }
        
        return areEqual;
    }
}
