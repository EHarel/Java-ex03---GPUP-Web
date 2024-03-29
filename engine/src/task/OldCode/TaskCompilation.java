package task.OldCode;

import com.sun.istack.internal.NotNull;
import file.FileManager;
import graph.DependenciesGraph;
import graph.Target;
import logic.EngineUtils;
import task.Execution;
import task.configuration.Configuration;
import task.configuration.ConfigurationCompilation;
import task.enums.TaskResult;
import task.enums.TaskType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

public class TaskCompilation extends Task {
    private static final int SUCCESS_CODE = 0;

    public TaskCompilation(@NotNull Target target,
                           @NotNull Configuration configuration,
                           DependenciesGraph workingGraph,
                           Execution execution) throws IllegalArgumentException {
        super(TaskType.COMPILATION, target, configuration, workingGraph, execution);
    }

    @Override
    protected TaskResult runActualTask () {
        TaskResult result = TaskResult.UNPROCESSED;

        try {
            prepareOutFolder();
            result = runCompilation();
        } catch (IOException e) {
            e.printStackTrace(); // TODO: delete, and maybe find a better way to deal with creation of folder. Perhaps before this method?
        }

        return result;
    }

    private TaskResult runCompilation() {
        TaskResult taskResult = TaskResult.UNPROCESSED;

        if (validProcessingConditions(target)) {
            ProcessBuilder processBuilderCompilation = getProcessBuilder(target);

            try {
                String fileNameOnly = getFileNameOnly(target);
                String errorFileName = fileNameOnly + "Error";
                File errorFile = File.createTempFile(errorFileName, ".txt", new File("C:/Temp/Error")); // TODO: read the location directory from WorkingDirectory in XML
                processBuilderCompilation.redirectError(errorFile);
                target.getTaskStatus().setStartInstant(Instant.now());
                Process process = processBuilderCompilation.start();
                int resultCode = process.waitFor();
                target.getTaskStatus().setEndInstant(Instant.now());
                taskResult = resultCodeToTaskResult(resultCode);
//                String errorMessage = getErrorMessage(errorFile); // TODO: major problem here, wtf is wrong
//                String errorMessage = "TempErrorMsg"; // TODO delete
//                target.getTaskStatus().setErrorDetails(errorMessage);
                errorFile.deleteOnExit();
            } catch (IOException | InterruptedException e) {
                target.getTaskStatus().setEndInstant(Instant.now());
                e.printStackTrace();
                taskResult = TaskResult.FAILURE;
            }
        }

        return taskResult;
    }

    /**
     * This method checks if the out folder exists.
     * If not, it creates it.
     */
    private void prepareOutFolder() throws IOException {
        if (configuration != null) {
            ConfigurationCompilation config = (ConfigurationCompilation) configuration;

            if (!config.isOutDirectoryCreated()) {
                FileManager.createDirectory(config.getOutPath());
                config.setOutDirectoryCreated(true);
            }
        }
    }

    private String getErrorMessage(File errorFile) {
        StringBuilder errorMessage = null;

        try {
            List<String> errorLines = Files.readAllLines(errorFile.toPath());

            boolean firstIteration = true;

            for (String line : errorLines) {
                if (firstIteration) {
                    errorMessage = new StringBuilder(line);
                    firstIteration = false;
                } else {
                    errorMessage.append("\n").append(line);
                }
            }
        } catch (IOException e) {
            errorMessage = new StringBuilder("Unknown error! :(");
        } catch (Exception e) {
            errorMessage = new StringBuilder("Unknown error 2.0! :(");
        }

        return errorMessage.toString();
    }

    private ProcessBuilder getProcessBuilder(Target target) {
        ConfigurationCompilation compConfig = (ConfigurationCompilation) configuration;
        String targetFolder_d = compConfig.getOutPath();
        String targetFolder_cp = targetFolder_d;
        String srcCodeFolder = compConfig.getSourceCodePath(); // Must be an existing directory
        String fileFQN = target.getUserData(); // engine.task.OldCode.TaskProcess
        String filePath = getFilePathFromFQN(fileFQN);

        ProcessBuilder processBuilderCompilation = new ProcessBuilder(
                "javac",
                "-d", targetFolder_d,
                "-cp", targetFolder_cp,
                filePath);
        processBuilderCompilation.directory(new File(srcCodeFolder));

        return processBuilderCompilation;
    }

    // TODO: any way to find out warnings?
    private TaskResult resultCodeToTaskResult(int resultCode) {
        TaskResult taskResult;

        if (resultCode == SUCCESS_CODE) {
            taskResult = TaskResult.SUCCESS;
        } else {
            taskResult = TaskResult.FAILURE;
        }

        return taskResult;
    }

    private String getFileNameOnly(Target target) {
        String fileFQN = target.getUserData();
        String[] splitStr = fileFQN.split("\\.");

        return splitStr[splitStr.length - 1];
    }

    // TODO there has got to be a better way than this
    private String getFilePathFromFQN(String fileFQN) {
        String newStr = fileFQN.replaceAll("\\.", "/");

        newStr = newStr + ".java";

        return newStr;
    }

    private boolean validProcessingConditions(Target target) {
        boolean valid = true;

        if (target == null) {
            valid = false;
        } else if (EngineUtils.isNullOrEmptyStr(target.getUserData())) {
            valid = false;
        }

        if (configuration == null) {
            valid = false;
        }

        return valid;
    }
}