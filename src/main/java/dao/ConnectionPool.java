package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionPool {

    private static ConnectionPool instance;

    private String url;
    private String user;
    private String password;

    private ConnectionPool() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Error while registering the MySql driver");
        }

        ResourceBundle resource = ResourceBundle.getBundle("database");

        url = resource.getString("url");
        user = resource.getString("user");
        password = resource.getString("password");
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
