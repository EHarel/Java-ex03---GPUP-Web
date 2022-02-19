package events;

import task.Execution;

public interface ExecutionEndListener {
    void executedEnded(Execution execution);
}
