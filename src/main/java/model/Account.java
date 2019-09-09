package model;

import lombok.*;

@Getter
@Setter
public class Account {
    public Account(String id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    private String id;
    long balance;
}
