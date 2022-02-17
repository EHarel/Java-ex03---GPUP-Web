package components.app;

import graph.DependenciesGraph;
import graph.GraphDTO;

import java.util.*;

public class GraphManager {
    Set<DependenciesGraph> graphs;

    public GraphManager() {
        graphs = new HashSet<>();
    }

    /**
     * Returns the requested graph (not a copy).
     */
    public synchronized DependenciesGraph getGraph(String graphName) {
        DependenciesGraph requestedGraph = null;

        for (DependenciesGraph graph :
                graphs) {
            if (graph.getName().equals(graphName)) {
                requestedGraph = graph;
                break;
            }
        }

        return requestedGraph;
    }

    public synchronized void addGraph(DependenciesGraph graph) {}


    public synchronized void addGraphs(GraphDTO[] graphDTOs) {
        if (graphs == null) {
            graphs = new HashSet<>();
        }

        List<GraphDTO> graphDTOsList = Arrays.asList(graphDTOs);

        graphDTOsList.forEach(graphGeneralData -> {
            boolean isExisting = false;

            for (DependenciesGraph graph : graphs) {
                if (graph.getName().trim().equals(graphGeneralData.getGraphName().trim())) {
                    isExisting = true;
                    break;
                }
            }

            if (!isExisting) {
                graphs.add(new DependenciesGraph(graphGeneralData));
            }
        });
    }
}
