package model.accountOperations;

import java.math.BigDecimal;
import java.sql.Date;

public class PayBillOperation extends AccountOperation {

    private int billId;

    public PayBillOperation(int operationId, int accountId, OperationType type,
                            BigDecimal amount, Date date, int billId) {
        super(operationId, accountId, type, amount, date);
        this.billId = billId;
    }

    public int getBillId() {
        return billId;
    }
}
