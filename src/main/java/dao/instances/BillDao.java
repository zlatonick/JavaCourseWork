package dao.instances;

import model.Bill;

import java.sql.*;
import java.util.ArrayList;

public class BillDao extends AbstractDao<Bill> {

    public BillDao(Connection connection) {
        super(connection);
    }

    @Override
    public Bill findById(int id) {

        Bill bill;

        try  (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Bills WHERE Bill_id = " + id);

            if (!res.next()) {
                return null;
            }

            bill = new Bill(id, res.getInt("User_id"), res.getString("Name"),
                    res.getBigDecimal("Amount"), res.getDate("Pay_until"),
                    res.getBoolean("Is_payed"));
        }
        catch (SQLException e) {
            return null;
        }

        return bill;
    }

    @Override
    public ArrayList<Bill> findAll() {

        ArrayList<Bill> bills = new ArrayList<>();

        try (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Bills");

            while (res.next()) {
                bills.add(new Bill(res.getInt("Bill_id"), res.getInt("User_id"),
                        res.getString("Name"),
                        res.getBigDecimal("Amount"), res.getDate("Pay_until"),
                        res.getBoolean("Is_payed")));
            }
        }
        catch (SQLException e) {
            return null;
        }

        return bills;
    }

    @Override
    public int insert(Bill entity) {

        int result;

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Bills (User_id, Name, Amount, Pay_until, Is_payed) " +
                                "VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, entity.getUserId());
            statement.setString(2, entity.getName());
            statement.setBigDecimal(3, entity.getAmount());
            statement.setDate(4, entity.getPayUntil());
            statement.setBoolean(5, entity.isPayed());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1);
                    entity.setBillId(result);
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
    public boolean update(Bill entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Bills SET User_id = ?, Name = ?, Amount = ?, Pay_until = ?, Is_payed = ? " +
                                "WHERE Bill_id = ?")
        ) {
            statement.setInt(1, entity.getUserId());
            statement.setString(2, entity.getName());
            statement.setBigDecimal(3, entity.getAmount());
            statement.setDate(4, entity.getPayUntil());
            statement.setBoolean(5, entity.isPayed());
            statement.setInt(6, entity.getBillId());

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
    public boolean delete(Bill entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Bills WHERE Bill_id = ?")
        ) {

            statement.setInt(1, entity.getBillId());

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
