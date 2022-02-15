package console.graph;

import console.Utils;
import console.menu.system.DoesAction;
import exception.NonexistentTargetException;
import exception.UninitializedNullException;
import graph.DependenciesGraph;
import graph.Graph;
import graph.TargetDTO;

import java.util.*;


public class PathBetweenTargets implements DoesAction {
    private Scanner scanner = new Scanner(System.in);
    private final Graph graph;

    public PathBetweenTargets(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void DoAction() {
        findPathsBetweenTargets();
    }

    private void findPathsBetweenTargets() {
        System.out.println("[First target]");
        Optional<String> optionalTarget1Name = Utils.getTargetName((DependenciesGraph) graph);
        if(!optionalTarget1Name.isPresent()) {
            return;
        }

        System.out.println("\n[Second target]");
        Optional<String> optionalTarget2Name = Utils.getTargetName((DependenciesGraph) graph);
        if (!optionalTarget2Name.isPresent()) {
            return;
        }

        System.out.print(System.lineSeparator());
        ArrayList<Collection<List<TargetDTO>>> paths;
        try {
            paths = graph.getPathsBetweenTargets(optionalTarget1Name.get(), optionalTarget2Name.get());

            if (paths == null) {
                System.out.println("Whoops, something went wrong! It's likely that one of the targets doesn't exist.");
                return;
            }

            if (paths.get(0).size() == 0 && paths.get(1).size() == 0) {
                if (optionalTarget1Name.get().equalsIgnoreCase(optionalTarget2Name.get())) {
                    System.out.println(optionalTarget1Name.get() + " is not part of a cycle (no paths from it to itself)");
                } else {
                    System.out.println("There are no paths from " + optionalTarget1Name.get() + " to " + optionalTarget2Name.get() + " or vice versa.");
                }
                return;
            }

            if (paths.get(0).size() > 0 && paths.get(1).size() > 0) {
                if (isSameTarget(optionalTarget1Name.get(), optionalTarget2Name.get())) {
                    System.out.println("Uh-oh, " + optionalTarget1Name.get() + " is in a cycle!");
                    Utils.printPaths(paths.get(0));
                } else {
                    System.out.println("Uh-oh, there's a cycle involving " + optionalTarget1Name + " and " + optionalTarget2Name + ".");
                    Utils.printPathsCycle(paths, optionalTarget1Name.get(), optionalTarget2Name.get());
                }
            } else {
                String targetRequiredFor;
                String targetDependentOn;

                if (paths.get(0).size() > 0) {
                    targetDependentOn = optionalTarget1Name.get();
                    targetRequiredFor = optionalTarget2Name.get();
                } else {
                    targetRequiredFor = optionalTarget1Name.get();
                    targetDependentOn = optionalTarget2Name.get();
                }

                System.out.println("\"" + targetDependentOn + "\" is dependent on \"" + targetRequiredFor + "\"" );
                Collection<List<TargetDTO>> pathsToSend = paths.get(0).size() > 0 ? paths.get(0) : paths.get(1);
                System.out.println();
                System.out.println();
                Utils.printPaths(pathsToSend);
            }
        } catch (NonexistentTargetException e) {
            System.out.println("Whoops, one of the targets doesn't exist!\n" + e.getMessage());
        } catch (UninitializedNullException e) {
            System.out.println("Whoops, something went wrong! We're sorry, we're not sure what :(");
        }
    }

    private boolean isSameTarget(String target1Name, String target2Name) {
        return target1Name.equalsIgnoreCase(target2Name);
    }
}