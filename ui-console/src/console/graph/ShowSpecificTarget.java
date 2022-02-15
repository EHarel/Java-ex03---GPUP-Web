package console.graph;

import console.Utils;
import console.menu.system.DoesAction;
import graph.DependenciesGraph;
import graph.Graph;
import logic.Engine;
import graph.TargetDTO;

import java.util.Optional;
import java.util.Scanner;


public class ShowSpecificTarget implements DoesAction {
    private Scanner scanner = new Scanner(System.in);
    private Engine engine = Engine.getInstance();
    private final Graph graph;

    public ShowSpecificTarget(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void DoAction() {
        printSpecificTargetDetails();
    }

    private void printSpecificTargetDetails() {
        /* TODO
         * (1) Make sure the use of scanner is correct.
         * (2) Add option to load graph if somehow user arrived here without it?
         * (3) Find a way to make the loop more generic, maybe as a static method of MenuManager, for reusability?
         */
        boolean backOption;

        do {
            Optional<String> optionalTargetName = Utils.getTargetName((DependenciesGraph) graph);
            backOption = !optionalTargetName.isPresent();

            optionalTargetName.ifPresent(name -> {
                if (!engine.graphLoaded()) {
                    System.out.println("Graph isn't loaded yet! How did you even get here?");
                } else if (!graph.contains(name)) {
                    System.out.println("\nLost? No such target in graph.\n");
                } else {
                    TargetDTO targetDTO = graph.getSpecificDataOneTarget(name);
                    if (targetDTO == null) {
                        System.out.println("Sorry, unknown error in fetching the data! We're not sure what went wrong.");
                    } else {
                        System.out.print(System.lineSeparator());
                        Utils.printTargetWithoutTask(targetDTO);
                        System.out.print(System.lineSeparator());
                    }
                }
            });
        } while (!backOption);
    }
}