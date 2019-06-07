package dao.instances;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class AbstractDao<T> {

    Connection connection;

    public AbstractDao(Connection connection) {
        this.connection = connection;
    }

    public abstract T findById(int id);

    public abstract ArrayList<T> findAll();

    public abstract int insert(T entity);

    public abstract boolean update(T entity);

    public abstract boolean delete(T entity);

}
