package components.app;

import graph.SerialSetDTO;
import graph.TargetDTO;
import javafx.scene.control.TextArea;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AppUtils {

    /**
     * This method receives a collection of targets and returns a collection of their names only.
     * If the collection is null or empty, it returns a new empty collection.
     */
    public static Collection<String> getNamesFromCollection(Collection<TargetDTO> targets) {
        Collection<String> namesCollection = new LinkedList<>();

        if (targets != null) {
            targets.forEach(targetDTO -> {
                namesCollection.add(targetDTO.getName());
            });
        }

        return namesCollection;
    }

    /**
     * This method appends paths to a text area. It does not clear the text area - user must clear it before if he wants.
     * @param pathsList
     * @param textArea
     */
    public static void showPathsInTextArea(Collection<List<TargetDTO>> pathsList, TextArea textArea) {
        if (pathsList != null) {

            if (pathsList.isEmpty()) {
                textArea.setText("No paths in this direction");
            } else {
                int number = 1;

                for (List<TargetDTO> path : pathsList) {
                    String pathsStr = AppUtils.getStrFromPath(path);
                    textArea.appendText(number + ") " + pathsStr + "\n\n");
                    number++;
                }
            }
        }
    }

    public static String getStrFromPath(List<TargetDTO> path) {
        String pathStr;

        if (path == null || path.size() == 0) {
            pathStr = "Empty path";
        } else {
            String delim = " -> ";
            StringBuilder pathStrBuilder = new StringBuilder();

            for (TargetDTO target : path) {
                pathStrBuilder.append(target.getName()).append(delim);
            }
            int strLen = pathStrBuilder.length();
            int delLen = delim.length();

            pathStrBuilder = new StringBuilder(pathStrBuilder.substring(0, (strLen - delLen)));

            pathStr = pathStrBuilder.toString();
        }

        return pathStr;
    }

    /**
     * This method finds all the names appearing in all the paths, and returns a collection of them, without repetition.
     */
    public static Collection<String> getNamesFromPaths(Collection<List<TargetDTO>> paths) {
        Collection<String> allNames = new LinkedList<>();

        if (paths != null) {
            paths.forEach(path -> {
                path.forEach(targetDTO -> {
                    String nameToCheck = targetDTO.getName();
                    if (!nameExistsInCollection(allNames, nameToCheck)) {
                        allNames.add(nameToCheck);
                    }
                });
            });
        }

        return allNames;
    }

    /**
     * This method finds all the names appearing in all the paths, and returns a collection of them, without repetition.
     * It excludes the chosen target given by the name parameter.
     * @return
     */
    public static Collection<String> getNamesFromPaths_ExcludeChosen(Collection<List<TargetDTO>> paths, String targetToIgnore) {
        Collection<String> allNames = new LinkedList<>();

        if (paths != null) {
            paths.forEach(path -> {
                path.forEach(targetDTO -> {
                    String nameToCheck = targetDTO.getName();
                    if (!nameExistsInCollection(allNames, nameToCheck) && !isTargetToIgnore(nameToCheck, targetToIgnore)) {
                        allNames.add(nameToCheck);
                    }
                });
            });
        }

        return allNames;
    }

    private static boolean isTargetToIgnore(String nameToCheck, String targetToIgnore) {
        boolean isTargetToIgnore = false;

        isTargetToIgnore = nameToCheck.toLowerCase().equals(targetToIgnore.toLowerCase());

        return isTargetToIgnore;
    }

    private static boolean nameExistsInCollection(Collection<String> namesCollection, String nameToCheck) {
        boolean exists = false;

        for (String name : namesCollection) {
            if (name.equals(nameToCheck)) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public static Collection<String> getNamesFromSerialSets(Collection<SerialSetDTO> serialSetsDTOS) {
        Collection<String> serialSetsNames = new LinkedList<>();

        if (serialSetsDTOS != null) {
            serialSetsDTOS.forEach(serialSetDTO -> {
                serialSetsNames.add(serialSetDTO.getName());
            });
        }

        return serialSetsNames;
    }

    public static long getMillisecondsBetweenTwoInstants(Instant earlyInstant, Instant laterInstant) {
        Duration duration = Duration.between(earlyInstant, laterInstant);

        return duration.toMillis();
    }
}
