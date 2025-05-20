package cool.drinkup.drinkup.workflow.internal.exception;

public class RetryException extends RuntimeException {
    public RetryException(String message) {
        super(message);
    }
}
