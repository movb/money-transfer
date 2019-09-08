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
    private final Map<Transaction, Transaction.Status> operationsLog = new ConcurrentHashMap<>();

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
                        .sorted((lh,rh)-> lh.getId().compareTo(rh.getId()))
                        .collect(Collectors.toList());
        return listOfAccounts;
    }

    @Override
    public void transfer(Transaction transaction) {
        if (idempotencyKeyPresent(transaction)) {
            /* This part of logic one can find a litle bit complicated. But for idempotency we need to
               store transaction status, and if some transaction already present, return the same result,
               as it was on first operation. If transaction is still performing, lets wait for it result.
             */
            var value = operationsLog.putIfAbsent(transaction, Transaction.Status.PERFORMING);
            // wait for result of performing transaction
            while (value == Transaction.Status.PERFORMING) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    break;
                }
                value = operationsLog.get(transaction);
            }
            if (value == Transaction.Status.SUCCESS) {
                // if transaction was successful just return
                return;
            } else if (value == Transaction.Status.FAILED) {
                // if previous transaction failed return error
                throw new TransactionFailedException("Transaction with this idempotency parameters falied");
            }
        }

        try {
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

                    if (!idempotencyKeyPresent(transaction) ||
                            operationsLog.replace(transaction, Transaction.Status.PERFORMING, Transaction.Status.SUCCESS)) {
                        accountFrom.setBalance(accountFrom.getBalance() - transaction.getAmount());
                        accountTo.setBalance(accountTo.getBalance() + transaction.getAmount());
                    } else {
                        throw new TransactionFailedException("Idempotency key is already used");
                    }
                }
            }
        } catch (Exception e) {
            operationsLog.replace(transaction, Transaction.Status.PERFORMING, Transaction.Status.FAILED);
            throw e;
        }
    }

    @Override
    public void clear() {
        accounts.clear();
        operationsLog.clear();
    }

    private boolean idempotencyKeyPresent(Transaction transaction) {
        return transaction.getIdempotencyKey() != null && !transaction.getIdempotencyKey().isEmpty();
    }
}
