package graph;

import task.configuration.ConfigurationDTO;
import task.configuration.ConfigurationDTOCompilation;
import task.configuration.ConfigurationDTOSimulation;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class TargetDTO implements Serializable, Cloneable {
//    private static final long serialVersionUID = 4; // 08-Dec-2021 - Added serial sets
    private static final long serialVersionUID = 5; // 12-Jan-2021 - Made fields protected


    public enum TargetState implements Serializable {
        FROZEN, SKIPPED, WAITING, IN_PROCESS, FINISHED
    }

    public enum Dependency implements Serializable {
        LEAF, ROOT, MIDDLE, INDEPENDENT
    }

    protected final String name;
    protected final Dependency dependency;
    protected final Collection<String> targetsThisDirectlyDependsOn;
    protected final Collection<String> targetsThisIsDirectlyRequiredFor;
    protected final String userData;
    protected final Collection<SerialSetDTO> serialSetsDTOS;
    protected final TaskStatusDTO taskStatusDTO;

    protected Instant timeOfEntryIntoTaskQueue = null;
    protected Instant timeOfExitOutOfTaskQueue = null;

    public TargetDTO(String name,
                     Dependency dependency,
                     Collection<String> namesTargetsThisDependsOn,
                     Collection<String> namesTargetsThisIsRequiredFor,
                     String userData,
                     Collection<SerialSetDTO> serialSetsDTOS,
                     TaskStatusDTO targetReportData) {
        this.name = name;
        this.dependency = dependency;
        this.targetsThisDirectlyDependsOn = namesTargetsThisDependsOn;
        this.targetsThisIsDirectlyRequiredFor = namesTargetsThisIsRequiredFor;
        this.userData = userData;
        this.serialSetsDTOS = serialSetsDTOS;
        this.taskStatusDTO = targetReportData;
    }

    public Instant getTimeOfExitOutOfTaskQueue() {
        return timeOfExitOutOfTaskQueue;
    }

    public void setTimeOfExitOutOfTaskQueue(Instant timeOfExitOutOfTaskQueue) {
        this.timeOfExitOutOfTaskQueue = timeOfExitOutOfTaskQueue;
    }

    public void setTimeOfEntryIntoTaskQueue(Instant timeOfEntry) {
        this.timeOfEntryIntoTaskQueue = timeOfEntry;
    }

    public Instant getTimeOfEntryIntoTaskQueue() {
        return timeOfEntryIntoTaskQueue;
    }

    public String getName() {
        return name;
    }

    public Dependency getDependency() {
        return dependency;
    }

    public TaskStatusDTO getTaskStatusDTO() { return taskStatusDTO; }

    public String getUserData() {
        return userData;
    }

    public Collection<SerialSetDTO> getSerialSetsDTOS() { return serialSetsDTOS; }

    public Collection<String> getTargetsThisDirectlyDependsOn() {
        return targetsThisDirectlyDependsOn;
    }

    public Collection<String> getTargetsThisIsDirectlyRequiredFor() {
        return targetsThisIsDirectlyRequiredFor;
    }

    public Integer getNoTargetsThisDirectlyDependsOn() { return targetsThisDirectlyDependsOn.size(); }

    public Integer getNoTargetsThisIsDirectlyRequiredFor() { return targetsThisIsDirectlyRequiredFor.size(); }

    @Override
    public TargetDTO clone() {
        TaskStatusDTO report = null;

        if (taskStatusDTO != null) {
            report = this.taskStatusDTO.clone();
        }

        return actualClone(report);
    }

//    public TargetDTO cloneWithoutReport() {
//        return actualClone(null);
//    }
//
//    public TargetDTO cloneWithNewReport(TaskStatusDTO targetReportData) {
//        TaskStatusDTO newReport = null;
//
//        if (targetReportData != null) {
//            newReport = targetReportData.clone();
//        }
//
//        return actualClone(newReport);
//    }

    private TargetDTO actualClone(TaskStatusDTO reportToAdd) {
        return new TargetDTO(
                this.name,
                this.dependency,
                this.targetsThisDirectlyDependsOn,
                this.targetsThisIsDirectlyRequiredFor,
                this.userData,
                this.serialSetsDTOS,
                reportToAdd
        );
    }

    /**
     * Upon performing a task, a report is generated for EACH target, describing the task process:
     * (1) Start time
     * (2) Free data upon Target, if such exists
     * (3) End time, and whether SUCCESS \ WARNING \ FAILURE
     * (4) All targets that may have opened up for processing due to this one
     * (5) All targets that may have closed down to processing (FROZEN) due to this one (if it FAILED)
     */
    public static class TaskStatusDTO implements Serializable, Cloneable {
        public task.configuration.ConfigurationDTO getConfigData() {
            ConfigurationDTO configDTO = configDTOComp != null ? configDTOComp : configDTOSim;

            return configDTO;
        }

        public enum TaskResult implements Serializable, Cloneable {
            SUCCESS, SUCCESS_WITH_WARNINGS, FAILURE, UNPROCESSED
        }

        private static final long serialVersionUID = 6; // 03-Dec-2021 - participates in execution

        private final int executionNum;
        private final boolean participatesInExecution;
        private final Instant startInstant;
        private final Instant endInstant;

        // Changes for ex03 json constructors
//        private final ConfigurationDTO configData;
        private ConfigurationDTOSimulation configDTOSim;
        private ConfigurationDTOCompilation configDTOComp;


        private final TaskResult taskResult;
        private final TargetState targetState;
        private final Collection<String> targetsOpenedAsResult;
        private final Collection<String> targetsSkippedAsResult;
        private final Collection<List<String>> targetsSkippedAsResult_AllPaths;
        private final String errorDetails;

        public TaskStatusDTO(
                             int executionNum,
                             boolean participatesInExecution,
                             Instant startInstant,
                             Instant endInstant,
//                             ConfigurationDTO configData,
                             ConfigurationDTOSimulation configDTOSim,
                             ConfigurationDTOCompilation configDTOComp,
                             TaskResult taskResult,
                             TargetState taskState,
                             Collection<String> targetsThatAreWaitingAsResult,
                             Collection<String> targetsSkippedAsResult,
                             Collection<List<String>> targetsSkippedAsResult_AllPaths,
                             String errorDetails) {
            this.executionNum = executionNum;
            this.participatesInExecution = participatesInExecution;
            this.startInstant = startInstant;
            this.endInstant = endInstant;

//            this.configData = configData;
            this.configDTOComp = configDTOComp;
            this.configDTOSim = configDTOSim;

            this.taskResult = taskResult;
            this.targetState = taskState;
            this.targetsOpenedAsResult = targetsThatAreWaitingAsResult;
            this.targetsSkippedAsResult = targetsSkippedAsResult;
            this.targetsSkippedAsResult_AllPaths = targetsSkippedAsResult_AllPaths;
            this.errorDetails = errorDetails;
        }

        public int getExecutionNum() {
            return executionNum;
        }

        public boolean isParticipatesInExecution() {
            return participatesInExecution;
        }

        public Instant getStartInstant() {
            return startInstant;
        }

        public Instant getEndInstant() {
            return endInstant;
        }

        public TaskResult getResult() {
            return taskResult;
        }

        public TargetState getState() { return targetState; }

        public Collection<String> getTargetsOpenedAsResult() {
            return targetsOpenedAsResult;
        }

        public Collection<String> getTargetsSkippedAsResult() { return targetsSkippedAsResult; }

        public Collection<List<String>> getTargetsSkippedAsResult_AllPaths() {
            return targetsSkippedAsResult_AllPaths;
        }

        public String getErrorDetails() {return this.errorDetails; }

//        public ConfigurationDTO getConfigData() {
//            return configData;
//        }

        public ConfigurationDTOSimulation getConfigDTOSim() {
            return configDTOSim;
        }

        public void setConfigDTOSim(ConfigurationDTOSimulation configDTOSim) {
            this.configDTOSim = configDTOSim;
        }

        public ConfigurationDTOCompilation getConfigDTOComp() {
            return configDTOComp;
        }

        public void setConfigDTOComp(ConfigurationDTOCompilation configDTOComp) {
            this.configDTOComp = configDTOComp;
        }

        public TaskResult getTaskResult() {
            return taskResult;
        }

        public TargetState getTargetState() {
            return targetState;
        }

        @Override
        public TaskStatusDTO clone() {
            Collection<String> openedClone = cloneCollection(targetsOpenedAsResult);
            Collection<String> skippedClone = cloneCollection(targetsSkippedAsResult);
            Collection<List<String>> skippedAllPaths = clonePaths(targetsSkippedAsResult_AllPaths);



//            ConfigurationDTO configData = null;
//            if (this.configData != null) {
//                configData = this.configData.clone();
//            }

            ConfigurationDTOCompilation DTOCompConfig = null;
            if (this.configDTOComp != null) {
                DTOCompConfig = this.configDTOComp.clone();
            }

            ConfigurationDTOSimulation DTOSimConfig = null;
            if (this.configDTOSim != null) {
                DTOSimConfig = this.configDTOSim.clone();
            }

            return new TaskStatusDTO(
                    this.executionNum,
                    this.participatesInExecution,
                    this.startInstant,
                    this.endInstant,
//                    configData,
                    DTOSimConfig,
                    DTOCompConfig,
                    this.taskResult,
                    this.targetState,
                    openedClone,
                    skippedClone,
                    skippedAllPaths,
                    this.errorDetails
            );
        }

        private Collection<List<String>> clonePaths(Collection<List<String>> allPaths) {
            Collection<List<String>> skippedAllPaths = null;

            if (allPaths != null) {
                skippedAllPaths = new LinkedList<>();

                for (List<String> path : allPaths) {
                    skippedAllPaths.add(cloneCollection(path));
                }
            }

            return  skippedAllPaths;
        }

        private List<String> cloneCollection(Collection<String> stringCollection) {
            List<String> clone = null;

            if (stringCollection != null) {
                clone = new LinkedList<>();

                for (String str : stringCollection) {
                    clone.add(str);
                }
            }

            return clone;
        }
    }
}