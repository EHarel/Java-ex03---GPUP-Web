package console.menu.loader;

import console.Utils;
import console.file.LoadRegular;
import console.file.LoadXML;
import console.menu.system.MenuAction;
import console.menu.system.SubMenu;
import console.menu.system.SubMenuLoad;

public class LoadMenu {
    public static boolean fileLoaded = false;
    private static SubMenuLoad loadMenu = new SubMenuLoad("Load file", null);
    private static MenuAction actionLoadXML = new MenuAction("Load XML file", loadMenu, new LoadXML());
    private static MenuAction actionLoadSave = new MenuAction("Load saved state", loadMenu, new LoadRegular());

    public static SubMenu initLoadSubMenu() {
        return loadMenu;
    }

    public static void getInitialLoad()
    {
        while (!fileLoaded) {
            System.out.println("Pick an option:");
            System.out.println("\t1) Load XML file");
            System.out.println("\t2) Load last saved state");

            int userChoice = Utils.readIntInRange(1,2);
            if (userChoice == 1) {
                actionLoadXML.selected();
            } else {
                actionLoadSave.selected();
            }
        }
    }
}