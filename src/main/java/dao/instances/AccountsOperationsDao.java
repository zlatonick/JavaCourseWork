package dao.instances;

import model.accountOperations.*;

import java.sql.*;
import java.util.ArrayList;

public class AccountsOperationsDao extends AbstractDao<AccountOperation> {

    public AccountsOperationsDao(Connection connection) {
        super(connection);
    }

    @Override
    public AccountOperation findById(int id) {

        AccountOperation operation;

        try  (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Accounts_operations WHERE Operation_id = " + id);

            if (!res.next()) {
                return null;
            }

            String operType = res.getString("Operation_type");

            if (operType.equals("transfer")) {
                operation = new TransferOperation(id, res.getInt("Account_id"),
                        OperationType.TRANSFER, res.getBigDecimal("Amount"),
                        res.getDate("Operation_date"), res.getInt("Dest_account_id"));
            }
            else if (operType.equals("pay_bill")) {
                operation = new PayBillOperation(id, res.getInt("Account_id"),
                        OperationType.PAY_BILL, res.getBigDecimal("Amount"),
                        res.getDate("Operation_date"), res.getInt("Bill_id"));
            }
            else if (operType.equals("deposit")) {
                operation = new DepositOperation(id, res.getInt("Account_id"),
                        OperationType.DEPOSIT, res.getBigDecimal("Amount"),
                        res.getDate("Operation_date"));
            }
            else {
                return null;
            }
        }
        catch (SQLException e) {
            return null;
        }

        return operation;
    }

    @Override
    public ArrayList<AccountOperation> findAll() {

        ArrayList<AccountOperation> operations = new ArrayList<>();

        try (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Accounts_operations");

            while (res.next()) {

                AccountOperation operation;

                String operType = res.getString("Operation_type");

                if (operType.equals("transfer")) {
                    operation = new TransferOperation(res.getInt("Operation_id"),
                            res.getInt("Account_id"),
                            OperationType.TRANSFER, res.getBigDecimal("Amount"),
                            res.getDate("Operation_date"), res.getInt("Dest_account_id"));
                }
                else if (operType.equals("pay_bill")) {
                    operation = new PayBillOperation(res.getInt("Operation_id"),
                            res.getInt("Account_id"),
                            OperationType.PAY_BILL, res.getBigDecimal("Amount"),
                            res.getDate("Operation_date"), res.getInt("Bill_id"));
                }
                else if (operType.equals("deposit")) {
                    operation = new DepositOperation(res.getInt("Operation_id"),
                            res.getInt("Account_id"),
                            OperationType.DEPOSIT, res.getBigDecimal("Amount"),
                            res.getDate("Operation_date"));
                }
                else {
                    continue;
                }

                operations.add(operation);
            }
        }
        catch (SQLException e) {
            return null;
        }

        return operations;
    }

    @Override
    public int insert(AccountOperation entity) {

        int result;

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Accounts_operations (Account_id, Dest_account_id, Bill_id, " +
                                "Operation_type, Amount, Operation_date) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            if (entity.getType() == OperationType.TRANSFER) {
                statement.setInt(1, entity.getAccountId());
                statement.setInt(2, ((TransferOperation) entity).getAccountDestId());
                statement.setInt(3, 0);
                statement.setString(4, "transfer");
                statement.setBigDecimal(5, entity.getAmount());
                statement.setDate(6, entity.getDate());
            }
            else if (entity.getType() == OperationType.PAY_BILL) {
                statement.setInt(1, entity.getAccountId());
                statement.setInt(2, 0);
                statement.setInt(3, ((PayBillOperation) entity).getBillId());
                statement.setString(4, "pay_bill");
                statement.setBigDecimal(5, entity.getAmount());
                statement.setDate(6, entity.getDate());
            }
            else if (entity.getType() == OperationType.DEPOSIT) {
                statement.setInt(1, entity.getAccountId());
                statement.setInt(2, 0);
                statement.setInt(3, 0);
                statement.setString(4, "deposit");
                statement.setBigDecimal(5, entity.getAmount());
                statement.setDate(6, entity.getDate());
            }
            else {
                return -1;
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return -1;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    result = generatedKeys.getInt(1);
                    entity.setOperationId(result);
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
    public boolean update(AccountOperation entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Accounts_operations SET Account_id = ?, Dest_account_id = ?, Bill_id = ?, " +
                                "Operation_type = ?, Amount = ?, Operation_date = ? WHERE Operation_id = ?")
        ) {
            if (entity.getType() == OperationType.TRANSFER) {
                statement.setInt(1, entity.getAccountId());
                statement.setInt(2, ((TransferOperation) entity).getAccountDestId());
                statement.setInt(3, 0);
                statement.setString(4, "transfer");
                statement.setBigDecimal(5, entity.getAmount());
                statement.setDate(6, entity.getDate());
                statement.setInt(7, entity.getOperationId());
            }
            else if (entity.getType() == OperationType.PAY_BILL) {
                statement.setInt(1, entity.getAccountId());
                statement.setInt(2, 0);
                statement.setInt(3, ((PayBillOperation) entity).getBillId());
                statement.setString(4, "pay_bill");
                statement.setBigDecimal(5, entity.getAmount());
                statement.setDate(6, entity.getDate());
                statement.setInt(7, entity.getOperationId());
            }
            else if (entity.getType() == OperationType.DEPOSIT) {
                statement.setInt(1, entity.getAccountId());
                statement.setInt(2, 0);
                statement.setInt(3, 0);
                statement.setString(4, "deposit");
                statement.setBigDecimal(5, entity.getAmount());
                statement.setDate(6, entity.getDate());
                statement.setInt(7, entity.getOperationId());
            }
            else {
                return false;
            }

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
    public boolean delete(AccountOperation entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Accounts_operations WHERE Operation_id = ?")
        ) {

            statement.setInt(1, entity.getOperationId());

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
