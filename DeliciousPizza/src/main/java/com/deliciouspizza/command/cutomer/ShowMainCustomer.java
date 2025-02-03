package com.deliciouspizza.command.cutomer;

import com.deliciouspizza.command.Command;

import java.nio.channels.SocketChannel;

public class ShowMainCustomer implements Command {

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PINK = "\u001B[35m";
    private static final String GRAY = "\u001B[90m";

    private static final int USERNAME_FIELD = 0;

    @Override
    public String execute(String[] args, SocketChannel client) {
        return String.format("""
                
                ===== Welcome to DeliciousPizza, %s%s%s ! =====
                %s1.%s %s create-order %s - Start a new order
                %s2.%s %s add-product %s %s<product-key> <quantity>%s - Add product to current order
                %s3.%s %s remove-product %s %s<product-key> <quantity>%s - Remove product to current order
                %s4.%s %s finish-order %s - Start a new order
                %s5.%s %s view-history %s - View your order history
                %s6.%s %s repeat-order %s %s<orderId>%s - Log in with your credentials
                %s7.%s %s products %s - View active products menu with product keys
                %s8.%s %s logout %s - Log out of your account
                =====================
                Choose a command:\s""",
            YELLOW, args[USERNAME_FIELD], RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET, GRAY, RESET,
            PINK, RESET, BLUE, RESET, GRAY, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET, GRAY, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET);
    }

}
