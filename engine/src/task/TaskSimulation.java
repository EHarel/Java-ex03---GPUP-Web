package task;

import com.sun.istack.internal.NotNull;
import graph.*;
import task.configuration.Configuration;
import task.configuration.ConfigurationSimulation;

import java.util.Random;


public class TaskSimulation extends Task {
    private static final int minSleepTime = 0;

    public TaskSimulation(@NotNull Target target,
                          @NotNull Configuration configuration,
                          DependenciesGraph workingGraph,
                          ExecutionData executionData)
            throws IllegalArgumentException {
        super(TaskType.SIMULATION, target, configuration, workingGraph, executionData);
    }

    @Override
    protected TargetDTO.TaskStatusDTO.TaskResult runActualTask() {
        return runSimulation();
    }

    private TargetDTO.TaskStatusDTO.TaskResult runSimulation() {
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

    private TargetDTO.TaskStatusDTO.TaskResult getSimulationResult() {
        boolean isSuccessful = isSuccessful();
        boolean isWithWarnings = isWithWarnings(isSuccessful);

        TargetDTO.TaskStatusDTO.TaskResult taskResult;
        if (isSuccessful) {
            taskResult = isWithWarnings? TargetDTO.TaskStatusDTO.TaskResult.SUCCESS_WITH_WARNINGS : TargetDTO.TaskStatusDTO.TaskResult.SUCCESS;
        } else {
            taskResult = TargetDTO.TaskStatusDTO.TaskResult.FAILURE;
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