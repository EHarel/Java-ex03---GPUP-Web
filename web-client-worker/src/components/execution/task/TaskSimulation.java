package components.execution.task;

import com.sun.istack.internal.NotNull;
import components.app.AppMainController;
import components.execution.ExecutionManager;
import graph.*;
import task.enums.TaskResult;
import task.enums.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationSimulation;

import java.util.Random;


public class TaskSimulation extends Task {
    private static final int minSleepTime = 0;

    public TaskSimulation(@NotNull Target target,
                          @NotNull Configuration configuration,
                          ExecutionManager executionManager,
                          AppMainController mainController)
            throws IllegalArgumentException {
        super(TaskType.SIMULATION, target, configuration, executionManager, mainController);
    }

    @Override
    protected TaskResult runActualTask() {
        return runSimulation();
    }

    private TaskResult runSimulation() {
        simulateSleep();

        return getSimulationResult();
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- TASK METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void simulateSleep() {
        int sleepTime = getSleepTime();

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignored) {

        }
    }

    private int getSleepTime() {
        ConfigurationSimulation config = (ConfigurationSimulation) configuration;
        int sleepTime = config.getProcessingTime();

        if (config.isRandomProcessingTime()) {
            sleepTime = getRandomInRange(minSleepTime, sleepTime);
        }

        return sleepTime;
    }

    private TaskResult getSimulationResult() {
        boolean isSuccessful = isSuccessful();
        boolean isWithWarnings = isWithWarnings(isSuccessful);

        TaskResult taskResult;
        if (isSuccessful) {
            taskResult = isWithWarnings? TaskResult.SUCCESS_WITH_WARNINGS : TaskResult.SUCCESS;
        } else {
            taskResult = TaskResult.FAILURE;
        }

        return taskResult;
    }

    public boolean isSuccessful() {
        ConfigurationSimulation config = (ConfigurationSimulation) configuration;

        return randomProbability() <= config.getSuccessProbability();
    }


    public boolean isWithWarnings(boolean isSuccessful) {
        ConfigurationSimulation config = (ConfigurationSimulation) configuration;
        boolean res = false;

        if (isSuccessful) {
            if (randomProbability() <= config.getWarningsProbability()) {
                res = true;
            }
        }

        return res;
    }

    private double randomProbability() {
        Random random = new Random();
        return random.nextDouble();
    }

    private int getRandomInRange(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}