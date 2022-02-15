package events;

import task.configuration.Configuration;

public interface ExecutionStartListener {
    void executionStarted(Configuration startingConfig);
}
