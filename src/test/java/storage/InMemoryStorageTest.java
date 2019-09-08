package storage;

import dao.Account;
import dao.Transaction;
import exceptions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageTest {

    private Storage storage;

    @BeforeEach
    void setup() {
        storage = new InMemoryStorage();
    }

    @Test
    @DisplayName("Basic create and get account")
    void createAndGet() {
        Account account = new Account("1", 100);
        storage.create(account);
        assertEquals(account, storage.get("1"));
    }

    @Test
    @DisplayName("Get all accounts")
    void getAll() {
        Account account1 = new Account("1", 100);
        storage.create(account1);
        Account account2 = new Account("2", 200);
        storage.create(account2);

        List<Account> result = storage.getAll();
        assertEquals(List.of(account1, account2), result);
    }

    @Test
    @DisplayName("Basic transfer operation")
    void transfer() {
        Account account1 = new Account("1", 100);
        storage.create(account1);
        Account account2 = new Account("2", 200);
        storage.create(account2);

        storage.transfer(new Transaction("2", "1", 59, "key1"));

        assertEquals(159, storage.get("1").getBalance());
        assertEquals(141, storage.get("2").getBalance());
    }

    @Test
    @DisplayName("Using idempotency key twice")
    void transferIdempotancyNoError() {
        Account account1 = new Account("id1", 100);
        storage.create(account1);
        Account account2 = new Account("id2", 200);
        storage.create(account2);

        storage.transfer(new Transaction("id2", "id1", 10, "key1"));

        assertEquals(190, storage.get("id2").getBalance());
        assertEquals(110, storage.get("id1").getBalance());

        Executable makeTransfer =
                () -> storage.transfer(new Transaction("id2", "id1", 10, "key1"));

        assertDoesNotThrow(makeTransfer);

        assertEquals(190, storage.get("id2").getBalance());
        assertEquals(110, storage.get("id1").getBalance());
    }

    @Test
    @DisplayName("Transfer validation errors")
    void transferValidationErrors() {
        Account account1 = new Account("id1", 100);
        storage.create(account1);
        Account account2 = new Account("id2", 200);
        storage.create(account2);

        Executable makeTransferFromNonExistingAccount =
                () -> storage.transfer(new Transaction("id3", "id1", 10, "key1"));

        assertThrows(AccountNotExistsException.class, makeTransferFromNonExistingAccount);

        Executable makeTransferToNonExistingAccount =
                () -> storage.transfer(new Transaction("id2", "id3", 10, "key1"));

        assertThrows(AccountNotExistsException.class, makeTransferToNonExistingAccount);

        Executable makeInsufficientBalanceTransfer =
                () -> storage.transfer(new Transaction("id2", "id1", 1000, "key1"));

        assertThrows(TransactionFailedException.class, makeInsufficientBalanceTransfer);

        Executable makeOverflowTransfer =
                () -> storage.transfer(new Transaction("id2", "id1", Long.MAX_VALUE, "key1"));

        assertThrows(TransactionFailedException.class, makeOverflowTransfer);
    }

    @Test
    @DisplayName("Clear storage")
    void clear() {
        Account account1 = new Account("id1", 100);
        storage.create(account1);
        Account account2 = new Account("id2", 200);
        storage.create(account2);

        storage.clear();

        assertEquals(Collections.EMPTY_LIST, storage.getAll());
    }
}