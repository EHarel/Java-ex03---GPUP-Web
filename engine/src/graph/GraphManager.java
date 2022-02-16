package graph;

import users.User;
import users.UserDTO;

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

    public synchronized boolean addGraph(DependenciesGraph graph) {
        boolean is_added = false;

//        if (!isUserExists(userDTO.getName())) {
//            User newUser = new User(userDTO.getName().trim(), userDTO.getType());
//
//            users.add(newUser);
//            is_added = true;
//        }

        graphs.add(graph);
        is_added = true;

        return is_added;
    }

    public synchronized Set<DependenciesGraph> getGraphs() {
//        return Collections.unmodifiableSet(users); // Aviad code
        return graphs;
    }

//    public boolean isUserExists(String username) {
//        boolean is_exists = false;
//
//        username = username.trim();
//
//        for (User user :
//                users) {
//            if (user.getName().equals(username)) {
//                is_exists = true;
//                break;
//            }
//        }
//
//        return is_exists;
//    }
}