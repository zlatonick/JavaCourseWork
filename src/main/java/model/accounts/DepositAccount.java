package model.accounts;

import java.math.BigDecimal;
import java.sql.Date;

public class DepositAccount extends Account {

    private BigDecimal depositRate;

    private BigDecimal depositAmount;

    public DepositAccount(int accountId, AccountType type, int userId,
                          BigDecimal currBalance, Date validity, String number,
                          BigDecimal depositRate, BigDecimal depositAmount) {
        super(accountId, type, userId, currBalance, validity, number);
        this.depositRate = depositRate;
        this.depositAmount = depositAmount;
    }

    public BigDecimal getDepositRate() {
        return depositRate;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }
}
