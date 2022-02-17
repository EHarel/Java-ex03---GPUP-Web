package graph;

import users.User;

import java.util.HashSet;
import java.util.Set;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class GraphManager {

    private final Set<DependenciesGraph> graphs;

    public GraphManager() {
        graphs = new HashSet<>();
    }

    public synchronized void addGraph(DependenciesGraph graph) {
        String originalGraphName = graph.getName().trim();          // GraphName
        String currGraphName = originalGraphName;                   // GraphName
        int i = 1;

        while (isExistingGraphName(currGraphName)) {
            currGraphName = originalGraphName + " (" + i++ + ")";   // GraphName (1)
        }

        graph.setName(currGraphName);
        graphs.add(graph);
    }

    public synchronized Set<DependenciesGraph> getGraphs() {
//        return Collections.unmodifiableSet(users); // Aviad code
        return graphs;
    }

    /**
     * Determines if a graph exists by the given name. It trims the names to avoid pointless spaces.
     * @param graphName
     */
    public boolean isExistingGraphName(String graphName) {
        boolean is_exists = false;

        graphName = graphName.trim();

        for (DependenciesGraph graph :
                graphs) {
            if (graph.getName().equals(graphName)) {
                is_exists = true;
                break;
            }
        }

        return is_exists;
    }
}