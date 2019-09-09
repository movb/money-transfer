package model;

import lombok.Value;
import lombok.EqualsAndHashCode;

@Value
@EqualsAndHashCode
public class Transaction {
    public enum Status {
        FAILED,
        PERFORMING,
        SUCCESS
    }

    public Transaction(String from, String to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.idempotencyKey = null;
    }

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
