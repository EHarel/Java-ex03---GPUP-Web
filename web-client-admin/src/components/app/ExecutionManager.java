package components.app;

import java.util.HashMap;
import java.util.Map;

public class ExecutionManager {
    private Map<String, Integer> executionName2Count;

    public ExecutionManager() {
        executionName2Count = new HashMap<>();
    }

    public synchronized Integer getExecutionCount(String executionName) {
        return executionName2Count.get(executionName);
    }

    public synchronized boolean increaseExecutionCount(String executionName) {
        Integer executionCount = executionName2Count.get(executionName);

        boolean increased = (executionCount != null);

        if (executionCount != null) {
            executionName2Count.put(executionName, executionCount + 1);
        }

        return increased;
    }

    public synchronized boolean addExecution(String executionName) {
        boolean added = false;

        Integer currentVal = executionName2Count.get(executionName);
        if (currentVal == null) {
            executionName2Count.put(executionName, 0);
            added = true;
        }

        return added;
    }
}
