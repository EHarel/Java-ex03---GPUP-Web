package file;

import exception.*;
import file.jaxb.schema.generated.GPUPConfiguration;
import file.jaxb.schema.generated.GPUPDescriptor;
import file.jaxb.schema.generated.GPUPTarget;
import file.jaxb.schema.generated.GPUPTargetDependencies;
import graph.*;
import task.enums.TaskType;
import utilsharedall.ConstantsAll;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileManager {
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "file.jaxb.schema.generated";
    public static String saveFilesLocation = "C:/gpup-working-dir";
    private static final String savedFilesSuffix = ".txt";
    private static boolean createdDirectories = false;
    private static final String requiredForStr = "requiredFor";
    private static final String dependsOnStr = "dependsOn";


    private Map<String, Path> executionName2Path = new HashMap<>();

    public FileManager() {
        executionName2Path = new HashMap<>();
    }




    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------ EX03 DIRECTORY MANAGEMENT ------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * @return the full directory path of the directory opened for this execution.
     */
    public void checkAndOpenExecutionDirectory(String executionName) {
        Path executionPath = executionName2Path.get(executionName);

        try {

            if (executionPath == null || !Files.isDirectory(executionPath)) {
                executionPath = createExecutionPath(executionName);
                Files.createDirectories(executionPath);
                executionName2Path.put(executionName, executionPath);
            }
        } catch (Exception ignore) {}
    }

    private Path createExecutionPath(String executionName) {
        Path mainDirPath = getMainDirPath();
        String executionPathStr = "/" + executionName;
        String fullExecutionPathStr = mainDirPath.toString() + executionPathStr;
        Path fullExecutionPath = Paths.get(fullExecutionPathStr);

        return fullExecutionPath;
    }

    private Path getMainDirPath() {
        return Paths.get(ConstantsAll.WORKING_DIR);
    }

    /**
     * @return the full path of the execution directory, as saved in the map.
     */
    public Path getExecutionPath(String executionName) {
        return executionName2Path.get(executionName);
    }

    public void saveTargetLog(String targetName, String targetLog, String executionName) {
        Path fullDirPath = getExecutionPath(executionName);

        String fullLogPath = fullDirPath.toString() + "/" + targetName + ".log";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullLogPath));
            writer.write(targetLog);
            writer.close();
        } catch (IOException ignore) {
        } // Can't stop the execution for this
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- XML LOADING -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public static SaveObject loadRegular(String pathWithoutSuffix) throws IOException {
        String pathWithSuffix = pathWithoutSuffix.trim() + savedFilesSuffix;
//        BufferedReader in = new BufferedReader(new FileReader(path));
//        saveObject = (SaveObject) in.read

        SaveObject saveObject = null;
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(pathWithSuffix))) {
            try {
                saveObject = (SaveObject) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                in.close();
            }
        }

        return saveObject;
    }

    public static void saveNonXML(String filePathWithoutSuffix, SaveObject saveObject) throws IOException {
        String filePathWithSuffix = filePathWithoutSuffix.trim() + savedFilesSuffix;

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(filePathWithSuffix))) {

            out.writeObject(saveObject);
            out.close();
            out.flush();
        }
    }

    /**
     * @return graph if the file is valid, otherwise null or exceptions.
     */
    public static DependenciesGraph getGraphFromXMLInputStreamIfValid(InputStream inputStream, String uploadingUser) throws
            FileNotFoundException,
            JAXBException,
            ExistingItemException,
            DependencyOnNonexistentTargetException,
            ImmediateCircularDependencyException,
            NullOrEmptyStringException,
            InvalidInputRangeException,
            NonexistentTargetException,
            SerialSetNameRepetitionException {
        SaveObject saveObject = FileManager.loadXMLFromInputStream(inputStream, uploadingUser);

        return saveObject.getDependenciesGraph();
    }

    // Code from ex01 and ex02
//    /**
//     * This method also updates the file path to save the reports into.
//     * @throws InvalidInputRangeException If maxParallelism value is 0 (must be a whole number larger than 1).
//     */
//    public static SaveObject loadXML(String filePath) throws
//            FileNotFoundException,
//            JAXBException,
//            ExistingItemException,
//            DependencyOnNonexistentTargetException,
//            ImmediateCircularDependencyException,
//            NullOrEmptyStringException,
//            InvalidInputRangeException,
//            NonexistentTargetException,
//            SerialSetNameRepetitionException {
//        InputStream inputStream = new FileInputStream(filePath);
//
//        return loadXMLFromInputStream(inputStream, uploadingUser);
//    }

    public static SaveObject loadXMLFromInputStream(InputStream inputStream, String uploadingUser) throws JAXBException, SerialSetNameRepetitionException, NonexistentTargetException, DependencyOnNonexistentTargetException, ExistingItemException, ImmediateCircularDependencyException, NullOrEmptyStringException, InvalidInputRangeException {
        GPUPDescriptor gDesc = deserializeFrom(inputStream);

        DependenciesGraph graph = createGraphFromDescriptor(gDesc);
        setPricing(gDesc, graph);
        graph.setUploadingUserName(uploadingUser);

        return new SaveObject(graph, null);
    }

    private static void setPricing(GPUPDescriptor gDesc, DependenciesGraph graph) {
        List<GPUPConfiguration.GPUPPricing.GPUPTask> juxbPricing = gDesc.getGPUPConfiguration().getGPUPPricing().getGPUPTask();

        juxbPricing.forEach(gpupTask -> {
            int price = gpupTask.getPricePerTarget();

            switch (gpupTask.getName()) {
                case "Simulation":
                    graph.setPriceSimulation(price);
                    break;
                case "Compilation":
                    graph.setPriceCompilation(price);
                    break;
            }
        });

    }

    private static GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();

        return (GPUPDescriptor) u.unmarshal(in);
    }

    private static DependenciesGraph createGraphFromDescriptor(GPUPDescriptor gDesc) throws
            ExistingItemException,
            DependencyOnNonexistentTargetException,
            ImmediateCircularDependencyException,
            NullOrEmptyStringException,
            NonexistentTargetException,
            SerialSetNameRepetitionException {
        Map<String, GPUPTarget> name2GPUP = addTargetsToMap(gDesc);
        Collection<SerialSetDTO> serialSets = getSerialSets(gDesc);

        checkValidSerialSets(serialSets, name2GPUP);
        checkDependenciesAreExistingTargets(name2GPUP);
        checkCircularDependencies(name2GPUP);

        return createGraphFromValidMapAndSets(name2GPUP, gDesc, serialSets);
    }

    private static Map<String, GPUPTarget> addTargetsToMap(GPUPDescriptor gDesc) throws ExistingItemException {
//        HashMap<String, GPUPTarget> name2GPUP = new HashMap<>(gDesc.getGPUPTargets().getGPUPTarget().size());
        Map<String, GPUPTarget> name2GPUP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        for (GPUPTarget gTarget : gDesc.getGPUPTargets().getGPUPTarget()) {
            if (name2GPUP.containsKey(gTarget.getName())) {
                throw new ExistingItemException("Target \"" + gTarget.getName() + "\" appears twice in file!");
            } else {
                name2GPUP.put(gTarget.getName(), gTarget);
            }
        }

        return name2GPUP;
    }

    private static Collection<SerialSetDTO> getSerialSets(GPUPDescriptor gDesc) {
        Collection<SerialSetDTO> serialSets = new LinkedList<>();
        GPUPDescriptor.GPUPSerialSets gSets = gDesc.getGPUPSerialSets();

        if (gSets != null) {
            for (GPUPDescriptor.GPUPSerialSets.GPUPSerialSet gSet : gSets.getGPUPSerialSet()) {
                serialSets.add(getSerialSet(gSet));
            }
        }

        return serialSets;
    }

    private static SerialSetDTO getSerialSet(GPUPDescriptor.GPUPSerialSets.GPUPSerialSet gSet) {
        String name = gSet.getName();
        String targets = gSet.getTargets();
        List<String> nameList = Arrays.asList(targets.split("\\s*,\\s*")); // Removes white space too

        return new SerialSetDTO(name, nameList);
    }

    /**
     * Checks if all the targets mentioned in the serial sets
     * exist among the graph targets.
     */
    private static void checkValidSerialSets(Collection<SerialSetDTO> serialSets, Map<String, GPUPTarget> name2GPUP) throws NonexistentTargetException, SerialSetNameRepetitionException {
        Collection<String> serialSetNames = new LinkedList<>();

        for (SerialSetDTO serialSet : serialSets) {
            if (serialSetNames.contains(serialSet.getName().toLowerCase())) {
                throw new SerialSetNameRepetitionException("Two or more serial sets share the name '" + serialSet.getName() + "' (case insensitive)");
            }

            serialSetNames.add(serialSet.getName().toLowerCase());

            for (String targetName : serialSet.getTargetNames()) {
                if (!name2GPUP.containsKey(targetName)) {
                    throw new NonexistentTargetException("Target \"" + targetName + "\" is mentioned in serial sets but not found in graph.");
                }
            }
        }
    }

    /**
     * Checks if all the dependencies described with the targets, are of targets that are given in the file.
     * If A is dependent on B but B doesn't appear as a target elsewhere in the file, it's an invalid dependency and invalid file.
     */
    private static void checkDependenciesAreExistingTargets(Map<String, GPUPTarget> name2GPUP) throws DependencyOnNonexistentTargetException {
        for (GPUPTarget gTarget : name2GPUP.values()) {
            // Go over all its dependencies and check that they exist
            if (gTarget.getGPUPTargetDependencies() != null) {
                for (GPUPTargetDependencies.GPUGDependency gTargDep : gTarget.getGPUPTargetDependencies().getGPUGDependency()) {
                    if (!name2GPUP.containsKey(gTargDep.getValue())) {
                        String dependencyTypeStr = getDependencyTypeStr(gTargDep.getType());

                        throw new DependencyOnNonexistentTargetException("ERROR! " +
                                "Target \"" + gTarget.getName() + "\" is " +
                                dependencyTypeStr + " non-existent target \"" + gTargDep.getValue() + "\"");
                    }
                }
            }
        }
    }

    private static String getDependencyTypeStr(String type) {
        String str = "";

        if (type != null && !type.trim().isEmpty()) {
            switch (type) {
                case requiredForStr:
                    str = "required for";
                    break;
                case dependsOnStr:
                    str = "dependent on";
                    break;
            }
        }

        return str;
    }

    private static void checkCircularDependencies(Map<String, GPUPTarget> name2GPUP) throws ImmediateCircularDependencyException {
        for (GPUPTarget gTarget : name2GPUP.values()) {
            if (gTarget.getGPUPTargetDependencies() != null) {

                // For each neighbor of current target
                for (GPUPTargetDependencies.GPUGDependency neighborOfCurrTarg : gTarget.getGPUPTargetDependencies().getGPUGDependency()) {


                    // Get neighbors of neighbor
                    GPUPTarget neighborAsTarget = name2GPUP.get(neighborOfCurrTarg.getValue());

                    if (neighborAsTarget.getGPUPTargetDependencies() != null) {
                        // For each neighbor of the neighbor
                        for (GPUPTargetDependencies.GPUGDependency neighborOfNeighbor : neighborAsTarget.getGPUPTargetDependencies().getGPUGDependency()) {

                            // Check if it's current target
                            if (neighborOfNeighbor.getValue().equalsIgnoreCase(gTarget.getName())) {
                                if (immediateCyclicalDependency(neighborOfCurrTarg, neighborOfNeighbor)) {
                                    String errorMessage = "ERROR! Targets \"" + gTarget.getName() + "\" and \"" +
                                            neighborOfCurrTarg.getValue() + "\" have a circular dependency!";

                                    throw new ImmediateCircularDependencyException(errorMessage);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean immediateCyclicalDependency(GPUPTargetDependencies.GPUGDependency neighborOfCurrTarg, GPUPTargetDependencies.GPUGDependency neighborOfNeighbor) {
        return neighborOfCurrTarg.getType().equals(neighborOfNeighbor.getType());
    }

    /**
     * This method should be called at the end of several checks. It assumes checks have been made to ensure no repeating names,
     * No dependencies on nonexistent targets, and no immediate cyclical dependencies.
     *
     * @param name2GPUP a map with all targets inserted into it, with names as keys.
     */
    private static DependenciesGraph createGraphFromValidMapAndSets(
            Map<String, GPUPTarget> name2GPUP,
            GPUPDescriptor gDesc,
            Collection<SerialSetDTO> SerialSetDTOs) throws
            NullOrEmptyStringException,
            ExistingItemException,
            DependencyOnNonexistentTargetException,
            NonexistentTargetException {
        DependenciesGraph graph = new DependenciesGraph();
        graph.setName(gDesc.getGPUPConfiguration().getGPUPGraphName());

        Map<String, Target> name2Target = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // Create targets
        for (GPUPTarget gTarget : name2GPUP.values()) {
            Target newTarget = new Target(gTarget.getName());
            newTarget.setUserData(gTarget.getGPUPUserData());
            graph.insertTarget(newTarget);
            name2Target.put(newTarget.getName(), newTarget);
        }

        // Create serial sets
        Collection<SerialSet> serialSets = new LinkedList<>();
        for (SerialSetDTO SerialSetDTO : SerialSetDTOs) {
            SerialSet serialSet = new SerialSet(SerialSetDTO.getName());
            for (String targetName : SerialSetDTO.getTargetNames()) {
                Target target = name2Target.get(targetName);

                if (target == null) {
                    throw new NonexistentTargetException("Target \"" + targetName + "\" exists in serial set but not among the targets.");
                }

                serialSet.addTarget(target);
            }
            serialSets.add(serialSet);
        }

        graph.setSerialSets(serialSets);

        // Add dependencies
        for (GPUPTarget gTarget : name2GPUP.values()) {
            // Go over its dependencies
            if (gTarget.getGPUPTargetDependencies() != null) {
                for (GPUPTargetDependencies.GPUGDependency gTargDep : gTarget.getGPUPTargetDependencies().getGPUGDependency()) {

                    switch (gTargDep.getType()) {
                        case requiredForStr:
                            graph.addRequiredDependency(gTarget.getName(), gTargDep.getValue());
                            break;
                        case "dependsOn":
                            graph.addRequiredDependency(gTargDep.getValue(), gTarget.getName());
                            break;
                    }
                }
            }
        }

        return graph;
    }

    public static String openTaskExecutionFolder(TaskType type, String taskTypeDirName) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss");
        LocalDateTime now = LocalDateTime.now();
        String currTimeStr = dtf.format(now);
        // String dirName = "\\" + type.name() + " (" + taskTypeDirName + ") - " + currTimeStr; // Original format
        String dirName = "/" + currTimeStr + " -- " + type.name() + " (" + taskTypeDirName + ")"; // Time first format
        String fullPath = saveFilesLocation + dirName;
        FileManager.createDirectory(fullPath);

        return fullPath;
    }

    public static void createDirectory(String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }
}