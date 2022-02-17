package task;

import exception.*;
import graph.DependenciesGraph;
import task.configuration.Configuration;
import task.configuration.ConfigurationData;
import task.consumer.ConsumerManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class TaskManager implements Serializable {
    private static final long serialVersionUID = 5; // 09-Dec-2021 - removed static scope (messes up serialization)
    private static int defaultMaxParallelism = 1;

    private ConsumerManager consumerManager;
    private ThreadManager threadManager;
    private Map<TaskType, TaskProcess> tasks;
    DependenciesGraph dependenciesGraph;

    public TaskManager(DependenciesGraph dependenciesGraph) {
        this(dependenciesGraph, defaultMaxParallelism);
    }

    public TaskManager(DependenciesGraph dependenciesGraph, int maxParallelism) {
        tasks = TaskFactory.init(dependenciesGraph);
        this.dependenciesGraph = dependenciesGraph;

        consumerManager = new ConsumerManager();
        threadManager = new ThreadManager(maxParallelism);
    }

    public int getMaxParallelism() {
        return threadManager.getMaxParallelism();
    }

    /**
     *
     * @param taskType what kind of task (SIMULATION, COMPILATION, etc.)
     * @param startPoint From scratch or incremental.
     * @param chosenTargetsNamesForExecution
     * @throws NoConfigurationException if no configuration has been set for the task, the execution may not begin.
     */
    public void executeTask(TaskType taskType,
                            TaskProcess.StartPoint startPoint, ConsumerManager consumerManager, Collection<String> chosenTargetsNamesForExecution)
            throws NoConfigurationException {
        this.consumerManager = consumerManager;
        tasks.get(taskType).execute(startPoint, chosenTargetsNamesForExecution);
    }

    public ConsumerManager getConsumerManager() {
        return consumerManager;
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public boolean addConfigAndSetActive(TaskType taskType, Configuration newConfig) {
        return tasks.get(taskType).addConfigAndSetActive(newConfig);
    }

    public boolean setActiveConfig(TaskType taskType, String name) {
        return tasks.get(taskType).setActiveConfiguration(name);
    }

    public boolean activeConfig_UpdateThreadCount(TaskType taskType, int threadNum) throws InvalidInputRangeException {
        return tasks.get(taskType).activeConfig_UpdateThreadCount(threadNum);
    }

    public boolean addConfiguration(TaskType taskType, Configuration configuration) {
        return tasks.get(taskType).addConfiguration(configuration);
    }

    public boolean removeConfiguration(TaskType taskType, String configName) {
        return tasks.get(taskType).removeConfiguration(configName);
    }

    public void removeConfigurationAll(TaskType taskType) {
        tasks.get(taskType).removeConfigurationAll();
    }

    public ConfigurationData getConfigActive(TaskType taskType) {
        return tasks.get(taskType).getConfigurationDataActiveOnly();
    }

    public Configuration getActiveConfigNotData(TaskType taskType) {
        return tasks.get(taskType).getConfigurationActive();
    }

    public Collection<ConfigurationData> getAllConfigurations(TaskType taskType) {
        return tasks.get(taskType).getConfigurationDataAll();
    }

    public ConfigurationData getSpecificConfig(TaskType taskType, String configName) {
        return tasks.get(taskType).getSpecificConfig(configName);
    }

    public int getExecutionCount(TaskType taskType) {
        return tasks.get(taskType).getExecutionCount();
    }

    public int getConfigCount(TaskType taskType) {
        return tasks.get(taskType).getConfigCount();
    }

    public ExecutionData getExecutionReportLast(TaskType taskType) {
        return tasks.get(taskType).getLastExecution();
    }

    public ExecutionData getExecutionReportIndex(TaskType taskType, int i) throws IndexOutOfBoundsException {
        return tasks.get(taskType).getReportAtIndex(i);
    }

    public ExecutionData getExecutionByExeNumber(TaskType taskType, int executionNumber) {
        return tasks.get(taskType).getReportByNumber(executionNumber);
    }

    public void toggleRememberLastConfiguration(TaskType taskType) {
        boolean currentState = tasks.get(taskType).isRememberConfigurationBetweenExecutions();

        tasks.get(taskType).setRememberConfigurationBetweenExecutions(!currentState);
    }

    public boolean isPersistentConfiguration(TaskType taskType) {
        return tasks.get(taskType).isRememberConfigurationBetweenExecutions();
    }

    public void pause(boolean isPaused) {
        threadManager.pause(isPaused);
    }

    public int getCurrentParallelism() {
        return threadManager.getCurrentParallelism();
    }

    public void setCurrentParallelism(int threadCount) {
        threadManager.setCurrentParallelism(threadCount);
    }

    public void activeConfig_UpdateParticipatingTargets(TaskType taskType, Collection<String> participatingTargetNames) {
        tasks.get(taskType).activeConfig_UpdateParticipatingTargets(participatingTargetNames);
    }
}