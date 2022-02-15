import console.menu.loader.MainMenu;
import console.menu.loader.LoadMenu;

public class Program {
    public static void main(String[] args) {
        LoadMenu.getInitialLoad();
        System.out.print(System.lineSeparator());
        MainMenu.initGraphMenu().RunMenu();
        System.out.println("\nGoodbye!\n");
    }
}