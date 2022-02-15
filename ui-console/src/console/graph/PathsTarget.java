package console.graph;

import algorithm.DFS;
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

public class PathsTarget implements DoesAction {
    private final Graph graph;

    public PathsTarget(Graph graph) { this.graph = graph;}

    @Override
    public void DoAction() {
        findTargetPaths();
    }

    private void findTargetPaths() {
        Optional<String> optionalTarget1Name = Utils.getTargetName((DependenciesGraph) graph);
        if(!optionalTarget1Name.isPresent()) {
            return;
        }

        DFS.EdgeDirection direction = Utils.getDirection();

        System.out.print(System.lineSeparator());
        Collection<List<TargetDTO>> paths;
        try {
            paths = graph.getTargetPaths(optionalTarget1Name.get(), direction);

            if (paths == null) {
                System.out.println("Whoops, something went wrong!");
            } else {
                Utils.printPaths(paths);
            }

        } catch (NonexistentTargetException e) {
            System.out.println("Uh-oh, couldn't find the target!");
        } catch (UninitializedNullException e) {
            System.out.println("Uh-oh, source target doesn't exist (huh?)");
        }
    }
}
