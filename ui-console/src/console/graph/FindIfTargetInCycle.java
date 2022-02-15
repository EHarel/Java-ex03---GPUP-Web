package console.graph;

import console.Utils;
import console.menu.system.DoesAction;

import exception.NonexistentTargetException;
import exception.UninitializedNullException;
import graph.DependenciesGraph;
import graph.Graph;
import graph.TargetDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class FindIfTargetInCycle implements DoesAction {
    private final DependenciesGraph graph;

    public FindIfTargetInCycle(Graph graph) {
        this.graph = (DependenciesGraph) graph;
    }

    @Override
    public void DoAction() {
        findCycle();
    }

    private void findCycle() {
        // Get target name to search for
        Optional<String> optionalName = Utils.getTargetName(graph);

        optionalName.ifPresent(targetName -> {
            Collection<List<TargetDTO>> allPaths;
            final String notCycleStr = "Target " + targetName + " is not part of a cycle. Rejoice!";

            try {
                allPaths = graph.getCycleWithTarget(targetName);
                if (allPaths == null || allPaths.size() == 0) {
                    System.out.println(notCycleStr);
                }
                else {
                    Collection<TargetDTO> firstPath = allPaths.iterator().next();
                    if (!Utils.isValidPath(firstPath)) {
                        System.out.println(notCycleStr);
                    } else {
                        Utils.printPaths(allPaths);
                    }
                }
            } catch (NonexistentTargetException e) {
                System.out.println("Whoa there, the target doesn't exist.\n" + e.getMessage());
            } catch (UninitializedNullException e) {
                System.out.println("Whoops, something went wrong! We're not sure what :(");
            }
        });
    }
}