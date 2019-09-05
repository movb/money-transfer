package dao;

import lombok.*;

@Value
public class Transaction {
    String from;
    String to;
    long amount;
    String idempotencyKey;
}
