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

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ManagerController {

    private BankModel bankModel;

    public ManagerController() {
        this.bankModel = new BankModel();
    }

    @PostMapping("/manager")
    public String managerGet(@SessionAttribute("session") BankSession session,
                             Model model) {

        Manager manager = session.getManager();
        UserInfo info = manager.getInfo();
        ArrayList<UserInfo> users = manager.getAllUsers();
        ArrayList<AccountRequest> requests = manager.getAllRequests();

        model.addAttribute("userinfo", info);
        model.addAttribute("users", users);
        model.addAttribute("requests", this.formRequests(requests));

        User user = session.getUser();

        if (user == null) {
            return "managerNoUser";
        }

        UserInfo clientInfo = user.getInfo();
        ArrayList<Account> accounts = user.getAccountsInfo();

        model.addAttribute("client_info", clientInfo);

        // Getting an account
        Account account = session.getAccount();
        if (account == null) {
            if (!accounts.isEmpty()) {
                account = accounts.get(0);
                session.setAccount(account);
            }
            else {
                return "managerNoAcc";
            }
        }

        // Forming the list of account operations
        ArrayList<AccountOperation> operations = Accounts.getAccountOperations(account);
        model.addAttribute("account_operations", formAccountOperations(operations));

        model.addAttribute("account", account);
        model.addAttribute("accounts", accounts);

        return "manager";
    }


    @PostMapping("/manager/user")
    public String modifyUser(@SessionAttribute("session") BankSession session,
                             @RequestParam(name="user_id") String userIdStr,
                             @RequestParam(name="action") String action) {

        // Getting the manager info
        Manager manager = session.getManager();
        int userId = Integer.parseInt(userIdStr);

        if (action.equals("manager")) {
            manager.makeManager(userId);
        }
        else {
            User user = bankModel.getUserById(userId);
            session.setUser(user);
        }
        return "forward:/manager";
    }


    @PostMapping("/manager/account")
    public String managerAccount(@SessionAttribute("session") BankSession session,
                              @RequestParam(name="account_id") String accountId) {

        User user = session.getUser();
        Account account = Accounts.getAccountById(Integer.parseInt(accountId));

        session.setAccount(account);
        return "forward:/manager";
    }


    @PostMapping("/manager/accept")
    public String acceptRequest(@SessionAttribute("session") BankSession session,
                                @RequestParam(name="request_id") String requestIdStr) {

        // Getting the manager info
        Manager manager = session.getManager();

        // Current date
        Date date = new Date(new java.util.Date().getTime());

        int requestId = Integer.parseInt(requestIdStr);
        manager.acceptRequest(requestId, this.addYearToDate(date));

        return "forward:/manager";
    }


    @PostMapping("/manager/decline")
    public String declineRequest(@SessionAttribute("session") BankSession session,
                                @RequestParam(name="request_id") String requestIdStr) {

        // Getting the manager info
        Manager manager = session.getManager();

        int requestId = Integer.parseInt(requestIdStr);
        manager.declineRequest(requestId);

        return "forward:/manager";
    }


    @PostMapping("/manager/change")
    public String managerChange(@SessionAttribute("session") BankSession session,
                             @RequestParam(name="new_first_name", defaultValue = "") String newFirstName,
                             @RequestParam(name="new_last_name", defaultValue = "") String newLastName,
                             @RequestParam(name="new_phone", defaultValue = "") String newPhone,
                             @RequestParam(name="new_email", defaultValue = "") String newEmail) {

        // Getting the user info
        Manager manager = session.getManager();
        UserInfo info = manager.getInfo();

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

        manager.changeInfo(info);
        return "forward:/manager";
    }


    @PostMapping("/manager/bill")
    public String managerBill(@SessionAttribute("session") BankSession session,
                                @RequestParam(name="bill_name") String billName,
                                @RequestParam(name="bill_amount") String billAmountStr,
                                @RequestParam(name="bill_term") String billTermStr) {

        // Getting the manager info
        Manager manager = session.getManager();
        int userId = session.getUser().getInfo().getUserId();

        try {
            BigDecimal billAmount = new BigDecimal(billAmountStr);
            Date billTerm = new Date(new SimpleDateFormat("yyyy-MM-dd").parse(
                    billTermStr).getTime());

            manager.addBill(new Bill(-1, userId, billName, billAmount,
                    addDayToDate(billTerm), false));

        } catch (Exception e) {

        }
        return "forward:/manager";
    }


    private Date addYearToDate(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, 1);

        return new Date(c.getTime().getTime());
    }


    private Date addDayToDate(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);

        return new Date(c.getTime().getTime());
    }


    private Map<AccountRequest, String> formRequests(ArrayList<AccountRequest> requests) {

        Map<AccountRequest, String> result = new HashMap<>();

        for (AccountRequest request : requests) {
            User user = bankModel.getUserById(request.getUserId());
            result.put(request, user.getInfo().getLogin());
        }

        return result;
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
