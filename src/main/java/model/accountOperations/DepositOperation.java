package model.accountOperations;

import java.math.BigDecimal;
import java.sql.Date;

public class DepositOperation extends AccountOperation {
    public DepositOperation(int operationId, int accountId, OperationType type, BigDecimal amount, Date date) {
        super(operationId, accountId, type, amount, date);
    }
}
