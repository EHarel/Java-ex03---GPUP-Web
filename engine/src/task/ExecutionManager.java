package task;

import graph.Target;
import task.enums.ExecutionStatus;
import task.enums.TaskResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExecutionManager {
    List<Execution> executions;
    int indexOfNextExecutionToRelease;

    public ExecutionManager() {
        executions = new ArrayList<>();
        indexOfNextExecutionToRelease = 0;
    }

    public boolean isExistingExecutionName(String executionName) {
        System.out.println("[ExecutionManager - isExistingExecutionName()] Start.");

        boolean isExisting = false;

        executionName = executionName.trim();

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                isExisting = true;
                break;
            }
        }

        System.out.println("[ExecutionManager - isExistingExecutionName()] End.");
        return isExisting;
    }

    public synchronized boolean addExecution(Execution execution) {
        System.out.println("[ExecutionManager - addExecution()] Start.");
        boolean isAdded = false;

        if (!isExistingExecutionName(execution.getExecutionName())) {
            executions.add(execution);
            isAdded = true;
        }

        System.out.println("[ExecutionManager - addExecution()] End.");
        return isAdded;
    }

    public synchronized Collection<Execution> getExecutions() {
//        System.out.println("[ExecutionManager - getExecutions()] Start.");

        return Collections.unmodifiableCollection(executions);
//        return executions;
    }

    public synchronized boolean addUserToConfiguration(String executionName, String userName) {
        boolean userAdded = false;

        // TODO: allow adding only if task is not executing?
        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                userAdded = execution.addNewWorkerName(userName);
                break;
            }
        }

        return userAdded;
    }

    /**
     * This method checks if the given user (userName) is the one who uploaded the execution.
     */
    public synchronized boolean isExecutionCreator(String executionName, String userName) {
        boolean isCreator = false;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                isCreator = (execution.getCreatingUser().equals(userName));
                break;
            }
        }

        return isCreator;
    }

    public synchronized boolean updateExecutionStatus(String executionName, ExecutionStatus executionStatus) {
        boolean updated = false;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                execution.setExecutionStatus(executionStatus);
                updated = true;
                break;
            }
        }

        return updated;
    }

    /**
     * This method returns targets to work on for the user.
     * It goes over all the executions, and checks which are active.
     * From the active executions it checks if the user is part of its work force.
     * Only then does it start releasing targets.
     */
    public synchronized Collection<Target> getTargetsForUser(String username, Integer targetCount) {
        Collection<Target> chosenTargets = new ArrayList<>();
        Collection<Execution> activeExecutionsUserIsPartOf = getActiveExecutionsUserIsPartOf(username);

        if (activeExecutionsUserIsPartOf.size() != 0) {
            boolean targetsAvailable = true;

            while (targetCount > 0 && targetsAvailable) {
                int targetCountAtBeginningOfRound = targetCount; // Relevant to find out if targets are available

                for (Execution execution : activeExecutionsUserIsPartOf) {
                    Target target = execution.removeTargetFromQueue();
                    if (target != null) {
                        chosenTargets.add(target);
                        targetCount--;
                    }

                    if (targetCount == 0) {
                        break;
                    }
                }

                /* If at the end of an iteration over the entire list of executions there have been no new targets found,
                it means the executions might be waiting for updates, and have no available targets.
                We stop in that case. */
                targetsAvailable = (targetCountAtBeginningOfRound != targetCount);
            }
        }

        return chosenTargets;
    }

    private Collection<Execution> getActiveExecutionsUserIsPartOf(String username) {
        Collection<Execution> activeExecutionsUserIsPartOf = new ArrayList<>();

        for (Execution execution : executions) {
            if (execution.getExecutionStatus() == ExecutionStatus.EXECUTING) {
                if (execution.containsWorker(username)) {
                    activeExecutionsUserIsPartOf.add(execution);
                }
            }
        }

        return activeExecutionsUserIsPartOf;
    }

    private void advanceIndex() {
        indexOfNextExecutionToRelease++;
        indexOfNextExecutionToRelease = indexOfNextExecutionToRelease % executions.size();
    }

    private boolean executionAllowsTargetRemovalForUser(Execution execution, String username) {
        boolean allowsTargetRemoval = false;

        if (execution != null) {
            if (execution.getExecutionStatus().equals(ExecutionStatus.EXECUTING)) {
                if (execution.containsWorker(username)) {
                    allowsTargetRemoval = true;
                }
            }
        }

        return allowsTargetRemoval;
    }

    public synchronized boolean updateTargetTaskResult(String executionName, String targetName, TaskResult taskResult) {
        boolean updated = false;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                updated = execution.updateTargetTaskResult(targetName, taskResult);
                break;
            }
        }

        return updated;
    }
}
