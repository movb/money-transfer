package exceptions;

public class IdenticalAccountsException extends RuntimeException {
    public IdenticalAccountsException() {
        super(String.format("Account 'from' and 'to' are identical"));
    }
}