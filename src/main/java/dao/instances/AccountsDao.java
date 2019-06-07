package dao.instances;

import model.accounts.Account;
import model.accounts.AccountType;
import model.accounts.CreditAccount;
import model.accounts.DepositAccount;

import java.sql.*;
import java.util.ArrayList;

public class AccountsDao extends AbstractDao<Account> {

    public AccountsDao(Connection connection) {
        super(connection);
    }

    @Override
    public Account findById(int id) {

        Account account;

        try  (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Accounts WHERE Account_id = " + id);

            if (!res.next()) {
                return null;
            }

            String accountType = res.getString("Account_type");

            if (accountType.equals("credit")) {
                account = new CreditAccount(id, AccountType.CREDIT, res.getInt("User_id"),
                        res.getBigDecimal("Amount"), res.getDate("Validity"),
                        res.getString("Account_number"), res.getBigDecimal("Rate"),
                        res.getBigDecimal("Credit_limit"), res.getBigDecimal("Current_debt"),
                        res.getBigDecimal("Interest_charges"));
            }
            else if (accountType.equals("deposit")) {
                account = new DepositAccount(id, AccountType.DEPOSIT, res.getInt("User_id"),
                        res.getBigDecimal("Amount"), res.getDate("Validity"),
                        res.getString("Account_number"), res.getBigDecimal("Rate"),
                        res.getBigDecimal("Deposit_amount"));
            }
            else {
                account = null;
            }
        }
        catch (SQLException e) {
            return null;
        }

        return account;
    }

    public Account findByNumber(String number) {

        Account account;

        try  (
                PreparedStatement statement = this.connection.prepareStatement(
                        "SELECT * FROM Accounts WHERE Account_number = ?");
        )
        {
            statement.setString(1, number);

            ResultSet res = statement.executeQuery();

            if (!res.next()) {
                return null;
            }

            String accountType = res.getString("Account_type");

            if (accountType.equals("credit")) {
                account = new CreditAccount(res.getInt("Account_id"), AccountType.CREDIT,
                        res.getInt("User_id"),
                        res.getBigDecimal("Amount"), res.getDate("Validity"),
                        res.getString("Account_number"), res.getBigDecimal("Rate"),
                        res.getBigDecimal("Credit_limit"), res.getBigDecimal("Current_debt"),
                        res.getBigDecimal("Interest_charges"));
            }
            else if (accountType.equals("deposit")) {
                account = new DepositAccount(res.getInt("Account_id"), AccountType.DEPOSIT,
                        res.getInt("User_id"),
                        res.getBigDecimal("Amount"), res.getDate("Validity"),
                        res.getString("Account_number"), res.getBigDecimal("Rate"),
                        res.getBigDecimal("Deposit_amount"));
            }
            else {
                account = null;
            }
        }
        catch (SQLException e) {
            return null;
        }

        return account;
    }

    @Override
    public ArrayList<Account> findAll() {

        ArrayList<Account> accounts = new ArrayList<>();

        try (
                Statement statement = this.connection.createStatement()
        )
        {
            ResultSet res = statement.executeQuery(
                    "SELECT * FROM Accounts");

            while (res.next()) {

                Account account;

                String accountType = res.getString("Account_type");

                if (accountType.equals("credit")) {
                    account = new CreditAccount(res.getInt("Account_id"), AccountType.CREDIT,
                            res.getInt("User_id"),
                            res.getBigDecimal("Amount"), res.getDate("Validity"),
                            res.getString("Account_number"), res.getBigDecimal("Rate"),
                            res.getBigDecimal("Credit_limit"), res.getBigDecimal("Current_debt"),
                            res.getBigDecimal("Interest_charges"));
                }
                else if (accountType.equals("deposit")) {
                    account = new DepositAccount(res.getInt("Account_id"), AccountType.DEPOSIT,
                            res.getInt("User_id"),
                            res.getBigDecimal("Amount"), res.getDate("Validity"),
                            res.getString("Account_number"), res.getBigDecimal("Rate"),
                            res.getBigDecimal("Deposit_amount"));
                }
                else {
                    continue;
                }

                accounts.add(account);
            }
        }
        catch (SQLException e) {
            return null;
        }

        return accounts;
    }

    @Override
    public int insert(Account entity) {

        int result;

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO Accounts (User_ID, Account_type, Account_number, " +
                                "Amount, Validity, Rate, Credit_limit, Current_debt, " +
                                "Interest_charges, Deposit_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)
        ) {
            if (entity.getType() == AccountType.CREDIT) {
                statement.setInt(1, entity.getUserId());
                statement.setString(2, "credit");
                statement.setString(3, entity.getNumber());
                statement.setBigDecimal(4, entity.getCurrBalance());
                statement.setDate(5, entity.getValidity());
                statement.setBigDecimal(6, ((CreditAccount) entity).getCreditRate());
                statement.setBigDecimal(7, ((CreditAccount) entity).getCreditLimit());
                statement.setBigDecimal(8, ((CreditAccount) entity).getCurrentDebt());
                statement.setBigDecimal(9, ((CreditAccount) entity).getInterestCharges());
                statement.setBigDecimal(10, null);
            }
            else if (entity.getType() == AccountType.DEPOSIT) {
                statement.setInt(1, entity.getUserId());
                statement.setString(2, "deposit");
                statement.setString(3, entity.getNumber());
                statement.setBigDecimal(4, entity.getCurrBalance());
                statement.setDate(5, entity.getValidity());
                statement.setBigDecimal(6, ((DepositAccount) entity).getDepositRate());
                statement.setBigDecimal(7, null);
                statement.setBigDecimal(8, null);
                statement.setBigDecimal(9, null);
                statement.setBigDecimal(10, ((DepositAccount) entity).getDepositAmount());
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
                    entity.setAccountId(result);
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
    public boolean update(Account entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE Accounts SET User_ID = ?, Account_type = ?, Account_number = ?, " +
                                "Amount = ?, Validity = ?, Rate = ?, Credit_limit = ?, Current_debt = ?, " +
                                "Interest_charges = ?, Deposit_amount = ? WHERE Account_ID = ?")
        ) {
            if (entity.getType() == AccountType.CREDIT) {
                statement.setInt(1, entity.getUserId());
                statement.setString(2, "credit");
                statement.setString(3, entity.getNumber());
                statement.setBigDecimal(4, entity.getCurrBalance());
                statement.setDate(5, entity.getValidity());
                statement.setBigDecimal(6, ((CreditAccount) entity).getCreditRate());
                statement.setBigDecimal(7, ((CreditAccount) entity).getCreditLimit());
                statement.setBigDecimal(8, ((CreditAccount) entity).getCurrentDebt());
                statement.setBigDecimal(9, ((CreditAccount) entity).getInterestCharges());
                statement.setBigDecimal(10, null);
                statement.setInt(11, entity.getAccountId());
            }
            else if (entity.getType() == AccountType.DEPOSIT) {
                statement.setInt(1, entity.getUserId());
                statement.setString(2, "deposit");
                statement.setString(3, entity.getNumber());
                statement.setBigDecimal(4, entity.getCurrBalance());
                statement.setDate(5, entity.getValidity());
                statement.setBigDecimal(6, ((DepositAccount) entity).getDepositRate());
                statement.setBigDecimal(7, null);
                statement.setBigDecimal(8, null);
                statement.setBigDecimal(9, null);
                statement.setBigDecimal(10, ((DepositAccount) entity).getDepositAmount());
                statement.setInt(11, entity.getAccountId());
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
    public boolean delete(Account entity) {

        try (
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM Accounts WHERE Account_ID = ?")
        ) {

            statement.setInt(1, entity.getAccountId());

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
