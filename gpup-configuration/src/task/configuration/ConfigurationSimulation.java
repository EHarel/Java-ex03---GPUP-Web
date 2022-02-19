package task.configuration;

import exception.InvalidInputRangeException;
import task.TaskType;

import javax.naming.NameNotFoundException;
import java.io.Serializable;

public class ConfigurationSimulation extends Configuration implements Serializable, Cloneable {
    //    private static final long serialVersionUID = 1; // 18-Nov-2021
    private static final long serialVersionUID = 2; // 24-Nov-2021

    private int processingTime; // An integer given in milliseconds (so 1500 is invalid)
    private boolean randomProcessingTime; // random means the time is somewhere between  0 and [processingTime]
    private double successProbability;
    private double warningsProbability; // If a task is successful, what are the odds for a warning?

    public ConfigurationSimulation(String name, int numberOfThreads, double successProbability, int processingTime, boolean randomProcessingTime, double warningsProbability) throws InvalidInputRangeException, NameNotFoundException {
        super(TaskType.SIMULATION, name, numberOfThreads);
        update(successProbability, processingTime, randomProcessingTime, warningsProbability);
    }

    public ConfigurationSimulation(ConfigurationDTOSimulation configData) throws NameNotFoundException, InvalidInputRangeException {
        super(TaskType.SIMULATION, configData.getName(), configData.threadCount);
        update(configData);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- SETTERS AND GETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void update(ConfigurationDTOSimulation configData) {
        try {
            setProcessingTime(configData.getProcessingTime());
            setRandomProcessingTime(configData.isRandomProcessingTime());
            setSuccessProbability(configData.getSuccessProbability());
            setWarningsProbability(configData.getWarningsProbability());
        } catch (Exception ignore) {
        }
    }

    private void update(double successProbability, int processingTime, boolean randomProcessingTime, double warningsProbability)
            throws InvalidInputRangeException {
        setProcessingTime(processingTime);
        setRandomProcessingTime(randomProcessingTime);
        setSuccessProbability(successProbability);
        setWarningsProbability(warningsProbability);
    }

    public void update(ConfigurationSimulation config) throws InvalidInputRangeException {
        setProcessingTime(config.getProcessingTime());
        setRandomProcessingTime(config.isRandomProcessingTime());
        setSuccessProbability(config.getSuccessProbability());
        setWarningsProbability(config.getWarningsProbability());
    }

    public ConfigurationDTOSimulation toDTO() {
        return new ConfigurationDTOSimulation(name, numberOfThreads, successProbability, processingTime, randomProcessingTime, warningsProbability);
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public boolean isRandomProcessingTime() {
        return randomProcessingTime;
    }

    public double getSuccessProbability() {
        return successProbability;
    }

    public double getWarningsProbability() {
        return warningsProbability;
    }

    /**
     * @param processingTime a milliseconds value representing an INTEGER! Meaning while numbers only.
     * @throws InvalidInputRangeException if the value doesn't represent an integer.
     */
    public void setProcessingTime(int processingTime) throws InvalidInputRangeException {
        if (isIntegerInMilliseconds(processingTime)) {
            this.processingTime = processingTime;
        } else {
            throw new InvalidInputRangeException("When assigning processing time, value must be an integer given in milliseconds! (Such as 3000, not 3200)");
        }
    }

    public void setRandomProcessingTime(boolean randomProcessingTime) {
        this.randomProcessingTime = randomProcessingTime;
    }

    public void setSuccessProbability(double successProbability) throws InvalidInputRangeException {
        if (isProbabilityValue(successProbability)) {
            this.successProbability = successProbability;
        } else {
            throw new InvalidInputRangeException("Value must be between (and including) 0 and 1 only!");
        }
    }

    public void setWarningsProbability(double warningsProbability) throws InvalidInputRangeException {
        if (isProbabilityValue(warningsProbability)) {
            this.warningsProbability = warningsProbability;
        } else {
            throw new InvalidInputRangeException("Value must be between (and including) 0 and 1 only!");
        }
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------------- VALIDITY CHECKS ------------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private static boolean isIntegerInMilliseconds(int value) {
        return (value % 1000 == 0);
    }

    private static boolean isProbabilityValue(double value) {
        return (value >= 0 && value <= 1);
    }

    public static boolean isValidProcessingTime(int processingTimeCandidate) {
        return isIntegerInMilliseconds(processingTimeCandidate);
    }

    public static boolean isValidSuccessProbability(double successProbabilityCandidate) {
        return isProbabilityValue(successProbabilityCandidate);
    }

    public static boolean isValidWarningsProbability(double warningsProbabilityCandidate) {
        return isProbabilityValue(warningsProbabilityCandidate);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------------- UTILITY METHODS ------------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public ConfigurationSimulation clone() {
        ConfigurationSimulation simConfig = null;
        try {
            simConfig = new ConfigurationSimulation(
                    this.name,
                    this.numberOfThreads,
                    this.successProbability,
                    this.processingTime,
                    this.randomProcessingTime,
                    this.warningsProbability
            );
        } catch (InvalidInputRangeException | NameNotFoundException ignore) {
        }

        return simConfig;
    }
}
