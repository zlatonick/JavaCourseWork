package dao;

import dao.instances.*;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Component
public class DaoFactoryImpl implements DaoFactory {

    @Override
    public AbstractDao getDao(DaoType daoType, Connection connection) {
        switch (daoType) {
            case USER:
                return new UserInfoDao(connection);
            case CREDIT_ACCOUNT_REQUEST:
                return new AccountRequestsDao(connection);
            case ACCOUNT:
                return new AccountsDao(connection);
            case BILL:
                return new BillDao(connection);
            case ACCOUNT_OPERATIONS:
                return new AccountsOperationsDao(connection);
        }
        return null;
    }
}
