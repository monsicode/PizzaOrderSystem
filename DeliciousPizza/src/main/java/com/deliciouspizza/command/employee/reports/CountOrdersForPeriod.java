package com.deliciouspizza.command.employee.reports;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CountOrdersForPeriod implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 2;
    private static final int START_DATE_FIELD = 0;
    private static final int END_DATE_FIELD = 1;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";

    private static final int END_TIME_HOURS = 23;
    private static final int END_TIME_MINUTES = 59;

    public CountOrdersForPeriod(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {

        if (args.length != COUNT_NEEDED_ARGUMENTS) {
            return "Usage: profit <start-date> <end-date>";
        }

        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {

                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate from = LocalDate.parse(args[START_DATE_FIELD], formatter);
                    LocalDate to = LocalDate.parse(args[END_DATE_FIELD], formatter);

                    long numberOfOrders = orderService.getCountOrderInPeriod(from.atTime(00, 00),
                        to.atTime(END_TIME_HOURS, END_TIME_MINUTES));
                    return "Number of orders between " + from + " and " + to + " is: " + BLUE + numberOfOrders + RESET;
                } catch (DateTimeParseException err) {
                    return "Wrong data format!";
                }

            } else {
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }

    }
}