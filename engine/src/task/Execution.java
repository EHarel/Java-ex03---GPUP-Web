package task;

import algorithm.DFS;
import graph.DependenciesGraph;
import graph.Target;
import graph.TargetDTO;
import logic.EngineUtils;
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
    protected String executionOriginalName;
    private final String creatingUser;
    protected final TaskType taskType;
    protected TaskStartPoint taskStartPoint;
    protected final int executionNumber;
    private DependenciesGraph originalGraph;
    private DependenciesGraph startingGraph;
    private DependenciesGraph endGraph;
    private Configuration configuration;
    private ProcessedData processedData;
    protected boolean taskComplete;
    private Instant startInstant;
    private Instant endInstant;
    private final Integer pricePerTarget;
    private ExecutionStatus executionStatus;
    private Collection<String> participatingWorkerNames;
    private boolean isExecutionFullyCompleted;

    private int targetStateCount_FROZEN;
    private int targetStateCount_WAITING;
    private int targetStateCount_IN_PROCESS;
    private int targetStateCount_FINISHED;
    private int targetStateCount_SKIPPED;
    private float executionProgress;

    private String executionLog;

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
        this.executionOriginalName = executionDTO.getExecutionOriginalName();
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

        this.originalGraph = new DependenciesGraph(executionDTO.getOriginalGraphDTO());
        this.startingGraph = new DependenciesGraph(executionDTO.getStartGraphDTO());
        this.endGraph = executionDTO.getEndGraphDTO() != null ? new DependenciesGraph(executionDTO.getEndGraphDTO()) : null;

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
        this.isExecutionFullyCompleted = new Boolean(executionDTO.getIsExecutionFullyCompleted().booleanValue());
        this.targetStateCount_WAITING = 0;
        this.targetStateCount_IN_PROCESS = 0;
        this.targetStateCount_FINISHED = 0;
        this.targetStateCount_SKIPPED = 0;
        this.executionProgress = 0;
    }


    public String getExecutionName() {
        return executionName;
    }

    public String getExecutionOriginalName() {
        return executionOriginalName;
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

    public String getExecutionLog() {
        return executionLog;
    }

    public void setTaskComplete(boolean taskComplete) {
        this.taskComplete = taskComplete;
    }

    public boolean getTaskComplete() {
        return this.isExecutionFullyCompleted;
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

    public float getExecutionProgress() {
        return executionProgress;
    }

    public boolean isExecutionFullyCompleted() {
        return isExecutionFullyCompleted;
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

    public void setExecutionFullyCompleted(boolean executionFullyCompleted) {
        isExecutionFullyCompleted = executionFullyCompleted;
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

        this.startInstant = Instant.now();
        this.executionLog = "Starting execution.";
        this.executionProgress = 0;
        this.targetStateCount_FINISHED = 0;
        this.targetStateCount_SKIPPED = 0;
        this.endGraph = this.startingGraph.duplicate();
        this.endGraph.setConfigurationForTargets(this.configuration);
        prepareEndGraphForExecution();
        prepareQueue();

//        executionHistory.add(execution);
//        activeConfiguration = rememberConfigurationBetweenExecutions ? activeConfiguration : null;
//        checkIfTaskCompleted(execution);
//        execution.setEndInstant(Instant.now());
    }

    /**
     * This sets all the targets to FROZEN, and also prepares the list of targets they wait for.
     */
    private void prepareEndGraphForExecution() {
        for (Target target :
                endGraph.targets()) {
//            updateTargetState(target, TargetDTO.TargetState.FROZEN);
            updateTargetStateNoOldUpdate(target, TargetDTO.TargetState.FROZEN);
            Collection<TargetDTO> allTargetsThisDependsOn = endGraph.getAllDependencies_Transitive(target.getName(), DFS.EdgeDirection.DEPENDENT_ON);

            target.getTaskStatus().setTargetsThisIsFrozenFrom(allTargetsThisDependsOn);
        }

    }

    private void updateTargetStateNoOldUpdate(Target target, TargetDTO.TargetState newState) {
        if (target == null || newState == null) {
            return;
        }

        switch (newState) {
            case FINISHED:
                targetStateCount_FINISHED++;
                break;
            case FROZEN:
                targetStateCount_FROZEN++;
                break;
            case WAITING:
                targetStateCount_WAITING++;
                break;
            case SKIPPED:
                targetStateCount_SKIPPED++;
                break;
            case IN_PROCESS:
                targetStateCount_IN_PROCESS++;
                break;
        }

        target.getTaskStatus().setTargetState(newState);
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
        updateTargetState(target, TargetDTO.TargetState.WAITING);

        target.setTimeOfEntryIntoTaskQueue(Instant.now());

        targetQueue.add(target);
    }

    private void updateTargetState(Target target, TargetDTO.TargetState newState) {
        if (target == null || newState == null) {
            return;
        }

        TargetDTO.TargetState currState = target.getTaskStatus().getTargetState();
        
        switch (currState) {
            case FINISHED:
                targetStateCount_FINISHED--;
                break;
            case FROZEN:
                targetStateCount_FROZEN--;
                break;
            case WAITING:
                targetStateCount_WAITING--;
                break;
            case SKIPPED:
                targetStateCount_SKIPPED--;
                break;
            case IN_PROCESS:
                targetStateCount_IN_PROCESS--;
                break;
        }
        
        switch (newState) {
            case FINISHED:
                targetStateCount_FINISHED++;
                break;
            case FROZEN:
                targetStateCount_FROZEN++;
                break;
            case WAITING:
                targetStateCount_WAITING++;
                break;
            case SKIPPED:
                targetStateCount_SKIPPED++;
                break;
            case IN_PROCESS:
                targetStateCount_IN_PROCESS++;
                break;
        }
        
        target.getTaskStatus().setTargetState(newState);
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
            updateTargetState(target, TargetDTO.TargetState.IN_PROCESS);

            executionLog = executionLog + "\n\nRemoved " + target.getName() + " from the queue.";
        }

        return target;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------- TASK PROCESS CODE - EX03 ------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public synchronized AffectedTargetsData updateTargetTaskResult(String targetName, TaskResult taskResult) {
        AffectedTargetsData affectedTargetsData = new AffectedTargetsData();

        Target target = endGraph.get(targetName);

        if (target == null) {
            return null;
        }

        // Code from task process:
        updateTargetState(target, TargetDTO.TargetState.FINISHED);

        target.getTaskStatus().setTaskResult(taskResult);
        Collection<List<String>> skippedTargetsNames_AllPaths = getSkippedTargetsAllPaths(target);
        Set<String> skippedTargetNames = getSkippedTargetNamesFromAllSkippedPathsTrimmed(skippedTargetsNames_AllPaths, target);
        targetStateCount_SKIPPED += skippedTargetNames.size();

        updateProgress();

        target.getTaskStatus().setTargetsSkippedAsResult(skippedTargetNames);
        affectedTargetsData.setSkippedTargetsNames(skippedTargetNames);

        Set<String> openedTargets = getOpenedTargets(target);
        target.getTaskStatus().setTargetsOpenedAsResult(openedTargets);
        affectedTargetsData.setOpenedTargetsNames(openedTargets);

        addOpenedTargets(target);

        checkAndUpdateIfExecutionIsDone();

        return affectedTargetsData;
    }

    private void updateProgress() {
        int totalTargetsDone = targetStateCount_SKIPPED + targetStateCount_FINISHED;
        int targetCount = startingGraph.size();

        this.executionProgress = (float) (totalTargetsDone) / targetCount;
        if (executionProgress > 1.0f) {
            executionProgress = 1.0f;
        }
    }

    private void checkAndUpdateIfExecutionIsDone() {
        boolean areTargetsLeftToProcess = areTargetsLeftToProcess();
        if (!areTargetsLeftToProcess) {
            setExecutionStatus(ExecutionStatus.ENDED);

            isExecutionFullyCompleted = areAllTargetsSuccessful();
            if (isExecutionFullyCompleted) {
                setExecutionStatus(ExecutionStatus.COMPLETED);
                isExecutionFullyCompleted = true;
            }

            endInstant = Instant.now();

            String executionReport = EngineUtils.getFormalizedExecutionReportString(this);

            this.executionLog = this.executionLog + "\n\n" + executionReport;

        }
    }

    private boolean areAllTargetsSuccessful() {
        boolean allTargetsSuccessful = true;

        for (Target target : endGraph.targets()) {
            switch (target.getTaskStatus().getTaskResult()) {
                case SUCCESS:
                case SUCCESS_WITH_WARNINGS:
                    break;
                case UNPROCESSED:
                case FAILURE:
                    allTargetsSuccessful = false;
                    break;
            }

            if (!allTargetsSuccessful) {
                break;
            }
        }

        return allTargetsSuccessful;
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
    protected Set<String> getSkippedTargetNamesFromAllSkippedPathsTrimmed(Collection<List<String>> skippedTargetsNames_allPaths, Target targetToIgnore) {
        Set<String> skippedNames = new LinkedHashSet<>();

        for (Collection<String> path : skippedTargetsNames_allPaths) {
            for (String target : path) {
                if (!skippedNames.contains(target) && !target.equals(targetToIgnore.getName())) {
                    skippedNames.add(target);
                }
            }
        }

        return skippedNames;
    }

    protected Set<String> getOpenedTargets(Target target) {
        Set<String> openedTargets = new LinkedHashSet<>();
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

        clone.originalGraph = this.originalGraph.duplicate();
        clone.setEndGraph(this.endGraph.duplicate());


        clone.setStartInstant(this.startInstant);
        clone.setEndInstant(this.endInstant);
        clone.setProcessedData(this.processedData.clone());
        clone.setTaskStartPoint(this.taskStartPoint);
        clone.setExecutionFullyCompleted(this.isExecutionFullyCompleted);

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
                this.executionOriginalName,
                this.creatingUser,
                this.originalGraph.toDTO(),
                this.startingGraph.toDTO(),
                this.endGraph != null ? this.endGraph.toDTO() : null,
                this.taskType,
                this.taskStartPoint,
                this.pricePerTarget,
                this.participatingWorkerNames.size(),
                this.executionStatus,
                configCompDTO,
                configSimDTO,
                this.participatingWorkerNames,
                this.isExecutionFullyCompleted,
                this.targetStateCount_WAITING,
                this.targetStateCount_IN_PROCESS,
                this.executionProgress,
                this.executionLog
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

    public synchronized boolean removeWorker(String userName) {
        boolean removed = participatingWorkerNames.remove(userName);

        return removed;
    }

    public boolean containsWorker(String username) {
        return getParticipatingWorkerNames().contains(username);
    }

    public Integer getPricePerTarget() {
        TaskType taskType = getTaskType();

        Integer price = startingGraph.getPrice(taskType);

        return price;
    }

    public int getTotalWorkers() {
        return participatingWorkerNames.size();
    }

    public void addTargetLog(String targetName, String targetLog) {
        if (endGraph == null) {
            return;
        }

        for (Target target : endGraph.targets()) {
            if (target.getName().equals(targetName)) {

                target.setTargetLog(targetLog);

                if (executionLog == null || executionLog.isEmpty()) {
                    executionLog = targetLog;
                } else {
                    this.executionLog = this.executionLog + "\n\n" + targetLog;
                }

                break;
            }
        }

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