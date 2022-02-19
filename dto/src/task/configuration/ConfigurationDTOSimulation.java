package task.configuration;

import task.TaskType;

public class ConfigurationDTOSimulation extends ConfigurationDTO implements Cloneable {
    private int processingTime; // An integer given in milliseconds (so 1500 is invalid)
    private boolean randomProcessingTime; // random means the time is somewhere between  0 and [processingTime]
    private double successProbability;
    private double warningsProbability; // If a task is successful, what are the odds for a warning?

    public ConfigurationDTOSimulation() {}

    public ConfigurationDTOSimulation(String name, int threadCount, double successProbability, int processingTime, boolean randomProcessingTime, double warningsProbability) {
        super(TaskType.SIMULATION, name, threadCount);

        this.successProbability = successProbability;
        this.processingTime = processingTime;
        this.randomProcessingTime = randomProcessingTime;
        this.warningsProbability = warningsProbability;
    }

    public void setProcessingTime(int processingTime) {
        this.processingTime = processingTime;
    }

    public void setRandomProcessingTime(boolean randomProcessingTime) {
        this.randomProcessingTime = randomProcessingTime;
    }

    public void setSuccessProbability(double successProbability) {
        this.successProbability = successProbability;
    }

    public void setWarningsProbability(double warningsProbability) {
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
    public ConfigurationDTOSimulation clone() {
        return new ConfigurationDTOSimulation(
                this.name,
                this.threadCount,
                this.successProbability,
                this.processingTime,
                this.randomProcessingTime,
                this.warningsProbability
        );
    }
}