package console.graph;

import console.Utils;
import console.menu.system.DoesAction;
import graph.Graph;
import graph.TargetDTO;

import java.util.Collection;

public class TargetsDataDetailed implements DoesAction {
    private final Graph graph;

    public TargetsDataDetailed(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void DoAction() {
        printAllTargetDataDetailed();
    }

    private void printAllTargetDataDetailed() {
        Collection<TargetDTO> targetsData = graph.getAllTargetData();

        for (TargetDTO targetDTO : targetsData) {
            Utils.printTargetWithoutTask(targetDTO);
            System.out.print(System.lineSeparator());
        }
    }
}
