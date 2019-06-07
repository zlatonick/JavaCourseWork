package controller;

import model.BankModel;
import model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@SessionAttributes("session")
public class MainController {

    private BankModel bankModel;

    public MainController() {
        this.bankModel = new BankModel();
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/login")
    public String loginPost(@RequestParam(name="name") String name,
                            @RequestParam(name="password") String password,
                            Model model) {

        int userId = bankModel.login(name, password);

        if (userId != -1) {

            UserInfo info = bankModel.getUserInfoById(userId);

            if (info.isManager()) {
                BankSession session = new BankSession(bankModel.getManagerById(userId), null, null);
                model.addAttribute("session", session);
                return "forward:/manager";
            }
            else {
                BankSession session = new BankSession(null, bankModel.getUserById(userId), null);
                model.addAttribute("session", session);
                return "forward:/user";
            }
        }
        else {
            return "login";
        }
    }

    @PostMapping("/register")
    public String registerPost(@RequestParam(name="name") String name,
                            @RequestParam(name="password") String password,
                               Model model) {

        int userId = bankModel.registerUser(name, password);

        if (userId != -1) {
            BankSession session = new BankSession(null, bankModel.getUserById(userId), null);
            model.addAttribute("session", session);
            return "forward:/user" + userId;
        }
        else {
            return "register";
        }
    }
}
