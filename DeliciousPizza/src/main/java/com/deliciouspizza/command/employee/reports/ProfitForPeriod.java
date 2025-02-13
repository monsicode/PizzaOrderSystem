package com.deliciouspizza.command.employee.reports;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProfitForPeriod implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    private static final int COUNT_NEEDED_ARGUMENTS = 2;
    private static final int START_DATE_FIELD = 0;
    private static final int END_DATE_FIELD = 1;

    protected static final String RESET = "\u001B[0m";
    protected static final String GREEN = "\u001B[32m";

    public ProfitForPeriod(OrderService orderService, SessionManager manager) {
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate from = LocalDate.parse(args[START_DATE_FIELD], formatter);
                LocalDate to = LocalDate.parse(args[END_DATE_FIELD], formatter);

                double profit = orderService.getProfitInPeriod(from.atTime(00, 00), to.atTime(00, 00));
                return "Number of orders between " + from + " and " + to + " is: " + GREEN + profit + RESET;

            } else {
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }

    }
}
