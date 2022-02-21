package task.configuration;

import task.TaskType;

public class ConfigurationDTOCompilation extends ConfigurationDTO implements Cloneable {
    private String sourceCodePath;
    private String outPath;
    private boolean outDirectoryCreated;

    public ConfigurationDTOCompilation() {}

    public ConfigurationDTOCompilation(String name, int threadCount, String sourceCodePath, String outPath, boolean outDirectoryCreated) {
        super(TaskType.COMPILATION, name, threadCount);
        this.sourceCodePath = sourceCodePath;
        this.outPath = outPath;
        this.outDirectoryCreated = outDirectoryCreated;
    }

    public void setSourceCodePath(String sourceCodePath) {
        this.sourceCodePath = sourceCodePath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public void setOutDirectoryCreated(boolean outDirectoryCreated) {
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

    @Override
    public ConfigurationDTOCompilation clone() {
        return new ConfigurationDTOCompilation(
                this.name,
                this.threadCount,
                this.sourceCodePath,
                this.outPath,
                this.outDirectoryCreated);
    }
}
