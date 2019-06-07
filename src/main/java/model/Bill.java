package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Bill {

    private int billId;

    private int userId;

    private String name;

    private BigDecimal amount;

    private Date payUntil;

    private boolean isPayed;

    public Bill(int billId, int userId, String name, BigDecimal amount, Date payUntil, boolean isPayed) {
        this.billId = billId;
        this.userId = userId;
        this.name = name;
        this.amount = amount;
        this.payUntil = payUntil;
        this.isPayed = isPayed;
    }

    public String getName() {
        return name;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getBillId() {
        return billId;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getPayUntil() {
        return payUntil;
    }

    public boolean isPayed() {
        return isPayed;
    }

    public void setPayed(boolean payed) {
        isPayed = payed;
    }
}
