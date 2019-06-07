package dao;

import dao.instances.AbstractDao;

import java.sql.Connection;

public interface DaoFactory {

    AbstractDao getDao (DaoType daoType, Connection connection);

}
