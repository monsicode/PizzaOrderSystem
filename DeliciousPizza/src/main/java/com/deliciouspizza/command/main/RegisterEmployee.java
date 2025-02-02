package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.channels.SocketChannel;

public class RegisterEmployee implements Command {

    private static final int COUNT_NEEDED_ARGUMENTS = 3;
    private static final String ADMIN_HASHED_PASSWORD = "$2a$10$QVQziCS3KXdEwgTQrT3LieiUrr5yd1iuwwYgOymQFoPqbnTJL1csq";
    private static final int ATTEMPTS_PASSWORD = 3;

    private final UserService userService;

    public RegisterEmployee(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: register-employee <admin password> <username> <password>";
        }

        String inputPassword = args[0];

        if (verifyPassword(inputPassword)) {
            String username = args[1];
            String password = args[2];

            userService.registerEmployee(username, password);
            return "User " + username + " successfully registered! ";

        } else {
            return "Invalid admin password. Access denied.";
        }

    }

    private static boolean verifyPassword(String inputPassword) {
        return BCrypt.checkpw(inputPassword, ADMIN_HASHED_PASSWORD);
    }

}