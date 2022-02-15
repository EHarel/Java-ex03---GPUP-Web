package console.task.configuration;

import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import exception.InvalidInputRangeException;
import exception.NonexistentElementException;
import logic.Engine;
import task.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationCompilation;
import task.configuration.ConfigurationSimulation;

import javax.naming.NameNotFoundException;

public class TaskSetConfigurationDefault implements DoesAction {
    private Engine engine = Engine.getInstance();

    ConfigurationSimulation defaultConfigSim = null;
    ConfigurationCompilation defaultConfigComp = null;

    private final int defaultParallelism = 3;

    private final String simName = "__DEFAULT_CONFIG__SIMULATION__";
    private final int processingTime = 2000; // An integer given in milliseconds (so 1500 is invalid)
    private final boolean randomProcessingTime = true; // random means the time is somewhere between  0 and [processingTime]
    private final double successProbability = 0.43;
    private final double warningsProbability = 0.32; // If a task is successful, what are the odds for a warning?

    private final String compName = "__DEFAULT_CONFIG__COMPILATION__";
    private final String sourcePath = "C:/Temp/XOO/src/";
    private final String outPath = "C:/Temp/XOO/out/";

    @Override
    public void DoAction() {
        { setTaskConfigDefault(); }
    }

    private void setTaskConfigDefault() {
        Configuration configToCheck = TaskSubMenu.getTaskType() == TaskType.COMPILATION ? defaultConfigComp : defaultConfigSim;

        if (configToCheck == null) {
            loadNew();
        } else {
            setActive(configToCheck);
        }
    }

    private void setActive(Configuration configToCheck) {
        if (engine.setActiveConfig(configToCheck.getTaskType(), configToCheck.getName())) {
            printLoaded(configToCheck);
        }
    }

    private void loadNew() {
        try {
            Configuration config = loadNewConfig();

            if (engine.addConfigAndSetActive(config.getTaskType(), config)) {
                printLoaded(config);
            } else {
                System.out.println("Sorry, configuration could not be loaded for unknown reason :(");
            }
        } catch (NonexistentElementException | InvalidInputRangeException | NameNotFoundException e) {
            System.out.println("Whoops, something went wrong with default configuration!");
            e.printStackTrace();
        }
    }

    private Configuration loadNewConfig() throws NonexistentElementException, InvalidInputRangeException, NameNotFoundException {
        Configuration config = null;

        switch (TaskSubMenu.getTaskType()) {
            case COMPILATION:
                config = getDefaultComp();
                break;
            case SIMULATION:
                config = getDefaultSim();
                break;
        }

        return config;
    }

    private Configuration getDefaultComp() throws NonexistentElementException {
        ConfigurationCompilation defaultConfigComp = null;
        try {
            defaultConfigComp = new ConfigurationCompilation(
                    compName, defaultParallelism, sourcePath, outPath);
        } catch (InvalidInputRangeException | NameNotFoundException e) {
            e.printStackTrace();
        }

        return defaultConfigComp;
    }

    private Configuration getDefaultSim() throws InvalidInputRangeException, NameNotFoundException {
        return new ConfigurationSimulation(
                simName, defaultParallelism, successProbability, processingTime, randomProcessingTime, warningsProbability);
    }

    private void printLoaded(Configuration config) {
        System.out.println("Default parameters loaded.\n");
        ConfigUtils.printConfig(config.getData());
        System.out.println();
    }
}