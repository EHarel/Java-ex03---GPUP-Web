package users;

import java.util.*;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final List<User> users;

    public UserManager() {
        users = new LinkedList<>();
    }

    public synchronized boolean addUser(UserDTO userDTO) {
        boolean is_added = false;

        if (!isUserExists(userDTO.getName())) {
            User newUser = new User(userDTO.getName().trim(), userDTO.getType());

            users.add(newUser);
            is_added = true;
        }

        return is_added;
    }

    public synchronized Collection<User> getUsers() {
        return Collections.unmodifiableCollection(users); // Aviad code
//        return users;
    }

    public boolean isUserExists(String username) {
        boolean is_exists = false;

        username = username.trim();

        for (User user :
                users) {
            if (user.getName().equals(username)) {
                is_exists = true;
                break;
            }
        }

        return is_exists;
    }

    public synchronized User getUser(String usernameFromSession) {
        User requestedUser = null;

        for (User user : users) {
            if (user.getName().equals(usernameFromSession)) {
                requestedUser = user;
                break;
            }
        }

        return requestedUser;
    }

    /**
     * This method updates a user's execution standing, by increasing the number of targets it processed from it to 1,
     * and adds the payment.
     */
    public synchronized void updateUserProcessedTarget(String username, String executionName, int paymentToAdd) {
        for (User user : users) {
            if (user.getName().equals(username)) {
                user.incrementExecutionProcessedTargets(executionName);
                user.addPaymentToExecution(executionName, paymentToAdd);

                break;
            }
        }
    }
}