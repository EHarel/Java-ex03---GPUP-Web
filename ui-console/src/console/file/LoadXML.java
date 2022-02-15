package console.file;

import exception.*;
import console.menu.system.DoesAction;
import console.menu.loader.LoadMenu;
import logic.Engine;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Scanner;

public class LoadXML implements DoesAction {
    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        loadSystem();
    }

    private void loadSystem() {
        boolean fileFound = false;
        String fileName;

        while (!fileFound) {
            System.out.println();
            fileName = getFileName();
            if (fileName == null) {
                return;
            }

            if (! fileName.endsWith(".xml")) {
                System.out.println("Hey, that's not a proper file name. Try ending with something like: \"myfile.xml\"");
            } else {
                try {
                    engine.loadXML(fileName);
                    fileFound = true;
                    System.out.println("File loaded successfully.");
                } catch (FileNotFoundException fnfe) {
                    System.out.println("Could not find file! Make sure you entered the path properly, and that the file is located there.");
                } catch (JAXBException JAXBexc) {
                    System.out.println("Some problem with JAXB, good luck figuring that out.");
                    JAXBexc.printStackTrace();
                } catch (ExistingItemException e) {
                    System.out.println("Error! Existing target exception!");
                    System.out.println(e.getMessage());
                } catch (DependencyOnNonexistentTargetException e) {
                    System.out.println("Error! Dependency on nonexistent target!");
                    System.out.println(e.getMessage());
                } catch (ImmediateCircularDependencyException e) {
                    System.out.println("Error! Immediate circular dependency exception!");
                    System.out.println(e.getMessage());
                } catch (NullOrEmptyStringException e) {
                    System.out.println("Error! null or empty string exception!");
                    System.out.println(e.getMessage());
                } catch (InvalidInputRangeException e) {
                    System.out.println("Error! maxParallelism.");
                    System.out.println(e.getMessage());
                } catch (NonexistentTargetException e) {
                    System.out.println("Error! nonexistent target exception.");
                    System.out.println(e.getMessage());
                } catch (SerialSetNameRepetitionException e) {
                    System.out.println("Error! Repeating serial set name");
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("General exception catch by Lazy Eli.");
                    System.out.println(e.getMessage());
                }
            }
        }

        LoadMenu.fileLoaded = true;
    }

    private String getFileName() {
        final String backOption = "back";
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter full path of xml file, and write .xml suffix (or write \"" + backOption + "\" to go back):");
        String fileName = scanner.nextLine();

        fileName = fileName.trim();

        if (fileName.equals(backOption)) {
            fileName = null;
        }

        return fileName;
    }
}