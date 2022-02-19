package task;

import java.util.ArrayList;
import java.util.Collection;

public class ExecutionManager {
    Collection<Execution> executions;

    public ExecutionManager() {
        executions = new ArrayList<>();
    }

    public boolean isExistingExecutionName(String executionName) {
        boolean isExisting = false;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName.trim())) {
                isExisting = true;
                break;
            }
        }

        return isExisting;
    }

    public synchronized boolean addExecution(Execution execution) {
        boolean isAdded = false;

        if (!isExistingExecutionName(execution.getExecutionName())) {
            executions.add(execution);
            isAdded = true;
        }

        return isAdded;
    }

    public synchronized Collection<Execution> getExecutions() {
        return executions;
    }

    public synchronized boolean addUserToConfiguration(String executionName, String userName) {
        boolean userAdded = false;

        for (Execution execution :
                executions) {
            if (execution.getExecutionName().equals(executionName)) {
                userAdded = execution.addNewWorkerName(userName);
                break;
            }
        }

        return userAdded;
    }
}
