package components.graph.targettable;

import console.task.TaskGeneral;
import graph.TargetDTO;
import javafx.scene.control.CheckBox;

public class TargetDTOTable extends TargetDTO {
    private CheckBox checkbox;
    private Integer numberTargetsThisTotalRequiredFor;
    private Integer numberTargetsThisTotalDependsOn;
    private Integer serialSetCount;
    private TargetState targetState;
    private TaskStatusDTO.TaskResult taskResult;

    public TargetDTOTable(TargetDTO targetDTO, Integer totalRequiredFor, Integer totalDependsOn) {
        super(
                targetDTO.getName(),
                targetDTO.getDependency(),
                targetDTO.getTargetsThisDirectlyDependsOn(),
                targetDTO.getTargetsThisIsDirectlyRequiredFor(),
                targetDTO.getUserData(),
                targetDTO.getSerialSetsDTOS(),
                targetDTO.getTaskStatusDTO()
        );

        super.setTimeOfEntryIntoTaskQueue(targetDTO.getTimeOfEntryIntoTaskQueue());
        super.setTimeOfExitOutOfTaskQueue(targetDTO.getTimeOfExitOutOfTaskQueue());

        this.checkbox = new CheckBox();
        this.numberTargetsThisTotalRequiredFor = totalRequiredFor;
        this.numberTargetsThisTotalDependsOn = totalDependsOn;
        this.serialSetCount = targetDTO.getSerialSetsDTOS().size();
        this.targetState = targetDTO.getTaskStatusDTO().getState();
        this.taskResult = targetDTO.getTaskStatusDTO().getResult();
    }

    public CheckBox getCheckbox() { return this.checkbox; }

    public Integer getNumberTargetsThisTotalRequiredFor() {
        return numberTargetsThisTotalRequiredFor;
    }

    public Integer getNumberTargetsThisTotalDependsOn() {
        return numberTargetsThisTotalDependsOn;
    }

    public Integer getSerialSetCount() {
        return serialSetCount;
    }

    public TargetState getTargetState() { return this.targetState; }

    public TaskStatusDTO.TaskResult getTaskResult() { return this.taskResult; }
}