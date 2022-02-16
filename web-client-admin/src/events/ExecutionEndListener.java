package events;

import task.ExecutionData;

public interface ExecutionEndListener {
    void executedEnded(ExecutionData executionData);
}
