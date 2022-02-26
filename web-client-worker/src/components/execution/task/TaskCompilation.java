package components.execution.task;

import com.sun.istack.internal.NotNull;
import components.app.AppMainController;
import components.execution.ExecutionManager;
import graph.Target;
import graph.TargetDTO;
import task.enums.TaskResult;
import task.enums.TaskType;
import task.configuration.Configuration;
import task.configuration.ConfigurationCompilation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;


public class TaskCompilation extends Task {
    private static String errorDirPath = "C:/Temp/gpup comp error/";
    private static final int SUCCESS_CODE = 0;

    public TaskCompilation(@NotNull Target target,
                           @NotNull Configuration configuration,
                           ExecutionManager executionManager,
                           AppMainController mainController)
            throws IllegalArgumentException {
        super(TaskType.COMPILATION, target, configuration, executionManager,mainController);
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
//                File errorFile = File.createTempFile(errorFileName, ".txt", new File("C:/Temp/Error")); // TODO: read the location directory from WorkingDirectory in XML
                File errorFile = File.createTempFile(errorFileName, ".txt", null); // TODO: read the location directory from WorkingDirectory in XML

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
//                FileManager.createDirectory(config.getOutPath());
                createDirectory(config.getOutPath());

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
        String fileFQN = target.getUserData(); // engine.task.TaskProcess
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
        } else if (isNullOrEmptyStr(target.getUserData())) {
            valid = false;
        }

        if (configuration == null) {
            valid = false;
        }

        return valid;
    }

    private void createDirectory(String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }

    private boolean isNullOrEmptyStr(String str) {
        return (str == null || str.trim().isEmpty());
    }
}
