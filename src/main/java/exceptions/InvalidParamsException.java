package exceptions;

public class InvalidParamsException extends RuntimeException {
    public InvalidParamsException(String message) {
        super(String.format("Invalid params: %s", message));
    }
}
