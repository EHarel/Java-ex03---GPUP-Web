package users;

public class WorkerExecutionStanding {
    private String executionName;
    private boolean isActive;
    private int totalTargetsProcessedWithAnyResult;
    private int totalCreditEarned;

    public WorkerExecutionStanding(String executionName) {
        this.executionName = executionName;
        this.isActive = true;
        this.totalTargetsProcessedWithAnyResult = 0;
        this.totalCreditEarned = 0;
    }

    public String getExecutionName() {
        return executionName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTotalTargetsProcessedWithAnyResult() {
        return totalTargetsProcessedWithAnyResult;
    }

    public int getTotalCreditEarned() {
        return totalCreditEarned;
    }

    public void incrementProcessedTargets() {
        totalTargetsProcessedWithAnyResult++;
    }

    public void addPayment(int paymentToAdd) {
        this.totalCreditEarned += paymentToAdd;
    }
}
