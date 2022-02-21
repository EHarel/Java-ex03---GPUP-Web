package components.execution.task;

import algorithm.DFS;
import com.sun.istack.internal.NotNull;
import graph.DependenciesGraph;
import graph.SerialSet;
import graph.Target;
import graph.TargetDTO;
import task.TaskType;
import task.configuration.Configuration;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public abstract class Task implements Runnable {
    protected final TaskType taskType;
    protected Target target;
    protected Configuration configuration;
//    protected Execution execution;


    public Task(
            TaskType taskType,
            @NotNull Target target,
            @NotNull Configuration configuration
    ) throws IllegalArgumentException {
        this.taskType = taskType;

        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null.");
        }

        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null.");
        }

        this.target = target;
        this.configuration = configuration;
//        this.workingGraph = workingGraph;
//        this.execution = execution;
//        this.taskManager = Engine.getInstance().getTaskManager();
    }

    public Target getTarget() {
        return target;
    }


    /* TODO
     * If the pause mechanic works,
     * I need to also take into consideration the start and end instants
     * Take a new instant every time I'm paused and everytime I continue,
     * and calculate deltas with it
     */

    @Override
    public void run() {
//        checkPause();
        Instant start = Instant.now();
        target.setTimeOfExitOutOfTaskQueue(Instant.now()); // TODO: change - set time of work start?
//        taskManager.getThreadManager().incrementActiveThreads(Thread.currentThread().getId());
//        taskManager.getThreadManager().decrementRemainingTasks(Thread.currentThread().getId());
//        Collection<SerialSet> lockedSets = acquiredSetLocks();
        target.getTaskStatus().setTargetState(TargetDTO.TargetState.IN_PROCESS);

//        taskManager.getConsumerManager().getTargetStateChangedConsumers().forEach(consumer -> consumer.accept(target.toDTO()));
//        taskManager.getConsumerManager().getStartTargetConsumers().forEach(consumer -> consumer.accept(target.toDTO()));


        runTaskOnTarget();

//        addOpenedTargets();

//        removeTargetFromGraphIfDone();
        updateInstantsIfNecessary(start);


//        taskManager.getConsumerManager().getEndTargetConsumers().forEach(consumer -> consumer.accept(target.toDTO()));
//        execution.getProcessedData().addTargetData(target.toDTO());
//        taskManager.getThreadManager().decrementActiveThreads(Thread.currentThread().getId());
//        releaseLocks(lockedSets);
    }

//    private void checkPause() {
//        Boolean pause = taskManager.getThreadManager().getPause();
//
//        if (pause == true) {
//            synchronized (pause) {
//                try {
//                    pause.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    protected void runTaskOnTarget() {
        TargetDTO.TaskStatusDTO.TaskResult result = runActualTask();

        target.getTaskStatus().setTargetState(TargetDTO.TargetState.FINISHED);
        target.getTaskStatus().setTaskResult(result);

//        taskManager.getConsumerManager().getTargetStateChangedConsumers().forEach(targetDTOConsumer -> targetDTOConsumer.accept(target.toDTO()));

//        Collection<List<String>> skippedTargetsNames_AllPaths = getSkippedTargetsAllPaths();
//        target.getTaskStatus().setTargetsSkippedAsResult_AllPaths(skippedTargetsNames_AllPaths);

//        Collection<String> skippedTargetNames = getSkippedTargetNamesFromAllSkippedPathsTrimmed(skippedTargetsNames_AllPaths);
//        target.getTaskStatus().setTargetsSkippedAsResult(skippedTargetNames);

//        target.getTaskStatus().setTargetsOpenedAsResult(getOpenedTargets());
    }

    protected abstract TargetDTO.TaskStatusDTO.TaskResult runActualTask();

    /**
     * Some tasks may update their own start and end instants themselves. In case they don't,
     * this method is meant to ensure that each target will have a start and end instant
     * based on when "runActualTask" was called, and was returned from.
     */
    private void updateInstantsIfNecessary(Instant start) {
        target.getTaskStatus().setStartInstant(start);
        target.getTaskStatus().setEndInstant(Instant.now());
    }
}

/* ---------------------------------------------------------------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
/* --------------------------------------------- OLD CODE --------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
/* ---------------------------------------------------------------------------------------------------- */
//
//    protected Collection<List<String>> getSkippedTargetsAllPaths() {
//        Collection<List<String>> allSkippedTargetsString = new ArrayList<>();
//        TargetDTO.TaskStatusDTO.TaskResult result = target.getTaskStatus().getTaskResult();
//
//        if (result == TargetDTO.TaskStatusDTO.TaskResult.FAILURE) {
//            Collection<Consumer<Target>> consumersForDFS = new LinkedList<>();
//            consumersForDFS.add(new ConsumerUpdateState(TargetDTO.TargetState.SKIPPED));
//            try {
//                DFS dfs = new DFS(workingGraph, target, null, DFS.EdgeDirection.REQUIRED_FOR, consumersForDFS);
//                Collection<List<Target>> allSkippedTargetsPaths = dfs.run();
//                allSkippedTargetsString = EngineUtils.pathsToNames(allSkippedTargetsPaths);
//
//                Collection<Target> allSkippedTargets = EngineUtils.pathsToTargets_Exclude(allSkippedTargetsPaths, target);
//
//                for (Target skippedTarget : allSkippedTargets) {
//                    taskManager.getConsumerManager().getTargetStateChangedConsumers().forEach(targetDTOConsumer -> targetDTOConsumer.accept(skippedTarget.toDTO()));
//                    taskManager.getConsumerManager().getEndTargetConsumers().forEach(targetDTOConsumer -> targetDTOConsumer.accept(skippedTarget.toDTO()));
//                }
//            } catch (Exception ignore) { } // From within a task we work only with existing targets, no room for user input error
//        }
//
//        return allSkippedTargetsString;
//    }
//
//    /**
//     * This method returns a trimmed collection of all target names,
//     * removing duplicates and this task's specific target from the collection.
//     */
//    protected Collection<String> getSkippedTargetNamesFromAllSkippedPathsTrimmed(Collection<List<String>> skippedTargetsNames_allPaths) {
//        Collection<String> skippedNames = new LinkedHashSet<>();
//
//        for (Collection<String> path : skippedTargetsNames_allPaths) {
//            for (String target : path) {
//                if (!skippedNames.contains(target) && !target.equals(this.target.getName())) {
//                    skippedNames.add(target);
//                }
//            }
//        }
//
//        return skippedNames;
//    }
//
//    protected Collection<String> getOpenedTargets() {
//        Collection<String> openedTargets = new ArrayList<>();
//        TargetDTO.TaskStatusDTO.TaskResult result = target.getTaskStatus().getTaskResult();
//
//        // if (result != TargetReport.TaskResult.FAILURE) { // OLD CODE when Opened meant "all dependencies succeeded"
//        if (result != TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED) {
//            for (Target targetToCheck : target.getTargetsThisIsRequiredFor()) {
//                if (targetHasOpened(targetToCheck)) {
//                    openedTargets.add(targetToCheck.getName());
//                }
//            }
//        }
//
//        return openedTargets;
//    }
//
//    /**
//     * A target is considered "opened" if all its dependencies have been processed,
//     * regardless of if they've been processed successfully or not.
//     * Meaning even a target whose all dependencies have been processed and all have failed will be considered "Opened".
//     */
//    private boolean targetHasOpened(Target target) {
//        boolean hasOpened = true;
//
//        for (Target dependentOnTarget : target.getTargetsThisIsDependentOn()) {
//            switch (dependentOnTarget.getTaskStatus().getTaskResult()) {
//                // case FAILURE: // Old version where I "Opened" only those whose all dependencies succeeded.
//                case UNPROCESSED:
//                    hasOpened = false;
//                    break;
//            }
//        }
//
//        return hasOpened;
//    }

//    private Collection<SerialSet> acquiredSetLocks() {
//        Collection<SerialSet> acquiredSetLocks = new LinkedList<>();
//        Collection<SerialSet> targetSerialSets = target.getSerialSets();
//        Map<String, Boolean> set2Boolean = prepareLockMap(targetSerialSets);
//
//        boolean failedToAcquireLock = true;
//
//        SerialSet previouslyAcquiredSet = null;
//
//        while (failedToAcquireLock) {
//            failedToAcquireLock = false;
//            SerialSet unacquiredSet = null;
//            acquiredSetLocks = new LinkedList<>();
//            if (previouslyAcquiredSet != null) {
//                acquiredSetLocks.add(previouslyAcquiredSet);
//                previouslyAcquiredSet = null;
//            }
//
//            for (SerialSet serialSet : targetSerialSets) {
//                if (!set2Boolean.get(serialSet.getName())) {
//                    System.out.println("Thread " + Thread.currentThread().getId() + " about to try and get lock of " + serialSet.getName() +
//                            " for target " + target.getName() + "\n");
//                    ReentrantLock lock = serialSet.getLock();
//                    if (!lock.tryLock()) {
//                        failedToAcquireLock = true;
//                        unacquiredSet = serialSet;
//                        System.out.println("Thread " + Thread.currentThread().getId() + " failed to get lock of " + serialSet.getName() +
//                                " for target " + target.getName() + "\n");
//
//                        break;
//                    } else {
//                        acquiredSetLocks.add(serialSet);
//                        set2Boolean.put(serialSet.getName(), true);
//                    }
//                }
//            }
//
//            if (unacquiredSet != null) {
//                System.out.println("Thread " + Thread.currentThread().getId() + " about to release locks and sleep for a bit.\n");
//                releaseLocks(acquiredSetLocks, set2Boolean);
//                unacquiredSet.getLock().lock();
//
//                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock of " + unacquiredSet.getName() + " for target " + target.getName() + ".\n");
//
//                set2Boolean.put(unacquiredSet.getName(), true);
//                previouslyAcquiredSet = unacquiredSet;
//            }
//        }
//
//        return acquiredSetLocks;
//    }

//    protected Map<String, Boolean> prepareLockMap(Collection<SerialSet> serialSets) {
//        Map<String, Boolean> acquiredLocks = new HashMap<>(serialSets.size());
//
//        for (SerialSet serialSet : serialSets) {
//            acquiredLocks.put(serialSet.getName(), false);
//        }
//
//        return acquiredLocks;
//    }

//    private void releaseLocks(Collection<SerialSet> acquiredSetLocks, Map<String, Boolean> set2Boolean) {
//        for (SerialSet serialSet : acquiredSetLocks) {
//            if (set2Boolean.get(serialSet.getName())) {
//
//                ReentrantLock lock = serialSet.getLock();
//                synchronized (lock) {
//                    System.out.println("Thread " + Thread.currentThread().getId() +
//                            " about to release lock of " + serialSet.getName() +
//                            " for target " + target.getName() + ".\n");
//                    lock.notifyAll();
//                    lock.unlock();
//                    set2Boolean.put(serialSet.getName(), false);
//                }
//            }
//        }
//    }

//    private void releaseLocks(Collection<SerialSet> lockedSets) {
//        for (SerialSet serialSet : lockedSets) {
//            ReentrantLock lock = serialSet.getLock();
//            synchronized (lock) {
//                System.out.println("Thread " + Thread.currentThread().getId() + " about to release lock of " + serialSet.getName() + " for target " + target.getName() + "\n");
//                lock.notifyAll();
//                lock.unlock();
//            }
//        }
//    }

//    /**
//     * This method receives a target that has just finished being processed,
//     * and adds all the targets that, as a result of its result, can now be
//     * inserted into the queue and be processed as well.
//     */
//    private void addOpenedTargets() {
//        if (TaskProcess.targetSucceededWithOrWithoutWarnings(target)) {
//            for (Target dependentOn : target.getTargetsThisIsRequiredFor()) {
//                if (TaskProcess.targetCanBeProcessed(dependentOn)) {
//                    Task newTask = TaskFactory.getActualTask(
//                            taskType,
//                            dependentOn,
//                            configuration,
//                            workingGraph,
//                            execution);
//
//                    taskManager.getThreadManager().execute(newTask);
//                }
//            }
//        }
//    }


//    private void removeTargetFromGraphIfDone() {
//        workingGraph.tryRemoveSuccessful(target);
//    }