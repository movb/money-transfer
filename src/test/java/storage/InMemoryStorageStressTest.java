package storage;

import dao.Account;
import dao.Transaction;
import exceptions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.stream.IntStream;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageStressTest {

    private Storage storage;

    @BeforeEach
    void setup() {
        storage = new InMemoryStorage();
    }

    @Test
    @DisplayName("A lot of parallel transactions")
    void ParallelTransactions() {
        storage.create(new Account("1", 10000));
        storage.create(new Account("2", 0));

        Executable transfer = () ->
                IntStream.range(1, 101).parallel().forEach(value -> {
                    storage.transfer(new Transaction("1", "2", value, "key" + value));
                });

        assertDoesNotThrow(transfer);

        assertEquals(10000 - 5050, storage.get("1").getBalance());
        assertEquals(5050, storage.get("2").getBalance());
    }

    @Test
    @DisplayName("A lot of parallel transactions and some can lead to error")
    void ParallelTransactionsWithErrors() {
        storage.create(new Account("1", 1000));
        storage.create(new Account("2", 0));

        var failedOperations = new AtomicLong(0);
        Executable transfer = () ->
                IntStream.range(0, 200).parallel().forEach(value -> {
                    try {
                        storage.transfer(new Transaction("1", "2", 10, "key" + value));
                    } catch (Exception e) {
                        failedOperations.incrementAndGet();
                    }
                });

        assertDoesNotThrow(transfer);

        assertEquals(0, storage.get("1").getBalance());
        assertEquals(1000, storage.get("2").getBalance());
        assertEquals(100, failedOperations.get());
    }


}