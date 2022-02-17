package exception;

public class DependencyOnNonexistentTargetException extends Exception {
    public DependencyOnNonexistentTargetException(String message) { super(message); }
}