package dao.instances;

import model.AccountRequest;
import model.accounts.AccountType;

import java.sql.*;
import java.util.ArrayList;

public class AccountRequestsDao extends AbstractDao<AccountRequest> {

    public AccountRequestsDao(Connection connection) {
        super(connection);
    }

    @Override
    public AccountRequest findById(int id) {

        AccountRequest accountRequest;

        try  (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Account_requests WHERE Request_id = " + id);

            if (!res.next()) {
                return null;
            }

            String typeStr = res.getString("Account_type");
            AccountType type = typeStr.equals("credit") ? AccountType.CREDIT : AccountType.DEPOSIT;

            accountRequest = new AccountRequest(id, res.getInt("User_id"), type,
                    res.getBigDecimal("Amount"), res.getBigDecimal("Rate"));
        }
        catch (SQLException e) {
            return null;
        }

        return accountRequest;
    }

    @Override
    public ArrayList<AccountRequest> findAll() {

        ArrayList<AccountRequest> accountRequests = new ArrayList<>();

        try (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Account_requests");

            while (res.next()) {

                String typeStr = res.getString("Account_type");
                AccountType type = typeStr.equals("credit") ? AccountType.CREDIT : AccountType.DEPOSIT;

                accountRequests.add(new AccountRequest(res.getInt("Request_id"),
                        res.getInt("User_id"), type,
                        res.getBigDecimal("Amount"), res.getBigDecimal("Rate")));
            }
        }
        catch (SQLException e) {
            return null;
        }

        return accountRequests;
    }

    @Override
    public int insert(AccountRequest entity) {

        int result;

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Account_requests (User_id, Account_type, Amount, Rate) " +
                                "VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {

            String typeStr = entity.getType() == AccountType.CREDIT ? "credit" : "deposit";

            statement.setInt(1, entity.getUserId());
            statement.setString(2, typeStr);
            statement.setBigDecimal(3, entity.getAmount());
            statement.setBigDecimal(4, entity.getRate());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1);
                    entity.setRequestId(result);
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
    public boolean update(AccountRequest entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Account_requests SET User_id = ?, Account_type = ?, Amount = ?, " +
                                "Rate = ? WHERE Request_id = ?")
        ) {

            String typeStr = entity.getType() == AccountType.CREDIT ? "credit" : "deposit";

            statement.setInt(1, entity.getUserId());
            statement.setString(2, typeStr);
            statement.setBigDecimal(3, entity.getAmount());
            statement.setBigDecimal(4, entity.getRate());
            statement.setInt(5, entity.getRequestId());

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
    public boolean delete(AccountRequest entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Account_requests WHERE Request_id = ?")
        ) {

            statement.setInt(1, entity.getRequestId());

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
}
