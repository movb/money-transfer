package dao;

import lombok.*;
import java.math.BigInteger;

@Getter
@Setter
public class Account {
    public Account(String id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    String id;
    long balance;
}
