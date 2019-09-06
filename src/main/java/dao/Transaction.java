package dao;

import lombok.*;

@Value
public class Transaction {
    public Transaction(String from, String to, long amount, String idempotencyKey) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    String from;
    String to;
    long amount;
    String idempotencyKey;
}
