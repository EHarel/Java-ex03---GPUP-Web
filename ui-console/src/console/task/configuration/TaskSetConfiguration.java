package console.task.configuration;

import console.Utils;
import console.menu.loader.TaskSubMenu;
import console.menu.system.DoesAction;
import console.menu.system.MenuManager;
import exception.InvalidInputRangeException;
import exception.NonexistentElementException;
import logic.Engine;
import task.configuration.Configuration;
import task.configuration.ConfigurationCompilation;
import task.configuration.ConfigurationSimulation;

import javax.naming.NameNotFoundException;
import java.util.Scanner;

public class TaskSetConfiguration implements DoesAction {
    private String warningStr = "The name must be unique, to not conflict with existing configuration.";

    private Engine engine = Engine.getInstance();

    @Override
    public void DoAction() {
        setConfiguration();
    }

    private void setConfiguration() {
        boolean continueLoop = true;

        while (continueLoop) {
            Configuration configuration = receiveUserParameters();

            if (configuration == null) {
                continueLoop = Utils.tryAgain();
            } else {
                if (!engine.addConfigAndSetActive(TaskSubMenu.getTaskType(), configuration)) {
                    System.out.println("ERROR! Configuration by name \"" + configuration.getName() + "\" already exists for this task type.");
                    continueLoop = MenuManager.tryAgain();
                } else {
                    System.out.println("\nConfiguration set.\n");
                    ConfigUtils.printConfig(configuration.getData());
                    continueLoop = false;
                }
            }
        }
    }

    /**
     *
     * @return null if user wants to go back, or if error occurred.
     */
    private Configuration receiveUserParameters() {
        Configuration config = null;
        boolean validInput = false;

        while(!validInput) {
            System.out.println("Fill up parameters for " + TaskSubMenu.getTaskType() + " task.");

            switch (TaskSubMenu.getTaskType()) {
                case SIMULATION:
                    config = getSimConfig();
                    break;
                case COMPILATION:
                    config = getCompConfig();
                    break;
            }

//            if (config == null) {
//                System.out.print("Something went wrong. ");
//                validInput = !Utils.tryAgain();
//            } else {
//                validInput = true;
//            }
            validInput = true;
        }

        return config;
    }

    private ConfigurationSimulation getSimConfig() {
        ConfigurationSimulation simConfig = null;
        int numOfThread = Utils.readNumOfThreads();

        String name = Utils.readName(warningStr);
        int processingTime = readProcessingTime();
        boolean isRandomProcessingTime = readRandom();
        double successProbability = readSuccessProbability();
        double warningProbability = readWarningProbability();

        try {
            simConfig = new ConfigurationSimulation(name, numOfThread, successProbability, processingTime, isRandomProcessingTime, warningProbability);
        } catch (InvalidInputRangeException | NameNotFoundException e) {
            System.out.println("ERROR! One of the inputs was invalid.");
            System.out.println(e.getMessage());
        }

        return simConfig;
    }


    private double readSuccessProbability() {
        String probability = "success per node";
        return readProbability(probability);
    }

    private double readWarningProbability() {
        String warning = "warnings per node";
        return readProbability(warning);
    }

    private double readProbability(String topic) {
        boolean validInput = false;
        double res;

        do {
            System.out.println("Please enter probability for " + topic + " (number between 0 and 1)");
            res = Utils.ReadDouble();

            if (ConfigurationSimulation.isValidSuccessProbability(res)) {
                validInput = true;
            } else {
                System.out.println("Value is out of range (insert things like 0.75, not 75)");
            }
        } while (!validInput);

        return res;
    }

    private boolean readRandom() {
        Scanner scanner = new Scanner(System.in);
        boolean res = false;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("Would you like processing time to be random? (y\\n)");
            String userInput = scanner.nextLine();
            userInput = userInput.trim();

            if (userInput.equals("y")) {
                res = true;
                validInput = true;
            } else if (userInput.equals("n")) {
                res = false;
                validInput = true;
            } else {
                System.out.println("Hey, you're not sticking to format.");
            }
        }

        return res;
    }

    private int readProcessingTime() {
        boolean backOption = false;
        int res = 0;

        while (!backOption) {
            System.out.println("Please enter processing time (integer given in milliseconds, e.g. 34000 and not 34950)");
            res = Utils.ReadInt();

            if (ConfigurationSimulation.isValidProcessingTime(res)) {
                backOption = true;
            } else {
                System.out.println("Invalid option!");
            }
        }

        return res;
    }

    private Configuration getCompConfig() {
        Scanner scanner = new Scanner(System.in);

        ConfigurationCompilation compConfig = null;

        String name = Utils.readName(warningStr);
        int numOfThreads = Utils.readNumOfThreads();

        System.out.println("\nPlease enter path of source files directory. Note that it must be an existing directory.");
        String srcPath = scanner.nextLine();

        System.out.println("\nPlease enter path for output files. It doesn't have to already exist.");
        String outputPath = scanner.nextLine();

        try {
            compConfig = new ConfigurationCompilation(name, numOfThreads, srcPath, outputPath);
        } catch (NonexistentElementException e) {
            System.out.println("Error! Non-existent source directory. I think. I dunno man, I'm the UI.");
            System.out.println(e.getMessage());
        } catch (InvalidInputRangeException invalidInputRangeException) {
            invalidInputRangeException.printStackTrace();
        } catch (NameNotFoundException e) {
            System.out.println("Configuration must have a name");
        }

        return compConfig;
    }
}