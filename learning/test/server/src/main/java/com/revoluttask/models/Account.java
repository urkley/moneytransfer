package models;

import java.math.BigDecimal;

public class Account {

    private String accountId;
    private BigDecimal balance;

    public Account(String accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
