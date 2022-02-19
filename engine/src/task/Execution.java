package task;

import graph.DependenciesGraph;
import graph.GraphDTO;
import graph.TargetDTO;
import task.configuration.*;
import task.execution.ExecutionDTO;
import task.execution.ExecutionStatus;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class Execution implements Cloneable {
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ DATA MEMBERS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    protected String executionName;
    private final String creatingUser;
    protected final TaskType taskType;
    protected final int executionNumber;
    private final DependenciesGraph startingGraph;
    private Configuration configuration;
    private TaskProcess.StartPoint startPoint;
    private ProcessedData processedData;
    protected boolean taskComplete;
    private DependenciesGraph endGraph;
    private Instant startInstant;
    private Instant endInstant;
    private final Integer pricePerTarget;
    private ExecutionStatus executionStatus;
    private Collection<String> participatingWorkerNames;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- CONSTRUCTOR -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public Execution(
            TaskType taskType,
            int executionNumber,
            DependenciesGraph startingGraph,
            Configuration configuration,
            ProcessedData previousProcessedData) {
        this.taskType = taskType;
        this.creatingUser = "Old code";
        this.executionNumber = executionNumber;
        this.startingGraph = startingGraph;
        this.configuration = configuration;
        this.processedData = new ProcessedData(previousProcessedData);
        this.pricePerTarget = null;
    }

    public Execution(ExecutionDTO executionDTO) {
        this.executionName = executionDTO.getExecutionName();
        this.creatingUser = executionDTO.getCreatingUser();
        this.taskType = executionDTO.getTaskType();
        this.executionNumber = 0;
        this.startingGraph = new DependenciesGraph(executionDTO.getGraphDTO());
        try {
            switch (taskType) {
                case COMPILATION:
                    this.configuration = new ConfigurationCompilation((ConfigurationDTOCompilation) executionDTO.getConfigDTO());
                    break;
                case SIMULATION:
                    this.configuration = new ConfigurationSimulation((ConfigurationDTOSimulation) executionDTO.getConfigDTO());
                    break;
            }
        } catch (Exception ignore) {
        }

        this.processedData = new ProcessedData(null);
        this.taskComplete = false; // TODO: should this be taken from the DTO as well?
        this.pricePerTarget = executionDTO.getPricePerTarget();
        this.executionStatus = executionDTO.getExecutionStatus();
        this.participatingWorkerNames = new ArrayList<>();
        executionDTO.getParticipatingUsersNames().forEach(s -> {this.participatingWorkerNames.add(s); });

        // private TaskProcess.StartPoint startPoint;
    }


    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public DependenciesGraph getStartingGraph() {
        return startingGraph;
    }

    public int getExecutionNumber() {
        return executionNumber;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setStartPoint(TaskProcess.StartPoint startPoint) {
        this.startPoint = startPoint;
    }

    public void setTaskComplete(boolean taskComplete) {
        this.taskComplete = taskComplete;
    }

    public boolean getTaskComplete() {
        return this.taskComplete;
    }

    public DependenciesGraph getEndGraph() {
        return endGraph;
    }

    public void setEndGraph(DependenciesGraph endGraph) {
        this.endGraph = endGraph;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
    }

    public ProcessedData getProcessedData() {
        return processedData;
    }

    public void setProcessedData(ProcessedData processedData) {
        this.processedData = processedData;
    }

    public TargetDTO getSpecificTargetData(String targetName) {
        return this.processedData.getSpecificTargetData(targetName);
    }

    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------- UTILITY ---------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public Execution clone() {
        Execution clone = new Execution(
                this.taskType,
                this.executionNumber,
                this.startingGraph.duplicate(),
                this.configuration.clone(),
                null
        );

        clone.setStartPoint(this.startPoint);
        clone.setTaskComplete(this.taskComplete);
        clone.setEndGraph(this.endGraph.duplicate());
        clone.setStartInstant(this.startInstant);
        clone.setEndInstant(this.endInstant);
        clone.setProcessedData(this.processedData.clone());

        return clone;
    }

    public ExecutionDTO toDTO() {
        ConfigurationDTOSimulation configSimDTO = null;
        ConfigurationDTOCompilation configCompDTO = null;

        switch (configuration.getTaskType()) {
            case SIMULATION:
                configSimDTO = (ConfigurationDTOSimulation) configuration.toDTO();
                break;
            case COMPILATION:
                configCompDTO = (ConfigurationDTOCompilation) configuration.toDTO();
                break;
        }

        ExecutionDTO executionDTO = new ExecutionDTO(
                this.executionName,
                this.creatingUser,
                // this.configuration.toDTO(),
                this.startingGraph.toDTO(),
                this.taskType,
                this.pricePerTarget,
                this.participatingWorkerNames.size(),
                this.executionStatus,
                configCompDTO,
                configSimDTO,
                this.participatingWorkerNames
        );

        return executionDTO;
    }

    /**
     * @return True if the name was added, false if a worker by given name already exists in the list.
     */
    public boolean addNewWorkerName(String newWorkerName) {
        boolean added = false;
        boolean existingName = false;

        for (String existingWorker :
                participatingWorkerNames) {
            if (existingWorker.equals(newWorkerName)) {
                existingName = true;
                break;
            }
        }

        if (!existingName) {
            participatingWorkerNames.add(newWorkerName);
            added = true;
        }


        return added;
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- INNER CLASS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public class ProcessedData implements Serializable, Cloneable {
        //        private static final long serialVersionUID = 2; // 25-Nov-2021
        private static final long serialVersionUID = 3; // 27-Nov-2021


        protected Map<String, TargetDTO> name2TargetReport;

        protected Collection<TargetDTO> failedTargets;
        protected Collection<TargetDTO> successfulTargets;
        protected Collection<TargetDTO> warningTargets;
        protected Collection<TargetDTO> successTargetsAllRunWithWarnings;

        public ProcessedData(ProcessedData previousProcessedData) {
//            name2TargetReport = new HashMap<>();
            name2TargetReport = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            failedTargets = new LinkedHashSet<>();
            successfulTargets = new LinkedHashSet<>();
            warningTargets = new LinkedHashSet<>();
            successTargetsAllRunWithWarnings = new LinkedList<>();

            initReports();
            initSuccessTargetsFromPreviousRuns(previousProcessedData);
        }

        private void initReports() {
            for (TargetDTO targetDTO : startingGraph.getAllTargetData()) {
//                TargetDTO clonedData = targetDTO.cloneWithoutReport();
                TargetDTO clonedData = targetDTO.clone();
                name2TargetReport.put(targetDTO.getName(), clonedData);
            }
        }

        private void initSuccessTargetsFromPreviousRuns(ProcessedData previousProcessedData) {
            if (previousProcessedData != null) {
                for (TargetDTO targetDTO : previousProcessedData.getSuccessTargetsAllRunWithWarnings()) {
                    this.successTargetsAllRunWithWarnings.add(targetDTO.clone());
                }
            }
        }

        public TargetDTO getSpecificTargetData(String targetName) {
            TargetDTO res = null;

            if (targetName != null && !targetName.trim().isEmpty()) {
                TargetDTO targetDTO = name2TargetReport.get(targetName);

                if (targetDTO == null) { // Double check because the case-insensitive tree isn't working!
                    for (TargetDTO target : name2TargetReport.values()) {
                        if (target.getName().equalsIgnoreCase(targetName)) {
                            res = target.clone();
                            break;
                        }
                    }
                } else {
                    res = targetDTO.clone();
                }
            }

            return res;
        }

        public Collection<TargetDTO> getAllTargetData() {
            return name2TargetReport.values();
        }

        public Collection<TargetDTO> getUnprocessedTargets() {
            Collection<TargetDTO> allUnprocessed = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() == null || targetDTO.getTaskStatusDTO().getResult() == TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED) {
                    allUnprocessed.add(targetDTO);
                }
            }

            return allUnprocessed;
        }

        public Collection<TargetDTO> getAllProcessedTargetsOfAllResults() {
            Collection<TargetDTO> allProcessed = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() != null && targetDTO.getTaskStatusDTO().getResult() != TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED) {
                    allProcessed.add(targetDTO);
                }
            }

            return allProcessed;
        }

        public Collection<TargetDTO> getFailedTargets() {
            return failedTargets;
        }

        public Collection<TargetDTO> getSuccessfulTargets() {
            return successfulTargets;
        }

        public Collection<TargetDTO> getWarningTargets() {
            return warningTargets;
        }

        public Collection<TargetDTO> getProcessedTargetsNoFailure() {
            Collection<TargetDTO> nonFailed = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() != null
                        &&
                        targetDTO.getTaskStatusDTO().getResult() != TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED
                        &&
                        targetDTO.getTaskStatusDTO().getResult() != TargetDTO.TaskStatusDTO.TaskResult.FAILURE) {
                    nonFailed.add(targetDTO);
                }
            }

            return nonFailed;
        }

        public Collection<TargetDTO> getTargetsLeftToCompletion() {
            Collection<TargetDTO> leftToCompletion = new LinkedList<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                if (targetDTO.getTaskStatusDTO() == null
                        ||
                        targetDTO.getTaskStatusDTO().getResult() == TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED
                        ||
                        targetDTO.getTaskStatusDTO().getResult() == TargetDTO.TaskStatusDTO.TaskResult.FAILURE) {
                    leftToCompletion.add(targetDTO);
                }
            }

            return leftToCompletion;
        }

        public Collection<TargetDTO> getSuccessTargetsAllRunWithWarnings() {
            return successTargetsAllRunWithWarnings;
        }

        @Override
        public ProcessedData clone() {
            ProcessedData clone = new ProcessedData(null);

            clone.name2TargetReport = cloneMap();
            clone.failedTargets = cloneCollection(this.failedTargets);
            clone.successfulTargets = cloneCollection(this.successfulTargets);
            clone.warningTargets = cloneCollection(this.warningTargets);
            clone.successTargetsAllRunWithWarnings = cloneCollection(this.successTargetsAllRunWithWarnings);

            return clone;
        }

        private Map<String, TargetDTO> cloneMap() {
            Map<String, TargetDTO> newName2TargetReport = new HashMap<>();

            for (TargetDTO targetDTO : name2TargetReport.values()) {
                newName2TargetReport.put(targetDTO.getName(), targetDTO.clone());
            }

            return newName2TargetReport;
        }

        private Collection<TargetDTO> cloneCollection(Collection<TargetDTO> collection) {
            Collection<TargetDTO> clone = null;

            if (collection != null) {
                clone = new LinkedList<>();

                for (TargetDTO targetDTO : collection) {
                    clone.add(targetDTO.clone());
                }
            }

            return clone;
        }

        /* ---------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------- REPORT MANAGEMENT ----------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------- */
        /* ---------------------------------------------------------------------------------------------------- */
        public synchronized void addTargetData(TargetDTO targetDTO) {
            name2TargetReport.put(targetDTO.getName(), targetDTO);

            if (targetDTO.getTaskStatusDTO() != null) {
                switch (targetDTO.getTaskStatusDTO().getResult()) {
                    case FAILURE:
                        failedTargets.add(targetDTO);
                        break;
                    case SUCCESS:
                        successfulTargets.add(targetDTO);
                        successTargetsAllRunWithWarnings.add(targetDTO);
                        break;
                    case SUCCESS_WITH_WARNINGS:
                        warningTargets.add(targetDTO);
                        successTargetsAllRunWithWarnings.add(targetDTO);
                        break;
                }
            }
        }
    }
}