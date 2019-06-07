package model.accounts;

import java.math.BigDecimal;
import java.sql.Date;

public class CreditAccount extends Account {

    private BigDecimal creditRate;

    private BigDecimal creditLimit;     // Maximal debt

    private BigDecimal currentDebt;

    private BigDecimal interestCharges;

    public CreditAccount(int accountId, AccountType type, int userId, BigDecimal currBalance,
                         Date validity, String number, BigDecimal creditRate, BigDecimal creditLimit,
                         BigDecimal currentDebt, BigDecimal interestCharges) {
        super(accountId, type, userId, currBalance, validity, number);
        this.creditRate = creditRate;
        this.creditLimit = creditLimit;
        this.currentDebt = currentDebt;
        this.interestCharges = interestCharges;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public BigDecimal getCreditRate() {
        return creditRate;
    }

    public BigDecimal getCurrentDebt() {
        return currentDebt;
    }

    public BigDecimal getInterestCharges() {
        return interestCharges;
    }
}
