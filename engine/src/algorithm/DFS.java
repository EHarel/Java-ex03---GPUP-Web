package algorithm;

import exception.NonexistentTargetException;
import exception.UninitializedNullException;
import graph.DependenciesGraph;
import graph.Target;

import java.util.*;
import java.util.function.Consumer;

public class DFS {
    public enum EdgeDirection {
        REQUIRED_FOR, DEPENDENT_ON
    }

    private DependenciesGraph graph;
    private Target sourceTarget;
    private Optional<Target> destinationTarget; // TODO: change to target name
    private String sourceName;
    private String destinationName;
    private EdgeDirection edgeDirection;
    private Collection<Consumer<Target>> consumersWhenArrivingAtTarget;

    /**
     *
     * @param edgeDirection from the source target you can choose to explore all paths that the source is dependent on,
     *                      or all paths the source is required for.
     * @param consumersWhenArrivingAtTarget these are consumers that will be activated upon arriving at each target, before any checks.
     *                                      Meaning, they're called as the first action in the method.
     * @throws UninitializedNullException Thrown when source target name is null. Must have a source target.
     */
    public DFS(
            DependenciesGraph graph,
            Target sourceTarget,
            Target destinationTarget,
            EdgeDirection edgeDirection,
            Collection<Consumer<Target>> consumersWhenArrivingAtTarget)
            throws NonexistentTargetException, UninitializedNullException {
        this.graph = graph;
        this.sourceTarget = sourceTarget;
        this.sourceName = sourceTarget.getName();
        this.destinationTarget = Optional.ofNullable(destinationTarget);
        this.destinationTarget.ifPresent(target -> this.destinationName = target.getName());
        this.edgeDirection = edgeDirection;
        this.consumersWhenArrivingAtTarget = consumersWhenArrivingAtTarget;

        if (sourceName == null) {
            throw new UninitializedNullException("ERROR! No source target given.");
        }

        if (!this.graph.contains(sourceName)) {
            throw new NonexistentTargetException("ERROR! Target \"" + sourceName + "\" given as source does not exist.");
        }

        if (destinationName != null && !this.graph.contains(destinationName)) {
            throw new NonexistentTargetException("ERROR! Target \"" + destinationName + "\" given as a destination does not exist.");
        }
    }

    /**
     * @return collection of paths from source target, either until destination or not, if given.
     */
    public Collection<List<Target>> run() {
        Map<Target, Boolean> visitedTargets = initVisited();
        Collection<List<Target>> allPaths = new ArrayList<>();
        List<Target> firstPath = new ArrayList<>();

        firstPath.add(sourceTarget);
        allPaths = getAllPathsRec(sourceTarget, visitedTargets, allPaths, firstPath);

        allPaths = removeBadPaths(allPaths);

        allPaths = reorderPathsBasedOnDirection(allPaths, edgeDirection);

        return allPaths;
    }

    Collection<List<Target>> getAllPathsRec(Target currTarget,
                                                  Map<Target, Boolean> visitedTargets,
                                                  Collection<List<Target>> allPaths,
                                                  List<Target> currPath) {
        runStartConsumers(currTarget);
//        if (currTarget.equals(destinationTarget) || currTarget.getDependency() == TargetData.Dependency.ROOT) {
//        if (currTarget.getName().equalsIgnoreCase(this.destinationName)) {
        if (stopCase(currTarget)) {
            if(currPath.size() > 1) {
                // Found path! Add it to list and end recursion.
                // If size was <=1 it would indicate we're in the first iteration, perhaps when finding a cycle.
                allPaths.add(currPath);
                return allPaths;
            }
        }

        // Mark the current vertex
        visitedTargets.put(currTarget, true);

        /* Recur for all vertices adjacent current vertex */
        boolean firstAdjacent = true;
        List<Target> pathUntilNowCopy = new ArrayList<>(currPath);

        for (Target target : graph.getAdjacent(currTarget, edgeDirection)) {
            try {
                if (!visitedTargets.get(target)) {
                    List<Target> pathToSend;
                    if (firstAdjacent) {
                        pathToSend = currPath;
                        firstAdjacent = false;
                    } else {
                        // Create new divergent path, identical to the current up until this node
                        pathToSend = new ArrayList<>(pathUntilNowCopy);
                    }
                    pathToSend.add(target);

                    // Store target in current path
                    allPaths = getAllPathsRec(target, visitedTargets, allPaths, pathToSend);
                } else if (target.getName().equalsIgnoreCase(destinationName)) { // TODO: this is added to deal with cycles. Didn't bug-test thoroughly yet. Might be bad.
                    List<Target> pathToWorkOn;
                    if (firstAdjacent) {
                        pathToWorkOn = currPath;
                        firstAdjacent = false;
                    } else {
                        pathToWorkOn = new ArrayList<>(pathUntilNowCopy);
                    }

                    pathToWorkOn.add(target);
                    allPaths.add(pathToWorkOn);
                }
            } catch (Exception ignore) {}
        }

        visitedTargets.put(currTarget, false);
        return allPaths;
    }

    private boolean stopCase(Target currTarget) {
        boolean stopCase = false;

        if (destinationName != null) { // Have a specific destination
            stopCase = currTarget.getName().equalsIgnoreCase(destinationName);
        } else { // No specific destination - exploring all paths
            switch (edgeDirection) {
                case REQUIRED_FOR:
                    stopCase = currTarget.isRoot();
                    break;
                case DEPENDENT_ON:
                    stopCase = currTarget.isLeaf();
                    break;
            }
        }

        return stopCase;
    }

    private void runStartConsumers(Target target) {
        if (consumersWhenArrivingAtTarget != null) {
            consumersWhenArrivingAtTarget.forEach(consumer -> consumer.accept(target));
        }
    }

    private Map<Target, Boolean> initVisited() {
        Map<Target,Boolean> name2Visited = new HashMap<>(graph.targets().size());

        for (Target target : graph.targets()) {
            name2Visited.put(target, false);
        }

        return name2Visited;
    }

    private Collection<List<Target>> removeBadPaths(Collection<List<Target>> allPaths) {
        for (Collection<Target> path : allPaths) {
            if (path.size() < 2) {
                allPaths.remove(path);
                System.out.println("At stop case!"); // TODO: delete once done testing
            }
        }

        return allPaths;
    }

    /**
     * If the edge direction is REQUIRED_FOR, then the source target actually becomes the destination.
     * For example, suppose we have target M as our source, and we want all the targets it is REQUIRED_FOR. We may
     * receive a paths such as: M, I, C. However, the actual edge direction means the path should be C->I->M.
     * This method reorders based on that.
     */
    private Collection<List<Target>> reorderPathsBasedOnDirection(Collection<List<Target>> allPaths, EdgeDirection edgeDirection) {
        Collection<List<Target>> reorderedPaths = allPaths;

        if (edgeDirection == EdgeDirection.REQUIRED_FOR) {
            reorderedPaths = new ArrayList<>(allPaths.size());

            for (List<Target> path : allPaths) {
                List<Target> reorderedPath = reorderPath(path);
                reorderedPaths.add(reorderedPath);
            }
        }

        return reorderedPaths;
    }

    /**
     * For input path [A, B, C, D], this method returns a new path [D, C, B, A]
     */
    private List<Target> reorderPath(List<Target> path) {
        List<Target> reorderedPath = null;

        if (path != null) {
            reorderedPath = new LinkedList<>();

            for (Target target : path) {
                reorderedPath.add(0, target);
            }
        }

        return reorderedPath;
    }
}