package task.consumer;

import graph.TargetDTO;
import logic.Engine;

import java.io.*;
import java.util.function.Consumer;

public class ConsumerWriteTargetToFile implements Consumer<TargetDTO>, Serializable{
    private static final long serialVersionUID = 1; // 13-Dec-2021

    private final String directoryPath;

    public ConsumerWriteTargetToFile(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    @Override
    public void accept(TargetDTO targetDTO) {
        writeToFile(targetDTO);
    }

    private void writeToFile(TargetDTO targetReport) {
        String fullPath = directoryPath + "/" + targetReport.getName() + ".log";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
            writer.write(Engine.getInstance().getFormalizedTargetDataString(targetReport));
            writer.close();
        } catch (IOException ignore) { } // Can't stop the execution for this
    }
}
