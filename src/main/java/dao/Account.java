package dao;

import lombok.*;
import java.math.BigInteger;

@Getter
@Setter
@Builder(toBuilder=true)
public class Account {
    String id;
    BigInteger balance;
}
