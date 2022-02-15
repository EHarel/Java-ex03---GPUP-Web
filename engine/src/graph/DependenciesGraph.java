package graph;

import algorithm.DFS;
import algorithm.TopologicalSort;
import datastructure.QueueLinkedList;
import exception.*;
import logic.EngineUtils;
import task.configuration.ChosenTarget;
import task.configuration.Configuration;
import task.consumer.ConsumerUpdateParticipation;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class DependenciesGraph implements Graph, Serializable {
    private static final long serialVersionUID = 5; // 11-Dec-2021, target participation count
    private final boolean caseSensitiveNames = false;

    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------ DATA MEMBERS -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    private String name;
    Map<String, Target> targets;
    Collection<SerialSet> serialSets;


    /* ---------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------- CONSTRUCTOR -------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public DependenciesGraph() {
        targets = new HashMap<>();
        serialSets = new LinkedList<>();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- SETTERS AND GETTERS ---------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean setName(String name) {
        boolean res = false;

        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
            res = true;
        }

        return res;
    }

    public Collection<SerialSet> getSerialSets() {
        return this.serialSets;
    }

    public void setSerialSets(Collection<SerialSet> serialSets) {
        this.serialSets = serialSets;
    }

    @Override
    public int size() {
        return targets.size();
    }

    private Target getTarget(String targetName) {
        if (targetName == null) {
            return null;
        }

        Target resTarget = null;

        for (Target target : targets.values()) {
            if (caseSensitiveNames) {
                if (target.getName().equals(targetName)) {
                    resTarget = target;
                }
            } else {
                if (target.getName().equalsIgnoreCase(targetName)) {
                    resTarget = target;
                }
            }

            if (resTarget != null) {
                break;
            }
        }

        return resTarget;
    }

    /**
     * @return the previous mapping associated with the key, or null if there was no mapping for the key.
     */
    private Target removeTarget(String targetName) {
        Target returnRes = null;

        if (targetName == null) {
            return returnRes;
        }

        if (caseSensitiveNames) {
            return targets.remove(targetName);
        }

        // Case-insensitive
        for (Target target : targets.values()) {
            if (target.getName().equalsIgnoreCase(targetName)) {
                returnRes = targets.remove(target.getName());
                break;
            }
        }

        return returnRes;
    }

    private Collection<String> getAllTargetNames() {
        Collection<String> targetNames = new LinkedList<>();

        for (Target target : targets.values()) {
            targetNames.add(target.getName());
        }

        return targetNames;
    }

    /**
     * @param targetName to search for.
     * @return target if found, null if no such target exists.
     */
    public Target get(String targetName) {
        return this.getTarget(targetName);
    }

    /**
     * Counts how many targets in the graph match a certain dependency state.
     *
     * @return a Map that maps the dependency value to the number of such dependencies in the graph.
     */
    public Map<TargetDTO.Dependency, Integer> sizeDependency() {
        Map<TargetDTO.Dependency, Integer> dependencyCount = new HashMap<>(TargetDTO.Dependency.values().length);

        // Init the Map
        for (TargetDTO.Dependency dependency : TargetDTO.Dependency.values()) {
            dependencyCount.put(dependency, 0);
        }

        for (Target target : targets.values()) {
            TargetDTO.Dependency dependency = target.getDependency();
            Integer currCount = dependencyCount.get(dependency);
            currCount++;
            dependencyCount.put(dependency, currCount);
        }

        return dependencyCount;
    }

    @Override
    public boolean contains(String targetName) {
        boolean contains = false;

        for (Target target : targets.values()) {
            if (caseSensitiveNames) {
                if (target.getName().equals(targetName)) {
                    contains = true;
                }
            } else {
                if (target.getName().equalsIgnoreCase(targetName)) {
                    contains = true;
                }
            }

            if (contains) {
                break;
            }
        }

        return contains;
    }

    /**
     * @param target        target to check (called "u" in description)
     * @param edgeDirection the type of neighbors requested (the neighbors "target" depends on, or is required for). Defaults to depends on.
     * @return collection of targets v such that there exists an edge from u to v, (u,v)
     */
    public Collection<Target> getAdjacent(Target target, DFS.EdgeDirection edgeDirection) {
        Collection<Target> adjacency;

        switch (edgeDirection) {
            case REQUIRED_FOR:
                adjacency = target.getTargetsThisIsRequiredFor();
                break;
            case DEPENDENT_ON:
            default:
                adjacency = target.getTargetsThisIsDependentOn();
                break;
        }

        return adjacency;
    }

    public Collection<Target> targets() {
        return targets.values();
    }

    public Set<String> getNamesOfTargetsThisDependsOn(Target target) {
        return addTargetNamesToSet(target.getTargetsThisIsDependentOn());
    }

    public Set<String> getNamesOfTargetsThisIsRequiredFor(Target target) {
        return addTargetNamesToSet(target.getTargetsThisIsRequiredFor());
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------- TARGET INSERTIONS ----------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    public void insertTarget(Target newTarget) throws ExistingItemException {
        if (newTarget == null) {
            return;
        }

        if (this.contains(newTarget.getName())) {
            throw new ExistingItemException(newTarget.getName() + " already exists in the Graph!");
        }

        targets.put(newTarget.getName(), newTarget);

        // newTarget.updateState(); // TODO: Delete?
    }

    /**
     * Adds a new "edge" in the graph, such that the second parameter is connected to the first.
     * If one of the targets does not exist in the graph yet, it will be added.
     *
     * @param targetRequiredForOther This will be the target that is required for the other.
     * @param otherTarget            This target will depend on the first target.
     */
    public void addRequiredDependency(Target targetRequiredForOther, Target otherTarget) {
        if (targetRequiredForOther == null || otherTarget == null) {
            return;
        }

        if (!targets.containsValue(targetRequiredForOther)) {
            targets.put(targetRequiredForOther.getName(), targetRequiredForOther);
        }

        if (!targets.containsValue(otherTarget)) {
            targets.put(otherTarget.getName(), otherTarget);
        }

        targetRequiredForOther.addTargetThatRequiresThis(otherTarget);

        // TODO?
        // Check if already exist in neighbor list
        // Update neighbor list
    }

    public void addRequiredDependency(String nameTargetRequiredForSecondParameter, String targetDependentOnFirstParameter) throws
            NullOrEmptyStringException,
            DependencyOnNonexistentTargetException {
        if (nullOrEmptyStr(nameTargetRequiredForSecondParameter) || nullOrEmptyStr(targetDependentOnFirstParameter)) {
            throw new NullOrEmptyStringException("");
        }

        if (!this.contains(nameTargetRequiredForSecondParameter)) {
            throw new DependencyOnNonexistentTargetException("Target \"" + nameTargetRequiredForSecondParameter + "\" does not exist in graph!");
        }

        if (!this.contains(targetDependentOnFirstParameter)) {
            throw new DependencyOnNonexistentTargetException("Target \"" + targetDependentOnFirstParameter + "\" does not exist in graph!");
        }

//        Target targetRequiredForOther = targets.get(nameTargetRequiredForSecondParameter); // Old version when it was case-sensitive
        Target targetRequiredForOther = this.getTarget(nameTargetRequiredForSecondParameter);

//        Target targetDependentOnOther = targets.get(targetDependentOnFirstParameter); // Old version when it was case-sensitive
        Target targetDependentOnOther = this.getTarget(targetDependentOnFirstParameter);

        addRequiredDependency(targetRequiredForOther, targetDependentOnOther);
        updateTargetStates();
    }

    private Set<String> addTargetNamesToSet(Collection<Target> targetsCollection) {
        if (targetsCollection == null || targetsCollection.size() == 0) {
            return null;
        }

        Set<String> resSet = new HashSet<>();

        for (Target target : targetsCollection) {
            resSet.add(target.getName());
        }

        return resSet;
    }

    @Override
    public boolean removeLeafOrIndependent(Target targetToRemove) {
        boolean removed = false;

        if (targetToRemove.isIndependent()) {
            removeTarget(targetToRemove.getName());
            removed = true;
        } else if (targetToRemove.isLeaf()) {
            for (Target target : targetToRemove.getTargetsThisIsRequiredFor()) {
                target.removeLeafThisDependsOn(targetToRemove);
            }

            removeTarget(targetToRemove.getName());
            removed = true;
        }

        return removed;
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ----------------------------------------- DATA RETRIEVAL ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */


    @Override
    public GeneralData getGeneralDataAllTargets() {
        Map<TargetDTO.Dependency, Integer> dependencyIntegerMap = sizeDependency();
        GeneralData generalData = new GeneralData();

        generalData.setCountAllTargets(targets.size());
        generalData.setCountLeaves(dependencyIntegerMap.get(TargetDTO.Dependency.LEAF));
        generalData.setCountMiddles(dependencyIntegerMap.get(TargetDTO.Dependency.MIDDLE));
        generalData.setCountRoots(dependencyIntegerMap.get(TargetDTO.Dependency.ROOT));
        generalData.setCountIndependents(dependencyIntegerMap.get(TargetDTO.Dependency.INDEPENDENT));
        generalData.setTargetNames(getAllTargetNames());

        return generalData;
    }

    @Override
    public Collection<SerialSetDTO> getSerialSetDTO() {
        Collection<SerialSetDTO> serialSetDTOS = new LinkedList<>();

        for (SerialSet serialSet : serialSets) {
            serialSetDTOS.add(serialSet.toData());
        }

        return serialSetDTOS;
    }

    public Collection<TargetDTO> getDataAllTargetsByDependency(TargetDTO.Dependency dependency) {
        List<TargetDTO> targetDTOList = new ArrayList<>();

        for (Target target : targets.values()) {
            if (target.getDependency() == dependency) {
                targetDTOList.add(target.toData());
            }
        }


        return targetDTOList;
    }

    @Override
    public TargetDTO getSpecificDataOneTarget(String targetName) {
        Target target = getTarget(targetName);

        if (target == null) {
            return null;
        }

        return target.toData();
    }

    @Override
    public Collection<TargetDTO> getAllRequiredForTargetsFromTarget(String targetName) {
        Collection<TargetDTO> dependencies = new LinkedList<>();
        Target target = getTarget(targetName);

        if (target != null) {
            Collection<Target> collection = target.getTargetsThisIsRequiredFor();
            if (collection != null) {
                for (Target targetDependency : collection) {
                    dependencies.add(targetDependency.toData());
                }
            }
        }

        return dependencies;
    }

    /**
     * This method returns all the immediate dependencies of the given target. The direction refers to the given target.
     * Meaning, either all the targets given target is immediately dependent on,
     * or all the targets given target is immediately required for.
     *
     * @param name
     * @param direction
     * @return
     */
    public Collection<TargetDTO> getAllDependencies_Immediate(String name, DFS.EdgeDirection direction) {
        Collection<TargetDTO> dependencies;
        if (direction == DFS.EdgeDirection.DEPENDENT_ON) {
            dependencies = getAllDependentOnTargetsFromTarget(name);
        } else {
            dependencies = getAllRequiredForTargetsFromTarget(name);
        }

        return dependencies;
    }

    /**
     * This method returns all the total dependencies of the given target. The direction refers to the given target.
     * Meaning, either all the targets given target is in total dependent on,
     * or all the targets given target is in total required for.
     * If target doesn't exist, returns null.
     *
     * @param targetName
     * @param direction
     * @return null if no target exists by given name.
     */
    public Collection<TargetDTO> getAllDependencies_Transitive(String targetName, DFS.EdgeDirection direction) {
        Collection<TargetDTO> allDependencies = null;

        try {
            Collection<List<TargetDTO>> allPaths = getTargetPaths(targetName, direction);
            if (allPaths != null) {
                allDependencies = EngineUtils.GetTargetDTOsFromPaths_Exclude(allPaths, targetName);
            }
        } catch (Exception ignore) {
        }

        return allDependencies;
    }

    @Override
    public Collection<TargetDTO> getAllDependentOnTargetsFromTarget(String targetName) {
        Collection<TargetDTO> dependencies = new LinkedList<>();
        Target target = getTarget(targetName);

        if (target != null) {
            Collection<Target> collection = target.getTargetsThisIsDependentOn();
            if (collection != null) {
                for (Target targetDependency : collection) {
                    dependencies.add(targetDependency.toData());
                }
            }
        }

        return dependencies;
    }

    @Override
    /* TODO
        I have two methods here that are essentially the same.
        They differ only in the return value.
        Seems like something I should change.
     */
    public Collection<TargetDTO> getAllTargetData() {
        List<TargetDTO> targetData = new ArrayList<>();

        for (Target target : targets.values()) {
            targetData.add(target.toData());
        }

        return targetData;
    }

    public Map<String, TargetDTO> getAllTargetDataMap() {
        Map<String, TargetDTO> targetsData = new HashMap<>(targets.size());

        for (Target target : targets.values()) {
            targetsData.put(target.getName(), target.toData());
        }

        return targetsData;
    }

    /**
     * @return number of edges leading into node given in parameter.
     */
    public int getIndegree(String nodeName) {
        Target target = getTarget(nodeName);

        return target.getTargetsThisIsDependentOn().size();
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------- PATHS ------------------------------------------------ */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    @Override
    public Collection<List<TargetDTO>> getTargetPaths(String targetName, DFS.EdgeDirection direction) throws NonexistentTargetException, UninitializedNullException {
        return getPathInTargetDataFormat(targetName, null, direction, null);
    }

    @Override
    public Collection<List<TargetDTO>> getCycleWithTarget(String targetName) throws NonexistentTargetException, UninitializedNullException {
        ArrayList<Collection<List<TargetDTO>>> paths = getPathsBetweenTargets(targetName, targetName);

        Collection<List<TargetDTO>> pathToReturn = null;

        if (paths != null) {
            pathToReturn = paths.get(0);
        }

        return pathToReturn;
    }

    /**
     * Find all paths between two targets.
     *
     * @return a two-sized array where index 0 are all the paths from target 1 to target 2, and index 1 are all the paths from target 2 to target 1.
     * If one or both targets don't exist, returns null.
     * If no path exists, returns an empty list.
     */
    @Override
    public ArrayList<Collection<List<TargetDTO>>> getPathsBetweenTargets(String target1Name, String target2Name) throws NonexistentTargetException, UninitializedNullException {
        Collection<List<TargetDTO>> paths1to2Data = getPathInTargetDataFormat(target1Name, target2Name, DFS.EdgeDirection.DEPENDENT_ON, null);
        Collection<List<TargetDTO>> paths2to1Data = getPathInTargetDataFormat(target2Name, target1Name, DFS.EdgeDirection.DEPENDENT_ON, null);

        ArrayList<Collection<List<TargetDTO>>> pathsPair = new ArrayList<>(2);

        pathsPair.add(paths1to2Data);
        pathsPair.add(paths2to1Data);

        return pathsPair;
    }

    private Collection<List<TargetDTO>> getPathInTargetDataFormat(
            String sourceTargetName,
            String destinationTargetName,
            DFS.EdgeDirection direction,
            Collection<Consumer<Target>> consumersWhenArriving)
            throws NonexistentTargetException, UninitializedNullException {
        Collection<List<Target>> paths;

        Target sourceTarget = get(sourceTargetName);
        Target destinationTarget = get(destinationTargetName);

        paths = new DFS(this, sourceTarget, destinationTarget, direction, consumersWhenArriving).run();

        return EngineUtils.pathToDTO(paths);
    }


    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* --------------------------------------- TOPOLOGICAL SORT ------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * @param returnNullOnCycle if user wants the sorted targets even in case of cycle, send FALSE.
     *                          If user instead wants to determine if there is a cycle,
     *                          send TRUE, and return value will be NULL in case of CYCLE.
     * @return A queue of sorted vertices. If there exists a cycle,
     * return value is determined by parameter returnNullOnCycle.
     */
    @Override
    public datastructure.Queue<TargetDTO> getTopologicalSort(boolean returnNullOnCycle) {
        datastructure.Queue<Target> targetQ = new TopologicalSort(this).getSortedTargets(returnNullOnCycle);
        datastructure.Queue<TargetDTO> targetDataQ = null;

        if (targetQ != null) {
            targetDataQ = new QueueLinkedList<>();

            while (!targetQ.isEmpty()) {
                Target currTarget = targetQ.dequeue();
                targetDataQ.enqueue(currTarget.toData());
            }
        }

        return targetDataQ;
    }





    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* -------------------------------------------- UTILITY ----------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */
    /* ---------------------------------------------------------------------------------------------------- */

    /**
     * This method removes a target from the graph if the following conditions apply:
     * It finished the task properly (success w/ or w/o warnings), and so did
     * all of its dependencies. If a target can be removed, it is also removed from the serial set.
     *
     * @return true if target was removed, false otherwise.
     */
    public synchronized boolean tryRemoveSuccessful(Target target) {
        boolean removed = false;

        if (target.finishedWell()) {
            boolean allDependenciesFinished = true;

            for (Target dependency : target.getTargetsThisIsDependentOn()) {
                if (!dependency.finishedWell()) {
                    allDependenciesFinished = false;
                    break;
                }
            }

            if (allDependenciesFinished) {
                removeTarget(target.getName());
                removed = true;

                // Remove from serial set
                for (SerialSet serialSet : target.getSerialSets()) {
                    serialSet.removeTarget(target.getName());
                }
            }
        }

        return removed;
    }

    /**
     * This method resets the states of all the targets, setting the execution number to 0 and the active configuration
     * to null.
     */
    public void resetState() {
        resetState(0, null);
    }

    public void resetState(int executionNumber, Configuration activeConfiguration) {
        for (Target target : targets.values()) {
            target.resetTaskStatus(executionNumber, activeConfiguration);
        }
    }

    private void updateTargetStates() {
        targets.values().forEach(Target::updateState);
    }

    private boolean nullOrEmptyStr(String str) {
        return (str == null || str.trim().isEmpty());
    }

    public DependenciesGraph duplicate() {
        DependenciesGraph duplicateGraph = new DependenciesGraph();
        duplicateGraph.setName(this.name + " (Duplicate)");

        duplicationAddTargetsWithoutDependencies(duplicateGraph);
        duplicationAddDependencies(duplicateGraph);
        duplicateSerialSets(duplicateGraph);

        return duplicateGraph;
    }

    /**
     * This method creates a sub-graph based on the given targets.
     * It keeps only the immediate neighbors of the given targets (if neighbors are included).
     * For example, if original graph was A->B->C, and chosen targets are [A, C], new graph will be: A, C.
     * If chosen targets are [A, B], new graph will be A->B.
     * If chosen targets are all the targets in the graph, it simply duplicates itself regularly.
     */
    public DependenciesGraph DuplicateChosenOnly(Collection<String> chosenTargets) {
        DependenciesGraph subGraph = new DependenciesGraph();

        if (areChosenTargetsAllGraphTargets(chosenTargets)) {
            subGraph = this.duplicate();
        } else {
            subGraph.setName(this.name + " (Sub-Graph)");

            duplicationAddTargetsWithoutDependenciesChosenOnly(subGraph, chosenTargets);
            duplicationAddDependenciesChosenOnly(subGraph, chosenTargets);
            duplicateSerialSetsChosenOnly(subGraph, chosenTargets);
        }

        return subGraph;
    }

    /**
     * This method checks if chosen targets for partial duplication meant to create a sub-graph
     * are all the targets in the graph.
     *
     * @param chosenTargets
     * @return
     */
    private boolean areChosenTargetsAllGraphTargets(Collection<String> chosenTargets) {
        boolean areAllGraphTargets = true;

        if (chosenTargets != null) {
            for (Target graphTarget : targets()) {

                // For all targets, check if they appear in the chosen targets collection
                if (!EngineUtils.isNameInCollection(chosenTargets, graphTarget.getName())) {

                    // Target doesn't appear in collection
                    areAllGraphTargets = false;
                    break;
                }
            }
        }

        return areAllGraphTargets;
    }

    private void duplicationAddTargetsWithoutDependenciesChosenOnly(DependenciesGraph partialDuplicate, Collection<String> chosenTargets) {
        // First loop to create all the targets and add them
        for (Target target : targets.values()) {
            if (chosenTargets.contains(target.getName())) {
                Target newTarget = target.cloneWithoutDependenciesAndSerialSet();

            /*
            Should not cause exception,
            because graph is already created.
             */
                try {
                    partialDuplicate.insertTarget(newTarget);
                } catch (ExistingItemException ignored) {
                }
            }
        }
    }

    private void duplicationAddTargetsWithoutDependencies(DependenciesGraph duplicateGraph) {
        // First loop to create all the targets and add them
        for (Target target : targets.values()) {
            Target newTarget = target.cloneWithoutDependenciesAndSerialSet();

            /*
            Should not cause exception,
            because graph is already created.
             */
            try {
                duplicateGraph.insertTarget(newTarget);
            } catch (ExistingItemException ignored) {

            }
        }
    }

    private void duplicationAddDependencies(DependenciesGraph duplicateGraph) {
        // Second loop to add all the neighbors
        for (Target target : targets.values()) {
            Target duplicatedTarget = duplicateGraph.get(target.getName());

            // Add required for
            for (Target targetThatDependsOnThis : target.getTargetsThisIsRequiredFor()) {
                Target duplicatedDependent = duplicateGraph.get(targetThatDependsOnThis.getName());
                duplicateGraph.addRequiredDependency(duplicatedTarget, duplicatedDependent);
            }

            // Add dependent on
            for (Target targetThisDependsOn : target.getTargetsThisIsDependentOn()) {
                Target duplicatedDependsOn = duplicateGraph.get(targetThisDependsOn.getName());
                duplicateGraph.addRequiredDependency(duplicatedDependsOn, duplicatedTarget);
            }
        }
    }

    private void duplicationAddDependenciesChosenOnly(DependenciesGraph subGraph, Collection<String> chosenTargets) {
    /*
    For each target (currTar) in the sub-graph,
        For each chosen (choTar) target != currTar,
            Check if choTar and currTar are neighbors in the original graph
                connect them if so
     */
        for (Target subGraphTarget : subGraph.targets()) {
            for (String chosenName : chosenTargets) {
                if (!chosenName.equals(subGraphTarget.getName())) {
                    Target target1OriginalGraph = this.getTarget(subGraphTarget.getName());

                    Target target1InDuplicate = subGraph.getTarget(target1OriginalGraph.getName());
                    Target target2InDuplicate = subGraph.getTarget(chosenName);

                    if (target1OriginalGraph != null) {
                        if (target1OriginalGraph.isDirectlyDependentOn(chosenName)) {
                            target1InDuplicate.addTargetThisDependsOn(target2InDuplicate);
                        }

                        if (target1OriginalGraph.isDirectlyRequiredFor(chosenName)) {
                            target1InDuplicate.addTargetThatRequiresThis(target2InDuplicate);
                        }
                    }
                }
            }
        }
    }

    private void duplicateSerialSets(DependenciesGraph duplicateGraph) {
        Collection<SerialSet> duplicateSets = new LinkedList<>();

        for (SerialSet serialSet : this.getSerialSets()) {
            SerialSet cloneSet = serialSet.cloneWithoutTargets();

            // Reconnect all the targets
            for (Target target : serialSet.getTargets()) {
                Target duplicateTarget = duplicateGraph.get(target.getName());
                cloneSet.addTarget(duplicateTarget);
            }

            if (cloneSet.getTargets().size() > 0) {
                duplicateSets.add(cloneSet);
            }
        }

        duplicateGraph.setSerialSets(duplicateSets);
    }

    private void duplicateSerialSetsChosenOnly(DependenciesGraph subGraph, Collection<String> chosenTargets) {
        Collection<SerialSet> duplicateSets = new LinkedList<>();

        for (SerialSet originalSerialSet : this.getSerialSets()) {
            SerialSet cloneSet = originalSerialSet.cloneWithoutTargets();

            // Reconnect all the targets
            for (Target targetInOriginalSet : originalSerialSet.getTargets()) {
                Target duplicateTarget = subGraph.get(targetInOriginalSet.getName());

                cloneSet.addTarget(duplicateTarget);
            }

            if (cloneSet.getTargets().size() > 0) {
                duplicateSets.add(cloneSet);
            }
        }

        subGraph.setSerialSets(duplicateSets);
    }

    public void setAllTargetsParticipating() {
        for (Target target : targets.values()) {
            target.setParticipatesInExecution(true);
        }
    }

    public void setParticipatingTargets(Collection<ChosenTarget> participatingTargets) {
        if (participatingTargets == null) {
            return;
        }

        for (ChosenTarget chosenTarget : participatingTargets) {
            boolean dependsOnDirection;
            boolean requiredForDirection;
            Target target = this.get(chosenTarget.getName());

            Collection<Consumer<Target>> collection = new LinkedList<>();
            collection.add(new ConsumerUpdateParticipation(true));

            if (target != null) {
                if (chosenTarget.getRelationshipDirection() == ChosenTarget.RelationshipDirection.BOTH) {
                    dependsOnDirection = true;
                    requiredForDirection = true;
                } else if (chosenTarget.getRelationshipDirection() == ChosenTarget.RelationshipDirection.DEPENDENT_ON) {
                    dependsOnDirection = true;
                    requiredForDirection = false;
                } else {
                    dependsOnDirection = false;
                    requiredForDirection = true;
                }

                try {
                    if (dependsOnDirection) {
                        new DFS(this, target, null, DFS.EdgeDirection.DEPENDENT_ON, collection).run();
                    }

                    if (requiredForDirection) {
                        new DFS(this, target, null, DFS.EdgeDirection.REQUIRED_FOR, collection).run();
                    }
                } catch (NonexistentTargetException e) {
                    e.printStackTrace();
                } catch (UninitializedNullException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    /**
//     * This method returns the total number of targets affected by the given target name, in a certain direction.
//     * More simply, it returns the total number of targets either dependent on or required for given target name.
//     * @param targetName Root target by which to check dependencies.
//     * @param dependencyDirection Dependent On, for all the targets the given target is dependent on.
//     *                            Required For, for all the targets the given target is required for.
//     * @return Number of targets in given direction, or null if target doesn't exist.
//     */
//    public Optional<Integer> getTotalNumberDependencies(String targetName, DFS.EdgeDirection dependencyDirection) {
//        Optional<Integer> totalDependencies;
//
//
//        return totalDependencies;
//    }
}