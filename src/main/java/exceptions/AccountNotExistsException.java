package exceptions;

public class AccountNotExistsException extends RuntimeException {
    public AccountNotExistsException(String id) {
        super(String.format("Account id %s does not exists", id));
    }
}
