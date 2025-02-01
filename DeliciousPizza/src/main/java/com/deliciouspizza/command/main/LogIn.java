package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.cutomer.ShowMainCustomer;
import com.deliciouspizza.command.employee.ShowMenuEmployee;
import com.deliciouspizza.enums.UserRights;
import com.deliciouspizza.service.UserService;

import java.util.Arrays;

public class LogIn implements Command {

    private final UserService userService;

    public LogIn(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(String[] args) {
        args = Arrays.stream(args)
            .filter(arg -> !arg.trim().isEmpty())
            .toArray(String[]::new);

        if (args.length != 2) {
            return "Usage: login <username> <password>";
        }

        String username = args[0].trim();
        String password = args[1].trim();

        UserRights role = userService.getUserRights(username);

        if (role == null) {
            return "Invalid username. Try again.";
        } else {
            boolean isLoggedIn = userService.loginUser(username, password);

            if (isLoggedIn) {
                Command nextMenuCommand = role.equals(UserRights.CUSTOMER)
                    ? new ShowMainCustomer()
                    : new ShowMenuEmployee();

                return nextMenuCommand.execute(new String[] {username});
            }

            return "Login failed. Incorrect password.";
        }
    }

}

