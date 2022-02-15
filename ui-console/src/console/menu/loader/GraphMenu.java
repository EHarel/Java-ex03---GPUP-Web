package console.menu.loader;

import console.graph.*;
import console.menu.system.MenuAction;
import console.menu.system.MenuManager;
import console.menu.system.SubMenu;
import graph.Graph;

import java.awt.*;

public class GraphMenu {
    private MenuManager graphMenuManager;
    private SubMenu mainMenu;
    private Graph graph;

    public GraphMenu(Graph graph, String menuName) {
        this.graph = graph;
        initMenu(menuName);
    }

    public void run() {
        graphMenuManager.RunMenu();
    }

    private void initMenu(String menuName) {
        mainMenu = new SubMenu(menuName, null);
        new MenuAction("[General] All target data", mainMenu, new TargetsDataGeneral(graph));
        new MenuAction("[Detailed] All target data", mainMenu, new TargetsDataDetailed(graph));
        new MenuAction("Show specific target", mainMenu, new ShowSpecificTarget(graph));
        new MenuAction("Show all paths related to specific target", mainMenu, new PathsTarget(graph));
        new MenuAction("Find paths between two targets", mainMenu, new PathBetweenTargets(graph));
        new MenuAction("Find cycles of a specific target", mainMenu, new FindIfTargetInCycle(graph));
        new MenuAction("Determine if graph has a cycle", mainMenu, new DetermineCycle(graph));
        new MenuAction("Get topological sorting of graph", mainMenu, new GetTopologicalSort(graph));

        graphMenuManager = new MenuManager(mainMenu, true);
    }
}