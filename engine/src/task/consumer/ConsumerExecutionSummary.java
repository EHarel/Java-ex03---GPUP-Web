package task.consumer;

import logic.Engine;
import task.ExecutionData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.Consumer;

public class ConsumerExecutionSummary implements Consumer<ExecutionData>, Serializable {
    private static final long serialVersionUID = 1; // 13-Dec-2021

    private final String directoryPath;

    public ConsumerExecutionSummary(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void accept(ExecutionData executionData) {
        writeToFile(executionData);
    }

    private void writeToFile(ExecutionData executionData) {
        String fullPath = directoryPath + "/summary.log";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
            writer.write(Engine.getInstance().getFormalizedExecutionReportString(executionData));
            writer.close();
        } catch (IOException ignore) {
        } // Can't stop the execution for this
    }
}
