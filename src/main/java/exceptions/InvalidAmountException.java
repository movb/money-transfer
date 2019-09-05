package exceptions;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(float amount) {
        super(String.format("Amount %f is invalid", amount));
    }
}
