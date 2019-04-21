package com.revoluttask.models;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private UUID fromAccountId;
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(UUID fromAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.amount = amount;
    }

    public UUID getFromAccountId() {
        return fromAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setFromAccountId(UUID fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}