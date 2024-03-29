package logic;

import file.FileManager;
import file.SaveObject;
import exception.*;
import graph.*;
import task.*;
import task.OldCode.TaskManager;
import task.OldCode.TaskProcess;
import task.configuration.Configuration;
import task.configuration.ConfigurationDTO;
import task.consumer.ConsumerExecutionSummary;
import task.consumer.ConsumerManager;
import task.consumer.ConsumerWriteTargetToFile;
import task.enums.TaskType;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.time.Instant;
import java.util.*;

public class Engine implements Serializable {
    //    private static final long serialVersionUID = 2; // 19-Nov-2021
    private static final long serialVersionUID = 3; // 29-Nov-2021

    private static Engine singleInstance = null;
    private static Object lock = new Object();

    private DependenciesGraph dependenciesGraph;
    private TaskManager taskManager;
    private GraphManager graphManager;
    private ExecutionManager executionManager;


    private Engine() {
        dependenciesGraph = new DependenciesGraph();
        // taskManager = new TaskManager(dependenciesGraph);

        graphManager = new GraphManager();
        executionManager = new ExecutionManager();
    }

    public static synchronized Engine getInstance() {
        if (singleInstance == null) {
            singleInstance = new Engine();
        }

        return singleInstance;
    }

    public DependenciesGraph getGraph() {
        return dependenciesGraph;
    }

    public boolean graphLoaded() {
        return dependenciesGraph != null && dependenciesGraph.size() > 0;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }



//    public synchronized GraphManager getGraphManager() {
//        return graphManager;
//    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------TASK METHODS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * @param taskType  the type of task you which to add to.
     * @param newConfig new configuration to add.
     * @return true if added, false if configuration by such name exists already.
     */
    public boolean addConfigAndSetActive(TaskType taskType, Configuration newConfig) {
        return taskManager.addConfigAndSetActive(taskType, newConfig);
    }

    public boolean addConfiguration(TaskType taskType, Configuration newConfig) {
        return taskManager.addConfiguration(taskType, newConfig);
    }

    public boolean activeConfig_UpdateThreadCount(TaskType taskType, int threadNum) throws InvalidInputRangeException {
        return taskManager.activeConfig_UpdateThreadCount(taskType, threadNum);
    }

    public boolean removeConfiguration(TaskType taskType, String configurationName) {
        return taskManager.removeConfiguration(taskType, configurationName);
    }

    public void removeConfigurationAll(TaskType taskType) {
        taskManager.removeConfigurationAll(taskType);
    }

    public ConfigurationDTO getConfigActive(TaskType taskType) {
        return taskManager.getConfigActive(taskType);
    }

    // TODO: remove this method, switch to data only
    public Configuration getActiveConfigNotData(TaskType taskType) {
        return taskManager.getActiveConfigNotData(taskType);
    }

    public Collection<ConfigurationDTO> getConfigAll(TaskType taskType) {
        return taskManager.getAllConfigurations(taskType);
    }

    public ConfigurationDTO getConfigSpecific(TaskType taskType, String configName) {
        return taskManager.getSpecificConfig(taskType, configName);
    }

    public int getConfigCount(TaskType taskType) {
        return taskManager.getConfigCount(taskType);
    }

    public boolean setActiveConfig(TaskType taskType, String name) {
        return taskManager.setActiveConfig(taskType, name);
    }

    /**
     * This method does not initialize the consumers for the different stages of the task process.
     * That must be done separately via the addConsumer methods.
     *
     * @param chosenTargetNames if this parameter is null or empty, the engine assumes all targets are meant
     *                          to participate, since it makes no sense to run a task with no targets in it.
     *                          So the default (i.e. null\empty) means ALL targets. Only if this parameter has
     *                          values does it include and exclude targets accordingly.
     */
    public void executeTask(TaskType taskType, TaskProcess.StartPoint startPoint, ConsumerManager consumerManager, Collection<String> chosenTargetNames) throws UninitializedTaskException, IOException, NoConfigurationException {
        String directoryPath = openExecutionFolderWithStartPointName(taskType, startPoint);
        consumerManager.getEndTargetConsumers().add(new ConsumerWriteTargetToFile(directoryPath));
        consumerManager.getEndProcessConsumers().add(new ConsumerExecutionSummary(directoryPath));
        taskManager.executeTask(taskType, startPoint, consumerManager, chosenTargetNames);
    }

    private String openExecutionFolderWithStartPointName(TaskType taskType, TaskProcess.StartPoint startPoint) throws IOException {
        // Check whether to attached start point or incremental
        String taskTypeDirName = "From Scratch";

        if (startPoint == TaskProcess.StartPoint.INCREMENTAL) {
            if (taskManager.getExecutionCount(taskType) > 0) {
                taskTypeDirName = "Incremental";
            }
        }

        return FileManager.openTaskExecutionFolder(taskType, taskTypeDirName);
    }

    public int getExecutionCount(TaskType taskType) {
        return taskManager.getExecutionCount(TaskType.SIMULATION);
    }

    public Execution getExecutionLast(TaskType taskType) {
        return taskManager.getExecutionReportLast(taskType).clone();
    }

    public Execution getExecutionReportIndex(TaskType taskType, int i) throws IndexOutOfBoundsException {
        return taskManager.getExecutionReportIndex(taskType, i).clone();
    }

    public Execution getExecutionByExeNumber(TaskType taskType, int executionNumber) {
        Execution execution = taskManager.getExecutionByExeNumber(taskType, executionNumber);
        if (execution != null) {
            execution = execution.clone();
        }

        return execution;
    }

    public String getFormalizedTargetDataString(TargetDTO targetReport) {
        return EngineUtils.getFormalizedTargetDataString(targetReport);
    }

    public String getFormalizedExecutionReportString(Execution execution) {
        return EngineUtils.getFormalizedExecutionReportString(execution);
    }

    public String getFormalizedConfigurationString(ConfigurationDTO config) {
        return EngineUtils.getFormalizedConfigurationString(config);
    }

    public String formatTimeDuration(Instant start, Instant end) {
        return EngineUtils.formatTimeDuration(start, end);
    }

    public String getDateTimeFromInstant(Instant instant) {
        return EngineUtils.getDateTimeFromInstant(instant);
    }

    public void pauseExecution(boolean isPaused) {
        taskManager.pause(isPaused);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- FILE METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void save(String filePathWithoutSuffix) throws IOException {
        SaveObject saveObject = new SaveObject(dependenciesGraph, taskManager);
        FileManager.saveNonXML(filePathWithoutSuffix, saveObject);
    }

    public void loadRegular(String pathWithoutSuffix) throws IOException {
        SaveObject saveObject = FileManager.loadRegular(pathWithoutSuffix);

        if (saveObject == null) {
            throw new FileNotFoundException();
        }

        dependenciesGraph = saveObject.getDependenciesGraph();
        taskManager = saveObject.getTaskManager();
    }

//    /**
//     * @throws InvalidInputRangeException If maxParallelism value is 0 (must be a whole number larger than 1).
//     */
//    public void loadXML(String filePath) throws
//            FileNotFoundException,
//            JAXBException,
//            ExistingItemException,
//            DependencyOnNonexistentTargetException,
//            ImmediateCircularDependencyException,
//            NullOrEmptyStringException,
//            InvalidInputRangeException,
//            NonexistentTargetException,
//            SerialSetNameRepetitionException {
//        SaveObject saveObject = FileManager.loadXML(filePath);
//        addNewSaveObject(saveObject);
//    }

    public void loadXMLFromInputStream(InputStream inputStream, String uploadingUser) throws
            FileNotFoundException,
            JAXBException,
            ExistingItemException,
            DependencyOnNonexistentTargetException,
            ImmediateCircularDependencyException,
            NullOrEmptyStringException,
            InvalidInputRangeException,
            NonexistentTargetException,
            SerialSetNameRepetitionException {
        SaveObject saveObject = FileManager.loadXMLFromInputStream(inputStream, uploadingUser);
        addNewSaveObject(saveObject);
    }

    private void addNewSaveObject(SaveObject saveObject) {
//        getGraphManager().addGraph(saveObject.getDependenciesGraph());
//        this.taskManager = new TaskManager(dependenciesGraph, saveObject.getMaxParallelism());
    }

    public void toggleRememberLastConfiguration(TaskType taskType) {
        taskManager.toggleRememberLastConfiguration(taskType);
    }

    /**
     * Checks if a task is set to remember its last configuration for next executions.
     *
     * @return true if remembers, false if doesn't.
     */
    public boolean isPersistentConfiguration(TaskType taskType) {
        return taskManager.isPersistentConfiguration(taskType);
    }

//    public int getThreadCount_maxParallelism() {
//        return taskManager.getMaxParallelism();
//    }

    public int getThreadCount_currentParallelism() {
        return taskManager.getCurrentParallelism();
    }

    public void setThreadCount_activeThreads(int threadCount) {
        taskManager.setCurrentParallelism(threadCount);
    }

    public void activeConfig_UpdateParticipatingTargets(TaskType taskType, Collection<String> participatingTargetNames) {
        taskManager.activeConfig_UpdateParticipatingTargets(taskType, participatingTargetNames);
    }

//    public synchronized  ExecutionManager getExecutionManager() {
//        return executionManager;
//    }

//    public boolean addExecution(Execution execution) {
//        return getExecutionManager().addExecution(execution);
//    }

//    public boolean addUserToConfiguration(String executionName, String userName) {
//        return getExecutionManager().addUserToConfiguration(executionName, userName);
//    }

//    public boolean isExecutionCreator(String executionName, String userName) {
//        return getExecutionManager().isExecutionCreator(executionName, userName);
//    }

//    public boolean updateExecutionStatus(String executionName, ExecutionStatus executionStatus) {
//        return getExecutionManager().updateExecutionStatus(executionName, executionStatus);
//    }

//    public Collection<Target> getTargetsForUser(String username, Integer targetCount) {
//        return getExecutionManager().getTargetsForUser(username, targetCount);
//    }
}