package console.graph;

import console.menu.system.DoesAction;
import datastructure.Queue;
import graph.Graph;
import graph.TargetDTO;

public class DetermineCycle implements DoesAction {
        private final Graph graph;

        public DetermineCycle(Graph graph) {
            this.graph = graph;
        }

        @Override
        public void DoAction() {
            determineCycle();
        }

        private void determineCycle() {
            Queue<TargetDTO> topologicalSortQueue = graph.getTopologicalSort(true);

            if (topologicalSortQueue == null) {
                System.out.println("There's a cycle in the graph! Oh no! D:");
            } else {
                System.out.println("Hallelujah! No cycle in the graph! :D");
            }
        }
    }
