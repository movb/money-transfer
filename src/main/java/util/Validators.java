package util;

import dao.Account;
import dao.Transaction;

import exceptions.*;
import java.math.BigInteger;

public class Validators {
    public static void validateTransaction(Transaction transaction) {
        if (transaction.getFrom() == null || transaction.getFrom().isEmpty()) {
            throw new InvalidParamsException("param 'getFrom' is empty");
        }
        if (transaction.getTo() == null || transaction.getTo().isEmpty()) {
            throw new InvalidParamsException("prarm 'to' is empty");
        }

        if (transaction.getFrom().equals(transaction.getTo())) {
            throw new IdenticalAccountsException();
        }
        if (!(transaction.getAmount() > 0)) {
            throw new InvalidAmountException(transaction.getAmount());
        }
    }

    public static void validateBalance(Account account, Transaction transaction) {
        if (account.getBalance() < transaction.getAmount()) {
            throw new TransactionFailedException("Insufficent balance on account");
        }
    }

    public static void validateMaxBalance(Account account, Transaction transaction) {
        var maxAmount = Long.MAX_VALUE - account.getBalance();
        if (transaction.getAmount() > maxAmount) {
            throw new TransactionFailedException("Resulting amount is more than the maximum possible balance.");
        }
    }

    public static void validateAccount(String id, Account account) {
        if (account == null) {
            throw new AccountNotExistsException(id);
        }
    }
}
