package model.accountOperations;

import java.math.BigDecimal;
import java.sql.Date;

public class TransferOperation extends AccountOperation {

    private int accountDestId;

    public TransferOperation(int operationId, int accountId, OperationType type,
                             BigDecimal amount, Date date, int accountDestId) {
        super(operationId, accountId, type, amount, date);
        this.accountDestId = accountDestId;
    }

    public int getAccountDestId() {
        return accountDestId;
    }
}
