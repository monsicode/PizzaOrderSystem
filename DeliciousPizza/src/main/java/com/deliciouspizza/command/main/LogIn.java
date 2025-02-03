package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.cutomer.ShowMainCustomer;
import com.deliciouspizza.command.employee.ShowMenuEmployee;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.enums.UserRights;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class LogIn implements Command {

    private final UserService userService;
    private final SessionManager manager;

    public LogIn(UserService userService, SessionManager manager) {
        this.userService = userService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
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
            manager.addSession(client, username);

            if (isLoggedIn) {
                Command nextMenuCommand = role.equals(UserRights.CUSTOMER)
                    ? new ShowMainCustomer()
                    : new ShowMenuEmployee();

                return nextMenuCommand.execute(new String[] {username}, client);
            }
            return "Login failed. Incorrect password.";
        }
    }

}

