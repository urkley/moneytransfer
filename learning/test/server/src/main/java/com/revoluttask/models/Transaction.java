package models;

import java.math.BigDecimal;

public class Transaction {

    private String fromAccountId;
    private BigDecimal amount;

    public Transaction() {
    }

    public Transaction(String fromAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.amount = amount;
    }

    public String getFromAccountId() {
        return fromAccountId;
    }

    public BigDecimal getMoneyAmount() {
        return amount;
    }

    public void setFromAccountId(String fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
