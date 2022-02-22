package task.configuration;

import exception.InvalidInputRangeException;
import task.enums.TaskType;

import javax.naming.NameNotFoundException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

public abstract class Configuration implements Serializable {
//    private static final long serialVersionUID = 1;
//    private static final long serialVersionUID = 2; // 06-Dec-2021, parallelism
    private static final long serialVersionUID = 3; // 11-Dec-2021, affected targets


    protected final TaskType taskType;
    protected String name;
    protected int numberOfThreads;
    protected Collection<String> participatingTargetNames;

    public Configuration(TaskType taskType, String name, int numberOfThreads) throws InvalidInputRangeException, NameNotFoundException {
        this.taskType = taskType;

        if (name == null) {
            throw  new NameNotFoundException("Configuration must have a name");
        }
        this.name = name;
        setNumberOfThreads(numberOfThreads);
        this.participatingTargetNames = new LinkedList<>();
    }

    public Configuration(ConfigurationDTO configData) {
        this.taskType = configData.getTaskType();
        this.name = configData.getName();
        this.numberOfThreads = configData.getThreadCount();
        this.participatingTargetNames = getParticipatingTargetsNames();
    }

    public String getName() { return name; }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) throws InvalidInputRangeException {
        String errorMsg = "Thread number (" + numberOfThreads + ") ";

        if (numberOfThreads == 0) {
            throw new InvalidInputRangeException(errorMsg + "must be an integer above 0.");
        } else {
            this.numberOfThreads = numberOfThreads;
        }
    }

    public Collection<String> getParticipatingTargetsNames() {
        return participatingTargetNames;
    }

    public void setParticipatingTargets(Collection<String> participatingTargetsNames) {
        if (participatingTargetsNames != null) {
            this.participatingTargetNames = participatingTargetsNames;
        }
    }

    /**
     * @return true if all targets are participating in the execution, false otherwise.
     * False suggests user should check the participatingTargets collection.
     * This comes down to what the default definition is - does a null or empty collection indicate all targets
     * participate, or that none do? If the default is that a null\empty collection implies all targets, it certainly
     * makes things easier, because it doesn't require to constantly create new collections based on the graph's
     * current state of targets.
     */
    public boolean areAllTargetsParticipating() {
        return (participatingTargetNames == null || participatingTargetNames.size() == 0);
    }

    public abstract ConfigurationDTO toDTO();

    @Override
    public abstract Configuration clone();
}
