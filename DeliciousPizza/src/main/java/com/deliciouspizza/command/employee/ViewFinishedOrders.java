package com.deliciouspizza.command.employee;

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
            if (!orderService.getHistoryOfOrders().isEmpty()) {
                return orderService.getHistoryOfOrders().toString();
            } else {
                return "No pending";
            }
        } else {
            return "Not logged in, error occurred";
        }
    }

}