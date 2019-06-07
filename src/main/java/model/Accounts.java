package model;

import dao.ConnectionPool;
import dao.DaoFactory;
import dao.DaoFactoryImpl;
import dao.DaoType;
import dao.instances.AbstractDao;
import dao.instances.AccountsDao;
import model.accountOperations.AccountOperation;
import model.accounts.Account;

import java.util.ArrayList;

public class Accounts {

    private static DaoFactory daoFactory = new DaoFactoryImpl();

    public static Account getAccountById(int accountId) {

        AbstractDao<Account> accountDao = daoFactory.getDao(DaoType.ACCOUNT,
                ConnectionPool.getInstance().getConnection());

        return accountDao.findById(accountId);
    }

    public static Account getAccountByNumber(String number) {

        AccountsDao accountDao = (AccountsDao) daoFactory.getDao(DaoType.ACCOUNT,
                ConnectionPool.getInstance().getConnection());

         return accountDao.findByNumber(number);
    }

    public static Bill getBillById(int billId) {

        AbstractDao<Bill> billDao = daoFactory.getDao(DaoType.BILL,
                ConnectionPool.getInstance().getConnection());

        return billDao.findById(billId);
    }

    public static ArrayList<AccountOperation> getAccountOperations(Account account) {

        AbstractDao<AccountOperation> operationsDao = daoFactory.getDao(DaoType.ACCOUNT_OPERATIONS,
                ConnectionPool.getInstance().getConnection());

        ArrayList<AccountOperation> operations = operationsDao.findAll();

        operations.removeIf(operation -> operation.getAccountId() != account.getAccountId());

        return operations;
    }
}
