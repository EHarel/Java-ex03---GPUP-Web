package exception;

public class ImmediateCircularDependencyException extends Exception {
    public ImmediateCircularDependencyException(String message) { super(message); }
}