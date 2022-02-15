package task.configuration;

import task.TaskType;

import java.io.Serializable;

public abstract class ConfigurationData implements Serializable, Cloneable {
//    private static final long serialVersionUID = 2; // 21-Nov-2021
    private static final long serialVersionUID = 3; // 06-Dec-2021


    protected final TaskType taskType;
    protected final String name;
    protected final int threadCount;

    public ConfigurationData(TaskType taskType,String name, int threadCount) {
        this.taskType = taskType;
        this.name = name;
        this.threadCount = threadCount;
    }

    public String getName() { return name; }

    public TaskType getTaskType() {
        return taskType;
    }


    public int getThreadCount() {
        return threadCount;
    }

    @Override
    public abstract ConfigurationData clone();
}
