package com.revoluttask.models;

import java.math.BigDecimal;

public class BalanceInput {

    private BigDecimal balance;

    public BalanceInput() {
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BalanceInput(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
