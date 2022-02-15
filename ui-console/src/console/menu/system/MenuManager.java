package console.menu.system;

import console.Utils;

import java.util.Scanner;

public class MenuManager {
    private MenuItem root; // todo final?
    private MenuItem currentMenuItem;
    private final int startingLevel = 1;
    private int currentLevel;
    private boolean subMenuSystem;
    static public final String returnStr = "back";
    static public final String tryAgainYes = "y";
    static public final String tryAgainNo = "n";


    public MenuManager(MenuItem root, boolean subMenuSystem) {
        this.root = root;
        currentLevel = startingLevel;
        currentMenuItem = root;
        this.subMenuSystem = subMenuSystem;
    }

    public void RunMenu() {
        while (currentLevel >= startingLevel) {
            System.out.println();
            currentMenuItem.updateMenuItemText();
            System.out.println(currentMenuItem.toString());
            printReturnOption();
            int selectedItem = readUserSelection();
            System.out.print(System.lineSeparator());
            activateMenuItem(selectedItem);
            System.out.print(System.lineSeparator());
        }
    }

    /**
     * @return user selected index as a number between 0 and [size], where [size] is "back" option
     * and [size-1] is the last item in the collection.
     */
    private int readUserSelection() {
        int userSelection;
        boolean validOption = false;
        int optionRange = currentMenuItem.getOptionRange();
        int optionRangeIncremented = optionRange + 1; // to start menu from 1, and allow for "Back" option

        System.out.print("Please choose an option between 1 and " + optionRangeIncremented + ": ");

        /* TODO
         * Maybe use a single method in Utils for reading an int in a certain range?
         * I think I implemented that for the enum options.
         */
        do {
            userSelection = Utils.ReadInt();

            if (userSelection < 1 || userSelection > optionRangeIncremented) {
                System.out.println("Fool! Option is out of range.");
            }
            else {
                validOption = true;
            }
        }
        while (!validOption);

        return (userSelection - 1);
    }

    private void printReturnOption() {
        String returnOption = getReturnOptionStr();
        int range = currentMenuItem.getOptionRange() + 1;
        String returnString = MenuItem.addNumberAndName(range, returnOption);
        System.out.println(returnString);
    }

    private String getReturnOptionStr() {
        String returnOption = "Return";

        if (currentMenuItem.parentMenuItem == null) {
            if (subMenuSystem) {
                returnOption = "Return";
            } else {
                returnOption = "Exit";
            }
        }

        return returnOption;
    }

    private void activateMenuItem(int selectedItemNumber) {
        /*
        [size-1] is last item in the collection
        [size] is "back" option
         */

        if (selectedItemNumber == currentMenuItem.getOptionRange()) { // Back option
            currentLevel--;
            currentMenuItem = currentMenuItem.getParentMenu();
        }
        else {
            MenuItem chosenItem = currentMenuItem.getItem(selectedItemNumber);

            if (chosenItem.isSubMenu()) {
                currentMenuItem = chosenItem;
                currentLevel++;
            }
            else {
                // TODO activate menuAction and figure out whether to go back or not
                ((MenuAction)chosenItem).selected();
            }
        }
    }

    /**
     * Asks the user if he wants to continue trying whatever section he's at.
     * @return Returns true if user wants to continue. False if user wants to stop.
     */
    public static boolean tryAgain() {
        Scanner scanner = new Scanner(System.in);
        String userChoice;
        boolean validInput = false;
        boolean res = false;

        do {
            System.out.println("Would you like to try again mister? (" + tryAgainYes + "\\" + tryAgainNo + ")");
            userChoice = scanner.nextLine();
            userChoice = userChoice.trim();

            if (userChoice.equals(tryAgainYes)) {
                validInput = true;
                res = true;
            } else if (userChoice.equals(tryAgainNo)) {
                validInput = true;
                res = false;
            } else {
                System.out.println("Invalid input! Enter exactly as stated, or be stuck here for eternity.");
            }
        } while (!validInput);


        return res;
    }

    /**
     *
     * @return true if user chose "Yes".
     */
    public static boolean readYesNoOption() {
        Scanner scanner = new Scanner(System.in);
        String userChoice;
        boolean validInput = false;
        boolean res = false;

        do {
            System.out.print("Enter " + tryAgainYes + "\\" + tryAgainNo + ": ");
            userChoice = scanner.nextLine();
            userChoice = userChoice.trim();

            if (userChoice.equals(tryAgainYes)) {
                validInput = true;
                res = true;
            } else if (userChoice.equals(tryAgainNo)) {
                validInput = true;
                res = false;
            } else {
                System.out.println("Invalid input! Enter exactly as stated, or be stuck here for eternity.");
            }
        } while (!validInput);


        return res;
    }
}