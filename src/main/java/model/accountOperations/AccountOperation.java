package model.accountOperations;

import java.math.BigDecimal;
import java.sql.Date;

public abstract class AccountOperation {

    private int operationId;

    private int accountId;

    private OperationType type;

    private BigDecimal amount;

    private Date date;

    public AccountOperation(int operationId, int accountId, OperationType type, BigDecimal amount, Date date) {
        this.operationId = operationId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public int getOperationId() {
        return operationId;
    }

    public OperationType getType() {
        return type;
    }

    public int getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }
}
