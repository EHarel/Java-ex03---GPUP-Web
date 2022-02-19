//package graph;
//
//import algorithm.DFS;
//import datastructure.Queue;
//import exception.NonexistentTargetException;
//import exception.UninitializedNullException;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//public interface Graph {
//    String getName();
//
//    boolean setName(String name);
//
//    /**
//     *
//     * @param targetName name of the target to search for.
//     * @return expanded data on a specific target. If no such target exists, or graph isn't loaded, returns null.
//     */
//    TargetDTO getSpecificDataOneTarget(String targetName);
//
//    Collection<TargetDTO> getAllRequiredForTargetsFromTarget(String targetName);
//
//    Collection<TargetDTO> getAllDependentOnTargetsFromTarget(String targetName);
//
//    Collection<TargetDTO> getAllTargetData();
//
//     /**
//     * @return general data about the graph and the targets. Returns null if no graph is loaded.
//     */
//    GraphDTO getGeneralDataAllTargets();
//
//    Collection<SerialSetDTO> getSerialSetDTO();
//
//    /**
//     * @param returnNullOnCycle if user wants the sorted targets even in case of cycle, send TRUE.
//     *                          If user instead wants to determine if there is a cycle,
//     *                          send FALSE, and return value will be NULL in case of CYCLE.
//     * @return A queue of sorted vertices. If there exists a cycle,
//     * return value is determined by parameter returnNullOnCycle.
//     */
//    Queue<TargetDTO> getTopologicalSort(boolean returnNullOnCycle);
//
//    Collection<TargetDTO> getDataAllTargetsByDependency(TargetDTO.Dependency dependency);
//
//    /**
//     * Finds all targets affected by given targetName parameter. Direction determines the direction of paths we find.
//     */
//    Collection<List<TargetDTO>> getTargetPaths(String targetName, DFS.EdgeDirection direction) throws NonexistentTargetException, UninitializedNullException;
//
//    /**
//     * @return true if graph has Target by parameter name.
//-     */
//    boolean contains(String targetName);
//
//    Collection<List<TargetDTO>> getCycleWithTarget(String targetName) throws NonexistentTargetException, UninitializedNullException;
//
//    /**
//     * Finds all paths between two targets.
//     * @return a two-sized array where index 0 are all the paths from target 1 to target 2, and index 1 are all the paths from target 2 to target 1.
//     * If one or both targets don't exist, returns null.
//     * If no path exists, returns an empty list.
//     */
//    ArrayList<Collection<List<TargetDTO>>> getPathsBetweenTargets(String target1Name, String target2Name) throws NonexistentTargetException, UninitializedNullException;
//
//    /**
//     *
//     * @return number of vertices in the graph.
//     */
//    int size();
//
//    /**
//     * Removes a target that is a Leaf or Independent.
//     * @param targetToRemove candidate to remove. Method will check if it is indeed Leaf or Independent.
//     * @return false if target has not been removed.
//     */
//    boolean removeLeafOrIndependent(Target targetToRemove);
//}