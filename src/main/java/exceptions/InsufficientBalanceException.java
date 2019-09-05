package exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String id) {
        super(String.format("Account id %s has insufficient balance", id));
    }
}