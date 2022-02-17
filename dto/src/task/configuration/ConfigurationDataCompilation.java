package task.configuration;

import task.TaskType;

import java.io.Serializable;

public class ConfigurationDataCompilation extends ConfigurationData implements Serializable, Cloneable {
//    private static final long serialVersionUID = 1; // 01-Dec-2021
    private static final long serialVersionUID = 2; // 06-Dec-2021

    private final String sourceCodePath;
    private final String outPath;
    private final boolean outDirectoryCreated;

    public ConfigurationDataCompilation(String name, int threadCount, String sourceCodePath, String outPath, boolean outDirectoryCreated) {
        super(TaskType.COMPILATION, name, threadCount);
        this.sourceCodePath = sourceCodePath;
        this.outPath = outPath;
        this.outDirectoryCreated = outDirectoryCreated;
    }

    public String getSourceCodePath() {
        return sourceCodePath;
    }

    public String getOutPath() {
        return outPath;
    }

    public boolean isOutDirectoryCreated() {
        return outDirectoryCreated;
    }

    @Override // TODO: this is immutable, maybe no point to clone?
    public ConfigurationData clone() {
        return new ConfigurationDataCompilation(
                this.name,
                this.threadCount,
                this.sourceCodePath,
                this.outPath,
                this.outDirectoryCreated);
    }
}
