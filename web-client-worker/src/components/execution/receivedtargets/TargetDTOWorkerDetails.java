package components.execution.receivedtargets;

import graph.SerialSetDTO;
import graph.TargetDTO;
import javafx.scene.control.cell.PropertyValueFactory;
import task.enums.TaskResult;
import task.enums.TaskType;

import java.util.Collection;

public class TargetDTOWorkerDetails extends TargetDTO {
    /* ------------------------------------------ CUSTOM FIELDS ------------------------------------------- */
    private String executionName;
    private TaskType taskType;
    private String targetName;
    private TaskResult taskResult;
    private Integer paycheck;

    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------- CONSTRUCTOR AND INITIALIZER ------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    public TargetDTOWorkerDetails(TargetDTO targetDTO) {
        super(targetDTO.getName(),
                targetDTO.getDependency(),
                targetDTO.getTargetsThisDirectlyDependsOn(),
                targetDTO.getTargetsThisIsDirectlyRequiredFor(),
                targetDTO.getUserData(),
                targetDTO.getSerialSetsDTOS(),
                targetDTO.getTaskStatusDTO());

        this.executionName = targetDTO.getExecutionName();
        this.taskType = targetDTO.getTaskStatusDTO().getConfigData().getTaskType();
        this.targetName = targetDTO.getName();
        this.taskResult = targetDTO.getTaskStatusDTO().getTaskResult();
        this.paycheck = null;
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- GETTERS AND SETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public Integer getPaycheck() {
        return paycheck;
    }

    public void setPaycheck(Integer paycheck) {
        this.paycheck = paycheck;
    }
}
