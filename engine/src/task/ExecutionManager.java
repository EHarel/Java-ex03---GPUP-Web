package task;

import graph.Target;
import task.enums.ExecutionStatus;
import task.enums.TaskResult;
import task.execution.ExecutionDTO;
import users.User;

import java.util.*;

public class ExecutionManager {
    List<Execution> executions;
    int indexOfNextExecutionToRelease;

    public ExecutionManager() {
        executions = new ArrayList<>();
        indexOfNextExecutionToRelease = 0;
    }

    public synchronized Integer getExecutionPaymentPerTarget(String executionName) {
        Integer payment = new Integer(0);

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                payment = execution.getPricePerTarget();
                break;
            }
        }

        return payment;
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
    public synchronized Collection<Target> getTargetsForUser(User user, Integer targetCount) {
        Collection<Target> chosenTargets = new ArrayList<>();
        Collection<Execution> activeExecutionsUserIsPartOf = getActiveExecutionsUserIsActiveIn(user);

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

    /**
     * This goes over all the executions and checks:
     * -   Is the execution active?
     * -   Is the user part of it?
     * -   Is the user active in it?
     */
    private Collection<Execution> getActiveExecutionsUserIsActiveIn(User user) {
        Collection<Execution> activeExecutionsUserIsPartOf = new ArrayList<>();

        for (Execution execution : executions) {
            if (execution.getExecutionStatus() == ExecutionStatus.EXECUTING) {
                if (user.isParticipatingInExecution(execution.getExecutionName(), true)) {
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

    public synchronized AffectedTargetsData updateTargetTaskResult(String executionName, String targetName, TaskResult taskResult) {
        AffectedTargetsData affectedTargetsData = null;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                affectedTargetsData = execution.updateTargetTaskResult(targetName, taskResult);
                break;
            }
        }

        return affectedTargetsData;
    }

    /**
     * This method returns a collection of all the executions a user participates in.
     *
     * @param filter_executionMustBeRunning determines whether to choose executions that are running or any execution
     *                                      the user is participating in (paused or stopped as well).
     * @param filter_userMustBeActive       determines whether to choose executions that the user is active in or also
     *                                      executions the user paused his participation in.
     */
    public synchronized Collection<Execution> getExecutionsUserParticipates(User user,
                                                                            boolean filter_executionMustBeRunning,
                                                                            boolean filter_userMustBeActive) {
        Collection<Execution> resExecutions = new LinkedList<>();

        for (Execution execution :
                executions) {
            boolean addExecution = false;

            if (user.isParticipatingInExecution(execution.getExecutionName(), filter_userMustBeActive)) {
                if (filter_executionMustBeRunning) {
                    addExecution = (execution.getExecutionStatus() == ExecutionStatus.EXECUTING);
                } else {
                    addExecution = true;
                }
            }

            if (addExecution) {
                resExecutions.add(execution);
            }
        }

        return resExecutions;
    }

    public synchronized boolean removeUserFromConfiguration(String executionName, String userName) {
        boolean userRemoved = false;

        // TODO: allow removing only if task is not done?
        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                userRemoved = execution.removeWorker(userName);
                break;
            }
        }

        return userRemoved;
    }

    public synchronized ExecutionStatus getExecutionStatus(String executionNameFromParameter) {
        ExecutionStatus executionStatus = null;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionNameFromParameter)) {
                executionStatus = execution.getExecutionStatus();
                break;
            }
        }

        return executionStatus;
    }

    public synchronized void addTargetLog(String executionName, String targetName, String targetLog) {
        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                execution.addTargetLog(targetName, targetLog);
                break;
            }
        }
    }
}
