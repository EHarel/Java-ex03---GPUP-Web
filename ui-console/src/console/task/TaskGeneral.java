package console.task;

import console.Utils;
import console.menu.loader.PathMenu;
import console.menu.loader.ExecutionAnalysisMenu;
import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import console.menu.system.MenuManager;
import console.task.consumer.ConsumerPrintExecution;
import console.task.consumer.ConsumerPrintTarget;
import console.task.consumer.ConsumerStartProcessingUI;
import exception.NoConfigurationException;
import exception.UninitializedTaskException;
import graph.TargetDTO;
import task.Execution;
import logic.Engine;
import task.OldCode.TaskProcess;
import task.configuration.ChosenTarget;
import task.consumer.ConsumerManager;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;

public class TaskGeneral implements DoesAction {
    private static TaskState taskState;
    public static synchronized TaskState getTaskState() { return taskState; }

    public static class TaskState {
        boolean finished = false;

        public boolean isFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }
    }


    private static Engine engine = Engine.getInstance();
    private static boolean printWithMilliseconds = true;
    TaskProcess.StartPoint startPoint;
    private static ConsumerManager consumerManager;

    public TaskGeneral(TaskProcess.StartPoint startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public void DoAction() {
        callTask(startPoint);
    }

    public static void callTask(TaskProcess.StartPoint startPoint) {
        try {
            TaskGeneral.taskState = new TaskState();

            Collection<String> chosenTargetsForExecution = chooseTargetsForExecution();
            System.out.println("UI about to call execution and go to sleep...\n");
            engine.executeTask(TaskSubMenu.getTaskType(), startPoint, getTaskConsumers(), chosenTargetsForExecution);

            managePause();
            System.out.println("\n ---------- After waking up! ---------- \n");

            Execution execution = engine.getExecutionLast(TaskSubMenu.getTaskType());
            new ExecutionAnalysisMenu(execution).run();
        } catch (UninitializedTaskException uninitializedTaskException) { // TODO: delete this?
            System.out.println("Whoa, hold on! No configuration has been set yet.\n" + uninitializedTaskException.getMessage());
        } catch (IOException e) {
            System.out.println("IOException :( \nProbably failed to open the directory");
            System.out.println(e.getMessage());
        } catch (NoConfigurationException e) {
            System.out.println("Whoa, hold on! No configuration has been set.\n" + e.getMessage());
        }
    }

    private static void managePause() {
        boolean paused = false;
        final String resumeStr = "resume";
        final String pauseStr = "pause";

        String userInput;
        Scanner scanner = new Scanner(System.in);

        int maxParallelism = engine.getThreadCount_maxParallelism();

        while(!getTaskState().finished) {
            if (paused) {
                int currentParallelism = engine.getThreadCount_currentParallelism();

                System.out.println("Type \"" + resumeStr + "\" to continue.");
                System.out.println("Otherwise, enter a number between 1 and " + maxParallelism + " to set the number of working threads.");
                System.out.println("(Currently there are " + currentParallelism + " working threads)");

                userInput = scanner.next();
                if (userInput.equalsIgnoreCase(resumeStr)) {
                    engine.pauseExecution(false);
                    paused = false;
                } else {
                    try {
                        int threadChoice = Integer.parseInt(userInput);

                        if (threadChoice >= 1 && threadChoice <= maxParallelism && threadChoice != currentParallelism) {
                            engine.setThreadCount_activeThreads(threadChoice);
                        }
                    } catch (NumberFormatException ignore) {}
                }
            } else {
                System.out.println("Type \"" + pauseStr + "\" to pause.");

                userInput = scanner.next();
                if (userInput.equals(pauseStr)) {
                    engine.pauseExecution(true);
                    paused = true;
                }
            }
        }
    }

    /**
     * Decides which targets the user wants
     */
    private static Collection<String> chooseTargetsForExecution() {
        final String backOption = "back";
        Collection<ChosenTarget> chosenTargets = new LinkedList<>();
        Collection<String> chosenNames = new LinkedList<>();

        System.out.println("Would you like to choose specific targets for the execution?");
        System.out.println("(Choosing 'no' means that by default all targets participate)");
        if (MenuManager.readYesNoOption()) {
            Scanner scanner = new Scanner(System.in);
            String userChoice;

            do {
                System.out.println("Enter a target name to include in the execution. Write \"back\" if you desire to stop. (Note that Eli is lazy and doesn't check if targets exist because the UI is dead in a few days)");
                userChoice = scanner.nextLine();
                userChoice = userChoice.trim();
                if (!userChoice.equals(backOption)) {
                    ChosenTarget.RelationshipDirection relationshipDirection = Utils.getRelationshipDirection();
                    ChosenTarget newChosen = new ChosenTarget(userChoice, relationshipDirection);
                    chosenTargets.add(newChosen);
                    chosenNames.add(newChosen.getName());
                    System.out.println();
                }
            } while (!userChoice.equals(backOption));
        }

        return chosenNames;
    }

    public static ConsumerManager getTaskConsumers() {
        if (consumerManager == null) {
            consumerManager = new ConsumerManager();

            consumerManager.getStartTargetConsumers().add(new ConsumerStartProcessingUI());
            consumerManager.getEndTargetConsumers().add(new ConsumerPrintTarget());
            consumerManager.getEndProcessConsumers().add(new ConsumerPrintExecution());
        }

        return consumerManager;
    }

    public static void printExecutionReport(Execution execution) {
        System.out.println();
        System.out.println(Engine.getInstance().getFormalizedExecutionReportString(execution));
        System.out.println();
        System.out.println();
    }

    public static void printReports(String title, Collection<TargetDTO> targetData, PathMenu.ReportType reportType) {
        if (targetData != null) {
            printTitle(title, targetData.size());

            switch (reportType) {
                case DETAIL:
                    printDetailed(targetData);
                    break;
                case GENERAL:
                    printGeneral(targetData);
                    break;
            }
        }
    }

    private static void printTitle(String title, int count) {
        if (title != null && !title.trim().isEmpty()) {
            System.out.println("[" + title + " (" + count + ")]");
        } else {
            System.out.println("Report (" + count + " items):");
        }
        System.out.println();
    }

    private static void printGeneral(Collection<TargetDTO> allReports) {
        if (allReports.size() > 0) {
            int i = 1;
            StringBuilder str = new StringBuilder();
            for (TargetDTO targetDTO : allReports) {
                TargetDTO.TaskStatusDTO report = targetDTO.getTaskStatusDTO();

                str.append("\n\t").append(i).append(") ").append(targetDTO.getName()).append(" [");

                if (report == null || report.getResult() == TargetDTO.TaskResult.UNPROCESSED) {
                    str.append(TargetDTO.TaskResult.UNPROCESSED);
                } else {
                    if (report.getResult() != TargetDTO.TaskResult.UNPROCESSED) {
                        str.append(report.getResult()).append(" -- ").append(engine.formatTimeDuration(report.getStartInstant(), report.getEndInstant()));
                    }
                }

                str.append("]");

                i++;
            }

            System.out.println(str);
        }
    }

    private static void printDetailed(Collection<TargetDTO> reports) {
        int i = 1;
        for (TargetDTO targetDTO : reports) {
            System.out.print(i + ") ");
            printTargetData(targetDTO);
            System.out.print(System.lineSeparator());
            i++;
        }
    }

    public static void printTargetData(TargetDTO targetDTO) {
        System.out.println(engine.getFormalizedTargetDataString(targetDTO));
    }

    public void pauseInput() {

    }
}

/* TODO:
    * Option to see all paths of skipped targets
 */
