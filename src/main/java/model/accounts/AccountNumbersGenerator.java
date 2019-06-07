package model.accounts;

import java.util.Date;

public class AccountNumbersGenerator {

    public static String nextNumber() {
        return "" + new Date().getTime() % 100000000;
    }
}
