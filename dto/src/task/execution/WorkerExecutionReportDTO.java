package task.execution;

import task.enums.ExecutionStatus;

public class WorkerExecutionReportDTO {
    private String executionName;
    private Integer totalWorkerCount;
    private Float executionProgress;
    private Integer targetsUserProcessedWithAnyResult;
    private Integer totalCreditUserEarned;
    private Boolean isUserActive;
    private ExecutionStatus executionStatus;

    public WorkerExecutionReportDTO(String executionName,
                                    int totalWorkerCount,
                                    float executionProgress,
                                    int targetsUserProcessedWithAnyResult,
                                    int totalCreditUserEarned,
                                    boolean isUserActive,
                                    ExecutionStatus executionStatus) {
        this.executionName = executionName;
        this.totalWorkerCount = totalWorkerCount;
        this.executionProgress = executionProgress;
        this.targetsUserProcessedWithAnyResult = targetsUserProcessedWithAnyResult;
        this.totalCreditUserEarned = totalCreditUserEarned;
        this.isUserActive = isUserActive;
        this.executionStatus = executionStatus;
    }

    public String getExecutionName() {
        return executionName;
    }

    public Integer getTotalWorkerCount() {
        return totalWorkerCount;
    }

    public Float getExecutionProgress() {
        return executionProgress;
    }

    public Integer getTargetsUserProcessedWithAnyResult() {
        return targetsUserProcessedWithAnyResult;
    }

    public Integer getTotalCreditUserEarned() {
        return totalCreditUserEarned;
    }

    public Boolean getUserActive() {
        return isUserActive;
    }

    public Boolean isUserActive() {
        return isUserActive;
    }

    public Boolean getIsUserActive() {
        return isUserActive;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }
}
