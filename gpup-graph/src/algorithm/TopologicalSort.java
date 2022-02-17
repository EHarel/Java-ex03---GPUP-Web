package algorithm;

import datastructure.QueueLinkedList;
import graph.DependenciesGraph;
import graph.Target;
import datastructure.Queue;

import java.util.*;

public class TopologicalSort {
    Queue<Target> sortedTargets;
    datastructure.Queue<Target> queue;
    DependenciesGraph graph;

    public TopologicalSort(DependenciesGraph graph) {
        this.graph = graph;
        queue = new QueueLinkedList<>();
        sortedTargets = new QueueLinkedList<>();
    }

    /**
     *
     * @param returnNullOnCycle if user wants the sorted targets even in case of cycle, send FALSE.
     *                          If user instead wants to determine if there is a cycle,
     *                          send TRUE, and return value will be NULL in case of CYCLE.
     * @return A queue of sorted vertices. If there exists a cycle,
     * return value is determined by parameter returnNullOnCycle.
     */
    public datastructure.Queue<Target> getSortedTargets(boolean returnNullOnCycle) {
        queue = getAllStartingTargets();
        Map<String, Integer> name2Indegree = getIndegrees();
        int countOfVisitedVertices = 0;

        while (!queue.isEmpty()) {
            Target currTarget = queue.dequeue();
            sortedTargets.enqueue(currTarget);

            for (Target target : currTarget.getTargetsThisIsRequiredFor()) {
                int currIndegree = name2Indegree.get(target.getName());
                int decrementedIndegree = currIndegree - 1;
                name2Indegree.put(target.getName(), decrementedIndegree);

                if (decrementedIndegree == 0) {
                    queue.enqueue(target);
                }
            }

            countOfVisitedVertices++;
        }

        if (countOfVisitedVertices != graph.targets().size()) { // Exists a cycle!
            if (returnNullOnCycle) {
                return null;
            }
        }

        return sortedTargets;
    }

    /**
     * Maps node name to its indegree.
     * @return a Map of names-to-indegree.
     */
    private Map<String,Integer> getIndegrees() {
        Map<String,Integer> name2Indegree = new HashMap<>(graph.size());

        for (Target target : graph.targets()) {
            int indegree = graph.getIndegree(target.getName());
            name2Indegree.put(target.getName(), indegree);
        }

        return name2Indegree;
    }

    private datastructure.Queue<Target> getAllStartingTargets() {
        datastructure.Queue<Target> startingTargets = new QueueLinkedList<>();

        for (Target target : graph.targets()) {
            if (target.getNamesOfTargetsThisIsDependentOn().size() == 0) {
                startingTargets.enqueue(target);
            }
        }

        return startingTargets;
    }
}