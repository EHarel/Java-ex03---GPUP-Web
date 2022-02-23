package users;

import utilsharedall.UserType;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private UserType userType;
    private Map<String, WorkerExecutionStanding> executionName2Standing;

    public User(String name, UserType userType) {
        this.name = name;
        this.userType = userType;
        this.executionName2Standing = new HashMap<>();
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public UserType getUserType() {
        return userType;
    }

    public UserDTO toDTO() {
        return new UserDTO(name, userType);
    }

    /**
     * @return true if user is participating in name of execution given.
     */

    /**
     *
     * @param executionName execution to check.
     * @param filter_UserMustBeActive determines whether result must also consider if user is active or paused his
     *                                participation. If filter is set to TRUE, then the result of the method will be
     *                                TRUE only if user participates and is currently active.
     * @return true if user participates in the execution, and based on the filter as well.
     */
    public boolean isParticipatingInExecution(String executionName, boolean filter_UserMustBeActive) {
        boolean isParticipating = false;

        WorkerExecutionStanding executionStanding = executionName2Standing.get(executionName);

        if (executionStanding != null) {
            if (filter_UserMustBeActive) {
                isParticipating = executionStanding.isActive();
            } else {
                isParticipating = true;
            }
        }

        return isParticipating;
    }

    /**
     * This method increments the amount of targets the user processed for an execution.
     */
    public void incrementExecutionProcessedTargets(String executionName) {
        WorkerExecutionStanding executionStanding = executionName2Standing.get(executionName);

        if (executionStanding != null) {
            executionStanding.incrementProcessedTargets();
        }
    }

    public void addPaymentToExecution(String executionName, int paymentToAdd) {
        WorkerExecutionStanding executionStanding = executionName2Standing.get(executionName);

        if (executionStanding != null) {
            executionStanding.addPayment(paymentToAdd);
        }
    }

    public int getNumberOfProcessedTargetsOfExecution(String executionName) {
        int numberOfProcessedTargets = 0;

        WorkerExecutionStanding workerExecutionStanding = executionName2Standing.get(executionName);
        if (workerExecutionStanding != null) {
            numberOfProcessedTargets = workerExecutionStanding.getTotalTargetsProcessedWithAnyResult();
        }

        return numberOfProcessedTargets;
    }

    public int getCreditsEarnedForExecution(String executionName) {
        int creditsEarned = 0;

        WorkerExecutionStanding workerExecutionStanding = executionName2Standing.get(executionName);
        if (workerExecutionStanding != null) {
            creditsEarned = workerExecutionStanding.getTotalCreditEarned();
        }

        return creditsEarned;
    }


    /**
     *
     * @param executionName new execution to add to collection of executions user participates in.
     * @return true if new execution added, false if such execution already exists.
     */
    public synchronized boolean addNewParticipatingExecution(String executionName) {
        boolean added = false;

        WorkerExecutionStanding workerExecutionStanding = executionName2Standing.get(executionName);
        if (workerExecutionStanding == null) {
            workerExecutionStanding = new WorkerExecutionStanding(executionName);
            executionName2Standing.put(executionName, workerExecutionStanding);
            added = true;
        }

        return added;
    }

    public boolean isActiveInExecution(String executionName) {
        boolean isActive = false;

        WorkerExecutionStanding workerExecutionStanding = executionName2Standing.get(executionName);
        if (workerExecutionStanding != null) {
            isActive = workerExecutionStanding.isActive();
        }

        return isActive;
    }

    public synchronized boolean updateExecutionParticipation(String executionName, boolean isActiveInExecution) {
        boolean updated = false;

        WorkerExecutionStanding workerExecutionStanding = executionName2Standing.get(executionName);
        if (workerExecutionStanding != null) {
            workerExecutionStanding.setActive(isActiveInExecution);
            updated = true;
        }

        return updated;
    }

    public synchronized boolean removeParticipatingExecution(String executionNameFromParameter) {
        boolean removed = false;

        WorkerExecutionStanding workerExecutionStanding = executionName2Standing.remove(executionNameFromParameter);

        removed = workerExecutionStanding != null;

        return removed;
    }
}
