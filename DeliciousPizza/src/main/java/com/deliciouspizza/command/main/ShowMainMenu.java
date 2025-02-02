package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;

import java.nio.channels.SocketChannel;

public class ShowMainMenu implements Command {

    @Override
    public String execute(String[] args, SocketChannel client) {

        return """
            ===== Main Menu =====
            1. register-customer <username> <password> <address> <age> - Register as a customer
            2. register-employee <admin password> <username> <password> - Register as an employee
            3. login <username> <password> - Log in with your credentials
            4. exit - Exit the application
            =====================
            Choose a command:\s""";

    }

}
