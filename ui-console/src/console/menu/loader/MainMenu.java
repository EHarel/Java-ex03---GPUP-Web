package console.menu.loader;

import console.file.Save;
import console.menu.system.DoesAction;
import console.menu.system.MenuAction;
import console.menu.system.MenuManager;
import console.menu.system.SubMenu;
import logic.Engine;

public class MainMenu {
    private static MenuManager graphMenuManager;
    private static SubMenu mainMenu = new SubMenu("Main Menu", null);

    public static MenuManager initGraphMenu() {
        setLoadMenu(); // 1
        setGraphMenu(); // 2
        setTaskMenu(); // 3
        setSaveMenu(); // 4

        graphMenuManager = new MenuManager(mainMenu, false);

        return graphMenuManager;
    }

    // 1
    private static void setLoadMenu() {
        SubMenu loadMenu = LoadMenu.initLoadSubMenu();
        loadMenu.setParentMenu(mainMenu);
    }

    // 2
    private static void setGraphMenu() {
        new MenuAction("Graph Analysis", mainMenu, new MainGraphMenu());
    }

    // 3
    private static void setTaskMenu() {
        new TaskSubMenu(mainMenu);
    }

    // 4
    private static void setSaveMenu() {
        new MenuAction("Save current system state to file", mainMenu, new Save());
    }

    // 5
    private static class MainGraphMenu implements DoesAction {
        @Override
        public void DoAction() {
            graphMenu();
        }

        private void graphMenu() {
            new GraphMenu(Engine.getInstance().getGraph(), "Graph Analysis").run();
        }
    }
}