package console.task;

import console.menu.loader.GraphMenu;
import console.menu.system.DoesAction;
import graph.Graph;

public class TaskGraphAnalysis implements DoesAction {
    private final Graph graph;
    private final String menuName;

    public TaskGraphAnalysis(Graph graph, String menuName) {
        this.graph = graph;
        this.menuName = menuName;
    }

    @Override
    public void DoAction() {
        new GraphMenu(graph, menuName).run();
    }
}
