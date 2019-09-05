package dao;

import dao.Storage;
import dao.Account;
import dao.Transaction;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import exceptions.*;
import util.Validators;

public class InMemoryStorage implements Storage {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public boolean create(Account account) {
        Account previousAccount = accounts.putIfAbsent(account.getId(), account);
        if (previousAccount != null) {
            return false;
        }
        return true;
    }

    @Override
    public Account get(String id) {
        return accounts.get(id);
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
    public void delete(String id) {
        accounts.remove(id);
    }

    @Override
    public void transfer(Transaction transaction) {
        Validators.validateTransaction(transaction);

        while(true) {
            Account accountFrom = accounts.get(transaction.getFrom());
            Validators.validateAccount(transaction.getFrom(), accountFrom);
            Validators.validateBalance(accountFrom, transaction);

            Account accountTo = accounts.get(transaction.getTo());
            Validators.validateAccount(transaction.getTo(), accountTo);

            Account accountFromChanged = accountFrom.toBuilder().build();
            accountFromChanged.setBalance(accountFrom.getBalance().add(BigInteger.valueOf(-transaction.getAmount())));

            if (!accounts.replace(accountFrom.getId(), accountFrom, accountFromChanged))
                continue;

            while(true) {
                accountTo = accounts.get(transaction.getTo());
                try {
                    Validators.validateAccount(transaction.getTo(), accountTo);
                } catch (AccountNotExistsException accountToNotExistsException) {
                    // return money to original account
                    while(true) {
                        accountFrom = accounts.get(transaction.getFrom());
                        try {
                            Validators.validateAccount(transaction.getFrom(), accountFrom);
                        } catch (AccountNotExistsException accountFromNotExistsException) {
                            // both accounts deleted
                            throw new TransactionFailedException("both accounts deleted during transaction");
                        }
                        accountFromChanged = accountFrom.toBuilder().build();
                        accountFromChanged.setBalance(accountFrom.getBalance().add(
                                BigInteger.valueOf(transaction.getAmount())));
                        if (!accounts.replace(accountFrom.getId(), accountFrom, accountFromChanged))
                            continue;

                        break;
                    }
                    throw accountToNotExistsException;
                }
                Account accountToChanged = accountTo.toBuilder().build();
                accountToChanged.setBalance(accountTo.getBalance().add(BigInteger.valueOf(transaction.getAmount())));

                if (!accounts.replace(accountToChanged.getId(), accountTo, accountToChanged))
                    continue;

                break;
            }

            break;
        }
    }
}
