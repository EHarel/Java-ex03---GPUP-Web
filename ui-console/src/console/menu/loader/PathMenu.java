package console.menu.loader;

import console.Utils;
import console.menu.system.MenuManager;
import graph.PathOptions;
import graph.TargetDTO;

import java.util.Collection;
import java.util.List;

public class PathMenu {
    public enum PathType {
        REGULAR, CYCLE
    }

    public enum ReportType {
        GENERAL, DETAIL
    }

    /**
     * @return null if user wants to go back.
     */
    public static PathOptions getPathOption(PathType pathType, Collection<List<TargetDTO>> allPaths) {
        String title;
        switch (pathType) {
            case CYCLE: title = "Cycle"; break;
            default: title = "Path"; break;
        }

        System.out.println(title + " Options:");
        int i = 1;
        for (PathOptions pathOption : PathOptions.values()) {
            System.out.println("\t" + i + ") " + pathOption + " " + numberOfPathsByOption(allPaths, pathOption));
            i++;
        }
        System.out.println("\t" + i + ") " + MenuManager.returnStr);

        System.out.print("What kind of " + title + " would you like? ");
        System.out.println("Please choose an option: ");

        /* TODO
         * Change this to ReadIntInRange, maybe?
         * Then I can use that in other parts of the menu too
         */
        int userInput = Utils.ReadInt();

        PathOptions res;
        if(userInput > PathOptions.values().length) {
            res = null;
        } else{
            userInput--;

            res = PathOptions.values()[userInput];
        }

        return res;
    }

    private static String numberOfPathsByOption(Collection<List<TargetDTO>> allPaths, PathOptions pathOption) {
        int count = 0;
        String addon = "";
        String ofLength = " of length ";

        switch (pathOption) {
            case ALL: {
                count = allPaths.size();
                break;
            }
            case SHORTEST: {
                count = pathOption.returnPath(allPaths).size();
                int shortestLen = PathOptions.shortestPathLength(allPaths);
                addon = ofLength + shortestLen;
                break;
            }
            case LONGEST: {
                count = pathOption.returnPath(allPaths).size();
                int longestLen = PathOptions.longestPathLength(allPaths);
                addon = ofLength + longestLen;
                break;
            }
            case ANY: {
                return "";
            }
        }

        String pathPluralOrSingular = count == 1 ? "path" : "paths";

        return "(" + count +" "+ pathPluralOrSingular + addon + ")";
    }
}