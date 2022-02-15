package exception;

public class ExistingItemException extends Exception {
    public ExistingItemException(String message) {
        super(message);
    }
}