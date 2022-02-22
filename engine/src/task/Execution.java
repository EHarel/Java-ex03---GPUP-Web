package task;

import algorithm.DFS;
import datastructure.TaskQueueStateUpdate;
import graph.DependenciesGraph;
import graph.Target;
import graph.TargetDTO;
import logic.EngineUtils;
import task.OldCode.TaskFactory;
import task.configuration.*;
import task.consumer.ConsumerUpdateState;
import task.enums.TaskResult;
import task.enums.TaskStartPoint;
import task.enums.TaskType;
import task.execution.ExecutionDTO;
import task.enums.ExecutionStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class Execution implements Cloneable {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ DATA MEMBERS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    protected String executionName;
    private final String creatingUser;
    protected final TaskType taskType;
    protected TaskStartPoint taskStartPoint;
    protected final int executionNumber;
    private final DependenciesGraph startingGraph;
    private DependenciesGraph endGraph;
    private Configuration configuration;
    private ProcessedData processedData;
    protected boolean taskComplete;
    private Instant startInstant;
    private Instant endInstant;
    private final Integer pricePerTarget;
    private ExecutionStatus executionStatus;
    private Collection<String> participatingWorkerNames;

    private boolean startedRunning = false;
//    TaskQueueStateUpdate queue;

    private Queue<Target> targetQueue;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- CONSTRUCTOR -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Execution(
            TaskType taskType,
            int executionNumber,
            DependenciesGraph startingGraph,
            Configuration configuration,
            ProcessedData previousProcessedData) {
        this.taskType = taskType;
        this.creatingUser = "Old code";
        this.executionNumber = executionNumber;
        this.startingGraph = startingGraph;
        this.configuration = configuration;
        this.processedData = new ProcessedData(previousProcessedData);
        this.pricePerTarget = null;
        this.targetQueue = new LinkedList<>();
    }

    public Execution(ExecutionDTO executionDTO) {
        this.executionName = executionDTO.getExecutionName();
        this.creatingUser = executionDTO.getCreatingUser();
        this.taskType = executionDTO.getTaskType();
        this.taskStartPoint = executionDTO.getTaskStartPoint();
        this.executionNumber = 0;
        try {
            switch (taskType) {
                case COMPILATION:
                    this.configuration = new ConfigurationCompilation((ConfigurationDTOCompilation) executionDTO.getConfigDTO());
                    break;
                case SIMULATION:
                    this.configuration = new ConfigurationSimulation((ConfigurationDTOSimulation) executionDTO.getConfigDTO());
                    break;
            }
        } catch (Exception ignore) {
        }
        this.startingGraph = new DependenciesGraph(executionDTO.getGraphDTO());

        this.startingGraph.setExecutionNameForGraphAndTargets(this.executionName);

        this.processedData = new ProcessedData(null);
        this.taskComplete = false; // TODO: should this be taken from the DTO as well?
        this.pricePerTarget = executionDTO.getPricePerTarget();
        this.executionStatus = executionDTO.getExecutionStatus();
        this.participatingWorkerNames = new ArrayList<>();
        executionDTO.getParticipatingUsersNames().forEach(s -> {
            this.participatingWorkerNames.add(s);
        });

        this.startingGraph.setConfigurationForTargets(configuration);
        this.targetQueue = new LinkedList<>();
    }


    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public String getCreatingUser() {
        return creatingUser;
    }

    public TaskType getTaskType() {
        return taskType;
    }


    public TaskStartPoint getTaskStartPoint() {
        return taskStartPoint;
    }

    public void setTaskStartPoint(TaskStartPoint taskStartPoint) {
        this.taskStartPoint = taskStartPoint;
    }

    public DependenciesGraph getStartingGraph() {
        return startingGraph;
    }

    public int getExecutionNumber() {
        return executionNumber;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

//    public void setStartPoint(TaskProcess.StartPoint startPoint) {
//        this.startPoint = startPoint;
//    }

    public void setTaskComplete(boolean taskComplete) {
        this.taskComplete = taskComplete;
    }

    public boolean getTaskComplete() {
        return this.taskComplete;
    }

    public DependenciesGraph getEndGraph() {
        return endGraph;
    }

    public void setEndGraph(DependenciesGraph endGraph) {
        this.endGraph = endGraph;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
    }

    public ProcessedData getProcessedData() {
        return processedData;
    }

    public void setProcessedData(ProcessedData processedData) {
        this.processedData = processedData;
    }

    public TargetDTO getSpecificTargetData(String targetName) {
        return this.processedData.getSpecificTargetData(targetName);
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public Collection<String> getParticipatingWorkerNames() {
        return participatingWorkerNames;
    }

    public synchronized void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;

        if (executionStatus == ExecutionStatus.EXECUTING) {
            if (!startedRunning) {
                // First run!
                startedRunning = true;
                startExecution();
            }
        }
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------- EXECUTION --------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private void startExecution() {
//        if (!executionValidityChecks(startPoint)) {
//            return;
//        }

        this.endGraph = this.startingGraph.duplicate();
        this.endGraph.setConfigurationForTargets(this.configuration);
        prepareQueue();

//        executionHistory.add(execution);
//        activeConfiguration = rememberConfigurationBetweenExecutions ? activeConfiguration : null;
//        checkIfTaskCompleted(execution);
//        execution.setEndInstant(Instant.now());
    }

    private void prepareQueue() {
        for (Target target : endGraph.targets()) {
            if (targetCanBeProcessed(target)) {
                addTargetToQueue(target);
            }
        }

//        this.taskManager.getConsumerManager().getStartTargetProcessingConsumers().forEach(dependenciesGraphConsumer -> dependenciesGraphConsumer.accept(workingGraph));
//        while (!queue.isEmpty()) {
//            taskManager.getThreadManager().execute(queue.dequeue());
//        }
    }

    public static boolean targetCanBeProcessed(Target target) {
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

    private void addTargetToQueue(Target target) {
        target.getTaskStatus().setTargetState(TargetDTO.TargetState.WAITING);
        target.setTimeOfEntryIntoTaskQueue(Instant.now());

        targetQueue.add(target);
    }

    public synchronized Target removeTargetFromQueue() {
        Target target = null;
//        Task task = queue.dequeue();
//
//        if (task != null) {
//            target = task.getTarget();
//        }
        target = targetQueue.poll();
        if (target != null) {
            target.setTimeOfExitOutOfTaskQueue(Instant.now());
        }

        return target;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------- TASK PROCESS CODE - EX03 ------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public synchronized boolean updateTargetTaskResult(String targetName, TaskResult taskResult) {
        Target target = endGraph.get(targetName);
        if (target == null) {
            return false;
        }

        // Code from task process:
        target.getTaskStatus().setTargetState(TargetDTO.TargetState.FINISHED);
        target.getTaskStatus().setTaskResult(taskResult);
        Collection<List<String>> skippedTargetsNames_AllPaths = getSkippedTargetsAllPaths(target);
        Collection<String> skippedTargetNames = getSkippedTargetNamesFromAllSkippedPathsTrimmed(skippedTargetsNames_AllPaths, target);

        target.getTaskStatus().setTargetsSkippedAsResult(skippedTargetNames);

        Collection<String> openedTargets = getOpenedTargets(target);
        target.getTaskStatus().setTargetsOpenedAsResult(openedTargets);

        addOpenedTargets(target);

        checkAndUpdateIfExecutionIsDone();

        return true;
    }

    private void checkAndUpdateIfExecutionIsDone() {
        boolean areTargetsLeftToProcess = areTargetsLeftToProcess();
        if (!areTargetsLeftToProcess) {
            setExecutionStatus(ExecutionStatus.ENDED);
        }
    }

    private boolean areTargetsLeftToProcess() {
        boolean foundTargetToProcess = false;

        for (Target target : endGraph.targets()) {
            switch (target.getTaskStatus().getTargetState()) {
                case WAITING:
                case FROZEN:
                    foundTargetToProcess = true;
                    break;
                case SKIPPED:
                case FINISHED:
                case IN_PROCESS:
            }

            if (foundTargetToProcess) {
                break;
            }
        }

        return foundTargetToProcess;
    }

    protected Collection<List<String>> getSkippedTargetsAllPaths(Target target) {
        Collection<List<String>> allSkippedTargetsString = new ArrayList<>();
        TaskResult result = target.getTaskStatus().getTaskResult();

        if (result == TaskResult.FAILURE) {
            Collection<Consumer<Target>> consumersForDFS = new LinkedList<>();
            consumersForDFS.add(new ConsumerUpdateState(TargetDTO.TargetState.SKIPPED));
            try {
                DFS dfs = new DFS(endGraph, target, null, DFS.EdgeDirection.REQUIRED_FOR, consumersForDFS);
                Collection<List<Target>> allSkippedTargetsPaths = dfs.run();
                allSkippedTargetsString = EngineUtils.pathsToNames(allSkippedTargetsPaths);

                Collection<Target> allSkippedTargets = EngineUtils.pathsToTargets_Exclude(allSkippedTargetsPaths, target);

//                for (Target skippedTarget : allSkippedTargets) {
//                    taskManager.getConsumerManager().getTargetStateChangedConsumers().forEach(targetDTOConsumer -> targetDTOConsumer.accept(skippedTarget.toDTO()));
//                    taskManager.getConsumerManager().getEndTargetConsumers().forEach(targetDTOConsumer -> targetDTOConsumer.accept(skippedTarget.toDTO()));
//                }
            } catch (Exception ignore) {
            } // From within a task we work only with existing targets, no room for user input error
        }

        return allSkippedTargetsString;
    }

    /**
     * This method returns a trimmed collection of all target names,
     * removing duplicates and the specific parameter target from the collection.
     */
    protected Collection<String> getSkippedTargetNamesFromAllSkippedPathsTrimmed(Collection<List<String>> skippedTargetsNames_allPaths, Target targetToIgnore) {
        Collection<String> skippedNames = new LinkedHashSet<>();

        for (Collection<String> path : skippedTargetsNames_allPaths) {
            for (String target : path) {
                if (!skippedNames.contains(target) && !target.equals(targetToIgnore.getName())) {
                    skippedNames.add(target);
                }
            }
        }

        return skippedNames;
    }

    protected Collection<String> getOpenedTargets(Target target) {
        Collection<String> openedTargets = new ArrayList<>();
        TaskResult result = target.getTaskStatus().getTaskResult();

        if (result != TaskResult.UNPROCESSED) {
            for (Target targetToCheck : target.getTargetsThisIsRequiredFor()) {
                if (targetHasOpened(targetToCheck)) {
                    openedTargets.add(targetToCheck.getName());
                }
            }
        }

        return openedTargets;
    }

    /**
     * A target is considered "opened" if all its dependencies have been processed,
     * regardless of if they've been processed successfully or not.
     * Meaning even a target whose all dependencies have been processed and all have failed will be considered "Opened".
     */
    private boolean targetHasOpened(Target target) {
        boolean hasOpened = true;

        for (Target dependentOnTarget : target.getTargetsThisIsDependentOn()) {
            switch (dependentOnTarget.getTaskStatus().getTaskResult()) {
                // case FAILURE: // Old version where I "Opened" only those whose all dependencies succeeded.
                case UNPROCESSED:
                    hasOpened = false;
                    break;
            }
        }

        return hasOpened;
    }

    /**
     * This method receives a target that has just finished being processed,
     * and adds all the targets that, as a result of its result, can now be
     * inserted into the queue and be processed as well.
     */
    private void addOpenedTargets(Target target) {
        if (targetSucceededWithOrWithoutWarnings(target)) {
            for (Target dependentOn : target.getTargetsThisIsRequiredFor()) {
                if (targetCanBeProcessed(dependentOn)) {
                    addTargetToQueue(dependentOn);
                }
            }
        }
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



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------- UTILITY ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public Execution clone() {
        Execution clone = new Execution(
                this.taskType,
                this.executionNumber,
                this.startingGraph.duplicate(),
                this.configuration.clone(),
                null
        );

//        clone.setStartPoint(this.startPoint); // Old ex01-ex02
        clone.setTaskComplete(this.taskComplete);
        clone.setEndGraph(this.endGraph.duplicate());
        clone.setStartInstant(this.startInstant);
        clone.setEndInstant(this.endInstant);
        clone.setProcessedData(this.processedData.clone());
        clone.setTaskStartPoint(this.taskStartPoint);

        return clone;
    }

    public ExecutionDTO toDTO() {
        ConfigurationDTOSimulation configSimDTO = null;
        ConfigurationDTOCompilation configCompDTO = null;

        switch (configuration.getTaskType()) {
            case SIMULATION:
                configSimDTO = (ConfigurationDTOSimulation) configuration.toDTO();
                break;
            case COMPILATION:
                configCompDTO = (ConfigurationDTOCompilation) configuration.toDTO();
                break;
        }

        ExecutionDTO executionDTO = new ExecutionDTO(
                this.executionName,
                this.creatingUser,
                // this.configuration.toDTO(),
                this.startingGraph.toDTO(),
                this.taskType,
                this.taskStartPoint,
                this.pricePerTarget,
                this.participatingWorkerNames.size(),
                this.executionStatus,
                configCompDTO,
                configSimDTO,
                this.participatingWorkerNames
        );

        return executionDTO;
    }

    /**
     * @return True if the name was added, false if a worker by given name already exists in the list.
     */
    public boolean addNewWorkerName(String newWorkerName) {
        boolean added = false;
        boolean existingName = false;

        for (String existingWorker :
                participatingWorkerNames) {
            if (existingWorker.equals(newWorkerName)) {
                existingName = true;
                break;
            }
        }

        if (!existingName) {
            participatingWorkerNames.add(newWorkerName);
            added = true;
        }


        return added;
    }

    public boolean containsWorker(String username) {
        return getParticipatingWorkerNames().contains(username);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- INNER CLASS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public class ProcessedData implements Serializable, Cloneable {
        //        private static final long serialVersionUID = 2; // 25-Nov-2021
        private static final long serialVersionUID = 3; // 27-Nov-2021


        protected Map<String, TargetDTO> name2TargetReport;

        protected Collection<TargetDTO> failedTargets;
        protected Collection<TargetDTO> successfulTargets;
        protected Collection<TargetDTO> warningTargets;
        protected Collection<TargetDTO> successTargetsAllRunWithWarnings;

        public ProcessedData(ProcessedData previousProcessedData) {
//            name2TargetReport = new HashMap<>();
            name2TargetReport = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            failedTargets = new LinkedHashSet<>();
            successfulTargets = new LinkedHashSet<>();
            warningTargets = new LinkedHashSet<>();
            successTargetsAllRunWithWarnings = new LinkedList<>();

            initReports();
            initSuccessTargetsFromPreviousRuns(previousProcessedData);
        }

        private void initReports() {
            for (TargetDTO targetDTO : startingGraph.getAllTargetData()) {
//                TargetDTO clonedData = targetDTO.cloneWithoutReport();
                TargetDTO clonedData = targetDTO.clone();
                name2TargetReport.put(targetDTO.getName(), clonedData);
            }
        }

        private void initSuccessTargetsFromPreviousRuns(ProcessedData previousProcessedData) {
            if (previousProcessedData != null) {
                for (TargetDTO targetDTO : previousProcessedData.getSuccessTargetsAllRunWithWarnings()) {
                    this.successTargetsAllRunWithWarnings.add(targetDTO.clone());
                }
            }
        }

        public TargetDTO getSpecificTargetData(String targetName) {
            TargetDTO res = null;

            if (targetName != null && !targetName.trim().isEmpty()) {
                TargetDTO targetDTO = name2TargetReport.get(targetName);

                if (targetDTO == null) { // Double check because the case-insensitive tree isn't working!
                    for (TargetDTO target : name2TargetReport.values()) {
                        if (target.getName().equalsIgnoreCase(targetName)) {
                            res = target.clone();
                            break;
                        }
                    }
                } else {
                    res = targetDTO.clone();
                }
            }

            return res;
        }

        public Collection<TargetDTO> getAllTargetData() {
            return name2TargetReport.values();
        }

        public Collection<TargetDTO> getUnprocessedTargets() {
            Collection<TargetDTO> allUnprocessed = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() == null || targetDTO.getTaskStatusDTO().getTaskResult() == TaskResult.UNPROCESSED) {
                    allUnprocessed.add(targetDTO);
                }
            }

            return allUnprocessed;
        }

        public Collection<TargetDTO> getAllProcessedTargetsOfAllResults() {
            Collection<TargetDTO> allProcessed = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() != null && targetDTO.getTaskStatusDTO().getTaskResult() != TaskResult.UNPROCESSED) {
                    allProcessed.add(targetDTO);
                }
            }

            return allProcessed;
        }

        public Collection<TargetDTO> getFailedTargets() {
            return failedTargets;
        }

        public Collection<TargetDTO> getSuccessfulTargets() {
            return successfulTargets;
        }

        public Collection<TargetDTO> getWarningTargets() {
            return warningTargets;
        }

        public Collection<TargetDTO> getProcessedTargetsNoFailure() {
            Collection<TargetDTO> nonFailed = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() != null
                        &&
                        targetDTO.getTaskStatusDTO().getTaskResult() != TaskResult.UNPROCESSED
                        &&
                        targetDTO.getTaskStatusDTO().getTaskResult() != TaskResult.FAILURE) {
                    nonFailed.add(targetDTO);
                }
            }

            return nonFailed;
        }

        public Collection<TargetDTO> getTargetsLeftToCompletion() {
            Collection<TargetDTO> leftToCompletion = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() == null
                        ||
                        targetDTO.getTaskStatusDTO().getTaskResult() == TaskResult.UNPROCESSED
                        ||
                        targetDTO.getTaskStatusDTO().getTaskResult() == TaskResult.FAILURE) {
                    leftToCompletion.add(targetDTO);
                }
            }

            return leftToCompletion;
        }

        public Collection<TargetDTO> getSuccessTargetsAllRunWithWarnings() {
            return successTargetsAllRunWithWarnings;
        }

        @Override
        public ProcessedData clone() {
            ProcessedData clone = new ProcessedData(null);

            clone.name2TargetReport = cloneMap();
            clone.failedTargets = cloneCollection(this.failedTargets);
            clone.successfulTargets = cloneCollection(this.successfulTargets);
            clone.warningTargets = cloneCollection(this.warningTargets);
            clone.successTargetsAllRunWithWarnings = cloneCollection(this.successTargetsAllRunWithWarnings);

            return clone;
        }

        private Map<String, TargetDTO> cloneMap() {
            Map<String, TargetDTO> newName2TargetReport = new HashMap<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                newName2TargetReport.put(targetDTO.getName(), targetDTO.clone());
            }

            return newName2TargetReport;
        }

        private Collection<TargetDTO> cloneCollection(Collection<TargetDTO> collection) {
            Collection<TargetDTO> clone = null;

            if (collection != null) {
                clone = new LinkedList<>();

                for (TargetDTO targetDTO : collection) {
                    clone.add(targetDTO.clone());
                }
            }

            return clone;
        }

        /* ---------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------- REPORT MANAGEMENT ----------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------- */
        public synchronized void addTargetData(TargetDTO targetDTO) {
            name2TargetReport.put(targetDTO.getName(), targetDTO);

            if (targetDTO.getTaskStatusDTO() != null) {
                switch (targetDTO.getTaskStatusDTO().getTaskResult()) {
                    case FAILURE:
                        failedTargets.add(targetDTO);
                        break;
                    case SUCCESS:
                        successfulTargets.add(targetDTO);
                        successTargetsAllRunWithWarnings.add(targetDTO);
                        break;
                    case SUCCESS_WITH_WARNINGS:
                        warningTargets.add(targetDTO);
                        successTargetsAllRunWithWarnings.add(targetDTO);
                        break;
                }
            }
        }
    }
}