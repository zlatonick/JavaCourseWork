package model.accounts;

import java.math.BigDecimal;
import java.sql.Date;

public abstract class Account {

    private int accountId;

    private AccountType type;

    private int userId;

    private BigDecimal currBalance;

    private Date validity;

    private String number;

    public Account(int accountId, AccountType type, int userId,
                   BigDecimal currBalance, Date validity, String number) {
        this.accountId = accountId;
        this.type = type;
        this.userId = userId;
        this.currBalance = currBalance;
        this.validity = validity;
        this.number = number;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }

    public AccountType getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getCurrBalance() {
        return currBalance;
    }

    public Date getValidity() {
        return validity;
    }

    public String getNumber() {
        return number;
    }

    public void setCurrBalance(BigDecimal currBalance) {
        this.currBalance = currBalance;
    }
}
