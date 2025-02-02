package com.deliciouspizza.command.employee;

import com.deliciouspizza.command.Command;

import java.nio.channels.SocketChannel;

public class ShowMenuEmployee implements Command {

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PINK = "\u001B[35m";

    @Override
    public String execute(String[] args, SocketChannel client) {
        return String.format("""
                
                ===== Welcome to DeliciousPizza, %s%s%s ! =====
                %s1.%s %s add-product %s - Add new product
                %s2.%s %s process %s - Process next order
                %s3.%s %s view-orders %s - View pending orders
                %s4.%s %s deactivated-products %s - View deactivated products
                %s5.%s %s menu %s - Current menu
                %s6.%s %s maintain %s - Maintain product
                %s5.%s %s reports %s - View reports
                %s5.%s %s logout %s - Log out of your account
                =====================
                Choose a command:\s""",
            YELLOW, args[0], RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET);
    }
}
