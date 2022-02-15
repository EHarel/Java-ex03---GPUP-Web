package console.graph;

import console.menu.system.DoesAction;
import datastructure.Queue;
import graph.Graph;
import graph.TargetDTO;

import java.util.Scanner;

public class GetTopologicalSort implements DoesAction {
    private Scanner scanner = new Scanner(System.in);
    private final Graph graph;

    public GetTopologicalSort(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void DoAction() {
        getTopologicalSort();
    }

    private void getTopologicalSort() {
        Queue<TargetDTO> topologicalSortQueue = graph.getTopologicalSort(false);

        System.out.println("Printing sorted targets after topological sort:");
        if (topologicalSortQueue.size() != graph.size()) {
            System.out.println("(Cyclical graph! This is a partial order -- printing only targets not affected by a cycle)");
        }

        while (!topologicalSortQueue.isEmpty()) {
            TargetDTO currTarget = topologicalSortQueue.dequeue();
            System.out.print(currTarget.getName());
            if (!topologicalSortQueue.isEmpty()) {
                System.out.print( ", ");
            }
        }

        System.out.println();
    }
}