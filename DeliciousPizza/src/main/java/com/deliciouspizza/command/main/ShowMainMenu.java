package com.deliciouspizza.command.main;

import com.deliciouspizza.command.Command;
import java.nio.channels.SocketChannel;

public class ShowMainMenu implements Command {

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PINK = "\u001B[35m";

    @Override
    public String execute(String[] args, SocketChannel client) {
        return String.format("""
            
            ===== Main Menu =====
            %s1.%s %s register-customer %s %s<username> <password> <address> <age>%s - Register as a customer
            %s2.%s %s register-employee %s %s<admin password> <username> <password>%s - Register as an employee
            %s3.%s %s login %s %s<username> <password>%s - Log in with your credentials
            %s4.%s %s exit %s - Exit the application
            =====================
            Choose a command:\s""",
            PINK, RESET, BLUE, RESET, YELLOW, RESET,
            PINK, RESET, BLUE, RESET, YELLOW, RESET,
            PINK, RESET, BLUE, RESET, YELLOW, RESET,
            PINK, RESET, BLUE, RESET);
    }

}
