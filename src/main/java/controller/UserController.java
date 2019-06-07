package controller;

import model.*;
import model.accountOperations.AccountOperation;
import model.accountOperations.OperationType;
import model.accountOperations.PayBillOperation;
import model.accountOperations.TransferOperation;
import model.accounts.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

@Controller
public class UserController {

    public UserController() {}

    @PostMapping("/user")
    public String userGet(@SessionAttribute("session") BankSession session,
                       Model model) {

        User user = session.getUser();
        ArrayList<Account> accounts = user.getAccountsInfo();
        UserInfo info = user.getInfo();
        ArrayList<Bill> bills = user.getAllBills();

        model.addAttribute("userinfo", info);
        model.addAttribute("bills", bills);
        model.addAttribute("accounts", accounts);

        // Getting an account
        Account account = session.getAccount();
        if (account == null) {
            if (!accounts.isEmpty()) {
                account = accounts.get(0);
                session.setAccount(account);
            }
            else {
                return "userNoAcc";
            }
        }

        // Forming the list of account operations
        ArrayList<AccountOperation> operations = Accounts.getAccountOperations(account);
        model.addAttribute("account_operations", formAccountOperations(operations));

        model.addAttribute("account", account);

        return "userAcc";
    }


    @PostMapping("/user/select")
    public String userAccount(@SessionAttribute("session") BankSession session,
                              @RequestParam(name="account_id") String accountId) {

        User user = session.getUser();
        Account account = Accounts.getAccountById(Integer.parseInt(accountId));

        session.setAccount(account);
        return "forward:/user";
    }


    @PostMapping("/user/request")
    public String userRequest(@SessionAttribute("session") BankSession session,
                           @RequestParam(name="new_acc_type") String accountType,
                           @RequestParam(name="new_acc_amount") String accountAmount,
                           @RequestParam(name="new_acc_rate") String accountRate) {

        User user = session.getUser();

        try {
            BigDecimal amount = new BigDecimal(accountAmount);
            BigDecimal rate = new BigDecimal(accountRate);

            user.newAccountRequest(accountType, amount, rate);
        }
        catch (Exception ignored) {

        }
        return "forward:/user";
    }


    @PostMapping("/user/change")
    public String userChange(@SessionAttribute("session") BankSession session,
                             @RequestParam(name="new_first_name", defaultValue = "") String newFirstName,
                             @RequestParam(name="new_last_name", defaultValue = "") String newLastName,
                             @RequestParam(name="new_phone", defaultValue = "") String newPhone,
                             @RequestParam(name="new_email", defaultValue = "") String newEmail) {

        // Getting the user info
        User user = session.getUser();
        UserInfo info = user.getInfo();

        if (!newFirstName.isEmpty()) {
            info.setFirstName(newFirstName);
        }
        if (!newLastName.isEmpty()) {
            info.setLastName(newLastName);
        }
        if (!newPhone.isEmpty()) {
            info.setPhone(newPhone);
        }
        if (!newEmail.isEmpty()) {
            info.setEmail(newEmail);
        }

        user.changeInfo(info);
        return "forward:/user";
    }


    @PostMapping("/user/pay_bill")
    public String userPayBill(@SessionAttribute("session") BankSession session,
                              @RequestParam(name="bill_id") String billId) {

        // Getting the user info
        User user = session.getUser();
        Account account = session.getAccount();

        // Current date
        Date date = new Date(new java.util.Date().getTime());

        Bill bill = Accounts.getBillById(Integer.parseInt(billId));
        user.payBill(account, bill, date);
        return "forward:/user";
    }


    @PostMapping("/user/transfer")
    public String userTransfer(@SessionAttribute("session") BankSession session,
                              @RequestParam(name="account_number") String accountNumber,
                               @RequestParam(name="amount") String amountStr) {

        // Getting the user info
        User user = session.getUser();
        Account account = session.getAccount();

        // Current date
        Date date = new Date(new java.util.Date().getTime());

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            Account accountTo = Accounts.getAccountByNumber(accountNumber);

            user.makeTransfer(account, accountTo, amount, date);
        }
        catch (Exception ignored) {

        }
        return "forward:/user";
    }


    private ArrayList<String> formAccountOperations(ArrayList<AccountOperation> operations) {

        ArrayList<String> result = new ArrayList<>();

        for (AccountOperation operation : operations) {

            String line = "";

            if (operation.getType() == OperationType.TRANSFER) {

                Account destAccount = Accounts.getAccountById(
                        ((TransferOperation)operation).getAccountDestId());

                line += "TRANSFER to " + destAccount.getNumber();
            }
            else if (operation.getType() == OperationType.PAY_BILL) {

                Bill bill = Accounts.getBillById(((PayBillOperation)operation).getBillId());

                line += "PAY BILL " + bill.getName();
            }

            line += ": sum - $" + operation.getAmount() + ", date - " + operation.getDate();

            result.add(line);
        }

        return result;
    }
}
