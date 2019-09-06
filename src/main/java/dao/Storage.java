package dao;

import dao.Transaction;

import java.util.List;

public interface Storage {
    Account get(String id);
    List<Account> getAll();
    boolean create(Account account);

    void transfer(Transaction transaction);
}
