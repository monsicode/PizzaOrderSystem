package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.service.UserService;

import java.nio.channels.SocketChannel;

public class RegisterCustomer implements Command {

    private static final int COUNT_NEEDED_ARGUMENTS = 4;
    private final UserService userService;

    public RegisterCustomer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: register-customer <username> <password> <address> <age>";
        }

        String username = args[0];
        String password = args[1];
        String address = args[2];
        int age = Integer.parseInt(args[3]);

        userService.registerCustomer(username, password, address, age);
        return "User " + username + " successfully registered! ";
    }

}