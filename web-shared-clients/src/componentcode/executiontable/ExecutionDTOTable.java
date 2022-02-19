package componentcode.executiontable;

import task.execution.ExecutionDTO;

public class ExecutionDTOTable extends ExecutionDTO {
    private String graphName;
    private Integer targetCount_Total;
    private Integer targetCount_Independent;
    private Integer targetCount_Leaf;
    private Integer targetCount_Middle;
    private Integer targetCount_Root;
    private Integer totalPrice;
    private Boolean userIsSubscribed;

    public ExecutionDTOTable(ExecutionDTO executionDTO, String username) {
        super(
                executionDTO.getExecutionName(),
                executionDTO.getCreatingUser(),
                // executionDTO.getConfigDTO(),
                executionDTO.getGraphDTO(),
                executionDTO.getTaskType(),
                executionDTO.getPricePerTarget(),
                executionDTO.getTotalWorkers(),
                executionDTO.getExecutionStatus(),
                executionDTO.getConfigDTOComp(),
                executionDTO.getConfigDTOSim(),
                executionDTO.getParticipatingUsersNames()
        );

        this.userIsSubscribed = executionDTO.isUserInList(username);
        this.graphName = executionDTO.getGraphDTO().getGraphName();
        this.targetCount_Total = executionDTO.getGraphDTO().getCountAllTargets();
        this.targetCount_Independent = executionDTO.getGraphDTO().getCountIndependents();
        this.targetCount_Leaf = executionDTO.getGraphDTO().getCountLeaves();
        this.targetCount_Middle = executionDTO.getGraphDTO().getCountMiddles();
        this.targetCount_Root = executionDTO.getGraphDTO().getCountRoots();
        this.totalPrice = super.getPricePerTarget() * targetCount_Total;
    }

    public String getGraphName() {
        return graphName;
    }

    public Integer getTargetCount_Total() {
        return targetCount_Total;
    }

    public Integer getTargetCount_Independent() {
        return targetCount_Independent;
    }

    public Integer getTargetCount_Leaf() {
        return targetCount_Leaf;
    }

    public Integer getTargetCount_Middle() {
        return targetCount_Middle;
    }

    public Integer getTargetCount_Root() {
        return targetCount_Root;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public Boolean getUserIsSubscribed() {
        return userIsSubscribed;
    }
}
