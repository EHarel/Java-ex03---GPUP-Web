package task;

import datastructure.QueueStateUpdate;
import exception.InvalidInputRangeException;
import exception.NoConfigurationException;
import graph.DependenciesGraph;
import graph.Target;
import graph.TargetDTO;
import logic.Engine;
import task.configuration.Configuration;
import task.configuration.ConfigurationDTO;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class TaskProcess implements Serializable {
    public enum StartPoint implements Serializable {
        FROM_SCRATCH, INCREMENTAL
    }

    private static final long serialVersionUID = 10; // 09-Dec-2021 - Added task manager


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ DATA MEMBERS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    protected boolean taskComplete = false;
    protected final TaskType type;
    protected final DependenciesGraph originalGraphDuplicate;
    protected List<Execution> executionHistory;
    protected Configuration activeConfiguration;
    protected Configuration lastUsedConfiguration;
    protected List<Configuration> configurations;
    protected boolean rememberConfigurationBetweenExecutions = false;
    protected TaskManager taskManager;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- CONSTRUCTOR -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public TaskProcess(TaskType type, DependenciesGraph graph, Configuration config) {
        this.type = type;
        this.originalGraphDuplicate = graph.duplicate();
        this.executionHistory = new LinkedList<>();
        this.activeConfiguration = null;
        this.configurations = new LinkedList<>();
        this.addConfigAndSetActive(config);
        this.taskManager = Engine.getInstance().getTaskManager();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- SETTERS AND GETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public TaskType getType() {
        return type;
    }

    public int getConfigCount() {
        return configurations.size();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------------- CONFIGURATIONS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public boolean removeConfiguration(String configName) {
        boolean removed = false;

        for (Configuration currConfig : configurations) {
            if (currConfig.getName().equals(configName)) {
                configurations.remove(currConfig);

                if (activeConfiguration == currConfig) {
                    activeConfiguration = getLastConfig();
                }

                removed = true;
            }
        }

        return removed;
    }

    protected Configuration getLastConfig() {
        Configuration config = null;

        if (configurations.size() > 0) {
            config = configurations.get(configurations.size() - 1);
        }

        return config;
    }

    public boolean activeConfig_UpdateThreadCount(int threadNum) throws InvalidInputRangeException {
        boolean updated = false;

        if (activeConfiguration != null) {
            activeConfiguration.setNumberOfThreads(threadNum);
            updated = true;
        }

        return updated;
    }

    public void activeConfig_UpdateParticipatingTargets(Collection<String> participatingTargetNames) {
        if (activeConfiguration != null) {
            activeConfiguration.setParticipatingTargets(participatingTargetNames);
        }
    }

    public void removeConfigurationAll() {
        configurations.clear();
        activeConfiguration = null;
    }

    public boolean addConfigAndSetActive(Configuration newConfig) {
        boolean added = false;

        if (newConfig != null) {
            if (addConfiguration(newConfig)) {
                if (setActiveConfiguration(newConfig.getName())) {
                    added = true;
                }
            }
            setActiveConfiguration(newConfig.getName());
        }

        return added;
    }

    /**
     * @param newConfig configuration to add.
     * @return true if added, false otherwise. Configuration names must be unique -- if there is already an existing configuration
     * by newConfig's name, newConfig won't be added.
     */
    public boolean addConfiguration(Configuration newConfig) {
        boolean added = false;
        boolean nameConflict = false;

        if (newConfig != null) {
            // Check that no configuration by such name exists
            for (Configuration configuration : configurations) {
                if (configuration.getName().equals(newConfig.getName())) {
                    nameConflict = true;
                    break;
                }
            }

            if (!nameConflict) {
                configurations.add(newConfig);
                added = true;
                if (activeConfiguration == null) {
                    activeConfiguration = newConfig;
                }
            }
        }

        return added;
    }

    public ConfigurationDTO getConfigurationDataActiveOnly() {
        ConfigurationDTO configData = null;

        if (activeConfiguration != null) {
            configData = activeConfiguration.toDTO();
        } else if (rememberConfigurationBetweenExecutions && lastUsedConfiguration != null) {
            configData = lastUsedConfiguration.toDTO();
        }

        return configData;
    }

    public Configuration getConfigurationActive() {
        return activeConfiguration;
    }

    public ConfigurationDTO getSpecificConfig(String configName) {
        ConfigurationDTO configData = null;

        if (configName != null) {
            for (Configuration config : configurations) {
                if (config.getName().equalsIgnoreCase(configName)) {
                    configData = config.toDTO();
                    break;
                }
            }
        }

        return configData;
    }

    public Collection<ConfigurationDTO> getConfigurationDataAll() {
        Collection<ConfigurationDTO> configData = new LinkedList<>();

        for (Configuration config : configurations) {
            configData.add(config.toDTO());
        }

        return configData;
    }

    /**
     * Sets a new active configuration.
     *
     * @param name configurations are unique by name. Parameter determines which configuration to add.
     * @return true if managed to switch, false if no such configuration was found.
     */
    public boolean setActiveConfiguration(String name) {
        boolean switched = false;

        for (Configuration configuration : configurations) {
            if (configuration.getName().equals(name)) {
                activeConfiguration = configuration;
                lastUsedConfiguration = activeConfiguration;
                switched = true;
                break;
            }
        }

        return switched;
    }

    public boolean isRememberConfigurationBetweenExecutions() {
        return rememberConfigurationBetweenExecutions;
    }

    public void setRememberConfigurationBetweenExecutions(boolean rememberConfigurationBetweenExecutions) {
        this.rememberConfigurationBetweenExecutions = rememberConfigurationBetweenExecutions;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- GET REPORTS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public List<Execution> getAllReports() {
        return executionHistory;
    }

    public int getExecutionCount() {
        return executionHistory.size();
    }

    /**
     * @return null if there have been no executions thus far.
     */
    public Execution getLastExecution() {
        Execution execution = null;

        if (executionHistory.size() > 0) {
            execution = executionHistory.get(executionHistory.size() - 1);
        }

        return execution;
    }

    public Execution getReportAtIndex(int i) throws IndexOutOfBoundsException {
        return executionHistory.get(i);
    }

    public Execution getReportByNumber(int executionNumber) {
        Execution execution = null;

        for (Execution exeData : executionHistory) {
            if (exeData.getExecutionNumber() == executionNumber) {
                execution = exeData;
                break;
            }
        }

        return execution;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------- EXECUTION --------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void execute(StartPoint startPoint, Collection<String> chosenTargetsForExecution) throws NoConfigurationException {
        if (!executionValidityChecks(startPoint)) {
            return;
        }
        activeConfiguration.setParticipatingTargets(chosenTargetsForExecution);
        Execution execution = createNextExecution(startPoint);
        prepareThreads(execution);
        startProcessingTargets(execution.getEndGraph(), execution);
        executionHistory.add(execution);
        activeConfiguration = rememberConfigurationBetweenExecutions ? activeConfiguration : null;
        checkIfTaskCompleted(execution);
        execution.setEndInstant(Instant.now());
    }

    private void prepareThreads(Execution execution) {
        int threadsForRun = activeConfiguration.getNumberOfThreads();

        taskManager.getThreadManager().prepareForNewExecution(threadsForRun, execution);
    }

    private Execution createNextExecution(StartPoint startPoint) {
        Instant startInstant = Instant.now();
        DependenciesGraph startGraph = getLastGraphForExecutionDuplicated(startPoint);
        startGraph = getGraphWithParticipatingTargets(startGraph);
        // updateParticipatingTargets(startGraph);
        DependenciesGraph endGraph = startGraph.duplicate();
        endGraph.setName("End Graph -- Execution " + (getNextExecutionNum(startPoint)));

        Execution.ProcessedData lastProcessedData = null;
        if (getLastExecution() != null) {
            lastProcessedData = getLastExecution().getProcessedData();
        }

        int nextExecutionNum = getNextExecutionNum(startPoint);
        Execution execution = new Execution(this.type, nextExecutionNum, startGraph, activeConfiguration, lastProcessedData);
        execution.setStartPoint(startPoint);
        execution.setStartInstant(startInstant);
        execution.setEndGraph(endGraph);

        return execution;
    }

//    private void updateParticipatingTargets(DependenciesGraph graph) {
//        if (activeConfiguration.areAllTargetsParticipating()) {
//            graph.setAllTargetsParticipating();
//        } else {
//            graph.setParticipatingTargets(activeConfiguration.getParticipatingTargetsNames());
//        }
//    }

    private DependenciesGraph getGraphWithParticipatingTargets(DependenciesGraph startGraph) {
        Collection<String> participatingTargetNames = activeConfiguration.getParticipatingTargetsNames();
        DependenciesGraph returnGraph = startGraph;

        if (participatingTargetNames != null && !participatingTargetNames.isEmpty()) {
            if (areParticipatingTargetsLessThanWholeGraph(returnGraph, participatingTargetNames)) {
                returnGraph = createGraphFromParticipatingTargets(returnGraph, participatingTargetNames);
            }
        }

        return returnGraph;
    }

    private boolean areParticipatingTargetsLessThanWholeGraph(DependenciesGraph graph, Collection<String> participatingTargetNames) {
        boolean lessThanWholeGraph = false;
        Collection<TargetDTO> allGraphTargets = graph.getAllTargetData();

        if (allGraphTargets != null && participatingTargetNames != null) {

            for (TargetDTO graphTarget : allGraphTargets) {
                boolean graphTargetMentionedAmongParticipating = false;

                for (String participatingTargetName : participatingTargetNames) {
                    graphTargetMentionedAmongParticipating = graphTarget.getName().equals(participatingTargetName);
                    if (graphTargetMentionedAmongParticipating) {
                        break;
                    }
                }

                if (!graphTargetMentionedAmongParticipating) {
                    lessThanWholeGraph = true;
                    break;
                }
            }
        }

        return lessThanWholeGraph;
    }

    private DependenciesGraph createGraphFromParticipatingTargets(DependenciesGraph graph, Collection<String> participatingTargetNames) {
        return graph.DuplicateChosenOnly(participatingTargetNames);
    }

    private DependenciesGraph getLastGraphForExecutionDuplicated(StartPoint startPoint) {
        DependenciesGraph resGraph;

        if (startPoint == StartPoint.FROM_SCRATCH || executionHistory.size() == 0) {
            resGraph = originalGraphDuplicate.duplicate();
            taskComplete = false;
        } else {
            Execution lastExecution = executionHistory.get(executionHistory.size() - 1);
            resGraph = lastExecution.getEndGraph().duplicate();
        }

        resGraph.setName("Start Graph -- Execution " + getNextExecutionNum(startPoint));
        resGraph.resetState(getNextExecutionNum(startPoint), activeConfiguration);

        return resGraph;
    }

    private int getNextExecutionNum(StartPoint startPoint) {
        int nextExecutionNum;

        if (startPoint == StartPoint.FROM_SCRATCH) {
            nextExecutionNum = 1;
        } else {
            nextExecutionNum = executionHistory.size() + 1;
        }

        return nextExecutionNum;
    }

    private void checkIfTaskCompleted(Execution execution) {
        taskComplete = determineTaskComplete(execution.getEndGraph());
        execution.setTaskComplete(taskComplete);
    }

    private void startProcessingTargets(DependenciesGraph workingGraph, Execution execution) {
        QueueStateUpdate queue = getInitialTasks(workingGraph, execution);

        this.taskManager.getConsumerManager().getStartTargetProcessingConsumers().forEach(dependenciesGraphConsumer -> dependenciesGraphConsumer.accept(workingGraph));
        while (!queue.isEmpty()) {
            taskManager.getThreadManager().execute(queue.dequeue());
        }
    }

    private QueueStateUpdate getInitialTasks(DependenciesGraph workingGraph, Execution execution) {
        QueueStateUpdate queue = new QueueStateUpdate(this.taskManager.getConsumerManager());

        for (Target target : workingGraph.targets()) {
            if (targetCanBeProcessed(target)) {
                queue.enqueue(TaskFactory.getActualTask(
                        type,
                        target,
                        activeConfiguration,
                        workingGraph,
                        execution));
                target.setTimeOfEntryIntoTaskQueue(Instant.now());
            }
        }

        return queue;
    }

    private boolean determineTaskComplete(DependenciesGraph workingGraph) {
        boolean complete = true;

        for (Target target : workingGraph.targets()) {
            if (target.getTaskStatus().getTaskResult() == TargetDTO.TaskStatusDTO.TaskResult.FAILURE
                    || target.getTaskStatus().getTaskResult() == TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED) {
                complete = false;
                break;
            }
        }

        return complete;
    }

    private boolean executionValidityChecks(StartPoint startPoint) throws NoConfigurationException {
        boolean valid = true;

        taskManager = Engine.getInstance().getTaskManager();
        checkValidActiveConfig();
        if (startPoint == StartPoint.INCREMENTAL && taskComplete) {
            valid = false;
        }

        return valid;
    }

    protected void checkValidActiveConfig() throws NoConfigurationException {
        if (activeConfiguration == null) {
            if (rememberConfigurationBetweenExecutions && lastUsedConfiguration != null) {
                activeConfiguration = lastUsedConfiguration;
            } else {
                throw new NoConfigurationException("ERROR! No active configuration set.");
            }
        }
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ TARGET CHECKS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public static boolean targetCanBeProcessed(Target target) {
//        if (!target.isParticipatesInExecution()) {
//            return false;
//        }

        if (target.getTaskStatus().getTargetState() == TargetDTO.TargetState.SKIPPED) {
            return false;
        }

        if (!targetStateAllowsInsertion(target)) {
            return false;
        }

        if (!targetDependenciesAllowProcessing(target)) {
            return false;
        }

        return true;
    }

    private static boolean targetDependenciesAllowProcessing(Target target) {
        boolean dependenciesAllow = true;

        for (Target dependencyTarget : target.getTargetsThisIsDependentOn()) {
            if (!targetSucceededWithOrWithoutWarnings(dependencyTarget)) {
                dependenciesAllow = false;
                break;
            }
        }

        return dependenciesAllow;
    }

    public static boolean targetSucceededWithOrWithoutWarnings(Target target) {
        boolean succeededWithOrWithoutWarnings = false;

        if (target.getTaskStatus().getTargetState() == TargetDTO.TargetState.FINISHED) {
            switch (target.getTaskStatus().getTaskResult()) {
                case SUCCESS_WITH_WARNINGS:
                case SUCCESS:
                    succeededWithOrWithoutWarnings = true;
                    break;
            }
        }

        return succeededWithOrWithoutWarnings;
    }

    /**
     * Checks if the target can be processed, solely on its current state.
     * This does not check for neighbors' state. That needs to be done separately.
     * Note that WAITING state means target is already in datastructure.Queue, and thus returns false, because it cannot be inserted again.
     *
     * @param target target to analyze.
     * @return true if target can be inserted.
     */
    private static boolean targetStateAllowsInsertion(Target target) {
        boolean allowsInsertion = false;

        switch (target.getTaskStatus().getTargetState()) {
            case IN_PROCESS:
            case WAITING:
                allowsInsertion = false;
                break;
            case FINISHED:
                switch (target.getTaskStatus().getTaskResult()) {
                    case SUCCESS:
                    case SUCCESS_WITH_WARNINGS:
                        allowsInsertion = false;
                        break;
                    case FAILURE:
                        allowsInsertion = true;
                        break;
                }
                break;
            default:
                allowsInsertion = true;
                break;
        }

        return allowsInsertion;
    }
}