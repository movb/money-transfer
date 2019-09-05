package exceptions;

public class InvalidParamsException extends RuntimeException {
    public InvalidParamsException() {
        super(String.format("Invalid params"));
    }
}
