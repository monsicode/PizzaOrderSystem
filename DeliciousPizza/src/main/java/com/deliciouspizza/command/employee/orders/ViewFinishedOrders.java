package com.deliciouspizza.command.employee.orders;

import com.deliciouspizza.command.Command;
import com.deliciouspizza.command.SessionManager;
import com.deliciouspizza.service.OrderService;

import java.nio.channels.SocketChannel;

public class ViewFinishedOrders implements Command {

    private final OrderService orderService;
    private final SessionManager manager;

    public ViewFinishedOrders(OrderService orderService, SessionManager manager) {
        this.orderService = orderService;
        this.manager = manager;
    }

    @Override
    public String execute(String[] args, SocketChannel client) {
        if (manager.isLoggedIn(client)) {
            String username = manager.getUsername(client);

            if (manager.isUserEmployee(username)) {
                if (!orderService.getHistoryOfOrders().isEmpty()) {
                    return orderService.getHistoryOfOrders().toString();
                } else {
                    return "No finished orders from the last week";
                }
            } else {
                return "You don't have the rights for this command!";
            }

        } else {
            return "Not logged in, error occurred";
        }
    }

}