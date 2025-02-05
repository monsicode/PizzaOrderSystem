package com.deliciouspizza.command.employee.reports;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CountOrdersForPeriod implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 2;
    private static final int START_DATE_FIELD = 0;
    private static final int END_DATE_FIELD = 1;

    protected static final String RESET = "\u001B[0m";
    protected static final String BLUE = "\u001B[34m";

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate from = LocalDate.parse(args[START_DATE_FIELD], formatter);
            LocalDate to = LocalDate.parse(args[END_DATE_FIELD], formatter);

            long numberOfOrders = orderService.getCountOrderInPeriod(from.atTime(00, 00), to.atTime(00, 00));
            return "Profit for period " + from + " - " + to + " is: $" + BLUE + numberOfOrders + RESET;

        } else {
            return "Not logged in, error occurred";
        }

    }
}