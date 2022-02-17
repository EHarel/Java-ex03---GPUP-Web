package util;

import graph.Target;
import graph.TargetDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GraphUtils {
    public static Collection<String> getNamesOfTargets(Collection<Target> targets) {
        Collection<String> names = new LinkedList<>();

        if (targets != null) {
            targets.forEach(target -> names.add(target.getName()));
        }
        return names;
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
}
