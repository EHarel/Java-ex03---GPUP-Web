package components.app;

import componentcode.executiontable.ExecutionDTOTable;
import utilsharedall.ConstantsAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileSystemUtils {
    private static String username;
    private static Map<String, Path> executionName2Path = new HashMap<>();

    public static void setUsername(String username) {
        FileSystemUtils.username = username;
    }

    public static String getUsername() { return FileSystemUtils.username;}

    /**
     * @return the full path of the execution directory, as saved in the map.
     */
    public static Path getExecutionPath(String executionName) {
        return executionName2Path.get(executionName);
    }



    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- FILE METHODS ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * @return the full directory path of the directory opened for this execution.
     */
    public static void openExecutionDirectory(ExecutionDTOTable executionDTOTable) {
        checkOrCreateWorkerDir();
        String executionDirName = getExecutionDirName(executionDTOTable);

        Path fullPath = getWorkerDirFullPath();
        Path fullDirPath = Paths.get(fullPath.toString() + executionDirName);

        try {
            Files.createDirectories(fullDirPath);
            executionName2Path.put(executionDTOTable.getExecutionName(), fullDirPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkOrCreateWorkerDir() {
        checkOrCreateMainDir();
        checkOrCreateSpecificWorkerDir();
    }

    private static void checkOrCreateMainDir() {
        Path generalWorkerDirectoryPath = getGeneralWorkerDirectoryPath();

        try {
            if (!Files.isDirectory(generalWorkerDirectoryPath)) {
                Files.createDirectories(generalWorkerDirectoryPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkOrCreateSpecificWorkerDir() {
        Path fullPath = getWorkerDirFullPath();

        try {
            if (!Files.isDirectory(fullPath)) {
                Files.createDirectories(fullPath);
            }
        } catch (IOException e) {
            System.out.println("[Worker - checkOrCreateSpecificWorkerDir] Failed to create specific worker directory (Username: " + getUsername() + ")");
            e.printStackTrace();
        }
    }

    private static Path getWorkerDirFullPath() {
        String specificWorkerDirectoryName = getSpecificWorkerDirectoryName();
        Path generalWorkerDirPath = getGeneralWorkerDirectoryPath();
        String fullWorkerPathStr = generalWorkerDirPath.toString() + specificWorkerDirectoryName;
        Path fullPath = Paths.get(fullWorkerPathStr);

        return fullPath;
    }

    private static String getSpecificWorkerDirectoryName() {
        String dirName = null;

        dirName = "/" + getUsername();

        return dirName;
    }

    private static Path getGeneralWorkerDirectoryPath() {
        Path path;
        path = Paths.get(ConstantsAll.WORKING_DIR_WORKER);

        /*
        Alternative options:
            -   gpup main directory and a worker directory within it
         */

        return path;
    }

    private static String getExecutionDirName(ExecutionDTOTable executionDTOTable) {
        DateTimeFormatter dtf = ConstantsAll.DATE_TIME_FORMATTER;
        LocalDateTime now = LocalDateTime.now();
        String currTimeStr = dtf.format(now);

        // String dirName = "\\" + type.name() + " (" + taskTypeDirName + ") - " + currTimeStr; // Original format
        String dirName =
                "/" +
                        currTimeStr +
                        " -- " +
                        executionDTOTable.getExecutionName() +
                        " (" +
                        executionDTOTable.getTaskType().name() +
                        ", " +
                        executionDTOTable.getTaskStartPoint() +
                        ")";
//                                taskTypeDirName + ")"; // Time first format


//        String fullPath = saveFilesLocation + dirName;
//        FileManager.createDirectory(fullPath);
//
//        return fullPath;

        return dirName;
    }
}
