package com.revoluttask.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private UUID accountId;
    private BigDecimal balance;

    public Account(UUID accountId, BigDecimal balance) {
        this.accountId = accountId;
        this.balance = balance;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
