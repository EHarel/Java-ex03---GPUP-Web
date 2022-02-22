package components.execution.task;

import graph.TargetDTO;
import task.enums.TaskResult;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class TaskUtils {
    private static boolean timeWithMilliseconds = true;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");

    public static String getFormalizedTargetDataString(TargetDTO targetDTO) {
        String res = null;

        if (targetDTO != null) {
            String userData = (targetDTO.getUserData() == null || targetDTO.getUserData().trim().isEmpty()) ? "None" : targetDTO.getUserData();

            res = "Target Data -- " + targetDTO.getName() +
                    "\n\tUser data: " + userData +
                    "\n\tDependency: " + targetDTO.getDependency() +
                    "\n\tTargets \"" + targetDTO.getName() + "\" is directly dependent on: " + targetDTO.getTargetsThisDirectlyDependsOn() +
                    "\n\tTargets \"" + targetDTO.getName() + "\" is directly required for: " + targetDTO.getTargetsThisIsDirectlyRequiredFor() +
                    getFormalizedTaskStatusString(targetDTO.getTaskStatusDTO());
        }

        return res;
    }

    private static String getFormalizedTaskStatusString(TargetDTO.TaskStatusDTO taskStatusDTO) {
        String res;

        res = "\n\tTask Report: " +
                "\n\t\tTarget state: " + taskStatusDTO.getState() +
                "\n\t\tTask result:  " + taskStatusDTO.getTaskResult();

        if (taskStatusDTO.getTaskResult() != TaskResult.UNPROCESSED) {
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

    public static boolean isNullOrEmptyStr(String str) {
        return (str == null || str.trim().isEmpty());
    }
}


// Original version:
// Original version:
//    private static String getFormalizedTaskStatusString(TargetDTO.TaskStatusDTO taskStatusDTO) {
//        String res;
//
//        if (taskStatusDTO.getExecutionNum() == 0) { // Not part of any execution!
//            res = "";
//        } else {
//            res = "\n\tTask Report: " +
//                    "\n\t\tTarget state: " + taskStatusDTO.getState() +
//                    "\n\t\tTask result:  " + taskStatusDTO.getResult();
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