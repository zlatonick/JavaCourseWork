package dao.instances;

import model.UserInfo;

import java.sql.*;
import java.util.ArrayList;

public class UserInfoDao extends AbstractDao<UserInfo> {

    public UserInfoDao(Connection connection) {
        super(connection);
    }

    @Override
    public UserInfo findById(int id) {

        UserInfo user;

        try  (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Users WHERE User_id = " + id);

            if (!res.next()) {
                return null;
            }

            user = new UserInfo(id, res.getString("Login"), res.getString("Password"),
                    res.getString("First_name"), res.getString("Last_name"),
                    res.getString("Email"), res.getString("Phone"),
                    res.getBoolean("Is_manager"));
        }
        catch (SQLException e) {
            return null;
        }

        return user;
    }

    @Override
    public ArrayList<UserInfo> findAll() {

        ArrayList<UserInfo> users = new ArrayList<>();

        try (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Users");

            while (res.next()) {
                users.add(new UserInfo(res.getInt("User_id"), res.getString("Login"),
                        res.getString("Password"),
                        res.getString("First_name"), res.getString("Last_name"),
                        res.getString("Email"), res.getString("Phone"),
                        res.getBoolean("Is_manager")));
            }
        }
        catch (SQLException e) {
            return null;
        }

        return users;
    }

    @Override
    public int insert(UserInfo entity) {

        int result;

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Users (Login, Password, First_name, Last_name, " +
                                "Phone, Email, Is_manager) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, entity.getLogin());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getFirstName());
            statement.setString(4, entity.getLastName());
            statement.setString(5, entity.getPhone());
            statement.setString(6, entity.getEmail());
            statement.setBoolean(7, entity.isManager());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1);
                    entity.setUserId(result);
                }
                else {
                    return -1;
                }
            }
        }
        catch (SQLException e) {
            return -1;
        }

        return result;
    }

    @Override
    public boolean update(UserInfo entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Users SET Login = ?, Password = ?, First_name = ?, " +
                                "Last_name = ?, Phone = ?, Email = ?, Is_manager = ? " +
                                "WHERE User_ID = ?")
        ) {
            statement.setString(1, entity.getLogin());
            statement.setString(2, entity.getPassword());
            statement.setString(3, entity.getFirstName());
            statement.setString(4, entity.getLastName());
            statement.setString(5, entity.getPhone());
            statement.setString(6, entity.getEmail());
            statement.setBoolean(7, entity.isManager());
            statement.setInt(8, entity.getUserId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }
        }
        catch (SQLException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(UserInfo entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Users WHERE User_ID = ?")
        ) {

            statement.setInt(1, entity.getUserId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }
        }
        catch (SQLException e) {
            return false;
        }

        return true;
    }

    public int login(String login, String password) {

        int result;

        try  (
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT User_ID FROM Users WHERE Login = ? AND Password = ?")
        )
        {

            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                return -1;
            }

            result = res.getInt("User_ID");
        }
        catch (SQLException e) {
            return -1;
        }

        return result;
    }

//    public int register(String login, String password) {
//
//        int result;
//
//        try  (
//                Statement statement = this.connection.createStatement()
//        )
//        {
//            ResultSet res = statement.executeQuery(
//                    "SELECT User_ID FROM Users WHERE Login = ?");
//
//            if (!res.next()) {
//                ResultSet resInsert = statement.executeQuery(
//                        "SELECT User_ID FROM Users WHERE Login = ?");
//            }
//
//            result = res.getInt("User_ID");
//        }
//        catch (SQLException e) {
//            return -1;
//        }
//
//        return result;
//    }
}
