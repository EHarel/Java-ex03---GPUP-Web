package graph;

import logic.EngineUtils;
import task.configuration.Configuration;
import exception.NullOrEmptyStringException;

import java.io.Serializable;
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
public class Target implements Serializable {
    //    private static final long serialVersionUID = 3; // 24-Nov-2021
//    private static final long serialVersionUID = 4; // 01-Dec-2021, participates in execution
    private static final long serialVersionUID = 5; // 08-Dec-2021, serial sets


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

    public TargetDTO toData() {
        TargetDTO.TaskStatusDTO taskStatusDTO = null;
        if (taskStatus != null) {
            taskStatusDTO = taskStatus.toData();
        }

        Collection<SerialSetDTO> serialSetDTOS = new LinkedList<>();

        for (SerialSet set : serialSets) {
            serialSetDTOS.add(set.toData());
        }

        TargetDTO res =  new TargetDTO(
                this.name,
                this.dependency,
                EngineUtils.getNamesOfTargets(this.targetsThisIsDependentOn),
                EngineUtils.getNamesOfTargets(this.targetsThisIsRequiredFor),
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

    public void setParticipatesInExecution(boolean participates) {
        taskStatus.setParticipatesInExecution(participates);
    }

    public boolean isParticipatesInExecution() {
        return taskStatus.isParticipatesInExecution();
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

    public static class TaskStatus implements Serializable, Cloneable {
        private static final long serialVersionUID = 1; // 03-Dec-2021

        private int executionNum;
        private boolean participatesInExecution;
        private Instant startInstant;
        private Instant endInstant;
        private Configuration config;
        private TargetDTO.TaskStatusDTO.TaskResult taskResult;
        private TargetDTO.TargetState targetState;
        private Collection<String> targetsOpenedAsResult;
        private Collection<String> targetsSkippedAsResult;
        private Collection<List<String>> targetsSkippedAsResult_AllPaths;
        private String errorDetails;

        public TaskStatus() {
            reset();
        }

        public void reset() {
            reset(0, null);
        }

        public void reset(int executionNumber, Configuration configuration) {
            this.executionNum = executionNumber;
            this.participatesInExecution = false;
            startInstant = null;
            endInstant = null;
            this.config = configuration;
            taskResult = TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED;
            targetState = TargetDTO.TargetState.FROZEN;
            targetsOpenedAsResult = new ArrayList<>();
            targetsSkippedAsResult = new ArrayList<>();
            targetsSkippedAsResult_AllPaths = new ArrayList<>();
            errorDetails = null;
        }

        public int getExecutionNum() {
            return executionNum;
        }

        public void setExecutionNum(int executionNum) {
            this.executionNum = executionNum;
        }

        public boolean isParticipatesInExecution() {
            return participatesInExecution;
        }

        public void setParticipatesInExecution(boolean participatesInExecution) {
            this.participatesInExecution = participatesInExecution;
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

        public Optional<Configuration> getConfig() {
            return Optional.ofNullable(config);
        }

        public void setConfig(Configuration config) {
            this.config = config;
        }

        public TargetDTO.TaskStatusDTO.TaskResult getTaskResult() {
            return taskResult;
        }

        public void setTaskResult(TargetDTO.TaskStatusDTO.TaskResult taskResult) {
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
            return EngineUtils.getNamesOfTargets(targetCollection);
        }

        @Override
        public TaskStatus clone() {
            TaskStatus clone = new TaskStatus();
            Configuration configClone = null;
            if (this.config != null) {
                configClone = this.config.clone();
            }

            clone.executionNum = this.executionNum;
            clone.participatesInExecution = this.participatesInExecution;
            clone.startInstant = this.startInstant;
            clone.endInstant = this.endInstant;
            clone.config = configClone;
            clone.taskResult = this.taskResult;
            clone.targetState = this.targetState;
            clone.targetsOpenedAsResult = EngineUtils.cloneCollection(targetsOpenedAsResult);
            clone.targetsSkippedAsResult = EngineUtils.cloneCollection(targetsSkippedAsResult);
            clone.targetsSkippedAsResult_AllPaths = EngineUtils.cloneListCollection(targetsSkippedAsResult_AllPaths);
            clone.errorDetails = this.errorDetails;

            return clone;
        }

        public TargetDTO.TaskStatusDTO toData() {
            return new TargetDTO.TaskStatusDTO(
                    this.executionNum,
                    this.participatesInExecution,
                    this.startInstant,
                    this.endInstant,
                    (config != null ? this.config.getData() : null),
                    this.taskResult,
                    this.targetState,
                    getTargetsOpenedAsResultString(),
                    getTargetsSkippedAsResultString(),
                    getTargetsSkippedAsResult_AllPaths_String(),
                    this.errorDetails
            );
        }
    }
}