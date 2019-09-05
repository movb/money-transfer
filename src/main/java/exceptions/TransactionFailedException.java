package exceptions;

public class TransactionFailedException extends RuntimeException {
    public TransactionFailedException(String message) {
        super(String.format("Transaction failed: %s", message));
    }
}

