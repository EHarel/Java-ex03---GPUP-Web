package logic;

import graph.SerialSetDTO;
import graph.Target;
import graph.TargetDTO;
import task.ExecutionData;
import task.TaskType;
import task.configuration.ConfigurationData;
import task.configuration.ConfigurationDataCompilation;
import task.configuration.ConfigurationDataSimulation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class EngineUtils {
    private static boolean timeWithMilliseconds = false;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

    public static String getFormalizedTargetDataString(TargetDTO targetDTO) {
        String res = null;

        if (targetDTO != null) {
            String userData = (targetDTO.getUserData() == null || targetDTO.getUserData().trim().isEmpty()) ? "None" : targetDTO.getUserData();
            String serialSetsStr = getSerialSetsStr(targetDTO);

            res = "Target Data -- " + targetDTO.getName() +
                    "\n\tUser data: " + userData +
                    "\n\tSerial sets: " + serialSetsStr +
                    "\n\tDependency: " + targetDTO.getDependency() +
                    "\n\tTargets \"" + targetDTO.getName() + "\" is directly dependent on: " + targetDTO.getTargetsThisDirectlyDependsOn() +
                    "\n\tTargets \"" + targetDTO.getName() + "\" is directly required for: " + targetDTO.getTargetsThisIsDirectlyRequiredFor() +
                    getFormalizedTaskStatusString(targetDTO.getTaskStatusDTO());
        }

        return res;
    }

    private static String getSerialSetsStr(TargetDTO targetDTO) {
        StringBuilder serialSetsStr = new StringBuilder("None");

        Collection<SerialSetDTO> serialSetDTOS = targetDTO.getSerialSetsDTOS();
        boolean firstIteration = true;

        for (SerialSetDTO set : serialSetDTOS) {
            if (firstIteration) {
                serialSetsStr = new StringBuilder(set.getName());
                firstIteration = false;
            } else {
                serialSetsStr.append(", ").append(set.getName());
            }
        }

        return serialSetsStr.toString();
    }

    private static String getFormalizedTaskStatusString(TargetDTO.TaskStatusDTO taskStatusDTO) {
        String res;

        if (taskStatusDTO.getExecutionNum() == 0) { // Not part of any execution!
            res = "";
        } else {
            res = "\n\tTask Report: " +
                        "\n\t\tTarget state: " + taskStatusDTO.getState() +
                        "\n\t\tTask result:  " + taskStatusDTO.getResult();

            if (taskStatusDTO.getResult() != TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED) {
                res = res +
                        "\n\t\tExecution run: " + taskStatusDTO.getExecutionNum() +
                        "\n\t\tConfiguration: " + taskStatusDTO.getConfigData().getName();

                String timeStart = getDateTimeFromInstant(taskStatusDTO.getStartInstant());
                String timeEnd = getDateTimeFromInstant(taskStatusDTO.getEndInstant());

                res = res +
                        "\n\t\tTotal time:   " + formatTimeDuration(taskStatusDTO.getStartInstant(), taskStatusDTO.getEndInstant()) +
                        "\n\t\t\tTime start: \t" + timeStart +
                        "\n\t\t\tTime end:   \t" + timeEnd +
                        "\n\t\tTargets that have opened as a result: " + taskStatusDTO.getTargetsOpenedAsResult() +
                        "\n\t\tTargets that are skipped as a result: " + taskStatusDTO.getTargetsSkippedAsResult();

                if (!isNullOrEmptyStr(taskStatusDTO.getErrorDetails())) {
                    res = res +
                            "\n\t\tError details: \n" + taskStatusDTO.getErrorDetails();
                }
            }
        }

        return res;
    }

    // Old code, making use of participatesInExecution field, before participating targets were made into their own sub-graph
//    private static String getFormalizedTaskStatusString(TargetDTO.TaskStatusDTO taskStatusDTO) {
//        String res;
//
//        if (taskStatusDTO.getExecutionNum() == 0) { // Not part of any execution!
//            res = "";
//        } else {
//            res = "\n\tTask Report: " +
//                    "\n\t\tParticipates in execution: " + taskStatusDTO.isParticipatesInExecution();
//
//
//            if (taskStatusDTO.isParticipatesInExecution()) {
//                res = res +
//                        "\n\t\tTarget state: " + taskStatusDTO.getState() +
//                        "\n\t\tTask result:  " + taskStatusDTO.getResult();
//            }
//
//            if (taskStatusDTO.getResult() != TargetDTO.TaskStatusDTO.TaskResult.UNPROCESSED) {
//                res = res +
//                        "\n\t\tExecution run: " + taskStatusDTO.getExecutionNum() +
//                        "\n\t\tConfiguration: " + taskStatusDTO.getConfigData().getName();
//
//                String timeStart = getDateTimeFromInstant(taskStatusDTO.getStartInstant());
//                String timeEnd = getDateTimeFromInstant(taskStatusDTO.getEndInstant());
//
//                res = res +
//                        "\n\t\tTotal time:   " + formatTimeDuration(taskStatusDTO.getStartInstant(), taskStatusDTO.getEndInstant()) +
//                        "\n\t\t\tTime start: \t" + timeStart +
//                        "\n\t\t\tTime end:   \t" + timeEnd +
//                        "\n\t\tTargets that have opened as a result: " + taskStatusDTO.getTargetsOpenedAsResult() +
//                        "\n\t\tTargets that are skipped as a result: " + taskStatusDTO.getTargetsSkippedAsResult();
//
//                if (!isNullOrEmptyStr(taskStatusDTO.getErrorDetails())) {
//                    res = res +
//                            "\n\t\tError details: \n" + taskStatusDTO.getErrorDetails();
//                }
//            }
//        }
//
//        return res;
//    }

    public static String getFormalizedExecutionReportString(ExecutionData executionData) {
        String res = "Execution Report (" + executionData.getTaskType() + ")";
        if (executionData.getTaskComplete()) {
            res = res + " -- TASK COMPLETE";
        }

        // Local variables just for ease of formatting
        Collection<String> allTargets = getNamesOfTargetsData(executionData.getProcessedData().getAllTargetData());
        Collection<String> allParticipatingTargets = getNamesOfParticipatingTargets(executionData.getProcessedData().getAllTargetData());
        Collection<String> allProcessed = getNamesOfTargetsData(executionData.getProcessedData().getAllProcessedTargetsOfAllResults());
        Collection<String> nonFailed = getNamesOfTargetsData(executionData.getProcessedData().getProcessedTargetsNoFailure());
        Collection<String> success = getNamesOfTargetsData(executionData.getProcessedData().getSuccessfulTargets());
        Collection<String> warnings = getNamesOfTargetsData(executionData.getProcessedData().getWarningTargets());
        Collection<String> failed = getNamesOfTargetsData(executionData.getProcessedData().getFailedTargets());
        Collection<String> unprocessed = getNamesOfTargetsData(executionData.getProcessedData().getUnprocessedTargets());
        Collection<String> leftToCompletion = getNamesOfTargetsData(executionData.getProcessedData().getTargetsLeftToCompletion());

        res = res +
                "\n\tExecution number: " + executionData.getExecutionNumber() +
                "\n\tOverall time:   " + formatTimeDuration(executionData.getStartInstant(), executionData.getEndInstant()) +
                "\n\t\tStart time:   " + getDateTimeFromInstant(executionData.getStartInstant()) +
                "\n\t\tEnd time:     " + getDateTimeFromInstant(executionData.getEndInstant()) +
                "\n\tTargets in graph: " + allTargets + " (" + allTargets.size() + ")" +
                "\n\tProcessed (success\\warnings\\failed): " + allProcessed + " (" + allProcessed.size() + ")" +
                "\n\t\tNon-failed targets: " + nonFailed + " (" + nonFailed.size() + ")" +
                "\n\t\t\tSuccess without warnings: " + success + " (" + success.size() + ")" +
                "\n\t\t\tSuccess   WITH  warnings: " + warnings + " (" + warnings.size() + ")" +
                "\n\t\tFailed: " + failed + " (" + failed.size() + ")" +
                "\n\tUnprocessed: " + unprocessed + " (" + unprocessed.size() + ")" +
                "\n\tTargets left for completion: " + leftToCompletion + " (" + leftToCompletion.size() + ")" +
                "\n\n" + getFormalizedConfigurationString(executionData.getConfiguration().getData());

        return res;
    }

    public static String getDateTimeFromInstant(Instant instant) {
        String timeStr = null;

        if (instant != null) {
            DateTimeFormatter formatter;

            if (timeWithMilliseconds) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
            } else {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            }

            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, OffsetDateTime.now().getOffset());

            timeStr = dateTime.format(formatter);
        }

        return timeStr;
    }

    public static String getFormalizedConfigurationString(ConfigurationData config) {
        String configStr = null;

        switch (config.getTaskType()) {
            case COMPILATION:
                configStr = getCompilationConfig(config);
                break;
            case SIMULATION:
                configStr = getSimulationConfig(config);
                break;
        }

        return configStr;
    }

    private static String getSimulationConfig(ConfigurationData config) {
        if (config.getTaskType() != TaskType.SIMULATION) {
            return null;
        }

        ConfigurationDataSimulation simConfig = (ConfigurationDataSimulation) config;

        return "Simulation Configuration" +
                "\n\tName: " + simConfig.getName() +
                "\n\tThreads in use: " + simConfig.getThreadCount() +
                "\n\tProcessing time: " + simConfig.getProcessingTime() / 1000 +
                "\n\tRandom time: " + simConfig.isRandomProcessingTime() +
                "\n\tSuccess probability: " + simConfig.getSuccessProbability() +
                "\n\tWarnings probability: " + simConfig.getWarningsProbability();
    }

    private static String getCompilationConfig(ConfigurationData config) {
        if (config.getTaskType() != TaskType.COMPILATION) {
            return null;
        }

        ConfigurationDataCompilation compConfig = (ConfigurationDataCompilation) config;

        return "Compilation Configuration" +
                "\n\tName: " + compConfig.getName() +
                "\n\tSource path: " + compConfig.getSourceCodePath() +
                "\n\tOut path: " + compConfig.getOutPath();
    }

    public static String formatTimeDuration(Instant start, Instant end) {
        if (start == null || end == null) {
            return null;
        }

        LocalDateTime startDateTime = LocalDateTime.ofInstant(start, OffsetDateTime.now().getOffset());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(end, OffsetDateTime.now().getOffset());

        return formatInstancesToTime(startDateTime, endDateTime);
    }

    private static String formatInstancesToTime(LocalDateTime timeStart, LocalDateTime timeEnd) {
        String overallTimeStr = "0";

        if (timeEnd != null && timeStart != null) {
            Duration timeBetween = Duration.between(timeStart, timeEnd);
            long overallTimeMillis = timeBetween.toMillis();

            long millis = overallTimeMillis % 1000;
            long second = (overallTimeMillis / 1000) % 60;
            long minute = (overallTimeMillis / (1000 * 60)) % 60;
            long hour = (overallTimeMillis / (1000 * 60 * 60)) % 24;

//            overallTimeStr = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis); // With milliseconds
            overallTimeStr = String.format("(HH:MM:SS) %02d:%02d:%02d", hour, minute, second);
        }

        return overallTimeStr;
    }

    public static Collection<String> getNamesOfTargets(Collection<Target> targets) {
        Collection<String> names = new LinkedList<>();

        if (targets != null) {
            targets.forEach(target -> names.add(target.getName()));
        }
        return names;
    }

    private static Collection<String> getNamesOfTargetsData(Collection<TargetDTO> data) {
        Collection<String> names = new LinkedList<>();

        data.forEach(targetData -> names.add(targetData.getName()));

        return names;
    }

    private static Collection<String> getNamesOfParticipatingTargets(Collection<TargetDTO> allTargetData) {
        Collection<String> names = new LinkedList<>();

        for (TargetDTO targetDTO : allTargetData) {
            if (targetDTO.getTaskStatusDTO().isParticipatesInExecution()) {
                names.add(targetDTO.getName());
            }
        }

        return names;
    }

    public static boolean isNullOrEmptyStr(String str) {
        return (str == null || str.trim().isEmpty());
    }


    private static List<String> cloneList(List<String> collection) {
        List<String> clone = null;

        if (collection != null) {
            clone = new ArrayList<>(collection.size());

            for (String str : collection) {
                clone.add(str);
            }
        }

        return clone;
    }

    public static Collection<String> cloneCollection(Collection<String> collection) {
        Collection<String> clone = null;

        if (collection != null) {
            clone = new ArrayList<>(collection.size());

            for (String str : collection) {
                clone.add(str);
            }
        }

        return clone;
    }

    public static Collection<List<String>> cloneListCollection(Collection<List<String>> listCollection) {
        Collection<List<String>> clone = null;

        if (listCollection != null) {
            clone = new ArrayList<>(listCollection.size());

            for (List<String> collection : listCollection) {
                clone.add(cloneList(collection));
            }
        }

        return clone;
    }

    public static Collection<Collection<String>> cloneDoubleCollection(Collection<Collection<String>> doubleCollection) {
        Collection<Collection<String>> clone = null;

        if (doubleCollection != null) {
            clone = new ArrayList<>(doubleCollection.size());

            for (Collection<String> collection : doubleCollection) {
                clone.add(cloneCollection(collection));
            }
        }

        return clone;
    }


    public static Collection<List<TargetDTO>> pathToDTO(Collection<List<Target>> paths) {
        Collection<List<TargetDTO>> data = new LinkedList<>();

        for (List<Target> path : paths) {
            List<TargetDTO> dataPath = new ArrayList<>(path.size());

            for (Target target : path) {
                dataPath.add(target.toData());
            }

            data.add(dataPath);
        }

        return data;
    }

    public static Collection<List<String>> pathsToNames(Collection<List<Target>> paths) {
        Collection<List<String>> data = new LinkedList<>();

        for (List<Target> path : paths) {
            data.add(pathToNames(path));
        }

        return data;
    }

    public static List<String> pathToNames(List<Target> path) {
        List<String> names = new ArrayList<>();

        if (path != null) {
            names = new ArrayList<>(path.size());

            for (Target target : path) {
                names.add(target.getName());
            }
        }

        return names;
    }

    /**
     * This method extracts all the targets appearing in all the paths, excluding the one of the given name parameter.
     * It returns a collection without repetition.
     *
     * @param allPaths      All paths to extract the targets from.
     * @param nameToExclude the name to ignore when scanning the paths. If null, no name is ignored, and all names are given.
     * @return null if allPaths is null.
     */
    public static Collection<TargetDTO> GetTargetDTOsFromPaths_Exclude(Collection<List<TargetDTO>> allPaths, String nameToExclude) {
        Collection<TargetDTO> allTargets = null;

        if (allPaths != null) {
            allTargets = new LinkedList<>();

            for (List<TargetDTO> path : allPaths) {
                for (TargetDTO target : path) {
                    String currName = target.getName();

                    if (!isNameToExclude(currName, nameToExclude) && !nameAppearsInCollection(allTargets, currName)) {
                        allTargets.add(target);
                    }
                }
            }
        }

        return allTargets;
    }

    /**
     * if nameToExclude is null, there is nothing to compare to, and thus result is false.
     * @param currName
     * @param nameToExclude
     * @return
     */
    private static boolean isNameToExclude(String currName, String nameToExclude) {
        boolean isNameToExclude = false;

        if (nameToExclude != null && currName != null) {
            isNameToExclude = currName.equalsIgnoreCase(nameToExclude);
        }

        return isNameToExclude;
    }

    private static boolean nameAppearsInCollection(Collection<TargetDTO> allNames, String nameToCheck) {
        boolean nameAppears = false;

        for (TargetDTO target : allNames) {
            if (target.getName().equalsIgnoreCase(nameToCheck)) {
                nameAppears = true;
                break;
            }
        }

        return nameAppears;
    }

    /**
     * This method checks if a given name is in the collection. Case-insensitive.
     * @param targetNames
     * @param name
     * @return
     */
    public static boolean isNameInCollection(Collection<String> targetNames, String name) {
        boolean isInCollection = false;

        for (String collectionName : targetNames) {
            if (collectionName.equalsIgnoreCase(name)) {
                isInCollection = true;
                break;
            }
        }

        return isInCollection;
    }

    /**
     * This method returns a collection of all targets appearing in the paths, without repeition.
     */
    public static Collection<Target> pathsToTargets_Exclude(Collection<List<Target>> allPaths, Target targetToExclude) {
        Collection<Target> allTargets = new LinkedList<>();

        if (allPaths != null) {
            for (List<Target> path : allPaths) {
                for (Target target : path) {
                    if (targetCanBeAddedToListWithNoRepetition(allTargets, target, targetToExclude)) {
                        allTargets.add(target);
                    }
                }
            }
        }

        return allTargets;
    }

    private static boolean targetCanBeAddedToListWithNoRepetition(Collection<Target> allTargets, Target candidateToAdd, Target targetToExclude) {
        boolean canBeAdded = true;

        if (candidateToAdd.getName().equalsIgnoreCase(targetToExclude.getName())) {
            canBeAdded = false;
        }

        if(canBeAdded) {
            for (Target target : allTargets) {
                if (target.getName().equalsIgnoreCase(candidateToAdd.getName())) {
                    // Target already appears
                    canBeAdded = false;
                    break;
                }
            }
        }


        return canBeAdded;
    }
}