package task.execution;

import graph.GraphDTO;
import task.enums.ExecutionStatus;
import task.enums.TaskStartPoint;
import task.enums.TaskType;
import task.configuration.ConfigurationDTO;
import task.configuration.ConfigurationDTOCompilation;
import task.configuration.ConfigurationDTOSimulation;

import java.util.ArrayList;
import java.util.Collection;

public class ExecutionDTO {
    private String executionName;
    private String executionOriginalName;
    private String creatingUser;
    private ConfigurationDTOSimulation configDTOSim;
    private ConfigurationDTOCompilation configDTOComp;
    private GraphDTO originalGraphDTO;
    private GraphDTO startGraphDTO;
    private GraphDTO endGraphDTO;
    private TaskType taskType;
    private TaskStartPoint taskStartPoint;
    private Integer pricePerTarget;
    private Integer totalWorkers;
    private ExecutionStatus executionStatus;
    private Collection<String> participatingUsersNames;
    private Boolean isExecutionFullyCompleted;
    private int targetStateCount_WAITING;
    private int targetStateCount_IN_PROCESS;
    private Float executionProgress;
    private String executionLog;


    public ExecutionDTO() {
    }

    public ExecutionDTO(String executionName,
                        String executionOriginalName,
                        String creatingUser,
                        GraphDTO originalGraphDTO,
                        GraphDTO startGraphDTO,
                        GraphDTO endGraphDTO,
                        TaskType taskType,
                        TaskStartPoint taskStartPoint,
                        Integer pricePerTarget,
                        int totalWorkers,
                        ExecutionStatus executionStatus,
                        ConfigurationDTOCompilation configCompDTO,
                        ConfigurationDTOSimulation configSimDTO,
                        Collection<String> participatingUsersNames,
                        boolean isExecutionFullyCompleted,
                        int targetStateCount_WAITING,
                        int targetStateCount_IN_PROCESS,
                        float executionProgress,
                        String executionLog) {
        this.executionName = executionName;
        this.executionOriginalName = executionOriginalName;
        this.creatingUser = creatingUser;
        this.originalGraphDTO = originalGraphDTO;
        this.startGraphDTO = startGraphDTO;
        this.endGraphDTO = endGraphDTO;
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

        this.taskStartPoint = taskStartPoint;
        this.isExecutionFullyCompleted = new Boolean(isExecutionFullyCompleted);

        this.targetStateCount_WAITING =  targetStateCount_WAITING;
        this.targetStateCount_IN_PROCESS = targetStateCount_IN_PROCESS;
        this.executionProgress = executionProgress;
        this.executionLog = executionLog;
    }

    public String getExecutionName() {
        return executionName;
    }

    public String getExecutionOriginalName() {
        return executionOriginalName;
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

    public GraphDTO getOriginalGraphDTO() {
        return originalGraphDTO;
    }

    public GraphDTO getStartGraphDTO() {
        return startGraphDTO;
    }

    public GraphDTO getEndGraphDTO() {
        return endGraphDTO;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public TaskStartPoint getTaskStartPoint() {
        return taskStartPoint;
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


    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
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

    public Boolean getIsExecutionFullyCompleted() {
        return isExecutionFullyCompleted;
    }

    public Boolean getExecutionFullyCompleted() {
        return isExecutionFullyCompleted;
    }

    public int getTargetStateCount_WAITING() {
        return targetStateCount_WAITING;
    }

    public int getTargetStateCount_IN_PROCESS() {
        return targetStateCount_IN_PROCESS;
    }

    public float getExecutionProgress() {
        return executionProgress;
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

    public String getExecutionLog() {
        return this.executionLog;
    }
}
