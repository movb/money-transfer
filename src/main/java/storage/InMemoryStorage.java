package storage;

import dao.Account;
import dao.Transaction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import exceptions.*;
import util.Validators;

public class InMemoryStorage implements Storage {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Boolean> operationsLog = new ConcurrentHashMap<>();

    @Override
    public boolean create(Account account) {
        return accounts.putIfAbsent(account.getId(), account) == null;
    }

    @Override
    public Account get(String id) {
        return  accounts.get(id);
    }

    @Override
    public List<Account> getAll() {
        List<Account> listOfAccounts =
                accounts.entrySet()
                        .stream()
                        .map(e -> e.getValue())
                        .collect(Collectors.toList());
        return listOfAccounts;
    }

    @Override
    public void transfer(Transaction transaction) {
        var accountFrom = accounts.get(transaction.getFrom());
        Validators.validateAccount(transaction.getFrom(), accountFrom);
        var accountTo = accounts.get(transaction.getTo());
        Validators.validateAccount(transaction.getTo(), accountTo);

        Account minAcc, maxAcc;
        if (accountFrom.getId().compareTo(accountTo.getId()) < 0) {
            minAcc = accountFrom;
            maxAcc = accountTo;
        } else {
            minAcc = accountTo;
            maxAcc = accountFrom;
        }

        synchronized (minAcc) {
            synchronized (maxAcc) {
                Validators.validateBalance(accountFrom, transaction);
                Validators.validateMaxBalance(accountFrom, transaction);

                if (operationsLog.putIfAbsent(transaction.getIdempotencyKey(), true) == null) {
                    accountFrom.setBalance(accountFrom.getBalance() - transaction.getAmount());
                    accountTo.setBalance(accountTo.getBalance() + transaction.getAmount());
                } else {
                    throw new TransactionFailedException("Idempotency key is already used");
                }
            }
        }
    }
}
