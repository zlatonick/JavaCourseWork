package model;

import dao.ConnectionPool;
import dao.DaoFactory;
import dao.DaoType;
import dao.instances.AbstractDao;
import model.accountOperations.AccountOperation;
import model.accountOperations.OperationType;
import model.accountOperations.PayBillOperation;
import model.accountOperations.TransferOperation;
import model.accounts.Account;
import model.accounts.AccountType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {

    private DaoFactory daoFactory;

    private int userId;

    public User(DaoFactory daoFactory, int userId) {
        this.daoFactory = daoFactory;
        this.userId = userId;
    }

    public ArrayList<Bill> getAllBills() {

        AbstractDao<Bill> billDao = daoFactory.getDao(DaoType.BILL,
                ConnectionPool.getInstance().getConnection());

        ArrayList<Bill> bills = billDao.findAll();

        bills.removeIf(bill -> bill.getUserId() != this.userId || bill.isPayed());

        return bills;
    }

    public boolean payBill(Account account, Bill bill, Date date) {

        Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
        }
        catch (SQLException e) {
            return false;
        }

        // Getting the DAOs
        AbstractDao<Account> accountDao = daoFactory.getDao(DaoType.ACCOUNT, connection);
        AbstractDao<Bill> billDao = daoFactory.getDao(DaoType.BILL, connection);
        AbstractDao<AccountOperation> operationDao = daoFactory.getDao(DaoType.ACCOUNT_OPERATIONS, connection);

        AccountOperation billOperation = new PayBillOperation(-1, account.getAccountId(),
                OperationType.PAY_BILL, bill.getAmount(), date, bill.getBillId());

        if (account.getCurrBalance().compareTo(bill.getAmount()) < 0) {
            // Not enough money
            return false;
        }

        // Subtracting the amount of money
        account.setCurrBalance(account.getCurrBalance().subtract(bill.getAmount()));

        // Marking the bill as payed
        bill.setPayed(true);

        // Updating the information in database
        try {
            if (accountDao.update(account) && billDao.update(bill)
                    && operationDao.insert(billOperation) != -1) {

                connection.commit();
                return true;
            }
            else {
                connection.rollback();
                return false;
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    public boolean makeTransfer(Account accountFrom, Account accountTo, BigDecimal amount, Date date) {

        Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
        }
        catch (SQLException e) {
            return false;
        }

        if (accountFrom.getCurrBalance().compareTo(amount) < 0) {
            // Not enough money
            return false;
        }

        // Changing the amount of money
        accountFrom.setCurrBalance(accountFrom.getCurrBalance().subtract(amount));
        accountTo.setCurrBalance(accountTo.getCurrBalance().add(amount));

        // Creating the transfer operation
        TransferOperation transfer = new TransferOperation(-1, accountFrom.getAccountId(),
                OperationType.TRANSFER, amount, date, accountTo.getAccountId());

        // Getting the DAOs
        AbstractDao<Account> accountDao = daoFactory.getDao(DaoType.ACCOUNT, connection);
        AbstractDao<AccountOperation> transferDao = daoFactory.getDao(DaoType.ACCOUNT_OPERATIONS, connection);

        // Updating the information in database
        try {
            if (accountDao.update(accountFrom) && accountDao.update(accountTo)
                    && transferDao.insert(transfer) != -1) {

                connection.commit();
                return true;
            }
            else {
                connection.rollback();
                return false;
            }
        }
        catch (SQLException e) {
            return false;
        }
    }

    public ArrayList<Account> getAccountsInfo() {

        AbstractDao<Account> accountDao = daoFactory.getDao(DaoType.ACCOUNT,
                ConnectionPool.getInstance().getConnection());

        ArrayList<Account> accounts = accountDao.findAll();

        accounts.removeIf(account -> account.getUserId() != this.userId);

        return accounts;

    }

    public boolean newAccountRequest(String typeStr, BigDecimal amount, BigDecimal rate) {

        AccountType type = typeStr.equals("credit") ? AccountType.CREDIT : AccountType.DEPOSIT;

        AccountRequest request = new AccountRequest(-1, this.userId, type,
                amount, rate);

        AbstractDao<AccountRequest> accountDao = daoFactory.getDao(DaoType.CREDIT_ACCOUNT_REQUEST,
                ConnectionPool.getInstance().getConnection());

        return accountDao.insert(request) != -1;
    }

    public UserInfo getInfo() {

        AbstractDao<UserInfo> accountDao = daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        return accountDao.findById(this.userId);
    }

    public boolean changeInfo(UserInfo newInfo) {

        AbstractDao<UserInfo> accountDao = daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        return accountDao.update(newInfo);
    }
}
