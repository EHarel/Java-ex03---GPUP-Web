package task.configuration;

import task.TaskType;

import java.io.Serializable;

public class ConfigurationDataSimulation extends ConfigurationData implements Serializable {
    private static final long serialVersionUID = 1; // 18-Nov-2021

    private final int processingTime; // An integer given in milliseconds (so 1500 is invalid)
    private final boolean randomProcessingTime; // random means the time is somewhere between  0 and [processingTime]
    private final double successProbability;
    private final double warningsProbability; // If a task is successful, what are the odds for a warning?

    public ConfigurationDataSimulation(String name, int threadCount, double successProbability, int processingTime, boolean randomProcessingTime, double warningsProbability) {
        super(TaskType.SIMULATION, name, threadCount);

        this.successProbability = successProbability;
        this.processingTime = processingTime;
        this.randomProcessingTime = randomProcessingTime;
        this.warningsProbability = warningsProbability;
    }

    public double getSuccessProbability() {
        return successProbability;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public boolean isRandomProcessingTime() {
        return randomProcessingTime;
    }

    public double getWarningsProbability() {
        return warningsProbability;
    }

    @Override
    public ConfigurationDataSimulation clone() {
        return new ConfigurationDataSimulation(
                this.name,
                this.threadCount,
                this.successProbability,
                this.processingTime,
                this.randomProcessingTime,
                this.warningsProbability
        );
    }
}