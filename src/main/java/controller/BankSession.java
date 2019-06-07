package controller;

import model.Manager;
import model.User;
import model.accounts.Account;

public class BankSession {

    private User user;
    private Manager manager;
    private Account account;

    public BankSession(Manager manager, User user, Account account) {
        this.user = user;
        this.manager = manager;
        this.account = account;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public User getUser() {
        return user;
    }

    public Manager getManager() {
        return manager;
    }

    public Account getAccount() {
        return account;
    }
}
