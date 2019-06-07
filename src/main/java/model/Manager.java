package model;

import dao.ConnectionPool;
import dao.DaoFactory;
import dao.DaoType;
import dao.instances.AbstractDao;
import model.accounts.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

public class Manager {

    private DaoFactory daoFactory;

    private int userId;

    public Manager(DaoFactory daoFactory, int userId) {
        this.daoFactory = daoFactory;
        this.userId = userId;
    }

    public ArrayList<AccountRequest> getAllRequests() {

        AbstractDao<AccountRequest> requestDao = daoFactory.getDao(DaoType.CREDIT_ACCOUNT_REQUEST,
                ConnectionPool.getInstance().getConnection());

        return requestDao.findAll();
    }

    public ArrayList<UserInfo> getAllUsers() {

        AbstractDao<UserInfo> accountDao = daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        ArrayList<UserInfo> result = accountDao.findAll();

        result.removeIf(UserInfo::isManager);

        return result;
    }

    public Account acceptRequest(int requestId, Date term) {

        Connection connection = ConnectionPool.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
        }
        catch (SQLException e) {
            return null;
        }

        AbstractDao<AccountRequest> requestDao = daoFactory.getDao(DaoType.CREDIT_ACCOUNT_REQUEST,
                connection);

        AccountRequest request = requestDao.findById(requestId);

        if (!requestDao.delete(request)) {
            try {
                connection.rollback();
            }
            catch (SQLException ignored) {

            }

            return null;
        }

        AbstractDao<Account> accountDao = daoFactory.getDao(DaoType.ACCOUNT, connection);

        Account newAccount;

        if (request.getType() == AccountType.CREDIT) {
            newAccount = new CreditAccount(-1, request.getType(),
                    request.getUserId(), request.getAmount(), term,
                    AccountNumbersGenerator.nextNumber(), request.getRate(),
                    request.getAmount(), new BigDecimal(0), new BigDecimal(0));
        }
        else {
            newAccount = new DepositAccount(-1, request.getType(),
                    request.getUserId(), request.getAmount(), term,
                    AccountNumbersGenerator.nextNumber(), request.getRate(),
                    request.getAmount());
        }

        int resId = accountDao.insert(newAccount);

        try {
            if (resId != -1) {
                newAccount.setAccountId(resId);
                connection.commit();
            }
            else {
                connection.rollback();
            }
        }
        catch (SQLException e) {
            return null;
        }


        return newAccount;
    }

    public boolean declineRequest(int requestId) {

        AbstractDao<AccountRequest> requestDao = daoFactory.getDao(DaoType.CREDIT_ACCOUNT_REQUEST,
                ConnectionPool.getInstance().getConnection());

        AccountRequest request = requestDao.findById(requestId);

        return requestDao.delete(request);

    }

    public boolean makeManager(int userId) {

        AbstractDao<UserInfo> userDao = daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        UserInfo info = userDao.findById(userId);

        info.setManager(true);

        return userDao.update(info);
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

    public boolean addBill(Bill bill) {

        AbstractDao<Bill> billDao = daoFactory.getDao(DaoType.BILL,
                ConnectionPool.getInstance().getConnection());

        return billDao.insert(bill) != -1;
    }
}
