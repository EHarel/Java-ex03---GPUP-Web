package console.file;

import console.menu.system.DoesAction;
import console.menu.loader.LoadMenu;
import logic.Engine;

import java.util.Scanner;

public class LoadRegular implements DoesAction {
    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        loadSaveFile();
    }

    private void loadSaveFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path to load (without file suffix!):");
        String filePathWithoutSuffix = scanner.nextLine();
        try {
            engine.loadRegular(filePathWithoutSuffix);
            LoadMenu.fileLoaded = true;
            System.out.println("File loaded successfully.");
        } catch (Exception e) {
            System.out.println("Could not find the file. Are you sure it's a full path, written correctly?");
            System.out.println();
        }
    }
}