package model;

import dao.ConnectionPool;
import dao.DaoFactory;
import dao.DaoFactoryImpl;
import dao.DaoType;
import dao.instances.AbstractDao;
import dao.instances.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BankModel {

    private DaoFactory daoFactory;

    public BankModel() {
        this.daoFactory = new DaoFactoryImpl();
    }

    public int login(String login, String password) {

        UserInfoDao userDao = (UserInfoDao) daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        return userDao.login(login, password);
    }

    public int registerUser(String login, String password) {

        UserInfoDao userDao = (UserInfoDao) daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        int userId = userDao.login(login, password);

        if (userId != -1) {
            return -1;
        }

        UserInfo info = new UserInfo(-1, login, password, "",
                "", "", "", false);

        return userDao.insert(info);
    }

    public User getUserById(int userId) {
        return new User(daoFactory, userId);
    }

    public Manager getManagerById(int userId) {
        return new Manager(daoFactory, userId);
    }

    public UserInfo getUserInfoById(int userId) {

        AbstractDao<UserInfo> accountDao = daoFactory.getDao(DaoType.USER,
                ConnectionPool.getInstance().getConnection());

        return accountDao.findById(userId);
    }
}
