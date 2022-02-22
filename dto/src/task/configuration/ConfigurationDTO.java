package task.configuration;

import task.enums.TaskType;

public abstract class ConfigurationDTO implements Cloneable {
    protected TaskType taskType;
    protected String name;
    protected int threadCount;

    public ConfigurationDTO() {}

    public ConfigurationDTO(TaskType taskType, String name, int threadCount) {
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

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public abstract ConfigurationDTO clone();
}
