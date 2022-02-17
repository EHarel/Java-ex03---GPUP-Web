package task.configuration;

import exception.InvalidInputRangeException;
import exception.NonexistentElementException;
import task.TaskType;

import javax.naming.NameNotFoundException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationCompilation extends Configuration implements Serializable, Cloneable {
//    private static final long serialVersionUID = 1; // 01-Dec-2021
    private static final long serialVersionUID = 2; // 06-Dec-2021


    private String sourceCodePath; // Expected to be opened. Warn if not.
    private String outPath;
    private boolean outDirectoryCreated;

    public ConfigurationCompilation(
            String name,
            int numberOfThreads,
            String sourceCodePath,
            String outPath)
            throws NonexistentElementException, InvalidInputRangeException, NameNotFoundException {
        super(TaskType.COMPILATION, name, numberOfThreads);

        setSourceCodePath(sourceCodePath);
        setOutPath(outPath);
        outDirectoryCreated = false;
    }

    public ConfigurationCompilation(ConfigurationDataCompilation compData) throws NameNotFoundException, InvalidInputRangeException, NonexistentElementException {
        super(TaskType.COMPILATION, compData.getName(), compData.getThreadCount());
        setSourceCodePath(compData.getSourceCodePath());
        setOutPath(compData.getOutPath());
        outDirectoryCreated = compData.isOutDirectoryCreated();
    }

    public String getSourceCodePath() {
        return sourceCodePath;
    }

    public String getOutPath() {
        return outPath;
    }

    // Source code directory is expected to exist. If not it's an error.
    private void setSourceCodePath(String sourceCodePath) throws NonexistentElementException {
        if (sourceCodePath == null) {
            throw new NullPointerException("No source path given");
        }

        Path path = Paths.get(sourceCodePath);
        if (!Files.isDirectory(path)) { // Non-existing directory
            throw new NonexistentElementException("There is no opened directory at given path \"" + sourceCodePath
            + "\".\nPath must lead to an existing directory.");
        } else {
            this.sourceCodePath = sourceCodePath;
        }
    }

    private void setOutPath(String outPath) throws InvalidPathException {
        if (outPath == null) {
            throw  new NullPointerException("No out path given");
        }

        if (isValidPath(outPath)) {
            this.outPath = outPath;
        } else {
            throw new InvalidPathException(outPath, "Invalid path!");
        }
    }

    /**
     * <pre>
     * Checks if a string is a valid path.
     * Null safe.
     *
     * Calling examples:
     *    isValidPath("c:/test");      //returns true
     *    isValidPath("c:/te:t");      //returns false
     *    isValidPath("c:/te?t");      //returns false
     *    isValidPath("c/te*t");       //returns false
     *    isValidPath("good.txt");     //returns true
     *    isValidPath("not|good.txt"); //returns false
     *    isValidPath("not:good.txt"); //returns false
     * </pre>
     */
    public static boolean isValidPath(String path) {
        boolean validPath = true;

        try {
            Paths.get(path);
        } catch (Exception e) {
            validPath = false;
        }

        return validPath;
    }

    public boolean isOutDirectoryCreated() {
        return outDirectoryCreated;
    }

    public void setOutDirectoryCreated(boolean outDirectoryCreated) {
        this.outDirectoryCreated = outDirectoryCreated;
    }

    @Override
    public ConfigurationData getData() {
        return new ConfigurationDataCompilation(this.name, this.numberOfThreads, this.sourceCodePath, this.outPath, this.outDirectoryCreated);
    }

    @Override
    public Configuration clone() {
        ConfigurationCompilation configComp = null;

        try {
            configComp = new ConfigurationCompilation(this.name, this.numberOfThreads, this.sourceCodePath, this.outPath);
        } catch (NonexistentElementException | InvalidInputRangeException | NameNotFoundException ignore) { // Cannot fail from inside
        }

        return configComp;
    }
}
