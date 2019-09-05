package util;

import dao.Account;
import dao.Transaction;

import exceptions.*;
import java.math.BigInteger;

public class Validators {
    public static void validateTransaction(Transaction transaction) {
        if (transaction.getFrom() == null || transaction.getFrom().isEmpty()
            || transaction.getTo() == null || transaction.getTo().isEmpty()) {
            throw new InvalidParamsException();
        }
        if (transaction.getFrom().equals(transaction.getTo())) {
            throw new IdenticalAccountsException();
        }
        if (!(transaction.getAmount() > 0)) {
            throw new InvalidAmountException(transaction.getAmount());
        }
    }

    public static void validateBalance(Account account, Transaction transaction) {
        if (BigInteger.valueOf(transaction.getAmount()).compareTo(account.getBalance()) == 1) {
            throw new InsufficientBalanceException(account.getId());
        }
    }

    public static void validateAccount(String id, Account account) {
        if (account == null) {
            throw new AccountNotExistsException(id);
        }
    }
}
