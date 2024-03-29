package graph;

import task.configuration.*;
import exception.NullOrEmptyStringException;
import task.enums.TaskResult;
import util.GraphUtils;

import java.time.Instant;
import java.util.*;

/**
 * A target is a node in the graph.
 * It can hold information relevant to the system in the future.
 * Each target can be dependent on more than one target, and more than one target can be dependent on it.
 * Leaves: targets which aren't dependent on any other target.
 * Roots: targets which aren't required for other targets.
 * Middles: targets which are both dependent on and required for other targets.
 * Independents: targets which aren't dependent on and aren't required for other targets.
 */
public class Target {
    private final boolean caseSensitive = false;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ DATA MEMBERS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private String name;
    private TargetDTO.Dependency dependency;
    protected Set<Target> targetsThisIsRequiredFor;
    protected Set<Target> targetsThisIsDependentOn;
    protected String userData;
    private TaskStatus taskStatus;
    private Collection<SerialSet> serialSets;
    private Instant timeOfEntryIntoTaskQueue;
    private Instant timeOfExitOutOfTaskQueue;

    private Collection<String> targetsThisIsDirectlyRequiredForNames;
    private Collection<String> targetsThisIsDirectlyDependentOnNames;



    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- CONSTRUCTOR -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Target(String name) throws NullOrEmptyStringException {
        setName(name);
        this.targetsThisIsRequiredFor = new HashSet<>();
        this.targetsThisIsDependentOn = new HashSet<>();
        this.dependency = TargetDTO.Dependency.INDEPENDENT;
        this.userData = "";
        this.taskStatus = new TaskStatus();
        serialSets = new LinkedList<>();
    }

    public static Target recreateTargetWithoutDependencies(TargetDTO targetDTO) {
        Target newTarget = null;

        try {
            newTarget = new Target(targetDTO.getName());
            newTarget.userData = targetDTO.getUserData();
            newTarget.taskStatus = new TaskStatus(targetDTO.getTaskStatusDTO());
        } catch (NullOrEmptyStringException ignore) {
        }

        return newTarget;
    }

    public static Target recreateTargetWithDependencyNamesOnly(TargetDTO targetDTO) {
        Target newTarget = null;

        try {
            newTarget = new Target(targetDTO.getName());
            newTarget.userData = targetDTO.getUserData();
            newTarget.taskStatus = new TaskStatus(targetDTO.getTaskStatusDTO());
            newTarget.targetsThisIsDirectlyDependentOnNames = targetDTO.targetsThisDirectlyDependsOn;
            newTarget.targetsThisIsDirectlyRequiredForNames = targetDTO.targetsThisIsDirectlyRequiredFor;
        } catch (NullOrEmptyStringException ignore) {
        }

        return newTarget;
    }

    public Collection<String> getTargetsThisIsDirectlyRequiredForNames() {
        return targetsThisIsDirectlyRequiredForNames;
    }

    public Collection<String> getTargetsThisIsDirectlyDependentOnNames() {
        return targetsThisIsDirectlyDependentOnNames;
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Instant getTimeOfExitOutOfTaskQueue() {
        return timeOfExitOutOfTaskQueue;
    }

    public void setTimeOfExitOutOfTaskQueue(Instant timeOfExitOutOfTaskQueue) {
        this.timeOfExitOutOfTaskQueue = timeOfExitOutOfTaskQueue;
    }

    public Instant getTimeOfEntryIntoTaskQueue() {
        return timeOfEntryIntoTaskQueue;
    }

    public void setTimeOfEntryIntoTaskQueue(Instant timeOfEntryIntoTaskQueue) {
        this.timeOfEntryIntoTaskQueue = timeOfEntryIntoTaskQueue;
    }

    public String getName() {
        return name;
    }

    public void setExecutionName(String executionName) {
        this.taskStatus.setExecutionName(executionName);
    }

    // Package Friendly
    void setName(String newName) throws NullOrEmptyStringException {
        if (newName == null) {
            throw new NullOrEmptyStringException("Given name for target is somehow null. Not sure how that happened!");
        }

        if (newName.trim().isEmpty()) {
            throw new NullOrEmptyStringException("Given name for target is empty. Big no-no! Give an actual name.");
        }

        this.name = newName;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String newData) {
        if (newData != null) {
            userData = newData;
        }
    }

    public Collection<SerialSet> getSerialSets() {
        return serialSets;
    }

    public TargetDTO.Dependency getDependency() {
        return dependency;
    }

    public Collection<Target> getTargetsThisIsRequiredFor() {
        return targetsThisIsRequiredFor;
    }

    public Collection<String> getNamesOfTargetsThisIsRequiredFor() {
        Collection<String> set = new ArrayList<>(targetsThisIsRequiredFor.size());

        for (Target target : targetsThisIsRequiredFor) {
            set.add(target.getName());
        }

        return set;
    }

    public Collection<Target> getTargetsThisIsDependentOn() {
        return targetsThisIsDependentOn;
    }

    public Collection<String> getNamesOfTargetsThisIsDependentOn() {
        Collection<String> set = new ArrayList<>(targetsThisIsDependentOn.size());

        for (Target target : targetsThisIsDependentOn) {
            set.add(target.getName());
        }

        return set;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTargetReport(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void addSet(SerialSet newSet) {
        for (SerialSet set : serialSets) {
            if (set.getName().equals(newSet.getName())) {
                return;
            }
        }

        serialSets.add(newSet);
        newSet.addTarget(this);
    }

    public void removeSet(String setName) {
        for (SerialSet set : serialSets) {
            if (set.getName().equalsIgnoreCase(setName)) {
                serialSets.remove(set);
                set.removeTarget(this.name);
                return;
            }
        }
    }


    /**
     * Add target "newTarget" that THIS is REQUIRED FOR.
     * In other words, newTarget is DEPENDENT ON THIS.
     */
    public void addTargetThatRequiresThis(Target newTarget) {
        addTargetToCollection(targetsThisIsRequiredFor, newTarget);
        addTargetToCollection(newTarget.getTargetsThisIsDependentOn(), this);
        updateState();
    }

    /**
     * Add target "newTarget" that THIS is DEPENDENT ON.
     * In other words, newTarget is REQUIRED FOR THIS.
     */
    public void addTargetThisDependsOn(Target newTarget) {
        addTargetToCollection(targetsThisIsDependentOn, newTarget);
        addTargetToCollection(newTarget.getTargetsThisIsRequiredFor(), this);
    }

    private void addTargetToCollection(Collection<Target> targetCollection, Target newTarget) {
        if (newTarget == null || newTarget.getName() == null || newTarget.getName().isEmpty()) {
            return;
        }

        targetCollection.add(newTarget);
        updateState();
        newTarget.updateState();
    }

    public void updateState() {
        updateDependency();
        // updateTaskState(); // TODO: implement this?

        /* We can't go recursively over all list of neighbors,
        because in case of a cycle we'll have an endless recursion.
        */
    }

    private void updateDependency() {
        boolean isRequiredForOthers = isRequiredFor();
        boolean isDependentOnOthers = isDependentOn();

        if (isDependentOnOthers && isRequiredForOthers) {
            dependency = TargetDTO.Dependency.MIDDLE;
        } else if (isDependentOnOthers) {
            dependency = TargetDTO.Dependency.ROOT;
        } else if (isRequiredForOthers) {
            dependency = TargetDTO.Dependency.LEAF;
        } else {
            dependency = TargetDTO.Dependency.INDEPENDENT;
        }
    }

    private boolean isRequiredFor() {
        return (targetsThisIsRequiredFor.size() > 0);
    }

    private boolean isDependentOn() {
        return (targetsThisIsDependentOn.size() > 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (((Target) o).name == null) {
            return false;
        }

        Target target = (Target) o;

        boolean res;

        if (caseSensitive) {
            res = name.equals(target.name);
        } else {
            res = name.equalsIgnoreCase(target.name);
        }

        return res;
    }

    @Override
    public int hashCode() {
        int hash = -1;

        if (name != null) {
            if (caseSensitive) {
                hash = name.hashCode();
            } else {
                hash = name.toLowerCase().hashCode();
            }
        }

        return hash;
    }

    public TargetDTO toDTO() {
        TargetDTO.TaskStatusDTO taskStatusDTO = null;
        if (taskStatus != null) {
            taskStatusDTO = taskStatus.toData();
        }

        Collection<SerialSetDTO> serialSetDTOS = new LinkedList<>();

        for (SerialSet set : serialSets) {
            serialSetDTOS.add(set.toData());
        }

        TargetDTO res = new TargetDTO(
                this.name,
                this.dependency,
                GraphUtils.getNamesOfTargets(this.targetsThisIsDependentOn),
                GraphUtils.getNamesOfTargets(this.targetsThisIsRequiredFor),
                this.userData,
                serialSetDTOS,
                taskStatusDTO
        );

        res.setTimeOfEntryIntoTaskQueue(this.timeOfEntryIntoTaskQueue);
        res.setTimeOfExitOutOfTaskQueue(this.timeOfExitOutOfTaskQueue);

        return res;
    }


    public boolean isLeaf() {
        return (targetsThisIsDependentOn.size() == 0 && targetsThisIsRequiredFor.size() >= 1);
    }

    public boolean isIndependent() {
        return (targetsThisIsDependentOn.size() == 0 && targetsThisIsRequiredFor.size() == 0);
    }

    public boolean isMiddle() {
        return (targetsThisIsRequiredFor.size() >= 1 && targetsThisIsDependentOn.size() >= 1);
    }

    public boolean isRoot() {
        return (targetsThisIsDependentOn.size() >= 1 && targetsThisIsRequiredFor.size() == 0);
    }

    /**
     * Dangerous method! Returns a new target with all the data of the instance target,
     * but without the neighbors, or the serial sets.
     * They must be inserted manually with the graph.
     *
     * @return new Target INDEPENDENT with same state, same task result, but no neighbors.
     */
    Target cloneWithoutDependenciesAndSerialSet() {
        Target newTarget = null;

        /*
        Exception should not be thrown, because target is already created.
         */
        try {
            newTarget = new Target(this.name);
            newTarget.dependency = TargetDTO.Dependency.INDEPENDENT; // Since we are duplicating without neighbors, it's independent
            newTarget.userData = this.userData;

            if (taskStatus != null) {
                newTarget.setTargetReport(taskStatus.clone());
            }

        } catch (NullOrEmptyStringException ignored) {

        }

        return newTarget;
    }

    /**
     * Tries to remove a target from the list of targets THIS depends on. The parameter target will be removed only if it's a leaf.
     *
     * @param targetToRemove a Leaf candidate to be removed.
     * @return false if target was not a Leaf and could not be removed (removal requires reconnections possibly, which haven't been done yet).
     * Or if target is not found in list of targets THIS depends on.
     */
    public boolean removeLeafThisDependsOn(Target targetToRemove) {
        boolean removed = false;

        if (targetToRemove.isLeaf()) {
            for (Target target : targetsThisIsDependentOn) {
                if (target.getName().equals(targetToRemove.getName())) { // Found the target
                    targetsThisIsDependentOn.remove(targetToRemove);
                    removed = true;
                    break;
                }
            }
        }

        return removed;
    }

    /**
     * This method returns true if the target finished the task with or without warnings.
     */
    public boolean finishedWell() {
        boolean finishedWell = false;

        if (taskStatus.getTargetState() == TargetDTO.TargetState.FINISHED) {
            switch (taskStatus.getTaskResult()) {
                case SUCCESS_WITH_WARNINGS:
                case SUCCESS:
                    finishedWell = true;
                    break;
            }
        }

        return finishedWell;
    }

    public void resetTaskStatus() {
        taskStatus.reset();
    }

    public void resetTaskStatus(int executionNumber, Configuration activeConfiguration) {
        taskStatus.reset(executionNumber, activeConfiguration);
        timeOfEntryIntoTaskQueue = timeOfExitOutOfTaskQueue = null;
    }

    /**
     * Checks if the target is directly dependent on a target by the given name.
     */
    public boolean isDirectlyDependentOn(String nameOfTargetToCheck) {
        return isDependencyWithTarget(targetsThisIsDependentOn, nameOfTargetToCheck);
    }

    public boolean isDirectlyRequiredFor(String nameOfTargetToCheck) {
        return isDependencyWithTarget(targetsThisIsRequiredFor, nameOfTargetToCheck);
    }

    /**
     * Checks if the target is directly required for a target by the given name.
     */
    private boolean isDependencyWithTarget(Set<Target> neighborsToCheck, String nameOfTargetToCheck) {
        boolean dependencyWithTarget = false;

        if (nameOfTargetToCheck != null) {
            for (Target targetWithDependency : neighborsToCheck) {
                if (targetWithDependency.getName().equals(nameOfTargetToCheck)) {
                    dependencyWithTarget = true;
                    break;
                }
            }
        }

        return dependencyWithTarget;
    }

    public void setTargetLog(String targetLog) {
        taskStatus.setTargetLog(targetLog);
    }

    public String getTargetLog() { return taskStatus.getTargetLog(); }

    public static class TaskStatus implements  Cloneable {

        private String executionName;
        private int executionNum;
        private Instant startInstant;
        private Instant endInstant;
        private ConfigurationCompilation configComp;
        private ConfigurationSimulation configSim;
        private TaskResult taskResult;
        private TargetDTO.TargetState targetState;
        private Collection<String> targetsOpenedAsResult;
        private Collection<String> targetsSkippedAsResult;
        private Collection<List<String>> targetsSkippedAsResult_AllPaths;
        private String errorDetails;
        private Collection<String> targetsThisIsFrozenAndWaitingToFinish;
        private String targetLog;


        public TaskStatus() {
            reset();
        }

        public TaskStatus(TargetDTO.TaskStatusDTO taskStatusDTO) {
            reset();

            if (taskStatusDTO != null) {
                this.executionName = taskStatusDTO.getExecutionName();
                this.executionNum = taskStatusDTO.getExecutionNum();
                this.startInstant = taskStatusDTO.getStartInstant();
                this.endInstant = taskStatusDTO.getEndInstant();
                this.targetState = taskStatusDTO.getState();
                this.taskResult = taskStatusDTO.getTaskResult();

                this.targetsOpenedAsResult = new LinkedList<>();
                taskStatusDTO.getTargetsOpenedAsResult().forEach(s -> {
                    this.targetsOpenedAsResult.add(s);
                });

                this.targetsSkippedAsResult = new LinkedList<>();
                taskStatusDTO.getTargetsSkippedAsResult().forEach(s -> {
                    this.targetsSkippedAsResult.add(s);
                });

                this.targetsThisIsFrozenAndWaitingToFinish = new LinkedList<>();
                Collection<String> frozenWaitingCollection = taskStatusDTO.getTargetsThisIsFrozenAndWaitingToFinish();
                if (frozenWaitingCollection != null) {
                    frozenWaitingCollection.forEach(s -> {
                        this.targetsThisIsFrozenAndWaitingToFinish.add(s);
                    });
                }

                this.errorDetails = getErrorDetails();

                this.targetsSkippedAsResult_AllPaths = new ArrayList<>();

                for (List<String> strings : taskStatusDTO.getTargetsSkippedAsResult_AllPaths()) {
                    List<String> newList = new LinkedList<>();

                    strings.forEach(s -> {
                        newList.add(s);
                    });

                    this.targetsSkippedAsResult_AllPaths.add(newList);
                }

                try {
                    if (taskStatusDTO.getConfigDTOComp() != null) {
                        this.configComp = new ConfigurationCompilation(taskStatusDTO.getConfigDTOComp());
                    }

                    if (taskStatusDTO.getConfigDTOSim() != null) {
                        this.configSim = new ConfigurationSimulation(taskStatusDTO.getConfigDTOSim());
                    }
                } catch (Exception ignore) {
                }

                this.targetLog = taskStatusDTO.getTargetLog();
            }
        }

        public void reset() {
            reset(0, null);
        }

        public void reset(int executionNumber, Configuration configuration) {
            this.executionNum = executionNumber;
            startInstant = null;
            endInstant = null;
            this.configComp = null;
            this.configSim = null;

            if (configuration != null) {
                switch (configuration.getTaskType()) {
                    case COMPILATION:
                        this.configComp = (ConfigurationCompilation) configuration;
                        break;
                    case SIMULATION:
                        this.configSim = (ConfigurationSimulation) configuration;
                        break;
                }
            }

            taskResult = TaskResult.UNPROCESSED;
            targetState = TargetDTO.TargetState.FROZEN;
            targetsOpenedAsResult = new ArrayList<>();
            targetsSkippedAsResult = new ArrayList<>();
            targetsSkippedAsResult_AllPaths = new ArrayList<>();
            errorDetails = null;
            targetLog = "";
        }

        public Collection<String> getTargetsThisIsFrozenAndWaitingToFinish() {
            return targetsThisIsFrozenAndWaitingToFinish;
        }

        public String getExecutionName() { return this.executionName; }

        public void setExecutionName(String executionName) { this.executionName = executionName; }

        public int getExecutionNum() {
            return executionNum;
        }

        public void setExecutionNum(int executionNum) {
            this.executionNum = executionNum;
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

        public ConfigurationCompilation getConfigComp() {
            return configComp;
        }

        public void setConfigComp(ConfigurationCompilation configComp) {
            this.configComp = configComp;
        }

        public ConfigurationSimulation getConfigSim() {
            return configSim;
        }

        public void setConfigSim(ConfigurationSimulation configSim) {
            this.configSim = configSim;
        }

        public TaskResult getTaskResult() {
            return taskResult;
        }

        public void setTaskResult(TaskResult taskResult) {
            this.taskResult = taskResult;
        }

        public TargetDTO.TargetState getTargetState() {
            return targetState;
        }

        public void setTargetState(TargetDTO.TargetState targetState) {
            this.targetState = targetState;
        }

        public Collection<String> getTargetsOpenedAsResult() {
            return targetsOpenedAsResult;
        }

        public void setTargetsOpenedAsResult(Collection<String> targetsOpenedAsResult) {
            this.targetsOpenedAsResult = targetsOpenedAsResult;
        }

        public Collection<String> getTargetsSkippedAsResult() {
            return targetsSkippedAsResult;
        }

        public void setTargetsSkippedAsResult(Collection<String> targetsSkippedAsResult) {
            this.targetsSkippedAsResult = targetsSkippedAsResult;
        }

        public Collection<List<String>> getTargetsSkippedAsResult_AllPaths() {
            return targetsSkippedAsResult_AllPaths;
        }

        public void setTargetsSkippedAsResult_AllPaths(Collection<List<String>> targetsSkippedAsResult_AllPaths) {
            this.targetsSkippedAsResult_AllPaths = targetsSkippedAsResult_AllPaths;
        }

        public String getErrorDetails() {
            return errorDetails;
        }

        public void setErrorDetails(String errorDetails) {
            this.errorDetails = errorDetails;
        }

        public Collection<String> getTargetsOpenedAsResultString() {
            return targetsOpenedAsResult;
        }

        public Collection<String> getTargetsSkippedAsResultString() {
            return targetsSkippedAsResult;
        }

        private Collection<List<String>> getTargetsSkippedAsResult_AllPaths_String() {
            return targetsSkippedAsResult_AllPaths;
        }

        // TODO: change to immutable?
        private Collection<String> getNamesFromCollection(Collection<Target> targetCollection) {
            return GraphUtils.getNamesOfTargets(targetCollection);
        }

        @Override
        public TaskStatus clone() {
            TaskStatus clone = new TaskStatus();

            clone.executionName = this.executionName;
            clone.executionNum = this.executionNum;
            clone.startInstant = this.startInstant;
            clone.endInstant = this.endInstant;
            clone.configSim = this.configSim != null ? this.configSim.clone() : null;
            clone.configComp = this.configComp != null ? this.configComp.clone() : null;
            clone.taskResult = this.taskResult;
            clone.targetState = this.targetState;
            clone.targetsOpenedAsResult = GraphUtils.cloneCollection(targetsOpenedAsResult);
            clone.targetsSkippedAsResult = GraphUtils.cloneCollection(targetsSkippedAsResult);
            clone.targetsSkippedAsResult_AllPaths = GraphUtils.cloneListCollection(targetsSkippedAsResult_AllPaths);
            clone.errorDetails = this.errorDetails;
            clone.targetLog = this.targetLog;

            return clone;
        }

        public TargetDTO.TaskStatusDTO toData() {
            ConfigurationDTOSimulation configDTOSim = this.configSim != null ? this.configSim.toDTO() : null;
            ConfigurationDTOCompilation configDTOComp = this.configComp != null ? this.configComp.toDTO() : null;

            TargetDTO.TaskStatusDTO taskDTO = new TargetDTO.TaskStatusDTO(
                    this.executionName,
                    this.executionNum,
                    this.startInstant,
                    this.endInstant,
                    configDTOSim,
                    configDTOComp,
                    this.taskResult,
                    this.targetState,
                    getTargetsOpenedAsResultString(),
                    getTargetsSkippedAsResultString(),
                    getTargetsSkippedAsResult_AllPaths_String(),
                    this.errorDetails,
                    getTargetsThisIsFrozenAndWaitingToFinish(),
                    this.targetLog
            );

            return taskDTO;
        }

        public void setConfig(Configuration configuration) {
            if (configuration != null) {
                switch (configuration.getTaskType()) {
                    case SIMULATION:
                        this.configSim = (ConfigurationSimulation) configuration;
                        break;
                    case COMPILATION:
                        this.configComp = (ConfigurationCompilation) configuration;
                        break;
                }
            }
        }

        /**
         * This method receives collection of targets this target depends on.
         * It adds all of them which are in the appropriate status to the list of targets this is still waiting on.
         * This only works if target is FROZEN.
         * @param allTargetsThisDependsOn
         */
        public void setTargetsThisIsFrozenFrom(Collection<TargetDTO> allTargetsThisDependsOn) {
            if (allTargetsThisDependsOn == null || getTargetState() != TargetDTO.TargetState.FROZEN) {
                return;
            }

            if (targetsThisIsFrozenAndWaitingToFinish == null) {
                targetsThisIsFrozenAndWaitingToFinish = new LinkedList<>();
            }

            allTargetsThisDependsOn.forEach(targetDTO -> {
                switch (targetDTO.getTaskStatusDTO().getTargetState()) {
                    case IN_PROCESS:
                    case WAITING:
                    case FROZEN:
                        targetsThisIsFrozenAndWaitingToFinish.add(targetDTO.getName());
                        break;
                    case SKIPPED:
                        break;
                    case FINISHED:
                        break;
                }
            });
        }

        public void setTargetLog(String targetLog) {
            this.targetLog = targetLog;
        }

        public String getTargetLog() {
            return this.targetLog;
        }
    }
}