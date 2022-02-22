package console;

import algorithm.DFS;
import console.menu.system.MenuManager;
import console.menu.loader.PathMenu;
import graph.DependenciesGraph;
import graph.TargetDTO;
import logic.Engine;
import graph.PathOptions;
import task.enums.TaskType;
import task.configuration.ChosenTarget;

import java.util.*;

public class Utils {
    private static String returnStr = MenuManager.returnStr;
    private static Engine engine = Engine.getInstance();

    /**
     * Reads an int from the user within a given range. If non-int input is inserted, it asks again.
     * @param minVal minimum acceptable int.
     * @param maxVal maximum acceptable int.
     * @return return
     */
    static public int readIntInRange(int minVal, int maxVal) {
        int userInput;
        boolean validInput = false;

        do {
            userInput = ReadInt();
            if(userInput < minVal || userInput > maxVal) {
                System.out.println(userInput + " is not between " + minVal + " and " + maxVal + ".");
            } else {
                validInput = true;
            }
        } while(!validInput);

        return userInput;
    }

    static public int ReadInt() {
        /* TODO
         * I had to insert an initial value (0) to userInput,
         * otherwise it wouldn't compile.
         * But this is a mostly wrong value (although ont that's out of range and does nothing).
         * Find out a way to implement this method without an initial value.
         */
        Scanner scanner = new Scanner(System.in);
        int userInput = 0;
        boolean validInput = false;

        do {
            try {
                userInput = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Input isn't an integer. y u do dis? Try again, now with an integer (whole number)!");
                scanner.nextLine();
            } catch (Exception ignore) {

            }
        } while (!validInput);

        return userInput;
    }

    /**
     * @return target name, once such a target exists. null if user wants to stop and go back.
     */
    static public Optional<String> getTargetName(DependenciesGraph graph) {
        boolean validInput = false;
        Scanner scanner = new Scanner(System.in);
        Optional<String> userInput;

        do {
            System.out.println("Please enter the name of the target to search, case-insensitive.\nWrite \"" + returnStr + "\" to, uh, go back (without quotation marks!)");
            userInput = Optional.of(scanner.nextLine());
            userInput = Optional.of(userInput.get().trim());
            if (userInput.get().equals(returnStr)) {
                validInput = true;
                userInput = Optional.empty();
            } else {
                if (graph.contains(userInput.get())) {
                    validInput = true;
                } else {
                    System.out.println("No such target in the graph. Come on, get to know this graph. Take it out, ask it how it's doing, learn its vertices.");
                }
            }
        } while (!validInput);

        return userInput;
    }

    static public String readName(String additionalMsg) {
        boolean validInput = false;
        Scanner scanner = new Scanner(System.in);
        String userInput;
        additionalMsg = additionalMsg == null ? "" : additionalMsg;

        /* TODO:
            * Add a way to go back? Like in getTargetName() method.
        * */
        do {
            System.out.println("Please enter a name, case-insensitive. " + additionalMsg);
            userInput = scanner.nextLine();
            userInput = userInput.trim();
            if (userInput.trim().isEmpty()) {
                System.out.println("You cannot enter a blank name.");
            } else {
                validInput = true;
            }
        } while (!validInput);

        return userInput;
    }

    public static void printPathsCycle(ArrayList<Collection<List<TargetDTO>>> paths, String target1, String target2) {
        if (paths != null) {
            Collection<List<TargetDTO>> pathToSend;
            Collection<List<TargetDTO>> path1 = paths.get(0);
            Collection<List<TargetDTO>> path2 = paths.get(1);

            if (paths.get(0).size() > 0 && paths.get(1).size() > 0) {                 // Handle cycle
                boolean backOption = false;
                String menuOptions =
                        "What would you like to see?" +
                        "\n\t1) Paths from " + target1 + " to " + target2 +
                        "\n\t2) Paths from " + target2 + " to " + target1 +
                        "\n\t3) Back";
                do {
                    System.out.println(menuOptions);
                    int choice = Utils.readIntInRange(1, 3);

                    if (choice == 3) {
                        backOption = true;
                    } else {
                        pathToSend = choice == 1 ? path1 : path2;
                        Utils.printPaths(pathToSend);
                    }
                } while (!backOption);
            } else {
                pathToSend = paths.get(0).size() > 0 ? paths.get(0) : paths.get(1);

                printPaths(pathToSend);
            }
        }
    }

    static public void printPaths(Collection<List<TargetDTO>> allPaths) {
        boolean backOption = false;

        do {
            PathOptions pathOption = PathMenu.getPathOption(PathMenu.PathType.REGULAR, allPaths);
            if (pathOption == null) {
                backOption = true;
            } else {
                printAllPaths(pathOption.returnPath(allPaths));
            }
        } while (!backOption);
    }

    static private void printAllPaths (Collection<List<TargetDTO>> allPaths) {
        int pathNum = 1;

        for (List<TargetDTO> path : allPaths) {
            System.out.println("[Path " + pathNum + "]");
            Utils.printPath(path);
            System.out.print(System.lineSeparator());
            pathNum++;
        }
    }

    static public void printPath(Collection<TargetDTO> path) {
        if (path == null) {
            return;
        } else if  (path.size() == 0) {
            System.out.println("Empty path! Nothing exists.");
            return;
        }

        String delim = " -> ";
        StringBuilder pathStr = new StringBuilder();

        for (TargetDTO target : path) {
            pathStr.append(target.getName()).append(delim);
        }
        int strLen = pathStr.length();
        int delLen = delim.length();

        pathStr = new StringBuilder(pathStr.substring(0, (strLen - delLen)));

        System.out.println(pathStr);
    }

    public static boolean isValidPath(Collection<TargetDTO> pathCandidate) {
        boolean res = pathCandidate != null;

        if(pathCandidate != null && pathCandidate.size() <= 1) {
            res = false;
        }

        return res;
    }

    public static double ReadDouble() {
        /* TODO
         * I had to insert an initial value (0) to userInput,
         * otherwise it wouldn't compile.
         * But this is a mostly wrong value
         * Find out a way to implement this method without an initial value.
         */
        double userInput = 0;
        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;

        do {
            try {
                userInput = scanner.nextDouble();
                validInput = true;
            } catch (InputMismatchException inputMismatchException) {
                System.out.println("Input isn't a double. y u do dis? Try again, now with a double! (Like 16, or 0.45)");
                scanner.nextLine();
            } catch (Exception ignore) {

            }
        } while (!validInput);

        return userInput;
    }

    public static void printTargetWithoutTask(TargetDTO targetDetails) {
        System.out.println(engine.getFormalizedTargetDataString(targetDetails));
    }

    public static boolean isNullOrEmptyStr(String str) {
        return (str == null || str.trim().isEmpty());
    }

    public static DFS.EdgeDirection getDirection() {
        String msg = "Please choose a direction:";
        int choice = readEnumChoice(msg, DFS.EdgeDirection.values());

        return DFS.EdgeDirection.values()[choice];
    }

    public static TaskType getTaskType() {
        String msg = "Please choose a task type:";
        int choice = readEnumChoice(msg, TaskType.values());

        return TaskType.values()[choice];
    }

    public static ChosenTarget.RelationshipDirection getRelationshipDirection() {
        String msg = "Please choose the direction you would like:";
        int choice = readEnumChoice(msg, ChosenTarget.RelationshipDirection.values());

        return ChosenTarget.RelationshipDirection.values()[choice];
    }

    /**
     * @return chosen index at the enum array.
     */
    private static int readEnumChoice(String message, Enum[] values) {
        System.out.print(message);
        int i = 0;

        for (Enum value : values) {
            i++;
            System.out.print("\n\t" + i + ") " + value);
        }
        System.out.println();
        int choice = Utils.readIntInRange(1, i);

        return (choice - 1);
    }

    public static boolean tryAgain() {
        final String yesStr = "y";
        final String noStr = "n";
        Scanner scanner = new Scanner(System.in);
        String answer = "";
        boolean validResponse = false;

        while (!validResponse) {
            System.out.println("Would you like to try again? (" + yesStr + "\\" + noStr + ")");
            answer = scanner.nextLine();
            answer = answer.trim();

            if (!answer.equalsIgnoreCase(yesStr) && !answer.equalsIgnoreCase(noStr)) {
                System.out.println("Invalid response. Write either \"" + yesStr + "\" or \"" + noStr + "\" please.");
            } else {
                validResponse = true;
            }
        }

        return answer.equalsIgnoreCase(yesStr);
    }

    public static int readNumOfThreads() {
        int maxThreads = engine.getThreadCount_maxParallelism();
        System.out.println("How many threads would you like? (Minimum 1, maximum " + maxThreads + ")");

        return Utils.readIntInRange(1, maxThreads);
    }
}