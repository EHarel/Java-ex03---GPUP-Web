package task.execution;

import graph.GraphDTO;
import task.TaskType;
import task.configuration.ConfigurationDTO;
import task.configuration.ConfigurationDTOCompilation;
import task.configuration.ConfigurationDTOSimulation;

import java.util.ArrayList;
import java.util.Collection;

public class ExecutionDTO {
    private String executionName;
    private String creatingUser;

    //    private ConfigurationDTO configDTO;
    private ConfigurationDTOSimulation configDTOSim;
    private ConfigurationDTOCompilation configDTOComp;

    private GraphDTO graphDTO;
    private TaskType taskType;
    private Integer pricePerTarget;
    private Integer totalWorkers;
    private ExecutionStatus executionStatus;
    private Collection<String> participatingUsersNames;

    public ExecutionDTO() {
    }

    public ExecutionDTO(String executionName,
                        String creatingUser,
                        GraphDTO graphDTO,
                        TaskType taskType,
                        Integer pricePerTarget,
                        int totalWorkers,
                        ExecutionStatus executionStatus,
                        ConfigurationDTOCompilation configCompDTO,
                        ConfigurationDTOSimulation configSimDTO,
                        Collection<String> participatingUsersNames) {
        this.executionName = executionName;
        this.creatingUser = creatingUser;
//        this.configDTOSim = configDTO;
        this.graphDTO = graphDTO;
        this.taskType = taskType;
        this.pricePerTarget = pricePerTarget;
        this.totalWorkers = totalWorkers;
        this.executionStatus = executionStatus;

        this.configDTOComp = configCompDTO;
        this.configDTOSim = configSimDTO;

        this.participatingUsersNames = new ArrayList<>();
        if (participatingUsersNames != null) {
            participatingUsersNames.forEach(s -> {
                this.participatingUsersNames.add(s);
            });
        }
    }

    public String getExecutionName() {
        return executionName;
    }

    public String getCreatingUser() {
        return creatingUser;
    }

    public ConfigurationDTO getConfigDTO() {
        ConfigurationDTO configDTO = null;

        if (taskType == TaskType.SIMULATION) {
            configDTO = configDTOSim;
        } else {
            configDTO = configDTOComp;
        }
        return configDTO;
    }

    public GraphDTO getGraphDTO() {
        return graphDTO;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Integer getPricePerTarget() {
        return pricePerTarget;
    }

    public int getTotalWorkers() {
        return totalWorkers;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public void setCreatingUser(String creatingUser) {
        this.creatingUser = creatingUser;
    }

//    public void setConfigDTO(ConfigurationDTO configDTO) {
//        this.configDTOSim = configDTO;
//    }

    public void setGraphDTO(GraphDTO graphDTO) {
        this.graphDTO = graphDTO;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setPricePerTarget(Integer pricePerTarget) {
        this.pricePerTarget = pricePerTarget;
    }

    public void setTotalWorkers(Integer totalWorkers) {
        this.totalWorkers = totalWorkers;
    }

    public void setExecutionStatus(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public ConfigurationDTOSimulation getConfigDTOSim() {
        return configDTOSim;
    }

    public ConfigurationDTOCompilation getConfigDTOComp() {
        return configDTOComp;
    }

    public Collection<String> getParticipatingUsersNames() {
        return participatingUsersNames;
    }

    public void setParticipatingUsersNames(Collection<String> participatingUsersNames) {
        this.participatingUsersNames = participatingUsersNames;
    }

    public Boolean isUserInList(String username) {
        boolean userInList = false;

        for (String user :
                participatingUsersNames) {
            if (user.equals(username)) {
                userInList = true;
                break;
            }
        }

        return userInList;
    }
}
