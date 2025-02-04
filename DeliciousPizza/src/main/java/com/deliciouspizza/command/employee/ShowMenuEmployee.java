package com.deliciouspizza.command.employee;

import com.deliciouspizza.command.Command;

import java.nio.channels.SocketChannel;

public class ShowMenuEmployee implements Command {

    private static final String RESET = "\u001B[0m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PINK = "\u001B[35m";

    private static final int USERNAME_FIELD = 0;

    @Override
    public String execute(String[] args, SocketChannel client) {
        return String.format("""
                
                ===== Welcome to DeliciousPizza, %s%s%s! ====
                %s------------------------%s
                %sOrder maintenance%s
                    %s1.%s%s process %s - Process next order
                    %s2.%s%s view-pending %s - View pending orders
                    %s3.%s%s finished-orders %s - View finished orders from the last week
                
                %sCustomer menu maintenance%s
                    %s4.%s%s menu %s - View current menu
                    %s5.%s%s add %s<product-key>%s - Add new product to menu (Activate)
                    %s6.%s%s remove %s<product-key>%s - Remove product from menu (Deactivate)
                
                %sStock maintenance%s
                    %s7.%s%s warehouse %s - View available products and their quantities in the warehouse
                    %s8.%s%s add-stock %s<product-key> <quantity>%s - Stock the warehouse with product
                    %s9.%s%s add-new-stock %s<product-name> <product-type> <product-size> <quantity>%s - Add new product to the warehouse
                
                %sReports%s
                    %s10.%s%s profit %s<start-date> <end-date>%s - View profit for period (yyyy-MM-dd)
                    %s11.%s%s count-orders %s<start-date> <end-date>%s - See number of orders for period (yyyy-MM-dd)
                
                =====================
                Choose a command:\s""",
            YELLOW, args[USERNAME_FIELD], RESET,

            BLUE, RESET,
            PINK, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, RESET,

            PINK, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, YELLOW, RESET,
            PINK, RESET, BLUE, YELLOW, RESET,

            PINK, RESET,
            PINK, RESET, BLUE, RESET,
            PINK, RESET, BLUE, YELLOW, RESET,
            PINK, RESET, BLUE, YELLOW, RESET,

            PINK, RESET,
            PINK, RESET, BLUE, YELLOW, RESET,
            PINK, RESET, BLUE, YELLOW, RESET
        );
    }

}
