package console.file;

import console.menu.system.DoesAction;
import logic.Engine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Save implements DoesAction {
    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        saveSystem();
    }

    private void saveSystem() {
        System.out.println("Please enter a file path to save to (without suffix!): ");
        Scanner scanner = new Scanner(System.in);
        String filePathWithoutSuffix = scanner.nextLine();

        try {
            engine.save(filePathWithoutSuffix);
            System.out.println("File saved successfully :)");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR! We couldn't find the file, sorry!");
            System.out.println("(Make sure you're not adding a suffix. C:\\Temp\\file is good, C:\\Temp\\file.txt is bad!)");
        }
    }
}