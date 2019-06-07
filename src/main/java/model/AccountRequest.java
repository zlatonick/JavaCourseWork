package model;

import model.accounts.AccountType;

import java.math.BigDecimal;
import java.sql.Date;

public class AccountRequest {

    private int requestId;

    private int userId;

    private AccountType type;

    private BigDecimal amount;

    private BigDecimal rate;

    public AccountRequest(int requestId, int userId, AccountType type, BigDecimal amount, BigDecimal rate) {
        this.requestId = requestId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.rate = rate;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getUserId() {
        return userId;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
