package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.exception.UserAlreadyExistsException;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class RegisterCustomer implements Command {

    private static final int COUNT_NEEDED_ARGUMENTS = 4;
    private final UserService userService;

    private static final int USERNAME_FIELD = 0;
    private static final int PASSWORD_FIELD = 1;
    private static final int ADDRESS_FIELD = 2;
    private static final int AGE_FIELD = 3;

    public RegisterCustomer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: register-customer <username> <password> <address> <age>";
        }

        String username = args[USERNAME_FIELD];
        String password = args[PASSWORD_FIELD];
        String address = args[ADDRESS_FIELD];
        int age = Integer.parseInt(args[AGE_FIELD]);

        try {
            userService.registerCustomer(username, password, address, age);
            return "User " + username + " successfully registered! ";
        } catch (UserAlreadyExistsException err) {
            return err.getMessage();
        }

    }

}