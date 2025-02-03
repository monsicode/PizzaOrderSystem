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

    private static final int COUNT_NEEDED_ARGUMENTS = 2;
    private static final int USERNAME_FIELD = 0;
    private static final int PASSWORD_FIELD = 1;

    public LogIn(UserService userService, SessionManager manager) {
        this.userService = userService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        args = Arrays.stream(args)
            .filter(arg -> !arg.trim().isEmpty())
            .toArray(String[]::new);

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: login <username> <password>";
        }

        String username = args[USERNAME_FIELD];
        String password = args[PASSWORD_FIELD];

        UserRights role = userService.getUserRights(username);

        if (role == null) {
            return "Invalid username. Try again.";
        } else {
            boolean isLoggedIn = userService.canUserLogIn(username, password);
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

